package com.kayar.yetanotherlabyrinth.labyrinth;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;

import java.util.Random;
import java.util.Stack;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.kayar.yetanotherlabyrinth.labyrinth.LabyrinthGame.EntityType.*;

/**
 * Generates a random labyrinth using a depth-first search algorithm.
 * The labyrinth consists of walls, floors, and an exit point.
 */
public class LabyrinthGenerator {
    // Cell states
    private static final int WALL = 0;
    private static final int PATH = 1;
    private static final int START = 2;
    private static final int EXIT = 3;
    
    // Cell size in 3D world
    private static final double CELL_SIZE = 2.0;
    private static final double WALL_HEIGHT = 3.0;
    
    // Grid dimensions
    private final int width;
    private final int height;
    
    // The grid representing the labyrinth
    private int[][] grid;
    
    // Start and exit positions
    private int startX, startZ;
    private int exitX, exitZ;
    
    // Random number generator
    private final Random random = new Random();
    
    /**
     * Creates a new labyrinth generator with the specified dimensions.
     *
     * @param width the width of the labyrinth
     * @param height the height of the labyrinth
     */
    public LabyrinthGenerator(int width, int height) {
        // Ensure odd dimensions for proper maze generation
        this.width = width % 2 == 0 ? width + 1 : width;
        this.height = height % 2 == 0 ? height + 1 : height;
        this.grid = new int[this.height][this.width];
    }
    
    /**
     * Generates a random labyrinth using a depth-first search algorithm.
     */
    public void generate() {
        // Initialize grid with walls
        for (int z = 0; z < height; z++) {
            for (int x = 0; x < width; x++) {
                grid[z][x] = WALL;
            }
        }
        
        // Start at a random odd position
        int startX = random.nextInt(width / 2) * 2 + 1;
        int startZ = random.nextInt(height / 2) * 2 + 1;
        
        // Mark as path
        grid[startZ][startX] = PATH;
        
        // Use depth-first search to carve paths
        Stack<int[]> stack = new Stack<>();
        stack.push(new int[]{startX, startZ});
        
        while (!stack.isEmpty()) {
            int[] current = stack.peek();
            int x = current[0];
            int z = current[1];
            
            // Find unvisited neighbors
            int[][] neighbors = getUnvisitedNeighbors(x, z);
            
            if (neighbors.length > 0) {
                // Choose a random neighbor
                int[] next = neighbors[random.nextInt(neighbors.length)];
                int nextX = next[0];
                int nextZ = next[1];
                
                // Remove wall between current and next
                grid[(z + nextZ) / 2][(x + nextX) / 2] = PATH;
                
                // Mark next as path
                grid[nextZ][nextX] = PATH;
                
                // Push next to stack
                stack.push(new int[]{nextX, nextZ});
            } else {
                // Backtrack
                stack.pop();
            }
        }
        
        // Set start position
        this.startX = startX;
        this.startZ = startZ;
        grid[startZ][startX] = START;
        
        // Set exit position (farthest from start)
        setExitPosition();
    }
    
    /**
     * Gets unvisited neighbors of the specified position.
     *
     * @param x the x-coordinate
     * @param z the z-coordinate
     * @return an array of unvisited neighbors
     */
    private int[][] getUnvisitedNeighbors(int x, int z) {
        // Possible directions: right, down, left, up
        int[][] directions = {{2, 0}, {0, 2}, {-2, 0}, {0, -2}};
        
        // Find unvisited neighbors
        Stack<int[]> neighbors = new Stack<>();
        
        for (int[] dir : directions) {
            int nextX = x + dir[0];
            int nextZ = z + dir[1];
            
            // Check if within bounds and unvisited
            if (nextX > 0 && nextX < width - 1 && nextZ > 0 && nextZ < height - 1
                    && grid[nextZ][nextX] == WALL) {
                neighbors.push(new int[]{nextX, nextZ});
            }
        }
        
        // Convert to array
        int[][] result = new int[neighbors.size()][2];
        for (int i = 0; i < result.length; i++) {
            result[i] = neighbors.get(i);
        }
        
        return result;
    }
    
    /**
     * Sets the exit position at the farthest point from the start.
     */
    private void setExitPosition() {
        // Use breadth-first search to find the farthest point
        boolean[][] visited = new boolean[height][width];
        int[][] distance = new int[height][width];
        
        // Initialize
        for (int z = 0; z < height; z++) {
            for (int x = 0; x < width; x++) {
                visited[z][x] = false;
                distance[z][x] = 0;
            }
        }
        
        // Start BFS from start position
        java.util.Queue<int[]> queue = new java.util.LinkedList<>();
        queue.add(new int[]{startX, startZ});
        visited[startZ][startX] = true;
        
        // Possible directions: right, down, left, up
        int[][] directions = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}};
        
        int maxDistance = 0;
        int maxX = startX;
        int maxZ = startZ;
        
        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int x = current[0];
            int z = current[1];
            
            // Check if this is the farthest point so far
            if (distance[z][x] > maxDistance && grid[z][x] == PATH) {
                maxDistance = distance[z][x];
                maxX = x;
                maxZ = z;
            }
            
            // Visit neighbors
            for (int[] dir : directions) {
                int nextX = x + dir[0];
                int nextZ = z + dir[1];
                
                // Check if within bounds, not visited, and is a path
                if (nextX >= 0 && nextX < width && nextZ >= 0 && nextZ < height
                        && !visited[nextZ][nextX] && grid[nextZ][nextX] != WALL) {
                    visited[nextZ][nextX] = true;
                    distance[nextZ][nextX] = distance[z][x] + 1;
                    queue.add(new int[]{nextX, nextZ});
                }
            }
        }
        
        // Set exit position
        exitX = maxX;
        exitZ = maxZ;
        grid[exitZ][exitX] = EXIT;
    }
    
    /**
     * Builds the 3D representation of the labyrinth.
     */
    public void build() {
        // Create floor
        Entity floor = entityBuilder()
                .type(FLOOR)
                .at(-CELL_SIZE, CELL_SIZE, -CELL_SIZE)
                .view(new Box(width * CELL_SIZE + 2 * CELL_SIZE, 0.1, height * CELL_SIZE + 2 * CELL_SIZE))
                .buildAndAttach();
        
        // Create ceiling
        Entity ceiling = entityBuilder()
                .type(CEILING)
                .at(-CELL_SIZE, -WALL_HEIGHT, -CELL_SIZE)
                .view(new Box(width * CELL_SIZE + 2 * CELL_SIZE, 0.1, height * CELL_SIZE + 2 * CELL_SIZE))
                .buildAndAttach();
        
        // Create walls
        Random random = new Random();
        String[] wallTextures = {"wall-1.png", "wall-2.png"};
        
        for (int z = 0; z < height; z++) {
            for (int x = 0; x < width; x++) {
                if (grid[z][x] == WALL) {
                    // Select random texture
                    String randomTexture = wallTextures[random.nextInt(wallTextures.length)];
                    
                    // Create wall material with random texture
                    PhongMaterial wallMaterial = new PhongMaterial();
                    try {
                        Image textureImage = new Image(getClass().getResourceAsStream("/assets/textures/" + randomTexture));
                        wallMaterial.setDiffuseMap(textureImage);
                    } catch (Exception e) {
                        // Fallback to gray color if texture loading fails
                        wallMaterial.setDiffuseColor(Color.GRAY);
                    }
                    
                    // Create wall box with material
                    Box wallBox = new Box(CELL_SIZE, WALL_HEIGHT, CELL_SIZE);
                    wallBox.setMaterial(wallMaterial);
                    
                    // Create wall
                    Entity wall = entityBuilder()
                            .type(LabyrinthGame.EntityType.WALL)
                            .at(x * CELL_SIZE, 0, z * CELL_SIZE)
                            .viewWithBBox(wallBox)
                            .collidable()
                            .buildAndAttach();
                } else if (grid[z][x] == EXIT) {
                    // Create exit marker
                    PhongMaterial exitMaterial = new PhongMaterial();
                    exitMaterial.setDiffuseColor(Color.GREEN);
                    
                    Box exitBox = new Box(CELL_SIZE, 0.1, CELL_SIZE);
                    exitBox.setMaterial(exitMaterial);
                    
                    Entity exit = entityBuilder()
                            .type(LabyrinthGame.EntityType.EXIT)
                            .at(x * CELL_SIZE, CELL_SIZE - 0.05, z * CELL_SIZE)
                            .view(exitBox)
                            .with("exitX", x)
                            .with("exitZ", z)
                            .collidable()
                            .buildAndAttach();
                }
            }
        }
    }
    
    /**
     * Gets the x-coordinate of the start position in the 3D world.
     *
     * @return the x-coordinate of the start position
     */
    public double getStartX() {
        return startX * CELL_SIZE;
    }
    
    /**
     * Gets the z-coordinate of the start position in the 3D world.
     *
     * @return the z-coordinate of the start position
     */
    public double getStartZ() {
        return startZ * CELL_SIZE;
    }
    
    /**
     * Gets the x-coordinate of the exit position in the 3D world.
     *
     * @return the x-coordinate of the exit position
     */
    public double getExitX() {
        return exitX * CELL_SIZE;
    }
    
    /**
     * Gets the z-coordinate of the exit position in the 3D world.
     *
     * @return the z-coordinate of the exit position
     */
    public double getExitZ() {
        return exitZ * CELL_SIZE;
    }
}