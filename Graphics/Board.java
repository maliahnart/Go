package Graphics;
import Model.Settings;
import java.awt.*;

public class Board {
    private final int SIZE = 19; // 19x19 ô
    private final int CELL_SIZE = 40; // Kích thước mỗi ô
    private final int BOARD_SIZE = (SIZE - 1) * CELL_SIZE; // Tổng kích thước bàn cờ

    // Sử dụng biến từ Settings để căn giữa
    private final int OFFSET_X = (Settings.Config.GAME_WIDTH - BOARD_SIZE) / 2;
    private final int OFFSET_Y = (Settings.Config.GAME_HEIGHT - BOARD_SIZE) / 2;

    public void draw(Graphics g) {
        g.setColor(Color.BLACK);

        for (int i = 0; i < SIZE; i++) {
            // Vẽ đường ngang
            g.drawLine(OFFSET_X, OFFSET_Y + i * CELL_SIZE, OFFSET_X + BOARD_SIZE, OFFSET_Y + i * CELL_SIZE);
            // Vẽ đường dọc
            g.drawLine(OFFSET_X + i * CELL_SIZE, OFFSET_Y, OFFSET_X + i * CELL_SIZE, OFFSET_Y + BOARD_SIZE);
        }
    }
}
