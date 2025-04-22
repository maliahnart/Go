package Model;

import java.awt.*;
import java.util.*;
import java.util.List;

public class MinimaxAI {
    private final boolean aiIsBlack;
    private final int maxDepth;
    private final Map<String, Integer> transpositionTable; // Bảng transposition để lưu trữ trạng thái

    public MinimaxAI(int maxDepth, boolean aiIsBlack) {
        this.maxDepth = maxDepth;
        this.aiIsBlack = aiIsBlack;
        this.transpositionTable = new HashMap<>();
    }

    public int[] getBestMove(GameLogic currentGameLogic) {
        List<int[]> possibleMoves = currentGameLogic.getStrategicMoves(aiIsBlack);
        if (possibleMoves.isEmpty()) return null;

        int[] bestMove = null;
        int bestScore = Integer.MIN_VALUE;
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;

        // Sắp xếp nước đi dựa trên tiềm năng
        possibleMoves = sortMoves(possibleMoves, currentGameLogic);

        for (int[] move : possibleMoves) {
            GameLogic clonedLogic = currentGameLogic.clone();
            boolean isPass = move[0] == -1 && move[1] == -1;
            if (isPass) {
                clonedLogic.pass();
            } else {
                if (!clonedLogic.placePiece(move[0], move[1])) continue;
            }

            String boardState = clonedLogic.getBoardState();
            int score;
            if (transpositionTable.containsKey(boardState)) {
                score = transpositionTable.get(boardState);
            } else {
                score = minimax(clonedLogic, 1, alpha, beta, false);
                transpositionTable.put(boardState, score);
            }

            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
            alpha = Math.max(alpha, bestScore);
            if (beta <= alpha) break; // Cắt tỉa
        }

        return bestMove;
    }

    private int minimax(GameLogic logic, int depth, int alpha, int beta, boolean isMaximizing) {
        String boardState = logic.getBoardState();
        if (transpositionTable.containsKey(boardState)) {
            return transpositionTable.get(boardState);
        }

        if (depth >= maxDepth || logic.isGameEnded()) {
            int score = logic.evaluateBoard(aiIsBlack);
            transpositionTable.put(boardState, score);
            return score;
        }

        List<int[]> moves = logic.getStrategicMoves(isMaximizing ? aiIsBlack : !aiIsBlack);
        moves = sortMoves(moves, logic); // Sắp xếp nước đi
        if (moves.isEmpty()) {
            int score = logic.evaluateBoard(aiIsBlack);
            transpositionTable.put(boardState, score);
            return score;
        }

        int value;
        if (isMaximizing) {
            value = Integer.MIN_VALUE;
            for (int[] move : moves) {
                GameLogic cloned = logic.clone();
                boolean isPass = move[0] == -1 && move[1] == -1;
                if (isPass) {
                    cloned.pass();
                } else {
                    if (!cloned.placePiece(move[0], move[1])) continue;
                }
                int eval = minimax(cloned, depth + 1, alpha, beta, false);
                value = Math.max(value, eval);
                alpha = Math.max(alpha, value);
                if (beta <= alpha) break;
            }
        } else {
            value = Integer.MAX_VALUE;
            for (int[] move : moves) {
                GameLogic cloned = logic.clone();
                boolean isPass = move[0] == -1 && move[1] == -1;
                if (isPass) {
                    cloned.pass();
                } else {
                    if (!cloned.placePiece(move[0], move[1])) continue;
                }
                int eval = minimax(cloned, depth + 1, alpha, beta, true);
                value = Math.min(value, eval);
                beta = Math.min(beta, value);
                if (beta <= alpha) break;
            }
        }

        transpositionTable.put(boardState, value);
        return value;
    }

    private List<int[]> sortMoves(List<int[]> moves, GameLogic logic) {
        List<MoveScore> moveScores = new ArrayList<>();
        for (int[] move : moves) {
            if (move[0] == -1 && move[1] == -1) {
                moveScores.add(new MoveScore(move, -1000)); // Pass có điểm thấp
                continue;
            }
            GameLogic cloned = logic.clone();
            if (cloned.placePiece(move[0], move[1])) {
                int score = evaluateMove(cloned, move);
                moveScores.add(new MoveScore(move, score));
            }
        }
        moveScores.sort((a, b) -> Integer.compare(b.score, a.score)); // Sắp xếp giảm dần
        List<int[]> sortedMoves = new ArrayList<>();
        for (MoveScore ms : moveScores) {
            sortedMoves.add(ms.move);
        }
        return sortedMoves;
    }

    private int evaluateMove(GameLogic logic, int[] move) {
        int score = 0;
        int x = move[0];
        int y = move[1];
        Color color = logic.isBlackTurn() ? Color.BLACK : Color.WHITE;

        // Ưu tiên nước đi gần các quân cùng màu
        int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
        for (int[] dir : directions) {
            int adjX = x + dir[0];
            int adjY = y + dir[1];
            if (logic.isValidPosition(adjX, adjY)) {
                Color adjColor = logic.getPieceColor(adjX, adjY);
                if (adjColor != null) {
                    if (adjColor.equals(color)) {
                        score += 10; // Gần quân cùng màu
                    } else {
                        score += 20; // Gần quân đối phương (có thể bắt)
                    }
                }
            }
        }

        // Ưu tiên nước đi ở trung tâm
        int boardSize = logic.getBoardSize();
        int center = boardSize / 2;
        int distToCenter = Math.abs(x - center) + Math.abs(y - center);
        score += (boardSize - distToCenter) * 5;

        return score;
    }

    public boolean isAIPlayingBlack() {
        return aiIsBlack;
    }

    private static class MoveScore {
        int[] move;
        int score;

        MoveScore(int[] move, int score) {
            this.move = move;
            this.score = score;
        }
    }
}