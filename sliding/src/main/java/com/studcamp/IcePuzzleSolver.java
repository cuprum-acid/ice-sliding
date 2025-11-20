package com.studcamp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

public class IcePuzzleSolver {
    
    static class Cell {
        int x, y, distance;
        
        Cell(int x, int y, int distance) {
            this.x = x;
            this.y = y;
            this.distance = distance;
        }
    }
    
    private static final int[][] DIRECTIONS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
    
    public static String[][] solvePuzzle(String[][] grid) {
        int n = grid.length;
        int m = grid[0].length;
        
        // Находим целевую позицию
        int targetX = -1, targetY = -1;
        
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if ("=".equals(grid[i][j])) {
                    targetX = i;
                    targetY = j;
                }
            }
        }
        
        if (targetX == -1) {
            System.out.println("Warning: No target found (=)");
            return createEmptyResult(grid);
        }
        
        // Матрица расстояний - инициализируем бесконечностью
        int[][] distances = new int[n][m];
        for (int i = 0; i < n; i++) {
            Arrays.fill(distances[i], Integer.MAX_VALUE);
        }
        distances[targetX][targetY] = 0;
        
        // Очередь для BFS
        Queue<Cell> queue = new LinkedList<>();
        queue.offer(new Cell(targetX, targetY, 0));
        
        // Множество посещенных клеток
        boolean[][] visited = new boolean[n][m];
        visited[targetX][targetY] = true;
        
        while (!queue.isEmpty()) {
            Cell current = queue.poll();
            int x = current.x;
            int y = current.y;
            int dist = current.distance;
            
            // Для текущей клетки ищем все клетки, откуда можно прийти ОДНИМ свайпом
            // Это клетки, от которых можно скользить и остановиться в текущей позиции
            for (int[] dir : DIRECTIONS) {
                // Находим все возможные стартовые позиции для свайпа в направлении dir,
                // которые заканчиваются в (x, y)
                findSlideStarts(x, y, dir, grid, distances, visited, queue, dist);
            }
        }
        
        // Создаем результирующую матрицу
        return createResultGrid(grid, distances);
    }
    
    private static void findSlideStarts(int endX, int endY, int[] dir, String[][] grid, 
                                      int[][] distances, boolean[][] visited, 
                                      Queue<Cell> queue, int currentDist) {
        int n = grid.length;
        int m = grid[0].length;
        
        // Идем в ПРОТИВОПОЛОЖНОМ направлении от конечной точки
        // чтобы найти, откуда можно начать скольжение
        int reverseDirX = -dir[0];
        int reverseDirY = -dir[1];
        
        // Начинаем с клетки перед конечной в обратном направлении
        int checkX = endX + reverseDirX;
        int checkY = endY + reverseDirY;
        
        // Если эта клетка вне границ или препятствие - нельзя начать скольжение отсюда
        if (checkX < 0 || checkX >= n || checkY < 0 || checkY >= m || 
            "0".equals(grid[checkX][checkY])) {
            return;
        }
        
        // Теперь идем назад по траектории скольжения, собирая все возможные стартовые позиции
        int currentX = checkX;
        int currentY = checkY;
        
        while (true) {
            // Проверяем, может ли скольжение из (currentX, currentY) в направлении dir
            // закончиться в (endX, endY)
            if (isValidSlideToTarget(currentX, currentY, dir, endX, endY, grid)) {
                // Нашли валидную стартовую позицию
                int newDist = currentDist + 1;
                
                if (newDist < distances[currentX][currentY]) {
                    distances[currentX][currentY] = newDist;
                    if (!visited[currentX][currentY]) {
                        visited[currentX][currentY] = true;
                        queue.offer(new Cell(currentX, currentY, newDist));
                    }
                }
            }
            
            // Двигаемся дальше в обратном направлении
            int nextX = currentX + reverseDirX;
            int nextY = currentY + reverseDirY;
            
            // Проверяем границы и препятствия
            if (nextX < 0 || nextX >= n || nextY < 0 || nextY >= m || 
                "0".equals(grid[nextX][nextY])) {
                break;
            }
            
            currentX = nextX;
            currentY = nextY;
        }
    }
    
    private static boolean isValidSlideToTarget(int startX, int startY, int[] dir, 
                                              int targetX, int targetY, String[][] grid) {
        int n = grid.length;
        int m = grid[0].length;
        
        int currentX = startX;
        int currentY = startY;
        
        // Моделируем скольжение от стартовой позиции
        while (true) {
            // Двигаемся в направлении скольжения
            int nextX = currentX + dir[0];
            int nextY = currentY + dir[1];
            
            // Проверяем границы
            if (nextX < 0 || nextX >= n || nextY < 0 || nextY >= m) {
                // Достигли границы - проверяем, остановились ли мы на цели
                return (currentX == targetX && currentY == targetY);
            }
            
            // Проверяем препятствия
            if ("0".equals(grid[nextX][nextY])) {
                // Достигли препятствия - проверяем, остановились ли мы на цели
                return (currentX == targetX && currentY == targetY);
            }
            
            // Переходим к следующей клетке
            currentX = nextX;
            currentY = nextY;
            
            // Если достигли целевой позиции - проверяем, можем ли здесь остановиться
            if (currentX == targetX && currentY == targetY) {
                // Можем остановиться на цели
                return true;
            }
            
            
        }
    }
    
    private static String[][] createEmptyResult(String[][] grid) {
        String[][] result = new String[grid.length][grid[0].length];
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if ("0".equals(grid[i][j]) || "=".equals(grid[i][j])) {
                    result[i][j] = "0";
                } else {
                    result[i][j] = "∞";
                }
            }
        }
        return result;
    }
    
    private static String[][] createResultGrid(String[][] grid, int[][] distances) {
        int n = grid.length;
        int m = grid[0].length;
        String[][] result = new String[n][m];
        
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if ("0".equals(grid[i][j])) {
                    result[i][j] = "X";
                } else if ("=".equals(grid[i][j])) {
                    result[i][j] = "0";
                } else if (distances[i][j] == Integer.MAX_VALUE) {
                    result[i][j] = "∞";
                } else {
                    result[i][j] = String.valueOf(distances[i][j]);
                }
            }
        }
        return result;
    }
    
    public static String[][] parseInput(String input) {
        String[] rows = input.trim().split("\n");
        List<String[]> gridList = new ArrayList<>();
        
        for (String row : rows) {
            row = row.trim();
            if (!row.isEmpty()) {
                // Разбиваем по символам (каждый символ - отдельная клетка)
                String[] cells = row.split("");
                gridList.add(cells);
            }
        }
        
        return gridList.toArray(new String[0][]);
    }
    
    public static String formatOutput(String[][] grid) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                sb.append(grid[i][j]);
                if (j < grid[i].length - 1) {
                    sb.append(" ");
                }
            }
            if (i < grid.length - 1) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }
    
    public static void processMazeFiles() {
        try {
            Path currentDir = Paths.get(".");
            List<Path> mazeFiles = Files.list(currentDir)
                    .filter(path -> {
                        String fileName = path.getFileName().toString();
                        return fileName.matches("maze.*\\.txt") && 
                               !fileName.contains("_result");
                    })
                    .sorted()
                    .collect(Collectors.toList());
            
            if (mazeFiles.isEmpty()) {
                System.out.println("No maze*.txt files found in current directory");
                return;
            }
            
            System.out.println("Found " + mazeFiles.size() + " maze files:");
            for (Path file : mazeFiles) {
                System.out.println("  - " + file.getFileName());
            }
            System.out.println();
            
            for (Path mazeFile : mazeFiles) {
                System.out.println("Processing: " + mazeFile.getFileName());
                
                try {
                    String content = Files.readString(mazeFile);
                    String[][] grid = parseInput(content);
                    String[][] result = solvePuzzle(grid);
                    
                    String inputFileName = mazeFile.getFileName().toString();
                    String outputFileName = inputFileName.replace(".txt", "_result.txt");
                    
                    String output = formatOutput(result);
                    Files.writeString(Paths.get(outputFileName), output);
                    
                    System.out.println("  Result saved to: " + outputFileName);
                    findAndPrintStartDistance(grid, result, inputFileName);
                    printReachabilityStats(result, inputFileName);
                    
                } catch (Exception e) {
                    System.out.println("  Error processing file: " + e.getMessage());
                    e.printStackTrace();
                }
                System.out.println();
            }
            
        } catch (IOException e) {
            System.out.println("Error reading directory: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void findAndPrintStartDistance(String[][] grid, String[][] result, String fileName) {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if ("+".equals(grid[i][j])) {
                    String dist = result[i][j];
                    System.out.println("  Start to target distance: " + 
                                     ("∞".equals(dist) ? "UNREACHABLE" : dist + " moves"));
                    return;
                }
            }
        }
        System.out.println("  Warning: No start position (+) found");
    }
    
    private static void printReachabilityStats(String[][] result, String fileName) {
        int totalCells = 0;
        int reachableCells = 0;
        int unreachableCells = 0;
        int obstacles = 0;
        
        for (String[] row : result) {
            for (String cell : row) {
                totalCells++;
                if ("∞".equals(cell)) {
                    unreachableCells++;
                } else if ("0".equals(cell)) {
                    obstacles++;
                } else {
                    reachableCells++;
                }
            }
        }
        
        System.out.println("  Reachability: " + reachableCells + " reachable, " + 
                         unreachableCells + " unreachable, " + obstacles + " obstacles/goal");
    }
    
}