package com.kayar.yetanotherlabyrinth.labyrinth;

import com.almasb.fxgl.dsl.components.EffectComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.kayar.yetanotherlabyrinth.labyrinth.components.PlayerComponent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.kayar.yetanotherlabyrinth.labyrinth.LabyrinthGame.EntityType.*;

/**
 * Factory for creating game entities like player, walls, floor, ceiling, and exit.
 */
public class LabyrinthFactory implements EntityFactory {

    /**
     * Creates a player entity.
     *
     * @param data spawn data
     * @return the player entity
     */
    @Spawns("player")
    public Entity newPlayer(SpawnData data) {
        // Player physics settings
        double width = 0.8;
        double height = 1.8;
        double depth = 0.8;
        
        // Create player entity
        return entityBuilder(data)
                .type(PLAYER)
                .bbox(new HitBox(BoundingShape.box3D(width, height, depth)))
                .with(new PlayerComponent())
                .with(new EffectComponent())
                .collidable()
                .build();
    }

    /**
     * Creates a wall entity.
     *
     * @param data spawn data
     * @return the wall entity
     */
    @Spawns("wall")
    public Entity newWall(SpawnData data) {
        double size = data.get("size");
        double height = data.get("height");
        
        // Create wall material
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(Color.GRAY);
        
        // Create wall box
        Box box = new Box(size, height, size);
        box.setMaterial(material);
        
        // Create wall entity
        return entityBuilder(data)
                .type(WALL)
                .viewWithBBox(box)
                .collidable()
                .build();
    }

    /**
     * Creates a floor entity.
     *
     * @param data spawn data
     * @return the floor entity
     */
    @Spawns("floor")
    public Entity newFloor(SpawnData data) {
        double width = data.get("width");
        double depth = data.get("depth");
        
        // Create floor material
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(Color.DARKGRAY);
        
        // Create floor box
        Box box = new Box(width, 0.1, depth);
        box.setMaterial(material);
        
        // Create floor entity
        return entityBuilder(data)
                .type(FLOOR)
                .view(box)
                .collidable()
                .build();
    }

    /**
     * Creates a ceiling entity.
     *
     * @param data spawn data
     * @return the ceiling entity
     */
    @Spawns("ceiling")
    public Entity newCeiling(SpawnData data) {
        double width = data.get("width");
        double depth = data.get("depth");
        
        // Create ceiling material
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(Color.DARKGRAY);
        
        // Create ceiling box
        Box box = new Box(width, 0.1, depth);
        box.setMaterial(material);
        
        // Create ceiling entity
        return entityBuilder(data)
                .type(CEILING)
                .view(box)
                .collidable()
                .build();
    }

    /**
     * Creates an exit entity.
     *
     * @param data spawn data
     * @return the exit entity
     */
    @Spawns("exit")
    public Entity newExit(SpawnData data) {
        double size = data.get("size");
        
        // Create exit material
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(Color.GREEN);
        
        // Create exit cylinder
        Cylinder cylinder = new Cylinder(size / 2, 0.1);
        cylinder.setMaterial(material);
        
        // Create exit entity
        return entityBuilder(data)
                .type(EXIT)
                .view(cylinder)
                .collidable()
                .with("exitX", data.getX())
                .with("exitZ", data.getZ())
                .build();
    }
}