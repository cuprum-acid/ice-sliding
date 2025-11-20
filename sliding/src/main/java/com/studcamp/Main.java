package com.studcamp;

public class Main {
    public static void main(String[] args) {
        System.out.println("Ice Puzzle Solver");
        System.out.println("Looking for maze*.txt files in current directory...\n");
        
        IcePuzzleSolver.processMazeFiles();
        
        System.out.println("\nProcessing complete!");
    }
}
