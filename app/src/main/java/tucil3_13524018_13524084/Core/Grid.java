package tucil3_13524018_13524084.Core;

import tucil3_13524018_13524084.Core.Tile;
import tucil3_13524018_13524084.Core.TileType;

import java.util.List;
import java.util.ArrayList;

public class Grid {
    private Integer columnSize;
    private Integer rowSize;

    private List<Tile> tiles;

    public Grid() {
        this.columnSize = 0;
        this.rowSize = 0;
        this.tiles = new ArrayList<Tile>();
    }

    public Grid(Integer rowSize, Integer colSize, List<Tile> tiles) {
        this.rowSize = rowSize;
        this.columnSize = colSize;
        this.tiles = tiles;
    }

    public void printGrid() {
        System.out.println();
        for (int i = 0; i < tiles.size(); i++) {
            Tile t = tiles.get(i);
            System.out.print(t.getType().getSymbol());
            if ((i + 1) % rowSize == 0) {
                System.out.println();
            }
        }
        System.out.println();
    }

    public List<Tile> getTiles() {
        return tiles;
    }

}
