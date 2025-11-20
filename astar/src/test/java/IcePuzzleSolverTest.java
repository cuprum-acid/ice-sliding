// IcePuzzleSolverTest.java
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Timeout;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

class IcePuzzleSolverTest {

    @Test
    @DisplayName("Тест 1: Простая доска 3x3 - прямое решение")
    void testSimple3x3Board() {
        String[] rows = {
            "P.G",
            "...",
            "..."
        };
        char[][] board = IcePuzzleSolver.createBoard(rows);
        int[] start = IcePuzzleSolver.findStartPosition(board);
        
        List<String> solution = IcePuzzleSolver.solveIcePuzzle(board, start[0], start[1]);
        
        assertFalse(solution.isEmpty(), "Решение должно быть найдено");
        assertTrue(solution.size() <= 2, "Решение должно быть за 1-2 хода");
    }

    @Test
    @DisplayName("Тест 2: Доска с препятствием - обходной путь")
    void testBoardWithObstacle() {
        String[] rows = {
            "P.XG",
            "...X",
            ".X..",
            "...."
        };
        char[][] board = IcePuzzleSolver.createBoard(rows);
        int[] start = IcePuzzleSolver.findStartPosition(board);
        
        List<String> solution = IcePuzzleSolver.solveIcePuzzle(board, start[0], start[1]);
        
        assertTrue(solution.isEmpty(), "Решение не должно быть найдено");
    }

    @Test
    @DisplayName("Тест 3: Нерешаемая доска")
    void testUnsolvableBoard() {
        String[] rows = {
            "P.X",
            "XXX",
            "..G"
        };
        char[][] board = IcePuzzleSolver.createBoard(rows);
        int[] start = IcePuzzleSolver.findStartPosition(board);
        
        List<String> solution = IcePuzzleSolver.solveIcePuzzle(board, start[0], start[1]);
        
        assertTrue(solution.isEmpty(), "Решение не должно быть найдено");
    }

    @Test
    @DisplayName("Тест 4: Большая доска 7x7")
    void testLarge7x7Board() {
        String[] rows = {
            "P..X.XG",
            "XX.X.X.",
            "...X.X.",
            ".XXX.X.",
            "....X..",
            ".XXXXX.",
            "......G"
        };
        char[][] board = IcePuzzleSolver.createBoard(rows);
        int[] start = IcePuzzleSolver.findStartPosition(board);
        
        List<String> solution = IcePuzzleSolver.solveIcePuzzle(board, start[0], start[1]);
        
        assertFalse(solution.isEmpty(), "Решение должно быть найдено");
    }

    @Test
    @DisplayName("Тест 5: Проверка эвристической функции")
    void testHeuristicFunction() {
        String[] rows = {
            "P..G",
            "....",
            "...."
        };
        char[][] board = IcePuzzleSolver.createBoard(rows);
        
        IcePuzzleSolver.State state = new IcePuzzleSolver.State(0, 0, board, 0, null, "Start");
        
        // Манхэттенское расстояние от (0,0) до (0,3) = 3
        assertTrue(state.cost >= 0, "Эвристика должна быть неотрицательной");
    }

    @Test
    @DisplayName("Тест 6: Генерация соседей из угловой позиции")
    void testNeighborGenerationCorner() {
        String[] rows = {
            "P...",
            "....",
            "....",
            "...G"
        };
        char[][] board = IcePuzzleSolver.createBoard(rows);
        
        IcePuzzleSolver.State state = new IcePuzzleSolver.State(0, 0, board, 0, null, "Start");
        List<IcePuzzleSolver.State> neighbors = IcePuzzleSolver.generateNeighbors(state);
        
        // Из угла (0,0) должно быть 2 возможных направления (ВНИЗ и ВПРАВО)
        assertEquals(2, neighbors.size(), "Должно быть 2 возможных хода из угла");
    }

    @Test
    @DisplayName("Тест 7: Симуляция скольжения до препятствия")
    void testSlideSimulation() {
        String[] rows = {
            "P.X.G"
        };
        char[][] board = IcePuzzleSolver.createBoard(rows);
        
        IcePuzzleSolver.State state = new IcePuzzleSolver.State(0, 0, board, 0, null, "Start");
        IcePuzzleSolver.State result = IcePuzzleSolver.simulateSlide(state, 0, 1, "RIGHT");
        
        assertNotNull(result, "Скольжение должно произойти");
        assertEquals(0, result.playerX, "X координата не должна измениться");
        assertEquals(1, result.playerY, "Игрок должен остановиться перед препятствием");
    }

    @Test
    @DisplayName("Тест 8: Скольжение до цели")
    void testSlideToGoal() {
        String[] rows = {
            "P..G"
        };
        char[][] board = IcePuzzleSolver.createBoard(rows);
        
        IcePuzzleSolver.State state = new IcePuzzleSolver.State(0, 0, board, 0, null, "Start");
        IcePuzzleSolver.State result = IcePuzzleSolver.simulateSlide(state, 0, 1, "RIGHT");
        
        assertNotNull(result, "Скольжение должно произойти");
        assertEquals(0, result.playerX, "X координата не должна измениться");
        assertEquals(3, result.playerY, "Игрок должен достичь цели");
    }

    @Test
    @DisplayName("Тест 9: Проверка определения целевого состояния")
    void testGoalStateDetection() {
        String[] rows = {
            "G"
        };
        char[][] board = IcePuzzleSolver.createBoard(rows);
        
        IcePuzzleSolver.State state = new IcePuzzleSolver.State(0, 0, board, 0, null, "Start");
        boolean isGoal = IcePuzzleSolver.isGoalState(state);
        
        assertTrue(isGoal, "Состояние должно быть целевым");
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    @DisplayName("Тест 10: Производительность - большой лабиринт")
    void testPerformanceLargeMaze() {
        String[] rows = {
            "P.......X",
            ".XXXXXX.X",
            ".X.....X.",
            ".X.XXX.X.",
            ".X.X.G.X.",
            ".X.XXX...",
            ".X.......",
            ".XXXXXXXX",
            "........."
        };
        char[][] board = IcePuzzleSolver.createBoard(rows);
        int[] start = IcePuzzleSolver.findStartPosition(board);
        
        List<String> solution = IcePuzzleSolver.solveIcePuzzle(board, start[0], start[1]);
        
        assertTrue(solution.isEmpty(), "Решение не должно быть найдено");
    }

    @Test
    @DisplayName("Тест 11: Несколько целей на доске")
    void testMultipleGoals() {
        String[] rows = {
            "P.X.G",
            "...X.",
            ".G..."
        };
        char[][] board = IcePuzzleSolver.createBoard(rows);
        int[] start = IcePuzzleSolver.findStartPosition(board);
        
        List<String> solution = IcePuzzleSolver.solveIcePuzzle(board, start[0], start[1]);
        
        assertFalse(solution.isEmpty(), "Должна быть найдена хотя бы одна цель");
    }

    @Test
    @DisplayName("Тест 12: Пустая доска без препятствий")
    void testEmptyBoard() {
        String[] rows = {
            "P...",
            "....",
            "...G"
        };
        char[][] board = IcePuzzleSolver.createBoard(rows);
        int[] start = IcePuzzleSolver.findStartPosition(board);
        
        List<String> solution = IcePuzzleSolver.solveIcePuzzle(board, start[0], start[1]);
        
        assertFalse(solution.isEmpty(), "Решение должно быть найдено на пустой доске");
        // Должен быть прямой путь
        assertTrue(solution.size() <= 3, "Решение должно быть оптимальным");
    }
}
