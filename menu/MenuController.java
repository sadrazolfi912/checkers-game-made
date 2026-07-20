package menu;

import javax.swing.SwingUtilities;

import checkers.Main;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

/*
 * Controls the main menu.
 * Initializes the background video, sound, custom cursor,
 * and handles menu actions.
 */
public class MenuController implements Initializable {

    @FXML
    private StackPane rootPane;

    @FXML
    private MediaView videoView;

    @FXML
    private VBox menuBox;

    private MediaPlayer videoPlayer;
    private MediaPlayer soundPlayer;
    private ImageCursor customCursor;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupCursor();
        setupMenuInitialState();
        setupBackgroundMedia();
        setupBackgroundSound();
    }

    // Hide menu until the intro finishes.
    private void setupMenuInitialState() {
        menuBox.setVisible(false);
        menuBox.setOpacity(0.0);
    }

    // Load the custom mouse cursor.
    private void setupCursor() {
        try {
            Image cursorImage = new Image(
                    getClass().getResourceAsStream("/resources/mouse.png")
            );

            customCursor = new ImageCursor(
                    cursorImage,
                    cursorImage.getWidth() / 2.0,
                    cursorImage.getHeight() / 2.0
            );

            rootPane.sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene != null) {
                    newScene.setCursor(customCursor);
                }
            });

        } catch (Exception e) {
            rootPane.sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene != null) {
                    newScene.setCursor(Cursor.DEFAULT);
                }
            });
        }
    }

    // Load and play the background video.
    private void setupBackgroundMedia() {
        try {
            Media media = new Media(
                    getClass().getResource("/resources/background.mp4").toExternalForm()
            );

            videoPlayer = new MediaPlayer(media);
            videoPlayer.setAutoPlay(true);
            videoPlayer.setCycleCount(1);
            videoPlayer.setVolume(0.0);

            videoView.setMediaPlayer(videoPlayer);
            videoView.setPreserveRatio(false);

            videoPlayer.setOnReady(() -> {
                videoView.fitWidthProperty().bind(rootPane.widthProperty());
                videoView.fitHeightProperty().bind(rootPane.heightProperty());
            });

            videoPlayer.setOnEndOfMedia(() -> {
                freezeLastFrame();
                showMenuButtons();
            });

            videoPlayer.setOnError(() -> {
                showMenuButtons();
            });

        } catch (Exception e) {
            showMenuButtons();
        }
    }

    // Play the menu background music.
    private void setupBackgroundSound() {
        try {
            Media sound = new Media(
                    getClass().getResource("/resources/sound.mp3").toExternalForm()
            );

            soundPlayer = new MediaPlayer(sound);
            soundPlayer.setCycleCount(1);
            soundPlayer.setVolume(1.0);
            soundPlayer.play();

        } catch (Exception e) {
            // Ignore audio loading errors.
        }
    }

    // Keep the last video frame visible.
    private void freezeLastFrame() {
        try {
            if (videoPlayer != null) {
                videoPlayer.pause();
                videoPlayer.seek(videoPlayer.getTotalDuration());
            }
        } catch (Exception e) {
            // Ignore playback errors.
        }
    }

    // Fade in the menu buttons.
    private void showMenuButtons() {
        Platform.runLater(() -> {
            menuBox.setVisible(true);
            menuBox.setOpacity(0.0);

            FadeTransition fade = new FadeTransition(Duration.millis(700), menuBox);
            fade.setFromValue(0.0);
            fade.setToValue(1.0);
            fade.play();
        });
    }

    // Start the game.
    @FXML
    private void newGame() {
        stopMedia();

        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.close();

        SwingUtilities.invokeLater(() -> Main.main(new String[0]));
    }

    // Exit the application.
    @FXML
    private void exit() {
        stopMedia();

        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.close();
        Platform.exit();
    }

    // Release media resources.
    private void stopMedia() {
        try {
            if (videoPlayer != null) {
                videoPlayer.stop();
                videoPlayer.dispose();
            }
        } catch (Exception ignored) {
        }

        try {
            if (soundPlayer != null) {
                soundPlayer.stop();
                soundPlayer.dispose();
            }
        } catch (Exception ignored) {
        }
    }
}