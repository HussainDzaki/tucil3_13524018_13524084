package tucil3_13524018_13524084.Controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.WindowEvent;
import tucil3_13524018_13524084.Animation.AnimationStep;
import tucil3_13524018_13524084.Animation.AnimationStepBundler;
import tucil3_13524018_13524084.Animation.LongAnimation;
import tucil3_13524018_13524084.Core.Board;
import tucil3_13524018_13524084.Core.Direction;
import tucil3_13524018_13524084.Core.Player;
import tucil3_13524018_13524084.FileReader.FileIO;
import tucil3_13524018_13524084.GUI.BoardGUI;
import tucil3_13524018_13524084.Animation.PlayerMoveAnimation;
import tucil3_13524018_13524084.Solver.AStarSolver;
import tucil3_13524018_13524084.Solver.GBFSSolver;
import tucil3_13524018_13524084.Solver.Solver;
import tucil3_13524018_13524084.Solver.UCSSolver;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
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

    // Play
    @FXML
    private VBox playingVbox;
    @FXML
    private Label playMessage;
    @FXML
    private Label currentCostLabel1;

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
    private Label currentCostLabel2;
    @FXML
    private Label executionTimeLabel;
    @FXML
    private Button saveButton;

    // GUI
    private BoardGUI boardGUI;
    private ScheduledThreadPoolExecutor drawingThreadExecutor;

    private GameController gameController;
    private AlgorithmController algorithmController;
    private AtomicLong playerCost = new AtomicLong(0);

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
                labelSpeedSlider.setText("Speed: (x" + (int) multiplier + ")");
                algorithmController.setSpeedMultiplier(multiplier);
            });
        }

        drawingThreadExecutor = new ScheduledThreadPoolExecutor(1);
        drawingThreadExecutor.scheduleWithFixedDelay(() -> {
            Platform.runLater(() -> {
                boardGUI.draw(mainCanvas.getGraphicsContext2D());

                if (algorithmController.getMaxProgress() == 0) {
                    iterationLabel.setText("No solution :( ");
                } else {
                    iterationLabel.setText("Iteration: " + algorithmController.getAnimationProgress() + "/"
                            + algorithmController.getMaxProgress());
                }
                playMessage.setText(gameController.getMessage());
                currentCostLabel1.setText("Total cost: " + boardGUI.getBoard().getPlayer().getTotalCost());
                currentCostLabel2.setText("Total cost: " + playerCost.get());
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
        handleClearSolution();
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
                boardRowInput.setText(Integer.toString(newBoard.getRowSize()));
                boardColumnInput.setText(Integer.toString(newBoard.getColumnSize()));
                boardInput.setText(newBoard.getBoardString());
                tileCostsInput.setText(newBoard.getCostString());
            }
        } catch (Exception e) {
            fileError.setManaged(true);
            fileError.setVisible(true);
            fileError.setText(e.getMessage());
        }
    }

    private Solver getSolver() {
        switch (algorithmCombo.getValue()) {
            case "A* Search":
                return new AStarSolver(boardGUI.getBoard());
            case "Uniform Cost Search":
                return new UCSSolver(boardGUI.getBoard());
            case "Greedy Best First Search":
                return new GBFSSolver(boardGUI.getBoard());
            default:
                return new AStarSolver(boardGUI.getBoard());
        }
    }

    @FXML
    private void handleExecuteAlgorithm() {
        gameController.setPlaying(false);
        boardGUI.resetBoard();
        playerCost.set(0);

        playingVbox.setManaged(false);
        playingVbox.setVisible(false);

        executionVbox.setManaged(true);
        executionVbox.setVisible(true);

        Solver solver = getSolver();

        long startTime = System.currentTimeMillis();
        List<Direction> solution = solver.solve();
        long endTime = System.currentTimeMillis();
        if (solution != null) {
            Board board = boardGUI.getBoard();
            board.resetBoard();
            Player player = boardGUI.getBoard().getPlayer();
            List<AnimationStep> animationSteps = new ArrayList<>();
            for (Direction direction : solution) {
                long initialCost = player.getTotalCost();
                int initialX = player.getXCoords();
                int initialY = player.getYCoords();
                try {
                    board.movePlayer(direction);
                } catch (Exception e) {
                    System.out.println(e);
                }
                long finalCost = player.getTotalCost();
                int finalX = player.getXCoords();
                int finalY = player.getYCoords();
                PlayerMoveAnimation moveAnimation = new PlayerMoveAnimation(boardGUI.getPlayerGUI(), initialX, initialY,
                        finalX, finalY, 10);
                LongAnimation costAnimation = new LongAnimation(playerCost, initialCost, finalCost);
                AnimationStepBundler bundler = new AnimationStepBundler(moveAnimation, costAnimation);
                animationSteps.add(bundler);
            }
            algorithmController.setAnimation(animationSteps);
            board.resetBoard();
            Platform.runLater(() -> {
                executionTimeLabel.setText("Finished in: " + (endTime - startTime) + "ms");
            });
        } else {
            List<AnimationStep> animationSteps = new ArrayList<>();
            algorithmController.setAnimation(animationSteps);
            executionTimeLabel.setText("Finished in: 0ms");
        }

        Button[] playbackButtons = new Button[] { prevButton, nextButton, playButton, reverseButton };
        Platform.runLater(() -> {
            pauseButton.getStyleClass().add("active");
            for (Button button : playbackButtons) {
                button.getStyleClass().removeAll("active");
            }
        });
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
        Button[] playbackButtons = new Button[] { prevButton, nextButton, playButton, reverseButton };
        Platform.runLater(() -> {
            pauseButton.getStyleClass().add("active");
            for (Button button : playbackButtons) {
                button.getStyleClass().removeAll("active");
            }
        });
    }

    @FXML
    private void handlePlayForward() {
        algorithmController.playForward();
        Button[] playbackButtons = new Button[] { prevButton, nextButton, reverseButton, pauseButton };
        Platform.runLater(() -> {
            playButton.getStyleClass().add("active");
            for (Button button : playbackButtons) {
                button.getStyleClass().removeAll("active");
            }
        });
    }

    @FXML
    private void handlePlayBackward() {
        algorithmController.playBackward();
        Button[] playbackButtons = new Button[] { prevButton, nextButton, playButton, pauseButton };
        Platform.runLater(() -> {
            reverseButton.getStyleClass().add("active");
            for (Button button : playbackButtons) {
                button.getStyleClass().removeAll("active");
            }
        });
    }

    @FXML
    private void handleNextStep() {
        algorithmController.nextStep();
        Button[] playbackButtons = new Button[] { prevButton, nextButton, playButton, reverseButton };
        Platform.runLater(() -> {
            pauseButton.getStyleClass().add("active");
            for (Button button : playbackButtons) {
                button.getStyleClass().removeAll("active");
            }
        });
    }

    @FXML
    private void handlePreviousStep() {
        algorithmController.previousStep();
        Button[] playbackButtons = new Button[] { prevButton, nextButton, playButton, reverseButton };
        Platform.runLater(() -> {
            pauseButton.getStyleClass().add("active");
            for (Button button : playbackButtons) {
                button.getStyleClass().removeAll("active");
            }
        });
    }

    @FXML
    private void handleSave() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save File");
        fileChooser.setInitialFileName("output.txt");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.txt"),
                new FileChooser.ExtensionFilter("All Files", "*.*"));
        File selectedFile = fileChooser.showSaveDialog(addFromFile.getScene().getWindow());
        if (selectedFile != null) {
            PrintStream originalOut = System.out;
            try {
                PrintStream newOut = new PrintStream(new FileOutputStream(selectedFile));
                System.setOut(newOut);
                Solver solver = getSolver();
                List<Direction> directions = solver.solve();
                Board board = boardGUI.getBoard();
                board.resetBoard();
                board.printBoard();
                System.out.println("Total Cost: " + board.getPlayer().getTotalCost());
                System.out.println();
                for (Direction direction : directions) {
                    board.movePlayer(direction);
                    board.printBoard();
                    System.out.println("Total Cost: " + board.getPlayer().getTotalCost());
                    System.out.println();
                }
                board.resetBoard();
                System.setOut(originalOut);
            } catch (Exception e) {
                System.setOut(originalOut);
                fileError.setManaged(true);
                fileError.setVisible(true);
                fileError.setText(e.getMessage());
            }
        }
    }
}
