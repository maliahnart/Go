package Graphics;

import Model.Player;
import Model.Settings;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel implements Runnable {
    private Thread _gameThread;
    private boolean _running;

    private Player _player;
    private Board _board;

    public GamePanel() {
        this.setPreferredSize(
                new Dimension(Settings.Config.GAME_WIDTH, Settings.Config.GAME_HEIGHT));
        this.setDoubleBuffered(true);
        this.setFocusable(true);

        _board = new Board(); // Khởi tạo bàn cờ
    }

    public void startGame() {
        if (_gameThread == null) {
            _running = true;
            _gameThread = new Thread(this);
            _gameThread.start();
        }
    }

    @Override
    public void run() {
        double drawInterval = 1e9 / Settings.Config.FPS;
        long lastTime = System.nanoTime();
        long currentTime;
        double delta = 0;

        while (_running) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
//                updateGame();
                repaint();
                delta--;
            }
        }
    }

//    private void updateGame() {
//        if (_player != null) {
//            _player.Update();
//        }
//    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        _board.draw(g); // Vẽ bàn cờ
    }

    public void stopGame() {
        _running = false;
        try {
            if (_gameThread != null) {
                _gameThread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
