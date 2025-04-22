package Graphics;

import java.awt.*;
import java.util.ArrayList;
import javax.swing.ImageIcon;

public class Pieces {
    private static final float PIECE_SCALE = 0.9f;
    private static final Color SHADOW_COLOR = new Color(0, 0, 0, 50);
    private static final int SHADOW_OFFSET = 2;

    private final Board board;
    private final ArrayList<Piece> pieceList;
    private final Image blackPieceImage;
    private final Image whitePieceImage;

    public static class Piece {
        private final int x;
        private final int y;
        private final Color color;

        public Piece(int x, int y, Color color) {
            this.x = x;
            this.y = y;
            this.color = color;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public Color getColor() {
            return color;
        }
    }

    public Pieces(Board board) {
        this.board = board;
        this.pieceList = new ArrayList<>();

        this.blackPieceImage = new ImageIcon(getClass().getResource("/Resources/black_stones.png")).getImage();
        this.whitePieceImage = new ImageIcon(getClass().getResource("/Resources/white_stones.png")).getImage();

        if (blackPieceImage == null || whitePieceImage == null) {
            System.err.println("Không thể tải ảnh quân cờ. Đảm bảo file black_stones.png và white_stones.png tồn tại trong /Resources/");
        }
    }

    public void addPiece(int x, int y, Color color) {
        synchronized (pieceList) {
            if (isValidPosition(x, y) && isEmpty(x, y)) {
                pieceList.add(new Piece(x, y, color));
            }
        }
    }

    public void removePiece(int x, int y) {
        synchronized (pieceList) {
            pieceList.removeIf(piece -> piece.getX() == x && piece.getY() == y);
        }
    }


    private boolean isValidPosition(int x, int y) {
        return x >= 0 && x < board.getSize() && y >= 0 && y < board.getSize();
    }

    public boolean isEmpty(int x, int y) {
        return pieceList.stream().noneMatch(piece -> piece.getX() == x && piece.getY() == y);
    }

    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int cellSize = board.getCellSize();
        int offsetX = board.getOffsetX();
        int offsetY = board.getOffsetY();
        int pieceSize = (int) (cellSize * PIECE_SCALE);

        synchronized (pieceList) {
            for (Piece piece : pieceList) {
                int pixelX = offsetX + piece.getX() * cellSize - pieceSize / 2;
                int pixelY = offsetY + piece.getY() * cellSize - pieceSize / 2;
                Image pieceImage = piece.getColor().equals(Color.BLACK) ? blackPieceImage : whitePieceImage;

                g2d.setColor(SHADOW_COLOR);
                g2d.fillOval(pixelX + SHADOW_OFFSET, pixelY + SHADOW_OFFSET, pieceSize, pieceSize);

                if (pieceImage != null) {
                    g2d.drawImage(pieceImage, pixelX, pixelY, pieceSize, pieceSize, null);
                } else {
                    g2d.setColor(piece.getColor());
                    g2d.fillOval(pixelX, pixelY, pieceSize, pieceSize);
                    if (piece.getColor().equals(Color.WHITE)) {
                        g2d.setColor(Color.BLACK);
                        g2d.setStroke(new BasicStroke(1.0f));
                        g2d.drawOval(pixelX, pixelY, pieceSize, pieceSize);
                    }
                }
            }
        }
    }


    public ArrayList<Piece> getPieceList() {
        return new ArrayList<>(pieceList);
    }

    // Thêm phương thức để GameLogic lấy kích thước bàn cờ
    public int getBoardSize() {
        return board.getSize();
    }


    public Board getBoard() {
        return board;
    }

    public Pieces clone(Board boardClone) {
        Pieces cloned = new Pieces(boardClone);
        for (Piece piece : this.pieceList) {
            cloned.pieceList.add(new Piece(piece.getX(), piece.getY(), piece.getColor()));
        }
        return cloned;
    }

}