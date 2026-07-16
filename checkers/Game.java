package checkers;

import java.util.ArrayDeque;
import java.util.Deque;

public class Game {

    private Board b;
    private Player[] players;
    private Player currentPlayer;

    private Piece pieceMustContinue;

    private final Deque<BoardSnapshot> history = new ArrayDeque<>();

    public Game(String player1Id, String player2Id) {
        this.b = new Board();
        this.players = new Player[2];
        this.players[0] = new Player(player1Id, Piece.PieceColor.RED);
        this.players[1] = new Player(player2Id, Piece.PieceColor.BLACK);

        this.b.assignPiecesToPlayers(players[0], players[1]);

        this.currentPlayer = players[0];
        this.pieceMustContinue = null;
    }

    public Board getBoard() {
        return b;
    }

    public Player[] getPlayers() {
        return players;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public boolean isChainCaptureInProgress() {
        return pieceMustContinue != null;
    }

    private Player getOpponent(Player player) {
        return (player == players[0]) ? players[1] : players[0];
    }

    public MoveResult makeMove(int fromRow, int fromCol, int toRow, int toCol) {

        if (pieceMustContinue != null) {
            if (fromRow != pieceMustContinue.getRow() || fromCol != pieceMustContinue.getCol()) {
                return new MoveResult(false,
                        "You must continue capturing with the same piece.");
            }
        }

        if (!b.isValidMove(fromRow, fromCol, toRow, toCol, currentPlayer)) {
            return new MoveResult(false, "This move is not allowed.");
        }

        history.push(captureSnapshot());

        Piece movedPiece = b.getTile(fromRow, fromCol).getPiece();
        Piece captured = b.movePiece(fromRow, fromCol, toRow, toCol, currentPlayer);

        if (captured != null) {
            getOpponent(currentPlayer).removePiece(captured);
        }

        if (captured != null && b.hasFurtherCapture(movedPiece, currentPlayer)) {
            pieceMustContinue = movedPiece;
            return new MoveResult(true, "Piece captured! You can capture again with the same piece.");
        }

        pieceMustContinue = null;
        String message = (captured != null) ? "Opponent's piece captured." : "Move made.";

        GameStatus status = checkGameOver();
        if (status != GameStatus.ONGOING) {
            return new MoveResult(true, message, status);
        }

        switchTurn();
        return new MoveResult(true, message);
    }

    private void switchTurn() {
        currentPlayer = getOpponent(currentPlayer);
    }

    /**
     * Reverts the board to the state it was in before the last move.
     * Returns false if there is no move to undo.
     */
    public boolean undo() {
        if (history.isEmpty()) {
            return false;
        }
        restoreSnapshot(history.pop());
        return true;
    }

    public boolean canUndo() {
        return !history.isEmpty();
    }

    /**
     * Captures a lightweight snapshot of the current board and turn state,
     * so it can be restored later by undo().
     */
    private BoardSnapshot captureSnapshot() {
        int size = Board.SIZE;
        Piece.PieceColor[][] colors = new Piece.PieceColor[size][size];
        boolean[][] kings = new boolean[size][size];

        Tile[][] arr = b.getBoardArray();
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                Tile t = arr[r][c];
                if (!t.isEmpty()) {
                    Piece p = t.getPiece();
                    colors[r][c] = p.getColor();
                    kings[r][c] = p.isKing();
                }
            }
        }

        int currentIndex = (currentPlayer == players[0]) ? 0 : 1;
        int continueRow = -1;
        int continueCol = -1;
        if (pieceMustContinue != null) {
            continueRow = pieceMustContinue.getRow();
            continueCol = pieceMustContinue.getCol();
        }

        return new BoardSnapshot(colors, kings, currentIndex, continueRow, continueCol);
    }

    /**
     * Rebuilds the board, both players' piece lists, the current turn and
     * the chain-capture state from a previously captured snapshot.
     */
    private void restoreSnapshot(BoardSnapshot snap) {
        Tile[][] arr = b.getBoardArray();
        players[0].getPieces().clear();
        players[1].getPieces().clear();

        Piece restoredContinuePiece = null;

        for (int r = 0; r < Board.SIZE; r++) {
            for (int c = 0; c < Board.SIZE; c++) {
                Tile t = arr[r][c];
                Piece.PieceColor color = snap.colors[r][c];

                if (color == null) {
                    t.clear();
                    continue;
                }

                Piece p = new Piece(color, r, c);
                if (snap.kings[r][c]) {
                    p.promoteToKing();
                }
                t.setPiece(p);

                Player owner = (color == players[0].getColor()) ? players[0] : players[1];
                owner.addPiece(p);

                if (r == snap.continueRow && c == snap.continueCol) {
                    restoredContinuePiece = p;
                }
            }
        }

        currentPlayer = players[snap.currentPlayerIndex];
        pieceMustContinue = restoredContinuePiece;
    }

    /**
     * Plain data holder for a saved board state, used only by undo().
     */
    private static class BoardSnapshot {
        final Piece.PieceColor[][] colors;
        final boolean[][] kings;
        final int currentPlayerIndex;
        final int continueRow;
        final int continueCol;

        BoardSnapshot(Piece.PieceColor[][] colors, boolean[][] kings,
                      int currentPlayerIndex, int continueRow, int continueCol) {
            this.colors = colors;
            this.kings = kings;
            this.currentPlayerIndex = currentPlayerIndex;
            this.continueRow = continueRow;
            this.continueCol = continueCol;
        }
    }

    private GameStatus checkGameOver() {
        Player opponent = getOpponent(currentPlayer);

        if (!opponent.hasPieces()) {
            return GameStatus.WIN;
        }
        if (!b.hasAnyValidMove(opponent)) {
            return GameStatus.WIN;
        }
        return GameStatus.ONGOING;
    }

    public enum GameStatus {
        ONGOING, WIN
    }

    public static class MoveResult {
        private boolean success;
        private String message;
        private GameStatus status;

        public MoveResult(boolean success, String message) {
            this(success, message, GameStatus.ONGOING);
        }

        public MoveResult(boolean success, String message, GameStatus status) {
            this.success = success;
            this.message = message;
            this.status = status;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public GameStatus getStatus() {
            return status;
        }
    }
}
