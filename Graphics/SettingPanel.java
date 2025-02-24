package Graphics;

import Utils.Sound;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SettingPanel extends JPanel {
    private JSlider musicSlider;
    private JSlider sfxSlider;
    private JCheckBox fullscreenCheckBox;
    private JButton btnBack;

    public SettingPanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Tiêu đề "Cài Đặt"
        JLabel title = new JLabel("CÀI ĐẶT", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(title, gbc);

        // Thanh điều chỉnh âm lượng nhạc nền
        JLabel lblMusic = new JLabel("Âm lượng nhạc:");
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        add(lblMusic, gbc);

        musicSlider = new JSlider(0, 100, 50);
        musicSlider.setMajorTickSpacing(25);
        musicSlider.setPaintTicks(true);
        musicSlider.setPaintLabels(true);
        gbc.gridx = 1;
        add(musicSlider, gbc);

        // Thanh điều chỉnh âm lượng hiệu ứng âm thanh
        JLabel lblSFX = new JLabel("Âm lượng hiệu ứng:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(lblSFX, gbc);

        sfxSlider = new JSlider(0, 100, 50);
        sfxSlider.setMajorTickSpacing(25);
        sfxSlider.setPaintTicks(true);
        sfxSlider.setPaintLabels(true);
        gbc.gridx = 1;
        add(sfxSlider, gbc);

        // Checkbox bật/tắt fullscreen
        fullscreenCheckBox = new JCheckBox("Chế độ toàn màn hình");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        add(fullscreenCheckBox, gbc);

        // Nút quay lại menu chính
        btnBack = new JButton("Quay lại");
        gbc.gridy = 4;
        btnBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GameFrame.getInstance().showMainMenu(); // Quay lại menu chính
            }
        });
        add(btnBack, gbc);
    }

    public int getMusicVolume() {
        return musicSlider.getValue();
    }

    public int getSFXVolume() {
        return sfxSlider.getValue();
    }

    public boolean isFullscreen() {
        return fullscreenCheckBox.isSelected();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(new Color(50, 50, 50)); // Màu nền xám đậm
        g.fillRect(0, 0, getWidth(), getHeight());
    }
}
