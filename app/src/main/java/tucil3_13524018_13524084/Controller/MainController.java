package tucil3_13524018_13524084.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.UnaryOperator;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.WindowEvent;
import tucil3_13524018_13524084.Core.Board;
import tucil3_13524018_13524084.Core.Direction;
import tucil3_13524018_13524084.Core.Tile;
import tucil3_13524018_13524084.Core.TileType;
import tucil3_13524018_13524084.FileReader.FileIO;
import tucil3_13524018_13524084.GUI.BoardGUI;
import tucil3_13524018_13524084.GUI.Animation.PlayerMoveAnimation;
import tucil3_13524018_13524084.GameEventException.GameOverException;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyCode;
import javafx.scene.control.TextArea;
import javafx.scene.control.Button;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.HBox;
import javafx.scene.control.Slider;
import javafx.scene.control.ComboBox;

public class MainController {
    @FXML
    private Label appLabel;

    // Board size
    @FXML
    private VBox boardSizeVbox;
    @FXML
    private TextField boardRowInput;
    @FXML
    private TextField boardColumnInput;
    @FXML
    private Label boardSizeError;

    // Board
    @FXML
    private VBox boardVbox;
    @FXML
    private TextArea boardInput;
    @FXML
    private Button boardHelp;
    @FXML
    private Label boardError;

    // Tile costs
    @FXML
    private VBox tileCostsVbox;
    @FXML
    private TextArea tileCostsInput;
    @FXML
    private Button tileCostsHelp;
    @FXML
    private Label tileCostsError;

    // File input
    @FXML
    private Button addFromFile;

    // Main Canvas
    @FXML
    private Canvas mainCanvas;

    // Playback
    @FXML
    private HBox playbackHbox;
    @FXML
    private Button prevButton;
    @FXML
    private Button reverseButton;
    @FXML
    private Button pauseButton;
    @FXML
    private Button playButton;
    @FXML
    private Button nextButton;
    @FXML
    private Label labelSpeedSlider;
    @FXML
    private Slider speedSlider;

    // Algorithm
    @FXML
    private ComboBox<String> algorithmCombo;
    @FXML
    private VBox executionVbox;
    @FXML
    private Button resetButton;
    @FXML
    private Button executeButton;

    // GUI
    private BoardGUI boardGUI;
    private ScheduledThreadPoolExecutor threadPoolExecutor;
    private ThreadPoolExecutor animationThreadExecutor;

    private GameController gameController;

    @FXML
    private void initialize() {
        boardGUI = new BoardGUI(createDefaultGraph(), mainCanvas.getWidth(), mainCanvas.getHeight());
        gameController = new GameController(boardGUI, mainCanvas);

        UnaryOperator<TextFormatter.Change> filter = change -> {
            String text = change.getControlNewText();
            if (text.matches("\\d*")) { // Only digits (0-9)
                return change;
            }
            return null; // Reject the change
        };

        boardRowInput.setTextFormatter(new TextFormatter<>(filter));
        boardColumnInput.setTextFormatter(new TextFormatter<>(filter));

        algorithmCombo.getItems().addAll(
                "Uniform Cost Search",
                "Greedy Best First Search",
                "A* Search");
        algorithmCombo.getSelectionModel().selectFirst();

        if (speedSlider != null) {
            speedSlider.setSnapToTicks(true);
            speedSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                double multiplier = Math.pow(2, Math.round(newValue.doubleValue()));
                speedSlider.setValue(Math.round(newValue.doubleValue()));
                labelSpeedSlider.setText("Speed: (x" + multiplier + ")");
            });
        }

        threadPoolExecutor = new ScheduledThreadPoolExecutor(1);
        threadPoolExecutor.scheduleWithFixedDelay(() -> {
            Platform.runLater(() -> {
                boardGUI.draw(mainCanvas.getGraphicsContext2D());
            });
        }, 0, 16, TimeUnit.MILLISECONDS);

        animationThreadExecutor = new ThreadPoolExecutor(1, 1, 2, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

        Platform.runLater(() -> {
            mainCanvas.requestFocus();
            mainCanvas.getScene().getWindow().addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, (e) -> {
                threadPoolExecutor.shutdownNow();
                animationThreadExecutor.shutdownNow();
            });
        });
    }

    private Board createDefaultGraph() {
        return FileIO.readInput("" +
                "7 7\n" +
                "XXXXXXX\n" +
                "X0****X\n" +
                "X**X**X\n" +
                "X****OX\n" +
                "X***1LX\n" +
                "XZ**X*X\n" +
                "XXXXXXX\n" +
                "999 999 999 999 999 999 999\n" +
                "999 3   5   2   8   1   999\n" +
                "999 7   4   999 6   9   999\n" +
                "999 2   8   3   5   4   999\n" +
                "999 6   1   7   2   999 999\n" +
                "999 9   3   4   999 8   999\n" +
                "999 999 999 999 999 999 999\n");
    }
}
