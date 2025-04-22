package Graphics;

import Model.Settings;

import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame {
    private static GameFrame _instance;
    private CardLayout _cardLayout;
    private JPanel _cardPanel;
    private MenuPanel _mp;
    private SettingPanel _gsp;
    private GamePanel _gp;
    private SplashScreen _splash;

    public GameFrame() {
        _instance = this;
        initComponent();
        initUI();
        this.add(_cardPanel);
    }

    private void initComponent() {
        _cardLayout = new CardLayout();
        _cardPanel = new JPanel(_cardLayout);

        _splash = new SplashScreen();
        _mp = new MenuPanel();
        _gsp = new SettingPanel();
        _gp = new GamePanel();

        _cardPanel.add(_splash, "splash");
        _cardPanel.add(_mp, "mp");
        _cardPanel.add(_gsp, "gsp");
        _cardPanel.add(_gp, "gp");

        this.add(_cardPanel);
    }

    private void initUI() {
        this.setTitle("Cờ Vây");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);

        // Đặt kích thước cố định thay vì sử dụng pack()
        this.setSize(new Dimension(Settings.Config.GAME_WIDTH, Settings.Config.GAME_HEIGHT));
        this.setLayout(new BorderLayout());

        // Không gọi pack() để tránh tự động điều chỉnh kích thước
        this.setLocationRelativeTo(null);

        // Gỡ lỗi: In kích thước thực tế của frame
        System.out.println("GameFrame size set to: " + Settings.Config.GAME_WIDTH + "x" + Settings.Config.GAME_HEIGHT);
    }

    public static GameFrame getInstance() {
        return _instance;
    }

    public void start() {
        showSplashScreen();
        this.setVisible(true);
    }

    public void showSplashScreen() {
        Timer timer = new Timer(3000, e -> {
            showMainMenu();
            ((Timer) e.getSource()).stop();
        });
        timer.setRepeats(false);
        timer.start();
    }

    public void showMainMenu() {
        _cardLayout.show(_cardPanel, "mp");
        _mp.requestFocusInWindow();
    }

    public void playGame() {
        System.out.println("GamePanel displayed");
        _cardLayout.show(_cardPanel, "gp");
        _gp.startGame();
        _gp.requestFocusInWindow();
    }

    public void showGameSettings() {
        _cardLayout.show(_cardPanel, "gsp");
    }
}