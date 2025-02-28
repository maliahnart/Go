package Graphics;

import java.awt.*;
import Model.Settings;

public class GameInfoDisplay {
    private static final Font SCORE_FONT = new Font("Arial", Font.BOLD, 18);
    private static final int PADDING = 10;

    public void draw(Graphics g, int blackScore, int whiteScore, int timeLeft) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setFont(SCORE_FONT);
        g2d.setColor(Color.BLACK);

        // Điểm số
        String blackText = "Đen: " + blackScore;
        String whiteText = "Trắng: " + whiteScore;
        g2d.drawString(blackText, PADDING, PADDING + 10);
        g2d.drawString(whiteText, PADDING, PADDING + 35);

        // Thời gian
        String timeText = "Thời gian: " + timeLeft + "s";
        FontMetrics fm = g2d.getFontMetrics();
        int timeX = Settings.Config.GAME_WIDTH - fm.stringWidth(timeText) - PADDING;
        g2d.drawString(timeText, timeX, PADDING + 10);
    }
}