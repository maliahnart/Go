package Model;

import java.awt.Color;

public class MinimaxAI {
    private final int boardSize;
    private final int maxDepth;
    private final boolean aiPlaysBlack;

    public MinimaxAI(int boardSize, int maxDepth, boolean aiPlaysBlack) {
        this.boardSize = boardSize;
        this.maxDepth = maxDepth;
        this.aiPlaysBlack = aiPlaysBlack;
    }

    public void makeBestMove(GameLogic gameLogic) {
        int bestScore = Integer.MIN_VALUE;
        int bestX = -1, bestY = -1;

        for (int y = 0; y < boardSize; y++) {
            for (int x = 0; x < boardSize; x++) {
                GameLogic cloned = gameLogic.clone();
                if (cloned.placePiece(x, y)) {
                    int score = minimax(cloned, maxDepth - 1, false, Integer.MIN_VALUE, Integer.MAX_VALUE);
                    if (score > bestScore) {
                        bestScore = score;
                        bestX = x;
                        bestY = y;
                    }
                }
            }
        }

        if (bestX != -1) {
            gameLogic.placePiece(bestX, bestY);
            System.out.println("AI placed at: " + bestX + ", " + bestY);
        } else {
            gameLogic.pass();
            System.out.println("AI passed.");
        }
    }

    private int minimax(GameLogic game, int depth, boolean maximizing, int alpha, int beta) {
        if (depth == 0 || game.isGameEnded()) {
            return evaluateBoard(game);
        }

        int bestScore = maximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        for (int y = 0; y < boardSize; y++) {
            for (int x = 0; x < boardSize; x++) {
                GameLogic cloned = game.clone();
                if (cloned.placePiece(x, y)) {
                    int score = minimax(cloned, depth - 1, !maximizing, alpha, beta);

                    if (maximizing) {
                        bestScore = Math.max(bestScore, score);
                        alpha = Math.max(alpha, score);
                    } else {
                        bestScore = Math.min(bestScore, score);
                        beta = Math.min(beta, score);
                    }

                    if (beta <= alpha) break;
                }
            }
        }

        return bestScore;
    }

    private int evaluateBoard(GameLogic game) {
        double black = game.getBlackScore();
        double white = game.getWhiteScore() + 6.5; // komi

        return aiPlaysBlack ? (int) (black - white) : (int) (white - black);
    }
}
