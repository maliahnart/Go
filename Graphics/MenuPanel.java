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
    private BufferedImage btnPlay, btnPlayHover;
    private BufferedImage btnSettings, btnSettingsHover;
    private BufferedImage btnExit, btnExitHover;
    private BufferedImage btnMusic, btnMusicHover;

    private boolean isPlayHover = false;
    private boolean isSettingsHover = false;
    private boolean isExitHover = false;
    private boolean isMusicHover = false;
    private boolean isMusicPlaying = true;  // Trạng thái nhạc

    private static final int BUTTON_WIDTH = 100;
    private static final int BUTTON_HEIGHT = 50;

    private Sound menuMusic; // Nhạc nền menu

    public MenuPanel() {
        loadResources();
        addMouseListener(new MouseHandler());
        addMouseMotionListener(new MouseHandler());

        // Khởi tạo và phát nhạc nền
        menuMusic = new Sound("Resources/BackgroundMusic.wav");
        menuMusic.playSound();
    }

    private void loadResources() {
        try {
            backgroundImage = ImageIO.read(getClass().getResource("/Resources/gowall.png"));
            btnPlay = ImageIO.read(getClass().getResource("/Resources/play.png"));
            btnPlayHover = ImageIO.read(getClass().getResource("/Resources/gowall.png"));
            btnSettings = ImageIO.read(getClass().getResource("/Resources/setting.png"));
            btnSettingsHover = ImageIO.read(getClass().getResource("/Resources/gowall.png"));
            btnExit = ImageIO.read(getClass().getResource("/Resources/quit.png"));
            btnExitHover = ImageIO.read(getClass().getResource("/Resources/gowall.png"));
            btnMusic = ImageIO.read(getClass().getResource("/Resources/quit.png"));
            btnMusicHover = ImageIO.read(getClass().getResource("/Resources/quit.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
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

        g2d.drawImage(isPlayHover ? btnPlayHover : btnPlay, centerX - BUTTON_WIDTH / 2, startY, BUTTON_WIDTH, BUTTON_HEIGHT, null);
        g2d.drawImage(isSettingsHover ? btnSettingsHover : btnSettings, centerX - BUTTON_WIDTH / 2, startY + 80, BUTTON_WIDTH, BUTTON_HEIGHT, null);
        g2d.drawImage(isExitHover ? btnExitHover : btnExit, centerX - BUTTON_WIDTH / 2, startY + 160, BUTTON_WIDTH, BUTTON_HEIGHT, null);

        // Vẽ nút bật/tắt nhạc
        g2d.drawImage(isMusicPlaying ? btnMusic : btnMusicHover, 20, getHeight() - 60, 50, 50, null);
    }

    private class MouseHandler extends MouseAdapter {
        @Override
        public void mouseMoved(MouseEvent e) {
            int x = e.getX();
            int y = e.getY();
            int centerX = getWidth() / 2;
            int startY = 200;

            isPlayHover = isMouseOver(x, y, centerX, startY);
            isSettingsHover = isMouseOver(x, y, centerX, startY + 80);
            isExitHover = isMouseOver(x, y, centerX, startY + 160);
            isMusicHover = isMouseOver(x, y, 20, getHeight() - 60, 50, 50);

            repaint();
        }

        @Override
        public void mousePressed(MouseEvent e) {
            int x = e.getX();
            int y = e.getY();
            int centerX = getWidth() / 2;
            int startY = 200;

            if (isMouseOver(x, y, centerX, startY)) {
                menuMusic.stopSound();
                GameFrame.getInstance().playGame();
            } else if (isMouseOver(x, y, centerX, startY + 80)) {
                GameFrame.getInstance().showGameSettings();
            } else if (isMouseOver(x, y, centerX, startY + 160)) {
                menuMusic.stopSound();
                System.exit(0);
            } else if (isMouseOver(x, y, 20, getHeight() - 60, 50, 50)) {
                toggleMusic();
            }
        }
    }

    private boolean isMouseOver(int x, int y, int btnX, int btnY) {
        return x >= btnX && x <= btnX + BUTTON_WIDTH && y >= btnY && y <= btnY + BUTTON_HEIGHT;
    }

    private boolean isMouseOver(int x, int y, int btnX, int btnY, int width, int height) {
        return x >= btnX && x <= btnX + width && y >= btnY && y <= btnY + height;
    }

    private void toggleMusic() {
        if (isMusicPlaying) {
            menuMusic.stopSound();
        } else {
            menuMusic.playSound();
        }
        isMusicPlaying = !isMusicPlaying;
        repaint();
    }
}
