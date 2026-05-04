package tucil3_13524018_13524084.Core;

public enum TileType {
    PATH('*', true),
    OBSTACLE('X', false),
    LAVA('L', true),
    GOAL('O', true),
    COIN_NUMBER('i', true),
    PLAYER('Z', true);

    private final char symbol;
    private final boolean traversable;

    TileType(char symbol, boolean traversable) {
        this.symbol = symbol;
        this.traversable = traversable;
    }

    public char getSymbol(){ return this.symbol;};

    public boolean isTraversable() {
        return traversable;
    };
}