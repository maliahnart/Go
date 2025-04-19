package Graphics;

import Utils.Sound;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class MenuPanel extends JPanel {
    private BufferedImage backgroundImage;
    private BufferedImage btnPlay;
    private BufferedImage btnSettings;
    private BufferedImage btnExit;

    private boolean isPlayHover = false;
    private boolean isSettingsHover = false;
    private boolean isExitHover = false;

    private static final int BUTTON_WIDTH = 100;
    private static final int BUTTON_HEIGHT = 50;

    private Sound menuMusic; // Nhạc nền menu

    public MenuPanel() {
        loadResources();
        addMouseListener(new MouseHandler());
        addMouseMotionListener(new MouseHandler());

        // Khởi tạo và phát nhạc nền
        try {
            menuMusic = new Sound("Resources/BackgroundMusic.wav");
            menuMusic.playSound();
        } catch (Exception e) {
            System.err.println("Không thể tải nhạc nền: " + e.getMessage());
        }
    }

    private void loadResources() {
        try {
            backgroundImage = ImageIO.read(getClass().getResource("/Resources/gowall.png"));
        } catch (IOException e) {
            System.err.println("Không thể tải ảnh nền: " + e.getMessage());
            setBackground(Color.LIGHT_GRAY); // Màu dự phòng
        }

        try {
            btnPlay = ImageIO.read(getClass().getResource("/Resources/play.png"));
        } catch (IOException e) {
            System.err.println("Không thể tải ảnh btnPlay: " + e.getMessage());
            btnPlay = createFallbackImage(BUTTON_WIDTH, BUTTON_HEIGHT, Color.GREEN);
        }

        try {
            btnSettings = ImageIO.read(getClass().getResource("/Resources/setting.png"));
        } catch (IOException e) {
            System.err.println("Không thể tải ảnh btnSettings: " + e.getMessage());
            btnSettings = createFallbackImage(BUTTON_WIDTH, BUTTON_HEIGHT, Color.BLUE);
        }

        try {
            btnExit = ImageIO.read(getClass().getResource("/Resources/quit.png"));
        } catch (IOException e) {
            System.err.println("Không thể tải ảnh btnExit: " + e.getMessage());
            btnExit = createFallbackImage(BUTTON_WIDTH, BUTTON_HEIGHT, Color.RED);
        }
    }

    // Tạo ảnh dự phòng nếu không tải được
    private BufferedImage createFallbackImage(int width, int height, Color color) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setColor(color);
        g.fillRect(0, 0, width, height);
        g.setColor(Color.BLACK);
        g.drawRect(0, 0, width - 1, height - 1); // Vẽ viền
        g.dispose();
        return image;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        if (backgroundImage != null) {
            g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
        }

        int centerX = getWidth() / 2;
        int startY = 200;

        // Vẽ nút với hiệu ứng hover đơn giản (đổi màu nếu hover)
        if (isPlayHover) {
            g2d.setColor(new Color(0, 0, 0, 100)); // Lớp phủ mờ
            g2d.fillRect(centerX - BUTTON_WIDTH / 2, startY, BUTTON_WIDTH, BUTTON_HEIGHT);
        }
        g2d.drawImage(btnPlay, centerX - BUTTON_WIDTH / 2, startY, BUTTON_WIDTH, BUTTON_HEIGHT, null);

        if (isSettingsHover) {
            g2d.setColor(new Color(0, 0, 0, 100));
            g2d.fillRect(centerX - BUTTON_WIDTH / 2, startY + 80, BUTTON_WIDTH, BUTTON_HEIGHT);
        }
        g2d.drawImage(btnSettings, centerX - BUTTON_WIDTH / 2, startY + 80, BUTTON_WIDTH, BUTTON_HEIGHT, null);

        if (isExitHover) {
            g2d.setColor(new Color(0, 0, 0, 100));
            g2d.fillRect(centerX - BUTTON_WIDTH / 2, startY + 160, BUTTON_WIDTH, BUTTON_HEIGHT);
        }
        g2d.drawImage(btnExit, centerX - BUTTON_WIDTH / 2, startY + 160, BUTTON_WIDTH, BUTTON_HEIGHT, null);
    }

    private class MouseHandler extends MouseAdapter {
        @Override
        public void mouseMoved(MouseEvent e) {
            int x = e.getX();
            int y = e.getY();
            int centerX = getWidth() / 2;
            int startY = 200;

            isPlayHover = isMouseOver(x, y, centerX - BUTTON_WIDTH / 2, startY);
            isSettingsHover = isMouseOver(x, y, centerX - BUTTON_WIDTH / 2, startY + 80);
            isExitHover = isMouseOver(x, y, centerX - BUTTON_WIDTH / 2, startY + 160);

            repaint();
        }

        @Override
        public void mousePressed(MouseEvent e) {
            int x = e.getX();
            int y = e.getY();
            int centerX = getWidth() / 2;
            int startY = 200;

            if (isMouseOver(x, y, centerX - BUTTON_WIDTH / 2, startY)) {
                // Cấu hình mặc định cho trò chơi
                SettingPanel.GameSettings defaultSettings = new SettingPanel.GameSettings(
                        "PVP", 19, 30, 6.5, "Trung Quốc", null, null
                );
                GameFrame.getInstance().startGame(defaultSettings);
            } else if (isMouseOver(x, y, centerX - BUTTON_WIDTH / 2, startY + 80)) {
                GameFrame.getInstance().showGameSettings(); // Chuyển sang SettingPanel
            } else if (isMouseOver(x, y, centerX - BUTTON_WIDTH / 2, startY + 160)) {
                if (menuMusic != null) {
                    menuMusic.stopSound();
                }
                System.exit(0);
            }
        }

    }

    private boolean isMouseOver(int x, int y, int btnX, int btnY) {
        return x >= btnX && x <= btnX + BUTTON_WIDTH && y >= btnY && y <= btnY + BUTTON_HEIGHT;
    }
}