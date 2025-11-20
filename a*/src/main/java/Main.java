// Main.java
import java.util.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== Ice Puzzle Solver ===");
        
        // Тест 1: Простая доска
        System.out.println("\n--- Тест 1: Простая доска 3x3 ---");
        testSimpleBoard();
        
        // Тест 2: Доска с препятствиями
        System.out.println("\n--- Тест 2: Доска с препятствиями ---");
        testBoardWithObstacles();
        
        // Тест 3: Исходная доска из вашего примера
        System.out.println("\n--- Тест 3: Исходная доска 7x7 ---");
        testOriginalBoard();
        
        // Тест 4: Нерешаемая доска
        System.out.println("\n--- Тест 4: Нерешаемая доска ---");
        testUnsolvableBoard();
    }
    
    static void testSimpleBoard() {
        String[] rows = {
            "P.G",
            "...",
            "..."
        };
        char[][] board = IcePuzzleSolver.createBoard(rows);
        int[] start = IcePuzzleSolver.findStartPosition(board);
        
        printBoard(board, start[0], start[1]);
        
        List<String> solution = IcePuzzleSolver.solveIcePuzzle(board, start[0], start[1]);
        
        if (solution.isEmpty()) {
            System.out.println("❌ Решение не найдено!");
        } else {
            System.out.println("✅ Решение найдено за " + solution.size() + " ходов:");
            for (int i = 0; i < solution.size(); i++) {
                System.out.println((i + 1) + ". " + solution.get(i));
            }
        }
    }
    
    static void testBoardWithObstacles() {
        String[] rows = {
            "P.XG",
            "...X",
            ".X..",
            "...."
        };
        char[][] board = IcePuzzleSolver.createBoard(rows);
        int[] start = IcePuzzleSolver.findStartPosition(board);
        
        printBoard(board, start[0], start[1]);
        
        List<String> solution = IcePuzzleSolver.solveIcePuzzle(board, start[0], start[1]);
        
        if (solution.isEmpty()) {
            System.out.println("❌ Решение не найдено!");
        } else {
            System.out.println("✅ Решение найдено за " + solution.size() + " ходов:");
            for (int i = 0; i < solution.size(); i++) {
                System.out.println((i + 1) + ". " + solution.get(i));
            }
        }
    }
    
    static void testOriginalBoard() {
        char[][] board = {
            {'P', '.', '.', 'X', '.', 'X', 'G'},
            {'X', 'X', '.', 'X', '.', 'X', '.'},
            {'.', '.', '.', 'X', '.', 'X', '.'},
            {'.', 'X', 'X', 'X', '.', 'X', '.'},
            {'.', '.', '.', '.', '.', 'X', '.'},
            {'.', 'X', 'X', 'X', 'X', 'X', '.'},
            {'.', '.', '.', '.', '.', '.', '.'}
        };
        
        printBoard(board, 0, 0);
        
        List<String> solution = IcePuzzleSolver.solveIcePuzzle(board, 0, 0);
        
        if (solution.isEmpty()) {
            System.out.println("❌ Решение не найдено!");
        } else {
            System.out.println("✅ Решение найдено за " + solution.size() + " ходов:");
            for (int i = 0; i < solution.size(); i++) {
                System.out.println((i + 1) + ". " + solution.get(i));
            }
            
            // Визуализация решения
            System.out.println("\nВизуализация пути:");
            visualizeSolution(board, solution);
        }
    }
    
    static void testUnsolvableBoard() {
        String[] rows = {
            "P.X",
            "XXX", 
            "..G"
        };
        char[][] board = IcePuzzleSolver.createBoard(rows);
        int[] start = IcePuzzleSolver.findStartPosition(board);
        
        printBoard(board, start[0], start[1]);
        
        List<String> solution = IcePuzzleSolver.solveIcePuzzle(board, start[0], start[1]);
        
        if (solution.isEmpty()) {
            System.out.println("✅ Правильно определена нерешаемость");
        } else {
            System.out.println("❌ Ожидалась нерешаемость, но найдено решение");
        }
    }
    
    static void printBoard(char[][] board, int playerX, int playerY) {
        System.out.println("Доска " + board.length + "x" + board[0].length + ":");
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (i == playerX && j == playerY) {
                    System.out.print("P ");
                } else {
                    System.out.print(board[i][j] + " ");
                }
            }
            System.out.println();
        }
        System.out.println();
    }
    
    static void visualizeSolution(char[][] initialBoard, List<String> solution) {
        char[][] board = copyBoard(initialBoard);
        int x = 0, y = 0;
        
        // Находим стартовую позицию
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] == 'P') {
                    x = i;
                    y = j;
                    break;
                }
            }
        }
        
        System.out.println("Шаг 0 - Начало:");
        printBoard(board, x, y);
        
        for (int step = 0; step < solution.size(); step++) {
            String move = solution.get(step);
            String direction = move.split(" ")[0];
            
            int dx = 0, dy = 0;
            switch (direction) {
                case "UP": dx = -1; break;
                case "DOWN": dx = 1; break;
                case "LEFT": dy = -1; break;
                case "RIGHT": dy = 1; break;
            }
            
            int startX = x, startY = y;
            while (true) {
                int nextX = x + dx;
                int nextY = y + dy;
                
                if (nextX < 0 || nextX >= board.length || nextY < 0 || nextY >= board[0].length || 
                    board[nextX][nextY] == 'X') {
                    break;
                }
                
                x = nextX;
                y = nextY;
                
                if (board[x][y] == 'G') break;
            }
            
            System.out.println("Шаг " + (step + 1) + " - " + move + " (из (" + startX + "," + startY + ")):");
            printBoard(board, x, y);
        }
    }
    
    static char[][] copyBoard(char[][] original) {
        char[][] copy = new char[original.length][original[0].length];
        for (int i = 0; i < original.length; i++) {
            System.arraycopy(original[i], 0, copy[i], 0, original[i].length);
        }
        return copy;
    }
}
