import java.util.*;

public class IcePuzzleSolver {
    
    // Представление состояния игры
    static class State implements Comparable<State> {
        int playerX, playerY;
        char[][] board;
        int moves; // количество ходов
        int cost; // f(x) = moves + heuristic
        State parent;
        String action; // последнее действие
        
        public State(int playerX, int playerY, char[][] board, int moves, State parent, String action) {
            this.playerX = playerX;
            this.playerY = playerY;
            this.board = copyBoard(board);
            this.moves = moves;
            this.parent = parent;
            this.action = action;
            this.cost = moves + heuristic();
        }
        
        // Копирование доски
        private char[][] copyBoard(char[][] original) {
            char[][] copy = new char[original.length][original[0].length];
            for (int i = 0; i < original.length; i++) {
                System.arraycopy(original[i], 0, copy[i], 0, original[i].length);
            }
            return copy;
        }
        
        // Эвристика: манхэттенское расстояние до цели
        private int heuristic() {
            int minDistance = Integer.MAX_VALUE;
            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board[0].length; j++) {
                    if (board[i][j] == 'G') {
                        int distance = Math.abs(playerX - i) + Math.abs(playerY - j);
                        minDistance = Math.min(minDistance, distance);
                    }
                }
            }
            return minDistance;
        }
        
        @Override
        public int compareTo(State other) {
            return Integer.compare(this.cost, other.cost);
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            State state = (State) obj;
            return playerX == state.playerY && 
                   playerY == state.playerY && 
                   Arrays.deepEquals(board, state.board);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(playerX, playerY, Arrays.deepHashCode(board));
        }
    }
    
    // Алгоритм A* для Ice Puzzle
    public static List<String> solveIcePuzzle(char[][] initialBoard, int startX, int startY) {
        PriorityQueue<State> openSet = new PriorityQueue<>();
        Set<State> visited = new HashSet<>();
        
        // Начальное состояние
        State start = new State(startX, startY, initialBoard, 0, null, "Start");
        openSet.add(start);
        visited.add(start);
        
        while (!openSet.isEmpty()) {
            State current = openSet.poll();
            
            // Проверка на победу
            if (isGoalState(current)) {
                return reconstructSolution(current);
            }
            
            // Генерация возможных ходов
            for (State neighbor : generateNeighbors(current)) {
                if (!visited.contains(neighbor)) {
                    openSet.add(neighbor);
                    visited.add(neighbor);
                }
            }
        }
        
        return new ArrayList<>(); // Решение не найдено
    }
    
    // Проверка, является ли состояние целевым
    private static boolean isGoalState(State state) {
        return state.board[state.playerX][state.playerY] == 'G';
    }
    
    // Генерация всех возможных ходов-скольжений
    private static List<State> generateNeighbors(State state) {
        List<State> neighbors = new ArrayList<>();
        int[] dx = {-1, 1, 0, 0}; // Вверх, вниз, влево, вправо
        int[] dy = {0, 0, -1, 1};
        String[] directions = {"UP", "DOWN", "LEFT", "RIGHT"};
        
        for (int i = 0; i < 4; i++) {
            State slideResult = simulateSlide(state, dx[i], dy[i], directions[i]);
            if (slideResult != null && !slideResult.equals(state)) {
                neighbors.add(slideResult);
            }
        }
        
        return neighbors;
    }
    
    // Симуляция скольжения в заданном направлении
    private static State simulateSlide(State state, int dx, int dy, String direction) {
        int x = state.playerX;
        int y = state.playerY;
        char[][] board = state.board;
        
        int newX = x;
        int newY = y;
        
        // Скользим пока не упремся в препятствие
        while (true) {
            int nextX = newX + dx;
            int nextY = newY + dy;
            
            // Проверка границ
            if (nextX < 0 || nextX >= board.length || nextY < 0 || nextY >= board[0].length) {
                break;
            }
            
            // Проверка на препятствие
            if (board[nextX][nextY] == 'X') {
                break;
            }
            
            newX = nextX;
            newY = nextY;
            
            // Если наткнулись на цель или остановились на месте
            if (board[newX][newY] == 'G') {
                break;
            }
        }
        
        // Если не сдвинулись с места - ход невалидный
        if (newX == x && newY == y) {
            return null;
        }
        
        // Создаем новое состояние (доска не меняется, кроме позиции игрока)
        return new State(newX, newY, board, state.moves + 1, state, direction);
    }
    
    // Восстановление решения
    private static List<String> reconstructSolution(State goalState) {
        List<String> solution = new ArrayList<>();
        State current = goalState;
        
        while (current.parent != null) {
            solution.add(0, current.action + " to (" + current.playerX + "," + current.playerY + ")");
            current = current.parent;
        }
        
        return solution;
    }
    
    // Визуализация доски
    public static void printBoard(char[][] board, int playerX, int playerY) {
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
    
    // Пример использования
    public static void main(String[] args) {
        // Пример уровня Ice Puzzle:
        // 'P' - игрок, 'X' - препятствие, 'G' - цель, '.' - лед
        char[][] board_old = {
            {'.', '.', '.', 'X', '.'},
            {'.', 'X', '.', '.', '.'},
            {'.', '.', '.', 'X', '.'},
            {'X', '.', '.', '.', 'G'},
            {'.', '.', 'X', '.', '.'}
        };

        char[][] board = {
            {'.', '.', '.', 'X', '.', '.', '.', 'G', 'X', '.',  '.', '.', '.', 'X', '.'},
            {'.', 'X', '.', '.', '.','.', 'X', '.', '.', '.','.', 'X', '.', '.', '.'},
            {'.', '.', '.', 'X', '.', '.', '.', '.', 'X', '.', '.', '.', '.', 'X', '.'},
            {'X', '.', '.', '.', '.', '.', '.', '.', 'X', '.', '.', '.', '.', 'X', '.'},
            {'.', '.', 'X', '.', '.', '.', '.', 'X', '.', '.', '.', '.', 'X', '.', '.'}
        };
        
        int startX = 0, startY = 0; // Стартовая позиция
        
        System.out.println("Исходная доска:");
        printBoard(board, startX, startY);
        
        List<String> solution = solveIcePuzzle(board, startX, startY);
        
        if (solution.isEmpty()) {
            System.out.println("Решение не найдено!");
        } else {
            System.out.println("Решение найдено за " + solution.size() + " ходов:");
            for (int i = 0; i < solution.size(); i++) {
                System.out.println((i + 1) + ". " + solution.get(i));
            }
            
            // Визуализация решения
            System.out.println("\nВизуализация решения:");
            visualizeSolution(board, startX, startY, solution);
        }
    }
    
    // Дополнительный метод для визуализации всего решения
    private static void visualizeSolution(char[][] initialBoard, int startX, int startY, List<String> solution) {
        char[][] board = copyBoard(initialBoard);
        int x = startX, y = startY;
        
        System.out.println("Начальная позиция:");
        printBoard(board, x, y);
        
        for (String move : solution) {
            String[] parts = move.split(" ");
            String direction = parts[0];
            
            // Определяем направление
            int dx = 0, dy = 0;
            switch (direction) {
                case "UP": dx = -1; break;
                case "DOWN": dx = 1; break;
                case "LEFT": dy = -1; break;
                case "RIGHT": dy = 1; break;
            }
            
            // Скользим
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
            
            System.out.println("После хода: " + move);
            printBoard(board, x, y);
        }
    }
    
    private static char[][] copyBoard(char[][] original) {
        char[][] copy = new char[original.length][original[0].length];
        for (int i = 0; i < original.length; i++) {
            System.arraycopy(original[i], 0, copy[i], 0, original[i].length);
        }
        return copy;
    }
}
