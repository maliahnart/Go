package Graphics;

import Model.Default;
import Model.Settings;

import java.awt.*;
import java.awt.geom.Ellipse2D;

public class Board {
    private static final Color WOOD_LIGHT = new Color(245, 222, 179);
    private static final Color WOOD_DARK = new Color(210, 180, 140);
    private static final Color BORDER_COLOR = new Color(139, 69, 19);
    private static final Color SHADOW_COLOR = new Color(50, 50, 50, 150);
    private static final int PADDING = 10;
    private static final int TOP_ADJUSTMENT = 20;
    private static final int MIN_CELL_SIZE = 20;

    private final int size;
    private final int cellSize;
    private final int boardSize;
    private final int offsetX;
    private final int offsetY;

    public Board() {
        this.size = Default.Config.BOARD_SIZE;
        this.cellSize = calculateCellSize();
        this.boardSize = (size - 1) * cellSize;
        this.offsetX = (Settings.Config.GAME_WIDTH - boardSize) / 2;
        this.offsetY = (Settings.Config.GAME_HEIGHT - boardSize) / 2 - TOP_ADJUSTMENT;
    }

    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawBackground(g2d);
        drawGrid(g2d);
        drawBorder(g2d);
        drawStarPoints(g2d);
    }

    private int calculateCellSize() {
        int maxBoardWidth = Settings.Config.GAME_WIDTH - 2 * PADDING;
        int maxBoardHeight = Settings.Config.GAME_HEIGHT - 2 * PADDING - TOP_ADJUSTMENT;
        int maxCellSize = Math.min(maxBoardWidth, maxBoardHeight) / (size - 1);
        return Math.max(maxCellSize, MIN_CELL_SIZE);
    }

    private void drawBackground(Graphics2D g2d) {
        GradientPaint gradient = new GradientPaint(offsetX, offsetY, WOOD_LIGHT,
                offsetX + boardSize, offsetY + boardSize, WOOD_DARK);
        g2d.setPaint(gradient);
        g2d.fillRect(offsetX - PADDING, offsetY - PADDING, boardSize + 2 * PADDING, boardSize + 2 * PADDING);
    }

    private void drawGrid(Graphics2D g2d) {
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(1.0f));
        for (int i = 0; i < size; i++) {
            int y = offsetY + i * cellSize;
            int x = offsetX + i * cellSize;
            g2d.drawLine(offsetX, y, offsetX + boardSize, y);
            g2d.drawLine(x, offsetY, x, offsetY + boardSize);
        }
    }

    private void drawBorder(Graphics2D g2d) {
        g2d.setColor(BORDER_COLOR);
        g2d.setStroke(new BasicStroke(4.0f));
        g2d.drawRect(offsetX - PADDING / 2, offsetY - PADDING / 2, boardSize + PADDING, boardSize + PADDING);
    }

    private void drawStarPoints(Graphics2D g2d) {
        int[] starPositions = getStarPositions();
        g2d.setColor(SHADOW_COLOR);
        for (int x : starPositions) {
            for (int y : starPositions) {
                int starX = offsetX + x * cellSize;
                int starY = offsetY + y * cellSize;
                g2d.fill(new Ellipse2D.Double(starX - cellSize / 8, starY - cellSize / 8, cellSize / 4, cellSize / 4));
                g2d.setColor(Color.BLACK);
                g2d.fill(new Ellipse2D.Double(starX - cellSize / 10, starY - cellSize / 10, cellSize / 5, cellSize / 5));
            }
        }
    }

    private int[] getStarPositions() {
        if (size == 9) return new int[]{2, 4, 6};
        if (size == 13) return new int[]{3, 6, 9};
        return new int[]{3, 9, 15};
    }

    // Getter
    public int getCellSize() { return cellSize; }
    public int getOffsetX() { return offsetX; }
    public int getOffsetY() { return offsetY; }
    public int getSize() { return size; }
}