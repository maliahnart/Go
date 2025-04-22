package Model;

import Graphics.Pieces;

import java.awt.Color;
import java.util.*;

public class GameLogic {
    private final Pieces pieces;
    private int blackScore;
    private int whiteScore;
    private boolean isBlackTurn;
    private ArrayList<int[]> moveHistory;
    private int[] lastCapturedPosition;
    private int lastCapturedCount;
    private int blackTime;
    private int whiteTime;
    private int consecutivePasses;
    private double komi;
    private LinkedList<String> boardStateHistory;

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
        this.boardStateHistory.add(getBoardState());
    }

    public boolean placePiece(int x, int y) {
        if (!isValidPosition(x, y) || !pieces.isEmpty(x, y)) return false;

        int[] koPosition = lastCapturedPosition != null ? lastCapturedPosition.clone() : null;
        int capturedCount = lastCapturedCount;

        if (capturedCount == 1 && koPosition != null && x == koPosition[0] && y == koPosition[1]) {
            return false;
        }

        Color color = isBlackTurn ? Color.BLACK : Color.WHITE;
        pieces.addPiece(x, y, color);

        int captured = captureOpponentPieces(x, y, color);
        if (captured > 0) {
            if (isBlackTurn) blackScore += captured;
            else whiteScore += captured;
        }

        if (!hasLiberties(x, y, color)) {
            pieces.removePiece(x, y);
            return false;
        }

        String newBoardState = getBoardState();

        if (boardStateHistory.contains(newBoardState)) {
            pieces.removePiece(x, y);
            return false;
        }

        moveHistory.add(new int[]{x, y});
        boardStateHistory.add(newBoardState);
        if (boardStateHistory.size() > 8) {
            boardStateHistory.removeFirst();
        }

        lastCapturedCount = captured;
        if (captured == 0) lastCapturedPosition = null;

        consecutivePasses = 0;
        isBlackTurn = !isBlackTurn;
        return true;
    }

    public void pass() {
        consecutivePasses++;
        lastCapturedPosition = null;
        lastCapturedCount = 0;
        isBlackTurn = !isBlackTurn;

        boardStateHistory.add(getBoardState());
        if (boardStateHistory.size() > 8) {
            boardStateHistory.removeFirst();
        }
    }

    public void updateTime() {
        if (isBlackTurn()) {
            blackTime -= 1;
        } else {
            whiteTime -= 1;
        }
    }

    private int captureOpponentPieces(int x, int y, Color color) {
        Color opponentColor = color.equals(Color.BLACK) ? Color.WHITE : Color.BLACK;
        int capturedCount = 0;
        lastCapturedPosition = null;

        int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};

        for (int[] dir : directions) {
            int adjX = x + dir[0];
            int adjY = y + dir[1];
            if (isValidPosition(adjX, adjY) && !pieces.isEmpty(adjX, adjY) &&
                    getPieceColor(adjX, adjY).equals(opponentColor)) {
                if (!hasLiberties(adjX, adjY, opponentColor)) {
                    int groupSize = removeGroup(adjX, adjY, opponentColor);
                    capturedCount += groupSize;

                    if (groupSize == 1) {
                        lastCapturedPosition = new int[]{adjX, adjY};
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

    public String getBoardState() {
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

    public String getWinnerMessage() {
        if (!isGameEnded()) return null;

        int blackTerritory = countTerritory(Color.BLACK);
        int whiteTerritory = countTerritory(Color.WHITE);
        double finalBlackScore = blackTerritory + blackScore;
        double finalWhiteScore = whiteTerritory + whiteScore + komi;

        StringBuilder message = new StringBuilder();
        message.append("Game Over!\n");
        message.append("Black Score: ").append(finalBlackScore).append("\n");
        message.append("White Score: ").append(finalWhiteScore).append("\n");
        if (finalBlackScore > finalWhiteScore) {
            message.append("Black wins!");
        } else if (finalWhiteScore > finalBlackScore) {
            message.append("White wins!");
        } else {
            message.append("It's a tie!");
        }
        return message.toString();
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

    public boolean isValidPosition(int x, int y) {
        int boardSize = pieces.getBoardSize();
        return x >= 0 && x < boardSize && y >= 0 && y < boardSize;
    }

    public Color getPieceColor(int x, int y) {
        for (Pieces.Piece piece : pieces.getPieceList()) {
            if (piece.getX() == x && piece.getY() == y) return piece.getColor();
        }
        return null;
    }

    public int getBoardSize() {
        return pieces.getBoardSize();
    }

    public int getBlackScore() { return blackScore; }
    public int getWhiteScore() { return whiteScore; }
    public boolean isBlackTurn() { return isBlackTurn; }
    public int getBlackTime() { return blackTime; }
    public int getWhiteTime() { return whiteTime; }
    public boolean isGameEnded() { return consecutivePasses >= 2 || blackTime <= 0 || whiteTime <= 0; }

    public List<int[]> getPossibleMoves(boolean isBlack) {
        List<int[]> moves = new ArrayList<>();
        int boardSize = pieces.getBoardSize();
        for (int x = 0; x < boardSize; x++) {
            for (int y = 0; y < boardSize; y++) {
                GameLogic tempLogic = this.clone();
                tempLogic.isBlackTurn = isBlack;
                if (tempLogic.placePiece(x, y)) {
                    moves.add(new int[]{x, y});
                }
            }
        }
        moves.add(new int[]{-1, -1});
        return moves;
    }

    public int evaluateBoard(boolean forBlack) {
        int blackTerritory = countTerritory(Color.BLACK);
        int whiteTerritory = countTerritory(Color.WHITE);
        int blackLiberties = countLiberties(Color.BLACK);
        int whiteLiberties = countLiberties(Color.WHITE);
        int blackInfluence = calculateInfluence(Color.BLACK);
        int whiteInfluence = calculateInfluence(Color.WHITE);

        int baseScore = (blackScore + blackTerritory) - (whiteScore + whiteTerritory);
        int libertyScore = (blackLiberties - whiteLiberties) * 2;
        int influenceScore = (blackInfluence - whiteInfluence) * 3;

        int totalScore = baseScore + libertyScore + influenceScore;

        return forBlack ? totalScore : -totalScore;
    }

    public void undoMove() {
        if (!moveHistory.isEmpty()) {
            int[] lastMove = moveHistory.remove(moveHistory.size() - 1);
            boardStateHistory.removeLast();
            pieces.removePiece(lastMove[0], lastMove[1]);
            isBlackTurn = !isBlackTurn;
            lastCapturedPosition = null;
            lastCapturedCount = 0;
        }
    }

    private int countLiberties(Color color) {
        Set<String> visited = new HashSet<>();
        int liberties = 0;
        for (Pieces.Piece piece : pieces.getPieceList()) {
            if (piece.getColor().equals(color)) {
                int x = piece.getX();
                int y = piece.getY();
                String key = x + "," + y;
                if (!visited.contains(key)) {
                    liberties += getGroupLiberties(x, y, color, visited);
                }
            }
        }
        return liberties;
    }

    private int getGroupLiberties(int x, int y, Color color, Set<String> visited) {
        Set<String> groupVisited = new HashSet<>();
        Set<String> libertyPositions = new HashSet<>();
        findGroupLiberties(x, y, color, groupVisited, libertyPositions);
        visited.addAll(groupVisited);
        return libertyPositions.size();
    }

    private void findGroupLiberties(int x, int y, Color color, Set<String> visited, Set<String> liberties) {
        String key = x + "," + y;
        if (!isValidPosition(x, y) || visited.contains(key) || pieces.isEmpty(x, y)) return;
        Color pieceColor = getPieceColor(x, y);
        if (pieceColor == null || !pieceColor.equals(color)) return;

        visited.add(key);
        int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
        for (int[] dir : directions) {
            int adjX = x + dir[0];
            int adjY = y + dir[1];
            String adjKey = adjX + "," + adjY;
            if (isValidPosition(adjX, adjY) && pieces.isEmpty(adjX, adjY)) {
                liberties.add(adjKey);
            } else if (isValidPosition(adjX, adjY) && !visited.contains(adjKey)) {
                findGroupLiberties(adjX, adjY, color, visited, liberties);
            }
        }
    }

    private int calculateInfluence(Color color) {
        int influence = 0;
        int boardSize = pieces.getBoardSize();
        for (int x = 0; x < boardSize; x++) {
            for (int y = 0; y < boardSize; y++) {
                if (pieces.isEmpty(x, y)) {
                    double distToNearest = getDistanceToNearestPiece(x, y, color);
                    if (distToNearest < 3) {
                        influence += (int) (10 / (distToNearest + 1));
                    }
                }
            }
        }
        return influence;
    }

    private double getDistanceToNearestPiece(int x, int y, Color color) {
        double minDistance = Double.MAX_VALUE;
        for (Pieces.Piece piece : pieces.getPieceList()) {
            if (piece.getColor().equals(color)) {
                double dist = Math.sqrt(Math.pow(piece.getX() - x, 2) + Math.pow(piece.getY() - y, 2));
                minDistance = Math.min(minDistance, dist);
            }
        }
        return minDistance;
    }

    public List<int[]> getStrategicMoves(boolean isBlack) {
        List<int[]> moves = new ArrayList<>();
        int boardSize = pieces.getBoardSize();
        Set<String> checked = new HashSet<>();

        for (Pieces.Piece piece : pieces.getPieceList()) {
            int x = piece.getX();
            int y = piece.getY();
            int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
            for (int[] dir : directions) {
                int newX = x + dir[0];
                int newY = y + dir[1];
                String key = newX + "," + newY;
                if (isValidPosition(newX, newY) && pieces.isEmpty(newX, newY) && !checked.contains(key)) {
                    GameLogic tempLogic = this.clone();
                    tempLogic.isBlackTurn = isBlack;
                    if (tempLogic.placePiece(newX, newY)) {
                        moves.add(new int[]{newX, newY});
                        checked.add(key);
                    }
                }
            }
        }

        if (moves.isEmpty()) {
            int center = boardSize / 2;
            int[][] centerPositions = {
                    {center, center}, {center - 1, center}, {center + 1, center},
                    {center, center - 1}, {center, center + 1}
            };
            for (int[] pos : centerPositions) {
                if (isValidPosition(pos[0], pos[1]) && pieces.isEmpty(pos[0], pos[1])) {
                    GameLogic tempLogic = this.clone();
                    tempLogic.isBlackTurn = isBlack;
                    if (tempLogic.placePiece(pos[0], pos[1])) {
                        moves.add(pos);
                    }
                }
            }
        }

        moves.add(new int[]{-1, -1});
        return moves;
    }

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

    public void updateTimeForAI(boolean aiColor) {
        if (aiColor) {
            blackTime -= 1;
        } else {
            whiteTime -= 1;
        }
    }
}