package Graphics;


import javax.swing.*;
import java.awt.*;

public class SplashScreen extends JPanel {
    private final Image splashImage;

    public SplashScreen() {
        splashImage = new ImageIcon(getClass().getResource("/Resources/Go.png")).getImage();    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(splashImage, 0, 0, getWidth(), getHeight(), this);
    }
}

