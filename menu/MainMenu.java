package menu;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/*
 * Entry point of the JavaFX menu.
 * Loads the FXML layout, configures the main window,
 * and displays the game menu.
 */
public class MainMenu extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        // Load the menu layout.
        Parent root = FXMLLoader.load(getClass().getResource("/menu/menu.fxml"));

        // Create the main scene.
        Scene scene = new Scene(root, 1280, 720);

        // Configure the application window.
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.setTitle("Checkers Menu");

        // Load the application icon if available.
        try {
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/icon1.png")));
        } catch (Exception ignored) {
        }

        stage.setResizable(false);
        stage.show();
    }

    // Launch the JavaFX application.
    public static void main(String[] args) {
        launch(args);
    }
}