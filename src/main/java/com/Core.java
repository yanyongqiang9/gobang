package com;

import java.util.Arrays;
import java.util.Stack;

/**
 * 棋盘数据结构
 */
public class Core {
    //棋盘大小
    private final int[][] core;
    private final int xLength;
    private final int yLength;
    //栈
    Stack<Chess> stack;

    //记录下棋的类
    static class Chess {
        int x;
        int y;

        public Chess(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    //构造方法
    public Core(int xLength, int yLength) {
        stack = new Stack<>();
        core = new int[xLength][yLength];
        this.xLength = xLength;
        this.yLength = yLength;
    }


    //判断输赢
    public int checkVictory(int x, int y, int var) {

        int trans = 0;
        for (int i = x - 4; i < x + 5; i++) {
            if (i < 0 || i >= this.xLength) continue;
            if (core[i][y] == var) {
                trans++;
            } else {
                trans = 0;
            }
            if (trans == 5) return var;
        }

        int longitudinal = 0;
        for (int i = y - 4; i < y + 5; i++) {
            if (i < 0 || i >= this.yLength) continue;
            if (core[x][i] == var) {
                longitudinal++;
            } else {
                longitudinal = 0;
            }
            if (longitudinal == 5) return var;
        }

        int leftUPToDown = 0;
        for (int i = x - 4, j = y - 4; i < x + 5 && j < y + 5; i++, j++) {
            if (i < 0 || i >= this.xLength || j < 0 || j >= this.yLength) continue;
            if (core[i][j] == var) {
                leftUPToDown++;
            } else {
                leftUPToDown = 0;
            }
            if (leftUPToDown == 5) return var;
        }

        int rightUpToDown = 0;
        for (int i = x + 4, j = y - 4; i > x - 5 && j < y + 5; i--, j++) {
            if (i < 0 || i >= this.xLength || j < 0 || j >= this.yLength) continue;
            if (core[i][j] == var) {
                rightUpToDown++;
            } else {
                rightUpToDown = 0;
            }
            if (rightUpToDown == 5) return var;
        }
        return 0;
    }

    /**
     * 在该位置下棋
     */
    public int chessIt(int x, int y, int var) {
        if (core[x][y] == 0) {
            core[x][y] = var;
            Chess chess = new Chess(x, y);
            stack.push(chess);
            return checkVictory(x, y, var);
        } else return -1;
    }


    //悔棋
    public void retChess() {
        if (stack.isEmpty()) return;
        for (int i = 0; i < 2 && !stack.isEmpty(); i++) {
            Chess chess = stack.pop();
            core[chess.x][chess.y] = 0;
        }
    }

    //获得棋盘状态
    public int[][] getCore() {
        return this.core;
    }

    //重新开始
    public void restart() {
        for (int i = 0; i < this.xLength; i++) {
            Arrays.fill(core[i], 0);
        }
        this.stack.clear();
    }
}