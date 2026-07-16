package checkers;

public class Board {

    public static final int SIZE = 8;

    private Tile[][] board;

    public Board() {
        board = new Tile[SIZE][SIZE];
        initializeTiles();
        placeInitialPieces();
    }

    private void initializeTiles() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                Tile.TileColor color = ((row + col) % 2 == 0)
                        ? Tile.TileColor.LIGHT
                        : Tile.TileColor.DARK;
                board[row][col] = new Tile(row, col, color);
            }
        }
    }

    private void placeInitialPieces() {

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (board[row][col].isDark()) {
                    Piece piece = new Piece(Piece.PieceColor.BLACK, row, col);
                    board[row][col].setPiece(piece);
                }
            }
        }

        for (int row = SIZE - 3; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (board[row][col].isDark()) {
                    Piece piece = new Piece(Piece.PieceColor.RED, row, col);
                    board[row][col].setPiece(piece);
                }
            }
        }
    }

    public void assignPiecesToPlayers(Player redPlayer, Player blackPlayer) {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                Tile tile = board[row][col];
                if (!tile.isEmpty()) {
                    Piece p = tile.getPiece();
                    if (p.getColor() == Piece.PieceColor.RED) {
                        redPlayer.addPiece(p);
                    } else {
                        blackPlayer.addPiece(p);
                    }
                }
            }
        }
    }

    public Tile getTile(int row, int col) {
        return board[row][col];
    }

    public Tile[][] getBoardArray() {
        return board;
    }

    /**
     * Returns every valid destination (row, col) that the piece at
     * (fromRow, fromCol) is currently allowed to move to, according to
     * isValidMove (which already accounts for the mandatory-capture rule).
     * Used by the GUI to highlight legal moves when a piece is selected.
     */
    public java.util.List<int[]> getValidDestinations(int fromRow, int fromCol, Player currentPlayer) {
        java.util.List<int[]> destinations = new java.util.ArrayList<>();
        int[][] allOffsets = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1},
                               {-2, -2}, {-2, 2}, {2, -2}, {2, 2}};
        for (int[] offset : allOffsets) {
            int toRow = fromRow + offset[0];
            int toCol = fromCol + offset[1];
            if (isValidMove(fromRow, fromCol, toRow, toCol, currentPlayer)) {
                destinations.add(new int[]{toRow, toCol});
            }
        }
        return destinations;
    }

    public boolean isInBounds(int row, int col) {
        return row >= 0 && row < SIZE && col >= 0 && col < SIZE;
    }

    public boolean isValidMove(int fromRow, int fromCol, int toRow, int toCol, Player currentPlayer) {
        if (!isInBounds(fromRow, fromCol) || !isInBounds(toRow, toCol)) {
            return false;
        }

        Tile fromTile = board[fromRow][fromCol];
        Tile toTile = board[toRow][toCol];

        if (fromTile.isEmpty()) {
            return false;
        }

        Piece piece = fromTile.getPiece();
        if (piece.getColor() != currentPlayer.getColor()) {
            return false;
        }

        if (!toTile.isDark() || !toTile.isEmpty()) {
            return false;
        }

        int rowDiff = toRow - fromRow;
        int colDiff = toCol - fromCol;

        if (Math.abs(rowDiff) != Math.abs(colDiff)) {
            return false;
        }

        int distance = Math.abs(rowDiff);
        if (distance != 1 && distance != 2) {
            return false;
        }

        if (!piece.isKing()) {
            int expectedDirection = piece.getForwardDirection();
            int actualDirection = Integer.signum(rowDiff);
            if (actualDirection != expectedDirection) {
                return false;
            }
        }

        if (distance == 2) {
            return getCapturedPiece(fromRow, fromCol, toRow, toCol, currentPlayer) != null;
        }

        if (distance == 1 && playerHasAnyCapture(currentPlayer)) {
            return false;
        }

        return true;
    }

    public boolean playerHasAnyCapture(Player currentPlayer) {
        int[][] jumpOffsets = {{-2, -2}, {-2, 2}, {2, -2}, {2, 2}};
        for (Piece piece : currentPlayer.getPieces()) {
            int row = piece.getRow();
            int col = piece.getCol();
            for (int[] offset : jumpOffsets) {
                int toRow = row + offset[0];
                int toCol = col + offset[1];

                if (isBasicJumpPossible(piece, row, col, toRow, toCol, currentPlayer)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isBasicJumpPossible(Piece piece, int fromRow, int fromCol,
                                         int toRow, int toCol, Player currentPlayer) {
        if (!isInBounds(toRow, toCol)) {
            return false;
        }
        Tile toTile = board[toRow][toCol];
        if (!toTile.isDark() || !toTile.isEmpty()) {
            return false;
        }
        if (!piece.isKing()) {
            int expectedDirection = piece.getForwardDirection();
            int actualDirection = Integer.signum(toRow - fromRow);
            if (actualDirection != expectedDirection) {
                return false;
            }
        }
        return getCapturedPiece(fromRow, fromCol, toRow, toCol, currentPlayer) != null;
    }

    public Piece getCapturedPiece(int fromRow, int fromCol, int toRow, int toCol, Player currentPlayer) {
        int midRow = (fromRow + toRow) / 2;
        int midCol = (fromCol + toCol) / 2;
        Tile midTile = board[midRow][midCol];

        if (midTile.isEmpty()) {
            return null;
        }

        Piece midPiece = midTile.getPiece();
        if (midPiece.getColor() == currentPlayer.getColor()) {
            return null;
        }

        return midPiece;
    }

    public Piece movePiece(int fromRow, int fromCol, int toRow, int toCol, Player currentPlayer) {
        Tile fromTile = board[fromRow][fromCol];
        Tile toTile = board[toRow][toCol];
        Piece piece = fromTile.getPiece();

        Piece captured = null;
        int distance = Math.abs(toRow - fromRow);
        if (distance == 2) {
            captured = getCapturedPiece(fromRow, fromCol, toRow, toCol, currentPlayer);
            if (captured != null) {
                int midRow = (fromRow + toRow) / 2;
                int midCol = (fromCol + toCol) / 2;
                board[midRow][midCol].clear();
            }
        }

        fromTile.clear();
        toTile.setPiece(piece);
        piece.setPosition(toRow, toCol);

        if (!piece.isKing()) {
            if ((piece.getColor() == Piece.PieceColor.RED && toRow == 0) ||
                (piece.getColor() == Piece.PieceColor.BLACK && toRow == SIZE - 1)) {
                piece.promoteToKing();
            }
        }

        return captured;
    }

    public boolean hasFurtherCapture(Piece piece, Player currentPlayer) {
        int row = piece.getRow();
        int col = piece.getCol();
        int[][] jumpOffsets = {{-2, -2}, {-2, 2}, {2, -2}, {2, 2}};

        for (int[] offset : jumpOffsets) {
            int toRow = row + offset[0];
            int toCol = col + offset[1];
            if (isValidMove(row, col, toRow, toCol, currentPlayer)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasAnyValidMove(Player currentPlayer) {
        for (Piece piece : currentPlayer.getPieces()) {
            int row = piece.getRow();
            int col = piece.getCol();
            int[][] allOffsets = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1},
                                   {-2, -2}, {-2, 2}, {2, -2}, {2, 2}};
            for (int[] offset : allOffsets) {
                int toRow = row + offset[0];
                int toCol = col + offset[1];
                if (isValidMove(row, col, toRow, toCol, currentPlayer)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void printBoard() {
        System.out.println("   0 1 2 3 4 5 6 7");
        for (int row = 0; row < SIZE; row++) {
            System.out.print(row + "  ");
            for (int col = 0; col < SIZE; col++) {
                System.out.print(board[row][col].toString() + " ");
            }
            System.out.println();
        }
    }
}
