package tucil3_13524018_13524084.GUI;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import tucil3_13524018_13524084.Core.Tile;
import tucil3_13524018_13524084.Core.TileType;

public class TileGUI implements Drawable {
    private Tile tile;
    private double width;
    private double height;

    private final Color COIN_COLOR = Color.web("#FDD303");
    private final Color TEXT_COLOR = Color.web("#2C3E50");

    public TileGUI(Tile tile, double width, double height) {
        this.tile = tile;
        setSize(width, height);
    }

    public void setSize(double width, double height) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("Width and height must not be negative");
        }
        this.width = width;
        this.height = height;
    }

    private Color getTileColor() {
        switch (tile.getType()) {
            case PATH:
                return Color.web("#E0F7FA");
            case GOAL:
                return Color.web("#FDD303");
            case OBSTACLE:
                return Color.web("#68B6E8");
            case LAVA:
                return Color.web("#FE5E39");
            default:
                return Color.web("#E0F7FA");
        }
    }

    public void draw(GraphicsContext gc) {
        double positionX = tile.getXCoords() * width;
        double positionY = tile.getYCoords() * height;

        gc.save();
        gc.setFill(Color.GRAY);
        gc.fillRect(positionX, positionY, width, height);
        gc.setFill(getTileColor());
        gc.fillRect(positionX + 0.5, positionY + 0.5, width - 1, height - 1);

        if (tile.getType() == TileType.COIN_NUMBER) {
            gc.setFill(COIN_COLOR);
            gc.fillOval(positionX + width * 0.1, positionY + height * 0.1, width * 0.8, height * 0.8);

            // Draw label centered in node
            double fontScale = 12 * height / 400;
            gc.setFill(TEXT_COLOR);
            gc.setFont(Font.font("Cascadia Code Regular", FontWeight.NORMAL, 12 * fontScale));

            // Center the text
            double textWidth = 7 * fontScale * Integer.toString(tile.getCoinSequence()).length();
            double textHeight = 10 * fontScale;
            double textX = positionX + width / 2 - textWidth / 2;
            double textY = positionY + height / 2 + textHeight / 2;

            gc.fillText(Integer.toString(tile.getCoinSequence()), textX, textY);
        } else if (tile.getType() != TileType.OBSTACLE && tile.getType() != TileType.LAVA) {
            // Draw label centered in node
            double fontScale = 12 * height / 400;
            gc.setFill(TEXT_COLOR);
            gc.setFont(Font.font("Cascadia Code Regular", FontWeight.NORMAL, 12 * fontScale));

            // Center the text
            double textWidth = 7 * fontScale * Integer.toString(tile.getTileCost()).length();
            double textHeight = 10 * fontScale;
            double textX = positionX + width / 2 - textWidth / 2;
            double textY = positionY + height / 2 + textHeight / 2;

            gc.fillText(Integer.toString(tile.getTileCost()), textX, textY);
        }

        gc.restore();
    }
}
