package Graphics;

import Model.Default;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SettingPanel extends JPanel {
    private JButton btnPlayerVsPlayer, btnPlayerVsAI, btnBack;
    private JPanel mainPanel, pvpPanel, aiPanel;
    private CardLayout cardLayout;

    public SettingPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 220)); // Nền màu nhạt giống gỗ

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(getBackground()); // Đồng bộ màu nền

        // Tạo các panel chính
        mainPanel.add(createMainMenu(), "main");
        mainPanel.add(createPVPSettings(), "pvp");
        mainPanel.add(createAISettings(), "ai");

        add(mainPanel, BorderLayout.CENTER);
        cardLayout.show(mainPanel, "main");
    }

    // Tạo nút với hiệu ứng hover
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(70, 130, 180)); // Màu xanh dương đậm
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Padding

        // Hiệu ứng hover
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(100, 149, 237)); // Xanh sáng hơn
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(70, 130, 180));
            }
        });

        return button;
    }

    /**
     * Tạo menu chính của SettingPanel
     */
    private JPanel createMainMenu() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(getBackground());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20); // Tăng khoảng cách
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("CÀI ĐẶT TRÒ CHƠI", SwingConstants.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 28));
        title.setForeground(new Color(139, 69, 19)); // Màu nâu gỗ
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(title, gbc);

        btnPlayerVsPlayer = createStyledButton("Chơi với Người");
        gbc.gridy = 1;
        btnPlayerVsPlayer.addActionListener(e -> cardLayout.show(mainPanel, "pvp"));
        panel.add(btnPlayerVsPlayer, gbc);

        btnPlayerVsAI = createStyledButton("Chơi với Máy");
        gbc.gridy = 2;
        btnPlayerVsAI.addActionListener(e -> cardLayout.show(mainPanel, "ai"));
        panel.add(btnPlayerVsAI, gbc);

        btnBack = createStyledButton("Quay lại");
        gbc.gridy = 3;
        btnBack.addActionListener(e -> GameFrame.getInstance().showMainMenu());
        panel.add(btnBack, gbc);

        return panel;
    }

    /**
     * Cài đặt khi chơi với người
     */
    private JPanel createPVPSettings() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(getBackground());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Cài Đặt Chơi Với Người", SwingConstants.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 24));
        title.setForeground(new Color(139, 69, 19));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(title, gbc);

        // Label và field
        Font labelFont = new Font("Arial", Font.PLAIN, 16);
        JLabel lblTime = new JLabel("Thời gian mỗi lượt (giây):");
        lblTime.setFont(labelFont);
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(lblTime, gbc);

        JSpinner spnTime = new JSpinner(new SpinnerNumberModel(30, 10, 300, 10));
        spnTime.setFont(labelFont);
        gbc.gridx = 1;
        panel.add(spnTime, gbc);

        JLabel lblBoardSize = new JLabel("Kích thước bàn:");
        lblBoardSize.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(lblBoardSize, gbc);

        JComboBox<String> cbBoardSize = new JComboBox<>(new String[]{"4x4","9x9", "13x13", "19x19"});
        cbBoardSize.setFont(labelFont);
        gbc.gridx = 1;
        panel.add(cbBoardSize, gbc);

        JLabel lblKomi = new JLabel("Komi:");
        lblKomi.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(lblKomi, gbc);

        JTextField txtKomi = new JTextField("6.5", 5);
        txtKomi.setFont(labelFont);
        gbc.gridx = 1;
        panel.add(txtKomi, gbc);

        JLabel lblScoring = new JLabel("Cách tính điểm:");
        lblScoring.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(lblScoring, gbc);

        JComboBox<String> cbScoring = new JComboBox<>(new String[]{"Trung Quốc", "Nhật Bản"});
        cbScoring.setFont(labelFont);
        gbc.gridx = 1;
        panel.add(cbScoring, gbc);

        // Nút xác nhận
        JButton btnConfirm = createStyledButton("Xác nhận");
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        btnConfirm.addActionListener(e -> {
            int boardSize = Integer.parseInt(cbBoardSize.getSelectedItem().toString().split("x")[0]);
            int timePerTurn = (int) spnTime.getValue();
            double komi;
            try {
                komi = Double.parseDouble(txtKomi.getText());
            } catch (NumberFormatException ex) {
                komi = 6.5;
                JOptionPane.showMessageDialog(this, "Komi không hợp lệ, dùng giá trị mặc định 6.5", "Lỗi", JOptionPane.WARNING_MESSAGE);
            }
            String scoring = cbScoring.getSelectedItem().toString();

            Default.updateSettings(boardSize, timePerTurn, komi, scoring);
            cardLayout.show(mainPanel, "main");
        });
        panel.add(btnConfirm, gbc);

        // Nút quay lại
        JButton btnBack = createStyledButton("Quay lại");
        gbc.gridy = 6;
        btnBack.addActionListener(e -> cardLayout.show(mainPanel, "main"));
        panel.add(btnBack, gbc);

        return panel;
    }

    /**
     * Cài đặt khi chơi với máy
     */
    private JPanel createAISettings() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(getBackground());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Cài Đặt Chơi Với Máy", SwingConstants.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 24));
        title.setForeground(new Color(139, 69, 19));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(title, gbc);

        // Label và field
        Font labelFont = new Font("Arial", Font.PLAIN, 16);
        JLabel lblSide = new JLabel("Bạn chơi bên nào:");
        lblSide.setFont(labelFont);
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(lblSide, gbc);

        JComboBox<String> cbSide = new JComboBox<>(new String[]{"Đen", "Trắng"});
        cbSide.setFont(labelFont);
        gbc.gridx = 1;
        panel.add(cbSide, gbc);

        JLabel lblDifficulty = new JLabel("Độ khó:");
        lblDifficulty.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(lblDifficulty, gbc);

        JComboBox<String> cbDifficulty = new JComboBox<>(new String[]{"Dễ", "Trung bình", "Khó"});
        cbDifficulty.setFont(labelFont);
        gbc.gridx = 1;
        panel.add(cbDifficulty, gbc);

        JLabel lblTime = new JLabel("Thời gian mỗi lượt (giây):");
        lblTime.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(lblTime, gbc);

        JSpinner spnTime = new JSpinner(new SpinnerNumberModel(30, 10, 300, 10));
        spnTime.setFont(labelFont);
        gbc.gridx = 1;
        panel.add(spnTime, gbc);

        JLabel lblBoardSize = new JLabel("Kích thước bàn:");
        lblBoardSize.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(lblBoardSize, gbc);

        JComboBox<String> cbBoardSize = new JComboBox<>(new String[]{"9x9", "13x13", "19x19"});
        cbBoardSize.setFont(labelFont);
        gbc.gridx = 1;
        panel.add(cbBoardSize, gbc);

        JLabel lblKomi = new JLabel("Komi:");
        lblKomi.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(lblKomi, gbc);

        JTextField txtKomi = new JTextField("6.5", 5);
        txtKomi.setFont(labelFont);
        gbc.gridx = 1;
        panel.add(txtKomi, gbc);

        JLabel lblScoring = new JLabel("Cách tính điểm:");
        lblScoring.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = 6;
        panel.add(lblScoring, gbc);

        JComboBox<String> cbScoring = new JComboBox<>(new String[]{"Trung Quốc", "Nhật Bản"});
        cbScoring.setFont(labelFont);
        gbc.gridx = 1;
        panel.add(cbScoring, gbc);

        // Nút xác nhận
        JButton btnConfirm = createStyledButton("Xác nhận");
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        btnConfirm.addActionListener(e -> {
            String playerSide = cbSide.getSelectedItem().toString();
            String difficulty = cbDifficulty.getSelectedItem().toString();
            int timePerTurn = (int) spnTime.getValue();
            int boardSize = Integer.parseInt(cbBoardSize.getSelectedItem().toString().split("x")[0]);
            double komi;
            try {
                komi = Double.parseDouble(txtKomi.getText());
            } catch (NumberFormatException ex) {
                komi = 6.5;
                JOptionPane.showMessageDialog(this, "Komi không hợp lệ, dùng giá trị mặc định 6.5", "Lỗi", JOptionPane.WARNING_MESSAGE);
            }
            String scoring = cbScoring.getSelectedItem().toString();

            Default.updateSettings(boardSize, timePerTurn, komi, scoring);
            Default.updateAISettings(difficulty, playerSide);
            cardLayout.show(mainPanel, "main");
        });
        panel.add(btnConfirm, gbc);

        // Nút quay lại
        JButton btnBack = createStyledButton("Quay lại");
        gbc.gridy = 8;
        btnBack.addActionListener(e -> cardLayout.show(mainPanel, "main"));
        panel.add(btnBack, gbc);

        return panel;
    }
}