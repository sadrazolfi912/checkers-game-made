package checkers;

import java.util.ArrayList;
import java.util.List;

public class Player {

    private String Id;
    private Piece.PieceColor color;
    private List<Piece> pieces;

    // constructor

    public Player(String id, Piece.PieceColor color) {
        this.Id = id;
        this.color = color;
        this.pieces = new ArrayList<>();
    }

    // using method for access to private fields of class 

    public String getId() {
        return Id;
    }

    public Piece.PieceColor getColor() {
        return color;
    }

    public List<Piece> getPieces() {
        return pieces;
    }

    // methods that related to pieces /add /remove /empty
    
    public void addPiece(Piece piece) {
        pieces.add(piece);
    }

    public void removePiece(Piece piece) {
        pieces.remove(piece);
    }

    public boolean hasPieces() {
        return !pieces.isEmpty();
    }

    @Override
    public String toString() {
        return "Player[" + Id + ", " + color + "]";
    }
}
