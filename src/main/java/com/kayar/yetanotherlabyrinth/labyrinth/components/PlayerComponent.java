package com.kayar.yetanotherlabyrinth.labyrinth.components;

import com.almasb.fxgl.core.math.Vec3;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.kayar.yetanotherlabyrinth.labyrinth.LabyrinthGame;
import javafx.geometry.Point3D;
import javafx.scene.input.KeyCode;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * Component that handles player movement and actions.
 * Implements first-person controls with WASD for movement,
 * space for jumping, C for crouching, and mouse for looking around.
 */
public class PlayerComponent extends Component {
    // Movement settings
    private static final double MOVE_SPEED = 0.1;
    private static final double STRAFE_SPEED = 0.08;
    private static final double JUMP_FORCE = 0.3;
    private static final double CROUCH_HEIGHT = 0.9;
    private static final double NORMAL_HEIGHT = 1.8;
    private static final double GRAVITY = 0.01;
    private static final double MAX_FALL_SPEED = 0.5;
    
    // Acceleration settings
    private static final double ACCELERATION = 0.01;
    private static final double DECELERATION = 0.02;
    private static final double MAX_SPEED = 0.2;
    
    // Mouse look settings
    private static final double MOUSE_SENSITIVITY = 0.2;
    
    // Player state
    private boolean isJumping = false;
    private boolean isCrouching = false;
    private boolean isOnGround = true;
    
    // Movement velocity
    private double velocityX = 0;
    private double velocityY = 0;
    private double velocityZ = 0;
    
    // Look direction
    private double rotationX = 0; // Horizontal rotation (yaw)
    private double rotationY = 0; // Vertical rotation (pitch)
    
    // Reference to the game
    private LabyrinthGame game;
    
    /**
     * Called when the component is added to an entity.
     */
    @Override
    public void onAdded() {
        // Set up initial position
        entity.setX(0);
        entity.setY(0);
        entity.setZ(0);
        
        // Mouse look will be handled in the game scene
        // Cursor visibility is now controlled by the LabyrinthGame class
    }
    
    /**
     * Sets the game reference.
     *
     * @param game the game instance
     */
    public void setGame(LabyrinthGame game) {
        this.game = game;
    }
    
    /**
     * Called on each game update tick.
     *
     * @param tpf time per frame
     */
    @Override
    public void onUpdate(double tpf) {
        // Apply gravity
        if (!isOnGround) {
            velocityY += GRAVITY;
            if (velocityY > MAX_FALL_SPEED) {
                velocityY = MAX_FALL_SPEED;
            }
        } else {
            velocityY = 0;
        }
        
        // Apply velocity
        entity.translateX(velocityX);
        entity.translateY(velocityY);
        entity.translateZ(velocityZ);
        
        // Check for collisions
        checkCollisions();
        
        // Check if player has reached the exit
        checkExit();
        
        // Apply deceleration
        applyDeceleration();
    }
    
    /**
     * Checks for collisions with walls and other objects.
     */
    private void checkCollisions() {
        // Check for collisions with walls
        getGameWorld().getEntitiesByType(LabyrinthGame.EntityType.WALL)
                .forEach(wall -> {
                    if (entity.isColliding(wall)) {
                        // Move back to resolve collision
                        entity.translateX(-velocityX);
                        entity.translateZ(-velocityZ);
                        velocityX = 0;
                        velocityZ = 0;
                    }
                });
        
        // Check if player is on ground
        isOnGround = false;
        getGameWorld().getEntitiesByType(LabyrinthGame.EntityType.FLOOR)
                .forEach(floor -> {
                    if (Math.abs(entity.getBottomY() - floor.getY()) < 0.1) {
                        isOnGround = true;
                    }
                });
    }
    
    /**
     * Checks if the player has reached the exit.
     */
    private void checkExit() {
        getGameWorld().getEntitiesByType(LabyrinthGame.EntityType.EXIT)
                .forEach(exit -> {
                    // Calculate 2D distance (ignoring Y)
                    double dx = entity.getX() - exit.getX();
                    double dz = entity.getZ() - exit.getZ();
                    double distance = Math.sqrt(dx * dx + dz * dz);
                    
                    if (distance < 1.5 && game != null) {
                        // Player has reached the exit, load next level
                        game.nextLevel();
                    }
                });
    }
    
    /**
     * Applies deceleration to smooth out movement.
     */
    private void applyDeceleration() {
        // Apply deceleration to X velocity
        if (velocityX > 0) {
            velocityX -= DECELERATION;
            if (velocityX < 0) velocityX = 0;
        } else if (velocityX < 0) {
            velocityX += DECELERATION;
            if (velocityX > 0) velocityX = 0;
        }
        
        // Apply deceleration to Z velocity
        if (velocityZ > 0) {
            velocityZ -= DECELERATION;
            if (velocityZ < 0) velocityZ = 0;
        } else if (velocityZ < 0) {
            velocityZ += DECELERATION;
            if (velocityZ > 0) velocityZ = 0;
        }
    }
    
    /**
     * Moves the player forward.
     */
    public void moveForward() {
        // Calculate direction vector based on rotation
        double dirX = Math.sin(Math.toRadians(rotationX));
        double dirZ = Math.cos(Math.toRadians(rotationX));
        
        // Apply acceleration
        velocityX += dirX * ACCELERATION;
        velocityZ += dirZ * ACCELERATION;
        
        // Clamp to max speed
        double speed = Math.sqrt(velocityX * velocityX + velocityZ * velocityZ);
        if (speed > MAX_SPEED) {
            velocityX = (velocityX / speed) * MAX_SPEED;
            velocityZ = (velocityZ / speed) * MAX_SPEED;
        }
    }
    
    /**
     * Moves the player backward.
     */
    public void moveBackward() {
        // Calculate direction vector based on rotation
        double dirX = Math.sin(Math.toRadians(rotationX));
        double dirZ = Math.cos(Math.toRadians(rotationX));
        
        // Apply acceleration in opposite direction
        velocityX -= dirX * ACCELERATION;
        velocityZ -= dirZ * ACCELERATION;
        
        // Clamp to max speed
        double speed = Math.sqrt(velocityX * velocityX + velocityZ * velocityZ);
        if (speed > MAX_SPEED) {
            velocityX = (velocityX / speed) * MAX_SPEED;
            velocityZ = (velocityZ / speed) * MAX_SPEED;
        }
    }
    
    /**
     * Strafes the player left.
     */
    public void strafeLeft() {
        // Calculate strafe vector (perpendicular to look direction)
        double strafeX = Math.sin(Math.toRadians(rotationX - 90));
        double strafeZ = Math.cos(Math.toRadians(rotationX - 90));
        
        // Apply acceleration
        velocityX += strafeX * ACCELERATION;
        velocityZ += strafeZ * ACCELERATION;
        
        // Clamp to max speed
        double speed = Math.sqrt(velocityX * velocityX + velocityZ * velocityZ);
        if (speed > MAX_SPEED) {
            velocityX = (velocityX / speed) * MAX_SPEED;
            velocityZ = (velocityZ / speed) * MAX_SPEED;
        }
    }
    
    /**
     * Strafes the player right.
     */
    public void strafeRight() {
        // Calculate strafe vector (perpendicular to look direction)
        double strafeX = Math.sin(Math.toRadians(rotationX + 90));
        double strafeZ = Math.cos(Math.toRadians(rotationX + 90));
        
        // Apply acceleration
        velocityX += strafeX * ACCELERATION;
        velocityZ += strafeZ * ACCELERATION;
        
        // Clamp to max speed
        double speed = Math.sqrt(velocityX * velocityX + velocityZ * velocityZ);
        if (speed > MAX_SPEED) {
            velocityX = (velocityX / speed) * MAX_SPEED;
            velocityZ = (velocityZ / speed) * MAX_SPEED;
        }
    }
    
    /**
     * Makes the player jump.
     */
    public void jump() {
        if (isOnGround && !isJumping) {
            isJumping = true;
            isOnGround = false;
            velocityY = -JUMP_FORCE;
        }
    }
    
    /**
     * Makes the player start crouching.
     */
    public void startCrouch() {
        if (!isCrouching) {
            isCrouching = true;
            // Adjust player height
            entity.setScaleY(CROUCH_HEIGHT / NORMAL_HEIGHT);
        }
    }
    
    /**
     * Makes the player stop crouching.
     */
    public void endCrouch() {
        if (isCrouching) {
            isCrouching = false;
            // Restore player height
            entity.setScaleY(1.0);
        }
    }
    
    /**
     * Updates the player's rotation.
     *
     * @param rotX the horizontal rotation (yaw)
     * @param rotY the vertical rotation (pitch)
     */
    public void updateRotation(double rotX, double rotY) {
        this.rotationX = rotX;
        this.rotationY = rotY;
    }
}