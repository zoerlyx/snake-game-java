package com.zetcode;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.KeyEvent;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

public class Snake extends JFrame {

    private Board board;

    public Snake() {
        initUI();
    }

    private void initUI() {
        // Terapkan Look and Feel sistem agar dialog & menu bergaya modern
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        board = new Board();
        
        // Atur Layout Utama
        setLayout(new BorderLayout());
        add(board, BorderLayout.CENTER);

        // Tambahkan Menu Bar dan Status Bar
        setJMenuBar(createCustomMenuBar());
        add(createStatusBar(), BorderLayout.SOUTH);

        // Pasang Icon Game di Window Titlebar
        setWindowIcon();

        setResizable(false);
        pack();

        setTitle("Snake Arcade Edition");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void setWindowIcon() {
        try {
            java.net.URL imgURL = getClass().getResource("head.png");
            if (imgURL != null) {
                Image icon = new ImageIcon(imgURL).getImage();
                setIconImage(icon);
            }
        } catch (Exception e) {
            // Abaikan jika aset ikon gagal dimuat
        }
    }

    private JMenuBar createCustomMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // Menu Game
        JMenu gameMenu = new JMenu("Game");
        gameMenu.setMnemonic(KeyEvent.VK_G);

        JMenuItem restartItem = new JMenuItem("Restart (R)");
        restartItem.addActionListener(e -> {
            // Mengirimkan event tombol R ke board
            board.dispatchEvent(new KeyEvent(board, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_R, 'R'));
        });

        JMenuItem pauseItem = new JMenuItem("Pause / Resume (P)");
        pauseItem.addActionListener(e -> {
            board.dispatchEvent(new KeyEvent(board, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_P, 'P'));
        });

        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));

        gameMenu.add(restartItem);
        gameMenu.add(pauseItem);
        gameMenu.addSeparator();
        gameMenu.add(exitItem);

        // Menu Help
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);

        JMenuItem controlsItem = new JMenuItem("Controls");
        controlsItem.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                    "Navigasi Ular:\n" +
                    " - Panah / WASD : Gerak Ular\n" +
                    " - P : Pause / Resume\n" +
                    " - R : Restart Game (saat Game Over)\n",
                    "Cara Bermain", JOptionPane.INFORMATION_MESSAGE);
        });

        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                    "Snake Game - Arcade Edition\n" +
                    "Refactored Java Swing Project\n" +
                    "Dibuat dengan Java & Swing Graphics",
                    "Tentang Game", JOptionPane.INFORMATION_MESSAGE);
        });

        helpMenu.add(controlsItem);
        helpMenu.add(aboutItem);

        menuBar.add(gameMenu);
        menuBar.add(helpMenu);

        return menuBar;
    }

    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(new Color(30, 41, 59)); // Matching dark theme
        statusBar.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));

        JLabel infoLabel = new JLabel("[WASD / Panah] Gerak  |  [P] Pause  |  [R] Restart");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        infoLabel.setForeground(new Color(148, 163, 184));
        infoLabel.setHorizontalAlignment(SwingConstants.CENTER);

        statusBar.add(infoLabel, BorderLayout.CENTER);
        return statusBar;
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            JFrame ex = new Snake();
            ex.setVisible(true);
        });
    }
}