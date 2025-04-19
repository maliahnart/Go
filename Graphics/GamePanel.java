package Graphics;

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
    private MinimaxAI ai;
    private final Image backgroundImage;

    // Các thuộc tính để lưu trữ cài đặt từ SettingPanel
    private String gameMode;
    private int boardSize;
    private int timePerTurn;
    private double komi;
    private String scoring;
    private String aiDifficulty;
    private String playerSide;

    public GamePanel() {
        this.setPreferredSize(new Dimension(Settings.Config.GAME_WIDTH, Settings.Config.GAME_HEIGHT));
        this.setDoubleBuffered(true);
        this.setFocusable(true);
        this.backgroundImage = new ImageIcon(getClass().getResource("/Resources/gowall.png")).getImage();
    }

    // Phương thức để gán cài đặt và bắt đầu game
    public void startGame(SettingPanel.GameSettings settings) {
        // Gán các giá trị cài đặt
        this.gameMode = settings.gameMode;
        this.boardSize = settings.boardSize;
        this.timePerTurn = settings.timePerTurn;
        this.komi = settings.komi;
        this.scoring = settings.scoring;
        this.aiDifficulty = settings.aiDifficulty;
        this.playerSide = settings.playerSide;

        // Khởi tạo các thành phần game
        board = new Board(boardSize); // Truyền boardSize vào constructor của Board
        pieces = new Pieces(board);
        gameInfoDisplay = new GameInfoDisplay();
        gameLogic = new GameLogic(pieces, timePerTurn, komi);

        // Xử lý chế độ AI
        if (gameMode.equals("AI")) {
            boolean aiIsBlack = playerSide.equals("Trắng");
            int aiDepth = getAIDepth(aiDifficulty);
            ai = new MinimaxAI(boardSize, aiDepth, aiIsBlack);

            // Nếu AI đi trước (đen), thực hiện nước đi ngay
            if (aiIsBlack && gameLogic.isBlackTurn()) {
                ai.makeBestMove(gameLogic);
                repaint();
            }
        }

        // Xử lý sự kiện nhấp chuột
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Chuyển đổi tọa độ chuột thành tọa độ ô trên bàn cờ
                int x = (e.getX() - board.getOffsetX() + board.getCellSize() / 2) / board.getCellSize();
                int y = (e.getY() - board.getOffsetY() + board.getCellSize() / 2) / board.getCellSize();

                // Kiểm tra nước đi hợp lệ
                if (x >= 0 && x < boardSize && y >= 0 && y < boardSize) {
                    if (gameMode.equals("AI")) {
                        // Chế độ AI: chỉ cho người chơi đi khi đến lượt
                        boolean isPlayerTurn = gameLogic.isBlackTurn() == playerSide.equals("Đen");
                        if (isPlayerTurn && gameLogic.placePiece(x, y)) {
                            repaint();
                            // Nếu game chưa kết thúc, để AI đi tiếp
                            if (!gameLogic.isGameEnded() && ai != null) {
                                ai.makeBestMove(gameLogic);
                                repaint();
                            }
                        }
                    } else {
                        // Chế độ PVP: cho phép cả hai người chơi đặt quân
                        if (gameLogic.placePiece(x, y)) {
                            repaint();
                        }
                    }
                }
            }
        });

        // Thread cập nhật thời gian
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
            // Hiển thị kết quả khi game kết thúc
            showGameResult();
        }).start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Vẽ nền
        if (backgroundImage != null) {
            g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
        } else {
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }

        // Vẽ bàn cờ, quân cờ và thông tin game
        if (board != null) board.draw(g2d);
        if (pieces != null) pieces.draw(g2d);
        if (gameInfoDisplay != null) {
            gameInfoDisplay.draw(g2d,
                    gameLogic.getBlackScore(),
                    gameLogic.getWhiteScore(),
                    gameLogic.getBlackTime(),
                    gameLogic.getWhiteTime(),
                    gameLogic.isBlackTurn());
        }
    }

    // Ánh xạ độ khó AI thành độ sâu tìm kiếm
    private int getAIDepth(String difficulty) {
        if (difficulty == null) return 2; // Mặc định cho an toàn
        switch (difficulty) {
            case "Dễ":
                return 1;
            case "Trung bình":
                return 2;
            case "Khó":
                return 3;
            default:
                return 2;
        }
    }

    // Hiển thị kết quả khi game kết thúc
    private void showGameResult() {
        String message;
        double blackScore = gameLogic.getBlackScore();
        double whiteScore = gameLogic.getWhiteScore() + komi; // Cộng komi cho trắng
        if (blackScore > whiteScore) {
            message = "Đen thắng! Điểm: " + blackScore + " - " + whiteScore;
        } else if (whiteScore > blackScore) {
            message = "Trắng thắng! Điểm: " + whiteScore + " - " + blackScore;
        } else {
            message = "Hòa! Điểm: " + blackScore + " - " + whiteScore;
        }
        JOptionPane.showMessageDialog(this, message, "Kết thúc ván cờ", JOptionPane.INFORMATION_MESSAGE);
        // Quay lại menu chính
        GameFrame.getInstance().showMainMenu();
    }
}