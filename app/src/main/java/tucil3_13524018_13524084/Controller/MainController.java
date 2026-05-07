package tucil3_13524018_13524084.Controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.UnaryOperator;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.WindowEvent;
import tucil3_13524018_13524084.Animation.AnimationStep;
import tucil3_13524018_13524084.Core.Board;
import tucil3_13524018_13524084.Core.Direction;
import tucil3_13524018_13524084.Core.Player;
import tucil3_13524018_13524084.Core.Tile;
import tucil3_13524018_13524084.Core.TileType;
import tucil3_13524018_13524084.FileReader.FileIO;
import tucil3_13524018_13524084.GUI.BoardGUI;
import tucil3_13524018_13524084.Animation.PlayerMoveAnimation;
import tucil3_13524018_13524084.GameEventException.GameOverException;
import tucil3_13524018_13524084.Solver.AStarSolver;
import tucil3_13524018_13524084.Solver.GBFSSolver;
import tucil3_13524018_13524084.Solver.Solver;
import tucil3_13524018_13524084.Solver.UCSSolver;
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
    private Label boardError;

    // Tile costs
    @FXML
    private VBox tileCostsVbox;
    @FXML
    private TextArea tileCostsInput;
    @FXML
    private Label tileCostsError;

    // File input
    @FXML
    private Button addFromFile;
    @FXML
    private Label fileError;

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
    @FXML
    private Label iterationLabel;
    @FXML
    private Label executionTimeLabel;

    // Play
    @FXML
    private VBox playingVbox;

    // GUI
    private BoardGUI boardGUI;
    private ScheduledThreadPoolExecutor drawingThreadExecutor;

    private GameController gameController;
    private AlgorithmController algorithmController;

    @FXML
    private void initialize() {
        boardGUI = new BoardGUI(createDefaultGraph(), mainCanvas.getWidth(), mainCanvas.getHeight());
        gameController = new GameController(boardGUI, mainCanvas);
        algorithmController = new AlgorithmController(boardGUI, mainCanvas);

        UnaryOperator<TextFormatter.Change> filter = change -> {
            String text = change.getControlNewText();
            if (text.matches("\\d*")) { // Only digits (0-9)
                return change;
            }
            return null; // Reject the change
        };

        boardRowInput.setTextFormatter(new TextFormatter<>(filter));
        boardColumnInput.setTextFormatter(new TextFormatter<>(filter));

        boardRowInput.setText("7");
        boardRowInput.textProperty().addListener((observable, oldValue, newValue) -> {
            updateBoard();
        });
        boardColumnInput.setText("7");
        boardColumnInput.textProperty().addListener((observable, oldValue, newValue) -> {
            updateBoard();
        });
        boardInput.setText("""
                XXXXXXX
                X0****X
                X**X**X
                X****OX
                X1***LX
                XZ**X*X
                XXXXXXX
                """);
        boardInput.textProperty().addListener((observable, oldValue, newValue) -> {
            updateBoard();
        });
        tileCostsInput.setText("""
                999 999 999 999 999 999 999
                999 3   5   2   8   1   999
                999 7   4   999 6   9   999
                999 2   8   3   5   4   999
                999 6   1   7   2   999 999
                999 9   3   4   999 8   999
                999 999 999 999 999 999 999
                """);
        tileCostsInput.textProperty().addListener((observable, oldValue, newValue) -> {
            updateBoard();
        });

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
                algorithmController.setSpeedMultiplier(multiplier);
            });
        }

        drawingThreadExecutor = new ScheduledThreadPoolExecutor(1);
        drawingThreadExecutor.scheduleWithFixedDelay(() -> {
            Platform.runLater(() -> {
                boardGUI.draw(mainCanvas.getGraphicsContext2D());

                if (algorithmController.getMaxProgress() == 0) {

                } else {
                    iterationLabel.setText("Iteration: " + algorithmController.getAnimationProgress() + "/"
                            + algorithmController.getMaxProgress());
                }
            });
        }, 0, 16, TimeUnit.MILLISECONDS);

        Platform.runLater(() -> {
            mainCanvas.requestFocus();
            mainCanvas.getScene().getWindow().addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, (e) -> {
                drawingThreadExecutor.shutdown();
            });
            mainCanvas.getScene().setOnMousePressed(event -> {
                mainCanvas.requestFocus();
            });
        });
    }

    private void validateBoard() throws IllegalArgumentException {
        var boardInputLines = boardInput.getText().lines().collect(Collectors.toList());
        int actualRowCount = 0;
        for (String line : boardInputLines) {
            if (!line.trim().isEmpty()) {
                actualRowCount++;
            }
        }
        if (!Integer.toString(actualRowCount).equals(boardRowInput.getText())) {
            throw new IllegalArgumentException(
                    "Board row count is not correct. Expected: " + boardRowInput.getText() +
                            ". Got: " + actualRowCount);
        }
        int row = 0;
        for (String line : boardInputLines) {
            row++;
            if (!Integer.toString(line.length()).equals(boardColumnInput.getText())) {
                throw new IllegalArgumentException(
                        "Column count in row " + row + " is not correct. Expected: " + boardColumnInput.getText() +
                                ". Got: " + line.length());
            }
        }
    }

    private void validateTileCosts() throws IllegalArgumentException {
        var tileCostsInputLines = tileCostsInput.getText().lines().collect(Collectors.toList());
        int actualRowCount = 0;
        for (String line : tileCostsInputLines) {
            if (!line.trim().isEmpty()) {
                actualRowCount++;
            }
        }
        if (!Integer.toString(actualRowCount).equals(boardRowInput.getText())) {
            throw new IllegalArgumentException(
                    "Tile costs row count is not correct. Expected: " + boardRowInput.getText() +
                            ". Got: " + actualRowCount);
        }
        int row = 0;
        for (String line : tileCostsInputLines) {
            row++;
            int length = line.split("\\s+").length;
            if (!Integer.toString(length).equals(boardColumnInput.getText())) {
                throw new IllegalArgumentException(
                        "Column size in row " + row + " is not correct. Expected: " + boardColumnInput.getText() +
                                ". Got: " + length);
            }
        }
    }

    private void updateBoard() {
        fileError.setManaged(false);
        fileError.setVisible(false);

        try {
            validateBoard();
        } catch (Exception e) {
            boardError.setManaged(true);
            boardError.setVisible(true);
            boardError.setText(e.getMessage());
            return;
        }
        boardError.setManaged(false);
        boardError.setVisible(false);

        try {
            validateTileCosts();
        } catch (Exception e) {
            tileCostsError.setManaged(true);
            tileCostsError.setVisible(true);
            tileCostsError.setText(e.getMessage());
            return;
        }
        tileCostsError.setManaged(false);
        tileCostsError.setVisible(false);

        String sizeInput = boardRowInput.getText() + " " + boardColumnInput.getText();
        boardGUI.setBoard(FileIO.readInput(sizeInput + "\n" + boardInput.getText() + "\n" + tileCostsInput.getText()));
    }

    private Board createDefaultGraph() {
        return FileIO.readInput("""
                    7 7
                    XXXXXXX
                    X0****X
                    X**X**X
                    X****OX
                    X1***LX
                    XZ**X*X
                    XXXXXXX
                    999 999 999 999 999 999 999
                    999 3   5   2   8   1   999
                    999 7   4   999 6   9   999
                    999 2   8   3   5   4   999
                    999 6   1   7   2   999 999
                    999 9   3   4   999 8   999
                    999 999 999 999 999 999 999
                """);
    }

    @FXML
    private void handleAddFromFile(ActionEvent event) {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialDirectory(new File("../app/data/"));
            fileChooser.setTitle("Load File");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Text Files", "*.txt"),
                    new FileChooser.ExtensionFilter("All Files", "*.*"));
            File selectedFile = fileChooser.showOpenDialog(addFromFile.getScene().getWindow());
            if (selectedFile != null) {
                String content = Files.readString(selectedFile.toPath());
                Board newBoard = FileIO.readInput(content);
                boardGUI.setBoard(newBoard);
            }
        } catch (Exception e) {
            fileError.setManaged(true);
            fileError.setVisible(true);
            fileError.setText(e.getMessage());
        }

    }

    @FXML
    private void handleExecuteAlgorithm() {
        gameController.setPlaying(false);
        boardGUI.resetBoard();

        playingVbox.setManaged(false);
        playingVbox.setVisible(false);

        executionVbox.setManaged(true);
        executionVbox.setVisible(true);

        Solver solver = null;
        switch (algorithmCombo.getValue()) {
            case "A* Search":
                solver = new AStarSolver(boardGUI.getBoard());
                break;
            case "Uniform Cost Search":
                solver = new UCSSolver(boardGUI.getBoard());
                break;
            case "Greedy Best First Search":
                solver = new GBFSSolver(boardGUI.getBoard());
                break;

            default:
                solver = new AStarSolver(boardGUI.getBoard());
                break;
        }

        List<Direction> solution = solver.solve();
        if (solution != null) {
            Board board = boardGUI.getBoard();
            board.resetBoard();
            Player player = boardGUI.getBoard().getPlayer();
            List<AnimationStep> animationSteps = new ArrayList<>();
            for (Direction direction : solution) {
                int initialX = player.getXCoords();
                int initialY = player.getYCoords();
                try {
                    board.movePlayer(direction);
                } catch (Exception e) {
                    System.out.println(e);
                }
                int finalX = player.getXCoords();
                int finalY = player.getYCoords();
                PlayerMoveAnimation moveAnimation = new PlayerMoveAnimation(boardGUI.getPlayerGUI(), initialX, initialY,
                        finalX, finalY, 10);
                animationSteps.add(moveAnimation);
            }
            algorithmController.setAnimation(animationSteps);
            board.resetBoard();
        } else {
            List<AnimationStep> animationSteps = new ArrayList<>();
            algorithmController.setAnimation(animationSteps);
        }
    }

    @FXML
    private void handleClearSolution() {
        gameController.setPlaying(true);
        algorithmController.pause();
        algorithmController.clearAnimation();
        boardGUI.resetBoard();

        playingVbox.setManaged(true);
        playingVbox.setVisible(true);

        executionVbox.setManaged(false);
        executionVbox.setVisible(false);
    }

    @FXML
    private void handlePause() {
        algorithmController.pause();
    }

    @FXML
    private void handlePlayForward() {
        algorithmController.playForward();
    }

    @FXML
    private void handlePlayBackward() {
        algorithmController.playBackward();
    }

    @FXML
    private void handleNextStep() {
        algorithmController.nextStep();
    }

    @FXML
    private void handlePreviousStep() {
        algorithmController.previousStep();
    }
}
