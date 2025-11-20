// IcePuzzleSolver.java
import java.util.*;

public class IcePuzzleSolver {
    
    public static class State implements Comparable<State> {
        public final int playerX, playerY;
        public final char[][] board;
        public final int moves;
        public final int cost;
        public final State parent;
        public final String action;
        
        public State(int playerX, int playerY, char[][] board, int moves, State parent, String action) {
            this.playerX = playerX;
            this.playerY = playerY;
            this.board = copyBoard(board);
            this.moves = moves;
            this.parent = parent;
            this.action = action;
            this.cost = moves + heuristic();
        }
        
        private char[][] copyBoard(char[][] original) {
            char[][] copy = new char[original.length][original[0].length];
            for (int i = 0; i < original.length; i++) {
                System.arraycopy(original[i], 0, copy[i], 0, original[i].length);
            }
            return copy;
        }
        
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
            return playerX == state.playerX && 
                   playerY == state.playerY && 
                   Arrays.deepEquals(board, state.board);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(playerX, playerY, Arrays.deepHashCode(board));
        }
        
        @Override
        public String toString() {
            return "(" + playerX + "," + playerY + ")";
        }
    }
    
    public static List<String> solveIcePuzzle(char[][] initialBoard, int startX, int startY) {
        PriorityQueue<State> openSet = new PriorityQueue<>();
        Set<State> visited = new HashSet<>();
        
        State start = new State(startX, startY, initialBoard, 0, null, "Start");
        openSet.add(start);
        visited.add(start);
        
        while (!openSet.isEmpty()) {
            State current = openSet.poll();
            
            if (isGoalState(current)) {
                return reconstructSolution(current);
            }
            
            for (State neighbor : generateNeighbors(current)) {
                if (!visited.contains(neighbor)) {
                    openSet.add(neighbor);
                    visited.add(neighbor);
                }
            }
        }
        
        return new ArrayList<>();
    }
    
    public static boolean isGoalState(State state) {
        return state.board[state.playerX][state.playerY] == 'G';
    }
    
    public static List<State> generateNeighbors(State state) {
        List<State> neighbors = new ArrayList<>();
        int[] dx = {-1, 1, 0, 0};
        int[] dy = {0, 0, -1, 1};
        String[] directions = {"UP", "DOWN", "LEFT", "RIGHT"};
        
        for (int i = 0; i < 4; i++) {
            State slideResult = simulateSlide(state, dx[i], dy[i], directions[i]);
            if (slideResult != null) {
                neighbors.add(slideResult);
            }
        }
        
        return neighbors;
    }
    
    public static State simulateSlide(State state, int dx, int dy, String direction) {
        int x = state.playerX;
        int y = state.playerY;
        char[][] board = state.board;
        
        int newX = x;
        int newY = y;
        
        while (true) {
            int nextX = newX + dx;
            int nextY = newY + dy;
            
            if (nextX < 0 || nextX >= board.length || nextY < 0 || nextY >= board[0].length) {
                break;
            }
            
            if (board[nextX][nextY] == 'X') {
                break;
            }
            
            newX = nextX;
            newY = nextY;
            
            if (board[newX][newY] == 'G') {
                break;
            }
        }
        
        if (newX == x && newY == y) {
            return null;
        }
        
        return new State(newX, newY, board, state.moves + 1, state, direction);
    }
    
    private static List<String> reconstructSolution(State goalState) {
        List<String> solution = new ArrayList<>();
        State current = goalState;
        
        while (current.parent != null) {
            solution.add(0, current.action + " to " + current);
            current = current.parent;
        }
        
        return solution;
    }
    
    // Вспомогательные методы для тестирования
    public static char[][] createBoard(String[] rows) {
        char[][] board = new char[rows.length][rows[0].length()];
        for (int i = 0; i < rows.length; i++) {
            board[i] = rows[i].toCharArray();
        }
        return board;
    }
    
    public static int[] findStartPosition(char[][] board) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] == 'P') {
                    return new int[]{i, j};
                }
            }
        }
        throw new IllegalArgumentException("Start position 'P' not found on board");
    }
}
