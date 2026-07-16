package checkers;

public class Piece {

    public enum PieceColor {
        RED, BLACK
    }

    private PieceColor color;
    private int row;
    private int col;
    private boolean isKing;

    public Piece(PieceColor color, int row, int col) {
        this.color = color;
        this.row = row;
        this.col = col;
        this.isKing = false;
    }

    public PieceColor getColor() {
        return color;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public boolean isKing() {
        return isKing;
    }

    public void setPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public void promoteToKing() {
        this.isKing = true;
    }

    public int getForwardDirection() {
        return (color == PieceColor.RED) ? -1 : 1;
    }

    @Override
    public String toString() {
        String symbol = (color == PieceColor.RED) ? "r" : "b";
        return isKing ? symbol.toUpperCase() : symbol;
    }
}
