package Graphics;

import java.awt.*;
import java.util.ArrayList;
import javax.swing.ImageIcon;

public class Pieces {
    private final Board board;
    private final ArrayList<Piece> pieceList;
    private final Image blackPieceImage;
    private final Image whitePieceImage;

    // Static inner class với encapsulation
    private static class Piece {
        private final int x;
        private final int y;
        private final Color color;

        Piece(int x, int y, Color color) {
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

    // Thêm quân cờ mới
    public void addPiece(int x, int y, Color color) {
        if (isValidPosition(x, y)) {
            pieceList.add(new Piece(x, y, color));
        }
    }

    // Xóa quân cờ tại vị trí (x, y)
    public void removePiece(int x, int y) {
        pieceList.removeIf(piece -> piece.getX() == x && piece.getY() == y);
    }

    // Kiểm tra vị trí hợp lệ
    private boolean isValidPosition(int x, int y) {
        return x >= 0 && x < board.getSize() && y >= 0 && y < board.getSize();
    }

    // Kiểm tra ô có trống không
    public boolean isEmpty(int x, int y) {
        return pieceList.stream().noneMatch(piece -> piece.getX() == x && piece.getY() == y);
    }

    // Vẽ tất cả quân cờ bằng ảnh
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int cellSize = board.getCellSize();
        int offsetX = board.getOffsetX();
        int offsetY = board.getOffsetY();
        int pieceSize = (int) (cellSize * 0.9);

        for (Piece piece : pieceList) {
            int pixelX = offsetX + piece.getX() * cellSize - pieceSize / 2;
            int pixelY = offsetY + piece.getY() * cellSize - pieceSize / 2;
            Image pieceImage = piece.getColor().equals(Color.BLACK) ? blackPieceImage : whitePieceImage;

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

    // Getter trả về danh sách (nếu cần, nhưng nên hạn chế dùng trực tiếp)
    public ArrayList<Piece> getPieceList() {
        return new ArrayList<>(pieceList); // Trả về bản sao để bảo vệ dữ liệu
    }
}