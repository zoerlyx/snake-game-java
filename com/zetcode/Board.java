package com.zetcode;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Board extends JPanel implements ActionListener {

    // Board ukuran kompak 320x320
    private final int B_WIDTH = 320;
    private final int B_HEIGHT = 320;
    
    // Ukuran grid/ular diperkecil ke 12px agar tidak kegedean
    private final int DOT_SIZE = 12;
    private final int ALL_DOTS = (B_WIDTH * B_HEIGHT) / (DOT_SIZE * DOT_SIZE);
    private final int RAND_POS = (B_WIDTH / DOT_SIZE) - 1; // 25 posisi grid
    private final int DELAY = 110; // Kecepatan gerak ular

    private final int x[] = new int[ALL_DOTS];
    private final int y[] = new int[ALL_DOTS];

    private int dots;
    private int apple_x;
    private int apple_y;
    private int score = 0;
    private int highScore = 0;

    private boolean leftDirection = false;
    private boolean rightDirection = true;
    private boolean upDirection = false;
    private boolean downDirection = false;
    private boolean inGame = true;
    private boolean isPaused = false;

    private Timer timer;
    private Image ball;
    private Image apple;
    private Image head;

    public Board() {
        initBoard();
    }

    private void initBoard() {
        addKeyListener(new TAdapter());
        setBackground(new Color(15, 23, 42)); // Dark navy background
        setFocusable(true);
        setPreferredSize(new Dimension(B_WIDTH, B_HEIGHT));

        loadImages();
        initGame();
    }

    private void loadImages() {
        // Skala gambar ke 12px tajam tanpa pecah
        ImageIcon iid = new ImageIcon(getClass().getResource("dot.png"));
        ball = iid.getImage().getScaledInstance(DOT_SIZE, DOT_SIZE, Image.SCALE_SMOOTH);

        ImageIcon iia = new ImageIcon(getClass().getResource("apple.png"));
        apple = iia.getImage().getScaledInstance(DOT_SIZE, DOT_SIZE, Image.SCALE_SMOOTH);

        ImageIcon iih = new ImageIcon(getClass().getResource("head.png"));
        head = iih.getImage().getScaledInstance(DOT_SIZE, DOT_SIZE, Image.SCALE_SMOOTH);
    }

    private void initGame() {
        dots = 3;
        score = 0;
        inGame = true;
        isPaused = false;

        leftDirection = false;
        rightDirection = true;
        upDirection = false;
        downDirection = false;

        // Posisi awal di tengah
        int startX = (B_WIDTH / 2) / DOT_SIZE * DOT_SIZE;
        int startY = (B_HEIGHT / 2) / DOT_SIZE * DOT_SIZE;

        for (int z = 0; z < dots; z++) {
            x[z] = startX - z * DOT_SIZE;
            y[z] = startY;
        }

        locateApple();

        if (timer != null) {
            timer.stop();
        }
        timer = new Timer(DELAY, this);
        timer.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawGrid(g2d);

        if (inGame) {
            drawGameObjects(g2d);
            drawScore(g2d);

            if (isPaused) {
                drawPauseOverlay(g2d);
            }
        } else {
            drawGameOverOverlay(g2d);
        }

        java.awt.Toolkit.getDefaultToolkit().sync();
    }

    private void drawGrid(Graphics2D g2d) {
        g2d.setColor(new Color(30, 41, 59, 70));
        for (int i = 0; i < B_WIDTH; i += DOT_SIZE) {
            g2d.drawLine(i, 0, i, B_HEIGHT);
        }
        for (int j = 0; j < B_HEIGHT; j += DOT_SIZE) {
            g2d.drawLine(0, j, B_WIDTH, j);
        }
    }

    private void drawGameObjects(Graphics2D g2d) {
        // Gambar Apel
        g2d.drawImage(apple, apple_x, apple_y, this);

        // Gambar Ular (Kepala & Badan)
        for (int z = 0; z < dots; z++) {
            if (z == 0) {
                g2d.drawImage(head, x[z], y[z], this);
            } else {
                g2d.drawImage(ball, x[z], y[z], this);
            }
        }
    }

    private void drawScore(Graphics2D g2d) {
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 12));
        g2d.setColor(Color.WHITE);
        g2d.drawString("Score: " + score, 10, 18);

        g2d.setColor(new Color(250, 204, 21));
        String hsText = "High: " + highScore;
        FontMetrics fm = getFontMetrics(g2d.getFont());
        g2d.drawString(hsText, B_WIDTH - fm.stringWidth(hsText) - 10, 18);
    }

    private void drawPauseOverlay(Graphics2D g2d) {
        g2d.setColor(new Color(0, 0, 0, 160));
        g2d.fillRect(0, 0, B_WIDTH, B_HEIGHT);

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 20));
        String msg = "PAUSED";
        FontMetrics metrics = getFontMetrics(g2d.getFont());
        g2d.drawString(msg, (B_WIDTH - metrics.stringWidth(msg)) / 2, B_HEIGHT / 2 - 10);

        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        String sub = "Tekan [P] untuk melanjutkan";
        FontMetrics subMetrics = getFontMetrics(g2d.getFont());
        g2d.drawString(sub, (B_WIDTH - subMetrics.stringWidth(sub)) / 2, B_HEIGHT / 2 + 18);
    }

    private void drawGameOverOverlay(Graphics2D g2d) {
        g2d.setColor(new Color(0, 0, 0, 180));
        g2d.fillRect(0, 0, B_WIDTH, B_HEIGHT);

        g2d.setFont(new Font("Segoe UI", Font.BOLD, 22));
        g2d.setColor(new Color(248, 113, 113));
        String msg = "GAME OVER";
        FontMetrics metrics = getFontMetrics(g2d.getFont());
        g2d.drawString(msg, (B_WIDTH - metrics.stringWidth(msg)) / 2, B_HEIGHT / 2 - 25);

        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        g2d.setColor(Color.WHITE);
        String scoreMsg = "Score: " + score + "  |  High: " + highScore;
        FontMetrics scoreMetrics = getFontMetrics(g2d.getFont());
        g2d.drawString(scoreMsg, (B_WIDTH - scoreMetrics.stringWidth(scoreMsg)) / 2, B_HEIGHT / 2 + 10);

        g2d.setFont(new Font("Segoe UI", Font.BOLD, 12));
        g2d.setColor(new Color(56, 189, 248));
        String restartMsg = "Tekan [R] untuk Restart";
        FontMetrics restartMetrics = getFontMetrics(g2d.getFont());
        g2d.drawString(restartMsg, (B_WIDTH - restartMetrics.stringWidth(restartMsg)) / 2, B_HEIGHT / 2 + 35);
    }

    private void checkApple() {
        if ((x[0] == apple_x) && (y[0] == apple_y)) {
            dots++;
            score += 10;
            if (score > highScore) {
                highScore = score;
            }
            locateApple();
        }
    }

    private void move() {
        for (int z = dots; z > 0; z--) {
            x[z] = x[(z - 1)];
            y[z] = y[(z - 1)];
        }

        if (leftDirection) {
            x[0] -= DOT_SIZE;
        }

        if (rightDirection) {
            x[0] += DOT_SIZE;
        }

        if (upDirection) {
            y[0] -= DOT_SIZE;
        }

        if (downDirection) {
            y[0] += DOT_SIZE;
        }
    }

    private void checkCollision() {
        for (int z = dots; z > 0; z--) {
            if ((z > 4) && (x[0] == x[z]) && (y[0] == y[z])) {
                inGame = false;
            }
        }

        if (y[0] >= B_HEIGHT || y[0] < 0 || x[0] >= B_WIDTH || x[0] < 0) {
            inGame = false;
        }

        if (!inGame) {
            timer.stop();
        }
    }

    private void locateApple() {
        int r = (int) (Math.random() * RAND_POS);
        apple_x = (r * DOT_SIZE);

        r = (int) (Math.random() * RAND_POS);
        apple_y = (r * DOT_SIZE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (inGame && !isPaused) {
            checkApple();
            checkCollision();
            move();
        }
        repaint();
    }

    private class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();

            if (key == KeyEvent.VK_R && !inGame) {
                initGame();
                return;
            }

            if (key == KeyEvent.VK_P && inGame) {
                isPaused = !isPaused;
                return;
            }

            if (!isPaused) {
                if ((key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) && (!rightDirection)) {
                    leftDirection = true;
                    upDirection = false;
                    downDirection = false;
                }

                if ((key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) && (!leftDirection)) {
                    rightDirection = true;
                    upDirection = false;
                    downDirection = false;
                }

                if ((key == KeyEvent.VK_UP || key == KeyEvent.VK_W) && (!downDirection)) {
                    upDirection = true;
                    rightDirection = false;
                    leftDirection = false;
                }

                if ((key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) && (!upDirection)) {
                    downDirection = true;
                    rightDirection = false;
                    leftDirection = false;
                }
            }
        }
    }
}