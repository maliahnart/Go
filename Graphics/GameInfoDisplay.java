package Graphics;

import java.awt.*;
import Model.Settings;

public class GameInfoDisplay {
    private static final Font SCORE_FONT = new Font("Arial", Font.BOLD, 18);
    private static final int PADDING = 10;
    private static final Color TEXT_COLOR = Color.WHITE; // Đổi sang màu trắng

    public void draw(Graphics g, int blackScore, int whiteScore, int blackTime, int whiteTime, boolean isBlackTurn) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Điểm số
        g2d.setFont(SCORE_FONT);
        g2d.setColor(TEXT_COLOR);

        String blackScoreText = "Đen: " + blackScore;
        String whiteScoreText = "Trắng: " + whiteScore;
        g2d.drawString(blackScoreText, PADDING, PADDING + 20);
        g2d.drawString(whiteScoreText, PADDING, PADDING + 45);

        // Thời gian của mỗi người chơi
        String blackTimeText = "Thời gian Đen: " + blackTime + "s";
        String whiteTimeText = "Thời gian Trắng: " + whiteTime + "s";
        FontMetrics fm = g2d.getFontMetrics();
        int blackTimeX = Settings.Config.GAME_WIDTH - fm.stringWidth(blackTimeText) - PADDING;
        int whiteTimeX = Settings.Config.GAME_WIDTH - fm.stringWidth(whiteTimeText) - PADDING;

        g2d.drawString(blackTimeText, blackTimeX, PADDING + 20);
        g2d.drawString(whiteTimeText, whiteTimeX, PADDING + 45);
    }
}