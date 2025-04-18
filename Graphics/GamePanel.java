//package Graphics;
//
//import Model.Player;
//import Model.Settings;
//
//import javax.swing.*;
//import java.awt.*;
//import java.awt.event.MouseAdapter;
//import java.awt.event.MouseEvent;
//
//public class GamePanel extends JPanel implements Runnable {
//    private Thread _gameThread;
//    private boolean _running;
//    private Player _player;
//    private  Board _board;
//    private Pieces _pieces;
//    private final Image _backgroundImage;
//
//    public GamePanel() {
//        this.setPreferredSize(new Dimension(Settings.Config.GAME_WIDTH, Settings.Config.GAME_HEIGHT));
//        this.setDoubleBuffered(true);
//        this.setFocusable(true);
//
//
//        _backgroundImage = new ImageIcon(getClass().getResource("/Resources/gowall.png")).getImage();
//    }
//
//    public void startGame() {
//        if (_gameThread == null) {
//            _board = new Board(); // Khởi tạo Board với BOARD_SIZE mới
//            _pieces = new Pieces(_board); // Khởi tạo Pieces dựa trên Board
//            addMouseListener(new MouseAdapter() { // Thêm quân cờ khi nhấp chuột
//                @Override
//                public void mouseClicked(MouseEvent e) {
//                    int cellSize = _board.getCellSize();
//                    int offsetX = _board.getOffsetX();
//                    int offsetY = _board.getOffsetY();
//                    int x = (e.getX() - offsetX + cellSize / 2) / cellSize; // Chuyển sang tọa độ ô
//                    int y = (e.getY() - offsetY + cellSize / 2) / cellSize;
//                    _pieces.addPiece(x, y, Color.BLACK); // Thêm quân đen (có thể thay đổi)
//                    repaint();
//                }
//            });
//            repaint();
//            _running = true;
//            _gameThread = new Thread(this);
//            _gameThread.start();
//        }
//    }
//
//    @Override
//    public void run() {
//        double drawInterval = 1e9 / Settings.Config.FPS;
//        long lastTime = System.nanoTime();
//        long currentTime;
//        double delta = 0;
//
//        while (_running) {
//            currentTime = System.nanoTime();
//            delta += (currentTime - lastTime) / drawInterval;
//            lastTime = currentTime;
//
//            if (delta >= 1) {
////                updateGame(); // Cập nhật logic (nếu có)
//                repaint();    // Vẽ lại giao diện
//                delta--;
//            }
//        }
//    }
//
////    private void updateGame() {
////        if (_player != null) {
////            _player.Update(); // Cập nhật trạng thái người chơi (nếu cần)
////        }
////        // Thêm logic game khác ở đây (ví dụ: di chuyển quân cờ)
////    }
//
//    @Override
//    protected void paintComponent(Graphics g) {
//        super.paintComponent(g);
//        Graphics2D g2d = (Graphics2D) g;
//
//        if (_backgroundImage != null) {
//            g2d.drawImage(_backgroundImage, 0, 0, getWidth(), getHeight(), null);
//        } else {
//            g2d.setColor(Color.LIGHT_GRAY);
//            g2d.fillRect(0, 0, getWidth(), getHeight());
//        }
//
//        if (_board != null) _board.draw(g2d);
//        if (_pieces != null) _pieces.draw(g2d);
//    }
//
//    public void stopGame() {
//        _running = false;
//        try {
//            if (_gameThread != null) {
//                _gameThread.join();
//            }
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//}
package Graphics;

import Model.Default;
import Model.GameLogic;
import Model.MinimaxAI;
import Model.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GamePanel extends JPanel {
    private Board board;
    private Pieces pieces;
    private GameInfoDisplay gameInfoDisplay;
    private GameLogic gameLogic;
    private final Image backgroundImage;

    private MinimaxAI ai;

    public GamePanel() {
        this.setPreferredSize(new Dimension(Settings.Config.GAME_WIDTH, Settings.Config.GAME_HEIGHT));
        this.setDoubleBuffered(true);
        this.setFocusable(true);
        this.backgroundImage = new ImageIcon(getClass().getResource("/Resources/gowall.png")).getImage();
    }

    public void startGame() {
        board = new Board();
        pieces = new Pieces(board);
        gameInfoDisplay = new GameInfoDisplay();
        gameLogic = new GameLogic(pieces, Default.Config.TIME_PER_TURN, 6.5); // Thêm komi


        // Kiểm tra nếu người chơi với AI
        boolean vsAI = !Default.Config.AI_DIFFICULTY.equals("Tắt"); // ví dụ: nếu "Tắt" là chơi người với người
        if (vsAI) {
            boolean aiIsBlack = Default.Config.PLAYER_SIDE.equals("Trắng");
            ai = new MinimaxAI(board.getSize(), 2, aiIsBlack);

            // Nếu AI chơi trước (đen), thì cho AI đánh luôn
            if (aiIsBlack && gameLogic.isBlackTurn()) {
                ai.makeBestMove(gameLogic);
                repaint();
            }
        }

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = (e.getX() - board.getOffsetX() + board.getCellSize() / 2) / board.getCellSize();
                int y = (e.getY() - board.getOffsetY() + board.getCellSize() / 2) / board.getCellSize();

                boolean vsAI = !Default.Config.AI_DIFFICULTY.equals("Tắt");
                boolean aiIsBlack = Default.Config.PLAYER_SIDE.equals("Trắng");
                if (vsAI) {
                    boolean playerTurn = gameLogic.isBlackTurn() == Default.Config.PLAYER_SIDE.equals("Đen");
                    if (playerTurn && gameLogic.placePiece(x, y)) {
                        repaint();

                        if (!gameLogic.isGameEnded()) {
                            ai.makeBestMove(gameLogic);
                            repaint();
                        }
                    }
                } else {
                    if (gameLogic.placePiece(x, y)) {
                        repaint();
                    }
                }
            }
        });

        new Thread(() -> {
            while (!gameLogic.isGameEnded()) {
                try {
                    Thread.sleep(1000);
                    gameLogic.updateTime();
                    repaint();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        if (backgroundImage != null) {
            g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
        } else {
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }

        if (board != null) board.draw(g2d);
        if (pieces != null) pieces.draw(g2d);
        if (gameInfoDisplay != null) {
            gameInfoDisplay.draw(g2d,
                    gameLogic.getBlackScore(),
                    gameLogic.getWhiteScore(),
                    gameLogic.getBlackTime(),
                    gameLogic.getWhiteTime(),
                    gameLogic.isBlackTurn()
            );
        }
    }
}