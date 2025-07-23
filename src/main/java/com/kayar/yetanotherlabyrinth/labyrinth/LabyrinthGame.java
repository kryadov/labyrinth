package com.kayar.yetanotherlabyrinth.labyrinth;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.Camera3D;
import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.SceneFactory;
import com.almasb.fxgl.entity.Entity;
import com.kayar.yetanotherlabyrinth.labyrinth.ui.LabyrinthMainMenu;
import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * Main game class for the 3D Labyrinth game.
 * Handles game initialization, configuration, and game loop management.
 */
public class LabyrinthGame extends GameApplication {

    // Game entity types
    public enum EntityType {
        PLAYER, WALL, FLOOR, CEILING, EXIT
    }

    // Game variables
    private int currentLevel = 1;
    private boolean isGameOver = false;
    private Camera3D camera3D;
    
    // Sound variables
    private boolean isWalkingSoundPlaying = false;
    
    // UI elements
    private Text levelText;

    /**
     * Initializes game settings.
     *
     * @param settings the game settings
     */
    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.setTitle("3D Labyrinth");
        settings.setVersion("1.0");
        settings.setMainMenuEnabled(true);
        settings.setGameMenuEnabled(true);
        settings.setFullScreenAllowed(true);
        settings.setManualResizeEnabled(false);
        settings.set3D(true);
        settings.setSceneFactory(new SceneFactory() {
            @Override
            public FXGLMenu newMainMenu() {
                return new LabyrinthMainMenu();
            }
        });
    }

    /**
     * Initializes the game using Camera3DSample approach.
     */
    @Override
    protected void initGame() {
        camera3D = getGameScene().getCamera3D();

        // place the camera at origin (by default the camera is moved slightly back)
        camera3D.getTransform().setZ(0);

        getGameScene().setBackgroundColor(Color.DARKCYAN);

        getGameScene().setFPSCamera(true);
        getGameScene().setCursorInvisible();

        getGameWorld().addEntityFactory(new LabyrinthFactory());
        
        // Load the first level
        loadLevel(currentLevel);
    }

    /**
     * Initializes the input handlers using Camera3DSample approach.
     */
    @Override
    protected void initInput() {
        // Movement with walking sound
        onKey(KeyCode.W, () -> {
            camera3D.moveForward();
            startWalkingSound();
        });
        onKey(KeyCode.S, () -> {
            camera3D.moveBack();
            startWalkingSound();
        });
        onKey(KeyCode.A, () -> {
            camera3D.moveLeft();
            startWalkingSound();
        });
        onKey(KeyCode.D, () -> {
            camera3D.moveRight();
            startWalkingSound();
        });


        onKey(KeyCode.L, () -> {
            getGameController().exit();
        });
    }

    /**
     * Initializes the UI elements.
     */
    @Override
    protected void initUI() {
        // Add level indicator
        levelText = getUIFactoryService().newText("Level: " + currentLevel, Color.WHITE, 24);
        levelText.setTranslateX(20);
        levelText.setTranslateY(30);
        
        getGameScene().addUINode(levelText);
    }
    
    /**
     * Called on each game update tick.
     *
     * @param tpf time per frame
     */
    @Override
    protected void onUpdate(double tpf) {
        // Update level text if needed
        if (levelText != null) {
            levelText.setText("Level: " + currentLevel);
        }
    }

    /**
     * Loads a level with the specified level number.
     *
     * @param levelNumber the level number to load
     */
    private void loadLevel(int levelNumber) {
        // Clear existing level
        getGameWorld().getEntitiesByType(EntityType.WALL, EntityType.FLOOR, 
                                         EntityType.CEILING, EntityType.EXIT)
                      .forEach(Entity::removeFromWorld);
        
        // Create new labyrinth
        LabyrinthGenerator generator = new LabyrinthGenerator(20, 20); // 20x20 grid
        generator.generate();
        
        // Position camera at start location
        camera3D.getTransform().setX(generator.getStartX());
        camera3D.getTransform().setY(0);
        camera3D.getTransform().setZ(generator.getStartZ());
        
        // Build the labyrinth
        generator.build();
        
        // Update level text
        if (levelText != null) {
            levelText.setText("Level: " + currentLevel);
        }
        
        // Ensure input processing is enabled for the new level
        getInput().setProcessInput(true);
        
        // Display level start message and ensure focus
        Platform.runLater(() -> {
            getNotificationService().pushNotification("Level " + currentLevel + " - Find the exit!");
            // Request focus to ensure input works after level load
            getGameScene().getRoot().requestFocus();
        });
    }

    /**
     * Starts playing the walking sound if not already playing.
     */
    private void startWalkingSound() {
        if (!isWalkingSoundPlaying) {
            isWalkingSoundPlaying = true;
            try {
                var walkSound = getAssetLoader().loadSound("walk.wav");
                getAudioPlayer().playSound(walkSound);
            } catch (Exception e) {
                // If sound loading fails, just continue without sound
                System.err.println("Could not load walking sound: " + e.getMessage());
            }
        }
    }

    /**
     * Stops playing the walking sound if currently playing.
     */
    private void stopWalkingSound() {
        if (isWalkingSoundPlaying) {
            isWalkingSoundPlaying = false;
            try {
                getAudioPlayer().stopAllSounds();
            } catch (Exception e) {
                // If stopping sound fails, just continue
                System.err.println("Could not stop walking sound: " + e.getMessage());
            }
        }
    }

    /**
     * Advances towthe next level.
     */
    public void nextLevel() {
        currentLevel++;
        loadLevel(currentLevel);
    }

    /**
     * Main method to launch the application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}