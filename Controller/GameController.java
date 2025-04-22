package Controller;

import Graphics.GameFrame;
import Model.GameLogic;
import Model.MinimaxAI;
import Graphics.Pieces;

import javax.swing.*;

public class GameController {
    private final GameLogic gameLogic;
    private final MinimaxAI ai;
    private final boolean vsAI;
    private final boolean aiIsBlack;

    public GameController(Pieces pieces, int timePerTurn, double komi,
                          boolean vsAI, boolean aiIsBlack, int aiDepth) {
        this.gameLogic = new GameLogic(pieces, timePerTurn, komi);
        this.vsAI = vsAI;
        this.aiIsBlack = aiIsBlack;
        this.ai = vsAI ? new MinimaxAI(aiDepth, aiIsBlack) : null;

        if (vsAI && aiIsBlack && gameLogic.isBlackTurn()) {
            makeAIMove();
        }

        // Kiểm tra trạng thái ban đầu
        checkGameEnd();
    }

    public boolean handlePlayerMove(int x, int y) {
        if (gameLogic.isGameEnded()) return false;

        boolean isPlayerTurn = !vsAI || (gameLogic.isBlackTurn() != aiIsBlack);
        if (!isPlayerTurn) return false;

        boolean placed = gameLogic.placePiece(x, y);

        if (placed && vsAI && !gameLogic.isGameEnded()) {
            makeAIMove();
        }

        return placed;
    }

    public void handlePlayerPass() {
        if (gameLogic.isGameEnded()) {
            System.out.println("Game already ended, cannot pass.");
            return;
        }

        boolean isPlayerTurn = !vsAI || (gameLogic.isBlackTurn() != aiIsBlack);
        if (!isPlayerTurn) return;

        gameLogic.pass();

        if (vsAI && !gameLogic.isGameEnded()) {
            makeAIMove();
        }

        checkGameEnd();
    }

    private void checkGameEnd() {
        if (gameLogic.isGameEnded()) {
            String winnerMessage = gameLogic.getWinnerMessage();
            System.out.println("Game ended. Winner message: " + winnerMessage);
            if (winnerMessage != null && GameFrame.getInstance() != null) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(GameFrame.getInstance(), winnerMessage, "Game Over", JOptionPane.INFORMATION_MESSAGE);
                });
            }
        }
    }

    public void updateTime() {
        GameLogic gameLogic = getGameLogic();
        if (vsAI && (gameLogic.isBlackTurn() == aiIsBlack)) {
            gameLogic.updateTimeForAI(aiIsBlack);
        } else {
            gameLogic.updateTime();
        }
        checkGameEnd();
    }

    public void makeAIMove() {
        if (gameLogic.isGameEnded()) return;
        final boolean aiColor = ai.isAIPlayingBlack();
        new Thread(() -> {
            int[] bestMove = ai.getBestMove(gameLogic);
            if (bestMove != null && !gameLogic.isGameEnded()) {
                if (bestMove[0] == -1 && bestMove[1] == -1) {
                    gameLogic.pass();
                } else {
                    gameLogic.placePiece(bestMove[0], bestMove[1]);
                }
                checkGameEnd();
                SwingUtilities.invokeLater(() -> {
                    if (GameFrame.getInstance() != null) {
                        GameFrame.getInstance().repaint();
                    }
                });
            }
        }).start();
    }

    public GameLogic getGameLogic() {
        return gameLogic;
    }
}