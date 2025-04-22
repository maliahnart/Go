package Graphics;

import Controller.GameController;
import Model.Default;
import Model.GameLogic;
import Model.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GamePanel extends JPanel {
    private Board board;
    private Pieces pieces;
    private GameInfoDisplay gameInfoDisplay;
    private GameController controller;
    private final Image backgroundImage;
    private JButton passButton;

    public GamePanel() {
        this.setPreferredSize(new Dimension(Settings.Config.GAME_WIDTH, Settings.Config.GAME_HEIGHT));
        this.setDoubleBuffered(true);
        this.setFocusable(true);
        this.backgroundImage = new ImageIcon(getClass().getResource("/Resources/gowall.png")).getImage();
        this.setLayout(null);

        // Initialize Pass button
        passButton = new JButton("Pass");
        passButton.setFocusable(false);
        passButton.setBackground(new Color(210, 180, 140));
        passButton.setForeground(Color.BLACK);
        passButton.setBorder(BorderFactory.createLineBorder(new Color(139, 69, 19), 2));
        // Đặt vị trí tạm thời ở góc dưới bên phải
        passButton.setBounds(500, 350, 80, 30); // Tọa độ an toàn để kiểm tra hiển thị
        this.add(passButton);

        // Gỡ lỗi: In thông báo khi nút được thêm
        System.out.println("Pass button added at: (500, 350)");
    }

    public void startGame() {
        board = new Board();
        pieces = new Pieces(board);
        gameInfoDisplay = new GameInfoDisplay();

        boolean vsAI = !Default.Config.AI_DIFFICULTY.equals("Tắt");
        boolean aiIsBlack = Default.Config.PLAYER_SIDE.equals("Trắng");
        int aiDepth = getAIDepth(Default.Config.AI_DIFFICULTY);

        controller = new GameController(
                pieces,
                Default.Config.TIME_PER_TURN,
                6.5,
                vsAI,
                aiIsBlack,
                aiDepth
        );

        // Cập nhật vị trí nút dựa trên kích thước thực tế của cửa sổ
        int panelWidth = this.getWidth();
        int panelHeight = this.getHeight();
        passButton.setBounds(panelWidth - 100, panelHeight - 50, 80, 30);

        // Gỡ lỗi: In thông tin vị trí nút
        System.out.println("Board size: " + board.getSize());
        System.out.println("Panel actual width: " + panelWidth);
        System.out.println("Panel actual height: " + panelHeight);
        System.out.println("Pass button final position: (" + (panelWidth - 100) + ", " + (panelHeight - 50) + ")");

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (controller.getGameLogic().isGameEnded()) return;

                int x = (e.getX() - board.getOffsetX() + board.getCellSize() / 2) / board.getCellSize();
                int y = (e.getY() - board.getOffsetY() + board.getCellSize() / 2) / board.getCellSize();

                if (controller.handlePlayerMove(x, y)) {
                    repaint();
                }
            }
        });

        passButton.addActionListener(e -> {
            if (!controller.getGameLogic().isGameEnded()) {
                System.out.println("Pass button clicked!");
                controller.handlePlayerPass();
            }
        });

        // Đảm bảo panel được vẽ lại sau khi thêm nút
        revalidate();
        repaint();

        new Thread(() -> {
            while (!controller.getGameLogic().isGameEnded()) {
                try {
                    Thread.sleep(1000);
                    controller.updateTime();
                    SwingUtilities.invokeLater(this::repaint);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private int getAIDepth(String difficulty) {
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
        if (gameInfoDisplay != null && controller != null) {
            GameLogic gameLogic = controller.getGameLogic();
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