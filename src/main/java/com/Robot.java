package com;

import java.util.ArrayList;
import java.util.List;

public class Robot {

    private int human;
    private int ai;
    private int depth;
    private final Core core;
    private final int EMPTY = 0;
    private final int FIVE = 100000;//五连
    private final int OPEN_FOUR = 10000;//活四
    private final int CLOSED_FOUR = 1000;//冲四
    private final int OPEN_THREE = 1000;//活三
    private final int CLOSED_THREE = 100;//眠三
    private final int OPEN_TWO = 100;//活二
    private final int OTHER = 10;//眠二
    private final int[][] directions = {{1, 0}, {0, 1}, {1, 1}, {1, -1}};


    public Robot(Core core, int human, int ai, int depth) {
        this.human = human;
        this.ai = ai;
        this.depth = depth;
        this.core = core;
    }


    public int[] bestAIMove() {
        int bestScore = Integer.MIN_VALUE;
        int[] bestMove = null;
        int[][] board = core.getCore();
        List<int[]> legalMoves = getLegalMoves(board);
        // 当前是MAX层
        for (int[] legalMove : legalMoves) {
            int x = legalMove[0], y = legalMove[1];
            board[x][y] = ai;
            int score = alphaBeta(board, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, false);
            board[x][y] = EMPTY;
            if (score > bestScore) {
                bestScore = score;
                bestMove = legalMove;
            }
        }
        return bestMove;
    }

    private int alphaBeta(int[][] board, int depth, int alpha, int beta, boolean maxPlayer) {
        int score = evaluateScore(board);
        if (Math.abs(score) >= FIVE) {
            return score;
        }

        if (depth == 0) {
            return score;
        }

        List<int[]> legalMoves = getLegalMoves(board);

        if (maxPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (int[] move : legalMoves) {
                int x = move[0], y = move[1];
                board[x][y] = ai;
                int evalScore = alphaBeta(board, depth - 1, alpha, beta, false);
                board[x][y] = EMPTY;

                if (evalScore > maxEval) {
                    maxEval = evalScore;
                }
                alpha = Math.max(alpha, evalScore);
                // beta 剪枝
                if (alpha >= beta) {
                    break;
                }
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (int[] move : legalMoves) {
                int x = move[0], y = move[1];
                board[x][y] = human;
                int evalScore = alphaBeta(board, depth - 1, alpha, beta, true);
                board[x][y] = EMPTY;

                if (evalScore < minEval) {
                    minEval = evalScore;
                }

                beta = Math.min(beta, evalScore);
                // alpha 剪枝
                if (alpha >= beta) {
                    break;
                }
            }
            return minEval;
        }
    }


    private int evaluateScore(int[][] board) {
        int aiScore = 0;
        int humanScore = 0;
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {

                if (board[i][j] == EMPTY) {
                    continue;
                }

                for (int[] direction : directions) {
                    int dx = direction[0], dy = direction[1];

                    int nx = i - dx, ny = j - dy;
                    if (inBoard(nx, ny) && board[nx][ny] == board[i][j]) continue;

                    int count = 0;
                    nx = i;
                    ny = j;
                    while (inBoard(nx, ny) && board[nx][ny] == board[i][j]) {
                        count++;
                        nx += dx;
                        ny += dy;
                    }
                    boolean rightBlock = !inBoard(nx, ny) || board[nx][ny] == 3 - board[i][j];
                    nx = i - dx;
                    ny = j - dy;
                    boolean leftBlock = !inBoard(nx, ny) || board[nx][ny] == 3 - board[i][j];

                    int val = getVal(count, rightBlock, leftBlock);

                    if (board[i][j] == ai) {
                        if (val == FIVE) {
                            return FIVE;
                        }
                        aiScore += val;
                    } else {
                        if (val == FIVE) {
                            return -FIVE;
                        }
                        humanScore += val;
                    }
                }
            }
        }

        return aiScore - humanScore;
    }

    private int getVal(int count, boolean rightBlock, boolean leftBlock) {
        int val = OTHER;
        if (count >= 5) {
            val = FIVE;
        } else if (count == 4) {
            if (!rightBlock && !leftBlock)
                val = OPEN_FOUR;
            else
                val = CLOSED_FOUR;
        } else if (count == 3) {
            if (!rightBlock && !leftBlock)
                val = OPEN_THREE;
            else
                val = CLOSED_THREE;
        } else if (count == 2) {
            if (!rightBlock && !leftBlock)
                val = OPEN_TWO;
        }
        return val;
    }

    private List<int[]> getLegalMoves(int[][] board) {
        List<int[]> res = new ArrayList<>();
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if (board[i][j] == 0 && hasNeighbor(board, i, j, 2)) {
                    res.add(new int[]{i, j});
                }
            }
        }
        // 排序（最关键）
        res.sort((a, b) -> {
            int sa = quickEval(board, a[0], a[1]);
            int sb = quickEval(board, b[0], b[1]);
            return sb - sa;
        });

        // 限制候选数
        if (res.size() > 15) {
            return res.subList(0, 15);
        }
        if (res.isEmpty()) {
            // 下中间
            res.add(new int[]{board.length / 2, board[0].length / 2});
        }
        return res;
    }

    private boolean hasNeighbor(int[][] board, int x, int y, int distance) {
        for (int i = Math.max(0, x - distance); i < Math.min(board.length, x + distance + 1); i++) {
            for (int j = Math.max(0, y - distance); j < Math.min(board[0].length, y + distance + 1); j++) {
                if (board[i][j] != 0) return true;
            }
        }
        return false;
    }

    private int quickEval(int[][] board, int i, int j) {
        int score = 0;

        // 放 AI 的分
        score += evaluatePoint(board, i, j, ai);
        // 放 Human 的分（防守非常重要）
        score += evaluatePoint(board, i, j, human) * 2; // 防守权重更大

        return score;
    }

    private int evaluatePoint(int[][] board, int x, int y, int player) {
        int total = 0;

        for (int[] d : directions) {
            int dx = d[0], dy = d[1];
            int count = 1;
            int nx = x + dx, ny = y + dy;
            while (inBoard(nx, ny)) {
                if (board[nx][ny] == player) {
                    nx += dx;
                    ny += dy;
                    count++;
                } else {
                    break;
                }
            }

            boolean rightBlocked = !inBoard(nx, ny) || (board[nx][ny] != EMPTY && board[nx][ny] != player);

            nx = x - dx;
            ny = y - dy;
            while (inBoard(nx, ny)) {
                if (board[nx][ny] == player) {
                    nx -= dx;
                    ny -= dy;
                    count++;
                } else {
                    break;
                }
            }

            boolean leftBlocked = !inBoard(nx, ny) || (board[nx][ny] != EMPTY && board[nx][ny] != player);

            total += getVal(count, rightBlocked, leftBlocked);
        }
        return total;
    }


    private boolean inBoard(int i, int j) {
        return i >= 0 && i < core.getCore().length && j >= 0 && j < core.getCore()[0].length;
    }

    public void setHuman(int human) {
        this.human = human;
    }

    public void setAi(int ai) {
        this.ai = ai;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public int getAi() {
        return ai;
    }

}
