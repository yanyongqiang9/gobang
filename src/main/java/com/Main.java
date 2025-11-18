package com;

public class Main {
    public static void main(String[] args) {
        int size = 21;
        if (args != null && args.length > 0) {
            size = Integer.parseInt(args[0]);
        }
        new Windows("五子棋", size);
    }
}