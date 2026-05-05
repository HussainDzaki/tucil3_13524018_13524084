package tucil3_13524018_13524084.GUI;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.Button;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.HBox;
import javafx.scene.control.Slider;

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
    // @FXML
    // private Label boardSizeError;

    // Board
    @FXML
    private VBox boardVbox;
    @FXML
    private TextArea boardInput;
    @FXML
    private Button boardHelp;
    // @FXML
    // private Label boardError;

    // Tile costs
    @FXML
    private VBox tileCostsVbox;
    @FXML
    private TextArea tileCostsInput;
    @FXML
    private Button tileCostsHelp;
    // @FXML
    // private Label tileCostsError;

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

    @FXML
    private void initialize() {

        
        if (speedSlider != null) {
            speedSlider.setSnapToTicks(true);
            speedSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                double multiplier = Math.pow(2, Math.round(newValue.doubleValue()));
                speedSlider.setValue(Math.round(newValue.doubleValue()));
                labelSpeedSlider.setText("Speed: (x" + multiplier + ")");
            });
        }
    }
}
