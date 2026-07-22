package checkers;

public class Tile {
    // Tile color.

    public enum TileColor {
        DARK, LIGHT
    }
    // Occupancy state.

    public enum TileStatus {
        EMPTY, OCCUPIED
    }

    private TileColor color;
    private TileStatus status;
    private int row;
    private int col;
    private Piece piece;

    public Tile(int row, int col, TileColor color) {
        this.row = row;
        this.col = col;
        this.color = color;
        this.status = TileStatus.EMPTY;
        this.piece = null;
    }

    public TileColor getColor() {
        return color;
    }

    public TileStatus getStatus() {
        return status;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public Piece getPiece() {
        return piece;
    }
    // Updates both the piece and tile status.

    public void setPiece(Piece piece) {
        this.piece = piece;
        this.status = (piece == null) ? TileStatus.EMPTY : TileStatus.OCCUPIED;
    }
    // Removes the piece from this tile.

    public void clear() {
        this.piece = null;
        this.status = TileStatus.EMPTY;
    }

    public boolean isEmpty() {
        return status == TileStatus.EMPTY;
    }

    public boolean isDark() {
        return color == TileColor.DARK;
    }

    @Override
    public String toString() {
        if (isEmpty()) {
            return ".";
        }
        return piece.toString();
    }
}
