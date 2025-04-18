package Model;

import Graphics.Pieces;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.LinkedList;

public class GameLogic {
    private final Pieces pieces;
    private int blackScore;
    private int whiteScore;
    private boolean isBlackTurn;
    private ArrayList<int[]> moveHistory;
    private int[] lastCapturedPosition; // Lưu vị trí quân bị ăn cuối cùng
    private int lastCapturedCount;      // Số quân bị ăn ở lượt cuối
    private int blackTime;
    private int whiteTime;
    private int consecutivePasses;
    private double komi;
    private LinkedList<String> boardStateHistory; // Lưu trạng thái bàn cờ sau mỗi nước đi

    public GameLogic(Pieces pieces, int initialTime, double komi) {
        this.pieces = pieces;
        this.blackScore = 0;
        this.whiteScore = 0;
        this.isBlackTurn = true;
        this.moveHistory = new ArrayList<>();
        this.lastCapturedPosition = null;
        this.lastCapturedCount = 0;
        this.blackTime = initialTime;
        this.whiteTime = initialTime;
        this.consecutivePasses = 0;
        this.komi = komi;
        this.boardStateHistory = new LinkedList<>();
        // Lưu trạng thái ban đầu của bàn cờ
        this.boardStateHistory.add(getBoardState());
    }

    public boolean placePiece(int x, int y) {
        if (!isValidPosition(x, y) || !pieces.isEmpty(x, y)) return false;

        // Lưu vị trí bị ăn trước đó để kiểm tra Ko
        int[] koPosition = lastCapturedPosition != null ? lastCapturedPosition.clone() : null;
        int capturedCount = lastCapturedCount;

        // Kiểm tra luật Ko đơn giản (không được đánh vào vị trí vừa bị ăn)
        // Chỉ áp dụng khi có đúng 1 quân bị ăn ở lượt trước
        if (capturedCount == 1 && koPosition != null && x == koPosition[0] && y == koPosition[1]) {
            return false; // Từ chối nước đi vi phạm luật Ko
        }

        Color color = isBlackTurn ? Color.BLACK : Color.WHITE;
        pieces.addPiece(x, y, color);

        // Kiểm tra và bắt quân đối phương nếu có thể
        int captured = captureOpponentPieces(x, y, color);
        if (captured > 0) {
            if (isBlackTurn) blackScore += captured;
            else whiteScore += captured;
        }

        // Kiểm tra tự do của nhóm vừa đặt
        if (!hasLiberties(x, y, color)) {
            pieces.removePiece(x, y);
            return false;
        }

        // Lấy trạng thái bàn cờ sau khi đặt quân và bắt quân
        String newBoardState = getBoardState();

        // Kiểm tra luật Ko mở rộng (không được tạo lại trạng thái bàn cờ đã tồn tại)
        if (boardStateHistory.contains(newBoardState)) {
            pieces.removePiece(x, y);
            return false;
        }

        // Cập nhật lịch sử
        moveHistory.add(new int[]{x, y});
        boardStateHistory.add(newBoardState);
        // Giới hạn kích thước lịch sử nếu cần thiết để tránh tràn bộ nhớ
        if (boardStateHistory.size() > 8) {
            boardStateHistory.removeFirst();
        }

        lastCapturedCount = captured;
        if (captured == 0) lastCapturedPosition = null; // Reset Ko nếu không ăn quân

        consecutivePasses = 0;
        isBlackTurn = !isBlackTurn;
        return true;
    }

    public void pass() {
        consecutivePasses++;
        lastCapturedPosition = null;
        lastCapturedCount = 0;
        isBlackTurn = !isBlackTurn;

        // Thêm trạng thái bàn cờ sau khi pass
        boardStateHistory.add(getBoardState());
        if (boardStateHistory.size() > 8) {
            boardStateHistory.removeFirst();
        }

        if (consecutivePasses >= 2) calculateFinalScore();
    }

    public void updateTime() {
        if (isBlackTurn) blackTime--;
        else whiteTime--;
    }

    private int captureOpponentPieces(int x, int y, Color color) {
        Color opponentColor = color.equals(Color.BLACK) ? Color.WHITE : Color.BLACK;
        int capturedCount = 0;
        lastCapturedPosition = null; // Reset vị trí bị ăn

        int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};

        for (int[] dir : directions) {
            int adjX = x + dir[0];
            int adjY = y + dir[1];
            if (isValidPosition(adjX, adjY) && !pieces.isEmpty(adjX, adjY) &&
                    getPieceColor(adjX, adjY).equals(opponentColor)) {
                if (!hasLiberties(adjX, adjY, opponentColor)) {
                    int groupSize = removeGroup(adjX, adjY, opponentColor);
                    capturedCount += groupSize;

                    // Chỉ lưu vị trí Ko khi đúng 1 quân bị ăn
                    if (groupSize == 1) {
                        lastCapturedPosition = new int[]{adjX, adjY}; // Lưu vị trí bị ăn
                    }
                }
            }
        }
        return capturedCount;
    }

    private boolean hasLiberties(int x, int y, Color color) {
        Set<String> visited = new HashSet<>();
        return checkLiberties(x, y, color, visited);
    }

    private boolean checkLiberties(int x, int y, Color color, Set<String> visited) {
        String key = x + "," + y;
        if (!isValidPosition(x, y) || visited.contains(key)) return false;
        if (pieces.isEmpty(x, y)) return true;
        Color pieceColor = getPieceColor(x, y);
        if (pieceColor == null || !pieceColor.equals(color)) return false;

        visited.add(key);
        int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
        for (int[] dir : directions) {
            if (checkLiberties(x + dir[0], y + dir[1], color, visited)) return true;
        }
        return false;
    }

    private int removeGroup(int x, int y, Color color) {
        Set<String> visited = new HashSet<>();
        ArrayList<int[]> group = new ArrayList<>();
        findGroup(x, y, color, visited, group);
        for (int[] pos : group) pieces.removePiece(pos[0], pos[1]);
        return group.size();
    }

    private void findGroup(int x, int y, Color color, Set<String> visited, ArrayList<int[]> group) {
        String key = x + "," + y;
        if (!isValidPosition(x, y) || visited.contains(key) || pieces.isEmpty(x, y)) return;
        Color pieceColor = getPieceColor(x, y);
        if (pieceColor == null || !pieceColor.equals(color)) return;

        visited.add(key);
        group.add(new int[]{x, y});
        int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
        for (int[] dir : directions) {
            findGroup(x + dir[0], y + dir[1], color, visited, group);
        }
    }

    private String getBoardState() {
        StringBuilder state = new StringBuilder();
        int boardSize = pieces.getBoardSize();
        for (int y = 0; y < boardSize; y++) {
            for (int x = 0; x < boardSize; x++) {
                Color c = getPieceColor(x, y);
                state.append(c == null ? "0" : (c.equals(Color.BLACK) ? "1" : "2"));
            }
        }
        return state.toString();
    }

    private void calculateFinalScore() {
        int blackTerritory = countTerritory(Color.BLACK);
        int whiteTerritory = countTerritory(Color.WHITE);
        double finalBlackScore = blackTerritory + blackScore;
        double finalWhiteScore = whiteTerritory + whiteScore + komi;
        System.out.println("Black: " + finalBlackScore + " - White: " + finalWhiteScore);
    }

    private int countTerritory(Color color) {
        Set<String> visited = new HashSet<>();
        int territory = 0;
        int boardSize = pieces.getBoardSize();
        for (int y = 0; y < boardSize; y++) {
            for (int x = 0; x < boardSize; x++) {
                if (pieces.isEmpty(x, y) && !visited.contains(x + "," + y)) {
                    if (isTerritory(x, y, color, visited)) {
                        territory += countEnclosedArea(x, y, color, visited);
                    }
                }
            }
        }
        return territory;
    }

    private boolean isTerritory(int x, int y, Color color, Set<String> visited) {
        Set<String> localVisited = new HashSet<>();
        ArrayList<int[]> area = new ArrayList<>();
        floodFill(x, y, color, localVisited, area);
        for (int[] pos : area) visited.add(pos[0] + "," + pos[1]);
        for (int[] pos : area) {
            int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
            for (int[] dir : directions) {
                int adjX = pos[0] + dir[0];
                int adjY = pos[1] + dir[1];
                if (isValidPosition(adjX, adjY) && !pieces.isEmpty(adjX, adjY)) {
                    Color adjColor = getPieceColor(adjX, adjY);
                    if (adjColor != null && !adjColor.equals(color)) return false;
                }
            }
        }
        return true;
    }

    private int countEnclosedArea(int x, int y, Color color, Set<String> visited) {
        Set<String> localVisited = new HashSet<>();
        ArrayList<int[]> area = new ArrayList<>();
        floodFill(x, y, color, localVisited, area);
        visited.addAll(localVisited);
        return area.size();
    }

    private void floodFill(int x, int y, Color color, Set<String> visited, ArrayList<int[]> area) {
        String key = x + "," + y;
        if (!isValidPosition(x, y) || visited.contains(key) || !pieces.isEmpty(x, y)) return;
        visited.add(key);
        area.add(new int[]{x, y});
        int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
        for (int[] dir : directions) {
            floodFill(x + dir[0], y + dir[1], color, visited, area);
        }
    }

    private boolean isValidPosition(int x, int y) {
        int boardSize = pieces.getBoardSize();
        return x >= 0 && x < boardSize && y >= 0 && y < boardSize;
    }

    private Color getPieceColor(int x, int y) {
        for (Pieces.Piece piece : pieces.getPieceList()) {
            if (piece.getX() == x && piece.getY() == y) return piece.getColor();
        }
        return null;
    }

    public int getBlackScore() { return blackScore; }
    public int getWhiteScore() { return whiteScore; }
    public boolean isBlackTurn() { return isBlackTurn; }
    public int getBlackTime() { return blackTime; }
    public int getWhiteTime() { return whiteTime; }
    public boolean isGameEnded() { return consecutivePasses >= 2 || blackTime <= 0 || whiteTime <= 0; }


    @Override
    public GameLogic clone() {
        GameLogic cloned = new GameLogic(pieces.clone(pieces.getBoard()), 0, komi);

        cloned.blackScore = this.blackScore;
        cloned.whiteScore = this.whiteScore;
        cloned.isBlackTurn = this.isBlackTurn;
        cloned.blackTime = this.blackTime;
        cloned.whiteTime = this.whiteTime;
        cloned.consecutivePasses = this.consecutivePasses;
        cloned.lastCapturedCount = this.lastCapturedCount;
        cloned.lastCapturedPosition = this.lastCapturedPosition != null ? this.lastCapturedPosition.clone() : null;
        cloned.moveHistory = new ArrayList<>(this.moveHistory);
        cloned.boardStateHistory = new LinkedList<>(this.boardStateHistory);

        return cloned;
    }

}