package tucil3_13524018_13524084.GUI;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import tucil3_13524018_13524084.Core.Player;

public class PlayerGUI implements Drawable {
    private double posX;
    private double posY;
    private double width;
    private double height;

    public PlayerGUI(Player player, double width, double height) {
        this.posX = player.getXCoords();
        this.posY = player.getYCoords();
        setSize(width, height);
    }

    public void setSize(double width, double height) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("Width and height must not be negative");
        }
        this.width = width;
        this.height = height;
    }

    public void setPosition(double x, double y) {
        this.posX = x;
        this.posY = y;
    }

    public double getPosX() {
        return posX;
    }
    public double getPosY() {
        return posY;
    }

    public void draw(GraphicsContext gc) {
        double positionX = posX * width;
        double positionY = posY * height;

        gc.save();
        gc.setFill(Color.GRAY);
        gc.fillRect(positionX, positionY, width, height);
        gc.setFill(Color.BLUE);
        gc.fillRect(positionX + 0.5, positionY + 0.5, width - 1, height - 1);
    }
}