package com.kayar.yetanotherlabyrinth.labyrinth.ui;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.dsl.FXGL;
import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

/**
 * Main menu for the 3D Labyrinth game.
 */
public class LabyrinthMainMenu extends FXGLMenu {

    /**
     * Creates a new main menu.
     */
    public LabyrinthMainMenu() {
        super(MenuType.MAIN_MENU);
        
        // Create background
        Rectangle background = new Rectangle(FXGL.getAppWidth(), FXGL.getAppHeight());
        background.setFill(Color.BLACK);
        
        // Create title
        Text title = FXGL.getUIFactoryService().newText("3D LABYRINTH", Color.WHITE, 48);
        title.setEffect(new DropShadow(10, Color.DARKGREEN));
        
        // Create menu items
        MenuButton btnPlay = new MenuButton("PLAY");
        btnPlay.setOnAction(e -> fireNewGame());
        
        MenuButton btnInstructions = new MenuButton("INSTRUCTIONS");
        btnInstructions.setOnAction(e -> showInstructions());
        
        MenuButton btnExit = new MenuButton("EXIT");
        btnExit.setOnAction(e -> fireExit());
        
        // Create menu container
        VBox menuBox = new VBox(15, title, btnPlay, btnInstructions, btnExit);
        menuBox.setAlignment(Pos.CENTER);
        menuBox.setTranslateX(FXGL.getAppWidth() / 2.0 - 100);
        menuBox.setTranslateY(FXGL.getAppHeight() / 2.0 - 100);
        
        // Add to root
        getContentRoot().getChildren().addAll(background, menuBox);
    }
    
    /**
     * Shows the instructions screen.
     */
    private void showInstructions() {
        // Create instructions text
        Text instructions = FXGL.getUIFactoryService().newText(
                "CONTROLS:\n\n" +
                "W - Move Forward\n" +
                "S - Move Backward\n" +
                "A - Strafe Left\n" +
                "D - Strafe Right\n" +
                "SPACE - Jump\n" +
                "C - Crouch\n" +
                "MOUSE - Look Around\n\n" +
                "GOAL:\n\n" +
                "Find the exit (highlighted in green) to advance to the next level.",
                Color.WHITE, 20);
        
        // Create background
        Rectangle background = new Rectangle(FXGL.getAppWidth(), FXGL.getAppHeight());
        background.setFill(Color.BLACK);
        
        // Create back button
        MenuButton btnBack = new MenuButton("BACK");
        btnBack.setOnAction(e -> {
            getContentRoot().getChildren().clear();
            getContentRoot().getChildren().addAll(
                    new Rectangle(FXGL.getAppWidth(), FXGL.getAppHeight(), Color.BLACK),
                    new VBox(15, 
                            FXGL.getUIFactoryService().newText("3D LABYRINTH", Color.WHITE, 48),
                            new MenuButton("PLAY", () -> fireNewGame()),
                            new MenuButton("INSTRUCTIONS", () -> showInstructions()),
                            new MenuButton("EXIT", () -> fireExit())
                    )
            );
        });
        
        // Create container
        VBox box = new VBox(20, instructions, btnBack);
        box.setAlignment(Pos.CENTER);
        box.setTranslateX(FXGL.getAppWidth() / 2.0 - 200);
        box.setTranslateY(FXGL.getAppHeight() / 2.0 - 200);
        
        // Update root
        getContentRoot().getChildren().clear();
        getContentRoot().getChildren().addAll(background, box);
    }
    
    /**
     * Custom menu button class.
     */
    private static class MenuButton extends javafx.scene.control.Button {
        
        /**
         * Creates a new menu button with the specified text.
         *
         * @param text the button text
         */
        public MenuButton(String text) {
            super(text);
            setStyle("-fx-background-color: #111111; -fx-text-fill: white; -fx-font-size: 20px; -fx-padding: 10px 20px;");
            setPrefWidth(200);
            
            setOnMouseEntered(e -> setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-font-size: 20px; -fx-padding: 10px 20px;"));
            setOnMouseExited(e -> setStyle("-fx-background-color: #111111; -fx-text-fill: white; -fx-font-size: 20px; -fx-padding: 10px 20px;"));
        }
        
        /**
         * Creates a new menu button with the specified text and action.
         *
         * @param text the button text
         * @param action the button action
         */
        public MenuButton(String text, Runnable action) {
            this(text);
            setOnAction(e -> action.run());
        }
    }
}