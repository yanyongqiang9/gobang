package com;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JFrame;
import javax.swing.JOptionPane;


public class Windows extends JFrame implements MouseListener {
    public Core core;
    private final Robot robot;

    private final int human = 1;
    private final int ai = 2;

    private int lastMoveX = -1;
    private int lastMoveY = -1;
    private boolean samePosition = false;
    // 棋盘大小
    private final int size;
    // 方格边长
    private final int gridSize = 30;
    private final int xStart = 30;
    private final int yStart = 60;
    private int xEnd = 0;
    private int yEnd = 0;

    private int menuLeft = 0;
    private int menuTop = 0;

    private int currentDifficulty = 3;
    private boolean isDifficultyDropdownOpen = false;
    private final String[] difficulties = {"青铜", "白银", "黄金", "砖石", "王者"};


    public Windows(String title, int size) {
        super(title);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setResizable(false);
        this.addMouseListener(this);
        this.size = size;//棋盘大小
        this.core = new Core(size, size);
        this.robot = new Robot(core, human, ai, currentDifficulty);
        this.xEnd = xStart + gridSize * (this.size - 1);
        this.yEnd = yStart + gridSize * (this.size - 1);
        this.menuLeft = xEnd + 50;
        this.menuTop = yStart;
        // 窗口大小
        this.setSize(menuLeft + 110, yEnd + 50);
        // 窗口显示在屏幕的位置
        this.setLocation(800, 300);

    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        // 横
        for (int i = 0; i < this.size; i++) {
            g.drawLine(xStart, yStart + i * gridSize, xEnd, yStart + i * gridSize);
            // 左侧行坐标
            g.drawString(String.valueOf(i), xStart - 20, yStart + i * gridSize + 4);
        }
        // 竖线
        for (int i = 0; i < this.size; i++) {
            g.drawLine(xStart + i * gridSize, yStart, xStart + i * gridSize, yEnd);
            // 上方列坐标
            g.drawString(String.valueOf(i), xStart + i * gridSize - 4, yStart - 10);
        }

        int[][] board = core.getCore();
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                // 计算圆心位置
                if (board[i][j] == 1) {
                    g.fillOval(20 + i * gridSize, 50 + j * gridSize, 20, 20);
                }
                if (board[i][j] == 2) {
                    g.drawOval(20 + i * gridSize, 50 + j * gridSize, 20, 20);
                }
            }
        }

        // 绘制最后落子位置的光标提示
        if (lastMoveX != -1 && lastMoveY != -1) {
            // 保存原来的颜色
            Color originalColor = g.getColor();
            // 设置高亮颜色（红色）
            g.setColor(Color.RED);
            // 计算交叉点中心位置
            int centerX = xStart + lastMoveX * gridSize;
            int centerY = yStart + lastMoveY * gridSize;
            g.setColor(new Color(255, 0, 0, 128)); // 红色
            int ringSize = 15;
            g.drawOval(centerX - ringSize, centerY - ringSize, ringSize * 2, ringSize * 2);
            // 恢复原来的颜色
            g.setColor(originalColor);
        }
        g.drawRect(menuLeft, menuTop, 50, 30);
        g.drawString("悔棋", menuLeft + 10, menuTop + 20);
        g.drawRect(menuLeft, menuTop + 60, 50, 30);
        g.drawString("开始", menuLeft + 10, menuTop + 60 + 20);
        g.drawRect(menuLeft, menuTop + 120, 50, 30);
        g.drawString("设置", menuLeft + 10, menuTop + 120 + 20);

        int difficultyLeft = menuLeft;
        int difficultyTop = menuTop + 180;
        int difficultyWidth = 75;
        int difficultyHeight = 30;

        // 下拉框背景
        g.drawRect(difficultyLeft, difficultyTop, difficultyWidth, difficultyHeight);
        g.drawString("难度: " + difficulties[currentDifficulty], difficultyLeft + 10, difficultyTop + 20);

        // 如果下拉框展开，显示选项
        if (isDifficultyDropdownOpen) {
            for (int i = 0; i < difficulties.length; i++) {
                int optionTop = difficultyTop + difficultyHeight * (i + 1);
                g.drawRect(difficultyLeft, optionTop, difficultyWidth, difficultyHeight);
                g.drawString(difficulties[i], difficultyLeft + 10, optionTop + 20);
            }
        }

        g.drawString("玩家黑子，AI白子！", menuLeft, yEnd - 20);
    }


    @Override
    public void mousePressed(MouseEvent e) {
        // 下棋
        if (e.getX() >= xStart && e.getX() <= xEnd && e.getY() >= yStart && e.getY() <= yEnd) {
            //System.out.println("robot: x is: " + getRealX(e.getX()) + ", y is: " + getRealY(e.getY()));
            int realX = getRealX(e.getX());
            int realY = getRealY(e.getY());
            samePosition = false;
            chess(realX, realY, this.human);
            return;
        }
        // 悔棋
        if (e.getX() > menuLeft && e.getX() < menuLeft + 50 && e.getY() > menuTop && e.getY() < menuTop + 30) {
            core.retChess();
            this.repaint();
            return;
        }
        // 开始
        if (e.getX() > menuLeft && e.getX() < menuLeft + 50 && e.getY() > menuTop + 60 && e.getY() < menuTop + 60 + 30) {
            restart();
            this.repaint();
            return;
        }
        //设置
        if (e.getX() > menuLeft && e.getX() < menuLeft + 50 && e.getY() > menuTop + 120 && e.getY() < menuTop + 120 + 30) {
            Object[] options = {"白先", "黑先"};
            int n = JOptionPane.showOptionDialog(null, "白先还是黑先？", "游戏设置",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            if (n == 0) {
                restart();
                robot();
            }
            if (n == 1) restart();
            this.repaint();
            return;
        }
        // 难度
        int difficultyLeft = menuLeft;
        int difficultyTop = menuTop + 180;
        int difficultyWidth = 75;
        int difficultyHeight = 30;

        // 检查是否点击了难度下拉框
        if (e.getX() >= difficultyLeft && e.getX() <= difficultyLeft + difficultyWidth &&
                e.getY() >= difficultyTop && e.getY() <= difficultyTop + difficultyHeight) {
            isDifficultyDropdownOpen = !isDifficultyDropdownOpen;
            //restart();
            repaint();
        } else if (isDifficultyDropdownOpen) {
            for (int i = 0; i < difficulties.length; i++) {
                int optionTop = difficultyTop + difficultyHeight * (i + 1);
                if (e.getX() >= difficultyLeft && e.getX() <= difficultyLeft + difficultyWidth &&
                        e.getY() >= optionTop && e.getY() <= optionTop + difficultyHeight) {
                    currentDifficulty = i;
                    this.robot.setDepth(currentDifficulty);
                    isDifficultyDropdownOpen = false;
                    restart();
                    repaint();
                    break;
                }
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (samePosition) return;
        if (e.getX() >= xStart && e.getX() <= xEnd && e.getY() >= yStart && e.getY() <= yEnd) {
            robot();
        }
    }


    private void chess(int x, int y, int player) {
        lastMoveX = x;
        lastMoveY = y;

        int a = core.chessIt(x, y, player);
        this.repaint();
        if (a == human) {
            JOptionPane.showMessageDialog(null, "你赢了！！！", "恭喜", JOptionPane.PLAIN_MESSAGE);
        }
        if (a == ai) {
            JOptionPane.showMessageDialog(null, "你输了...", "很遗憾", JOptionPane.PLAIN_MESSAGE);
        }
        if (a == -1) {
            samePosition = true;
        }
    }

    public void robot() {
        int[] move = robot.bestAIMove();
        //System.out.println("robot: x is: " + move[0] + ", y is: " + move[1]);
        chess(move[0], move[1], robot.getAi());
    }

    private int getRealX(int x) {
        x -= xStart;
        if (x % gridSize <= gridSize / 2)
            return x / gridSize;
        else
            return x / gridSize + 1;
    }

    private int getRealY(int y) {
        y -= yStart;
        if (y % gridSize <= gridSize / 2)
            return y / gridSize;
        else
            return y / gridSize + 1;
    }

    private void restart() {
        samePosition = false;
        core.restart();
    }

    @Override
    public void mouseClicked(MouseEvent arg0) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

}