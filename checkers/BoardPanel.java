package checkers;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BoardPanel extends JPanel {

    private static final int TILE_SIZE = 70;
    private static final int BOARD_SIZE = Board.SIZE;

    private static final Color LIGHT_TILE_COLOR = new Color(240, 217, 181);
    private static final Color DARK_TILE_COLOR = new Color(120, 80, 60);
    private static final Color HIGHLIGHT_COLOR = new Color(100, 200, 100, 150);
    private static final Color MOVE_HINT_COLOR = new Color(60, 120, 220, 150);
    private static final Color RED_PIECE_COLOR = new Color(200, 40, 40);
    private static final Color BLACK_PIECE_COLOR = new Color(30, 30, 30);
    private static final Color KING_MARK_COLOR = new Color(255, 215, 0);

    private final Game game;
    private final JLabel statusLabel;

    private Integer selectedRow = null;
    private Integer selectedCol = null;
    private java.util.List<int[]> validDestinations = new java.util.ArrayList<>();

    public BoardPanel(Game game, JLabel statusLabel) {
        this.game = game;
        this.statusLabel = statusLabel;
        setPreferredSize(new Dimension(TILE_SIZE * BOARD_SIZE, TILE_SIZE * BOARD_SIZE));
        addMouseListener(new BoardMouseListener());
        updateStatusLabel("Turn: " + game.getCurrentPlayer().getId());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawTiles(g2);
        drawSelectionHighlight(g2);
        drawValidMoveHints(g2);
        drawPieces(g2);
    }

    private void drawTiles(Graphics2D g2) {
        Tile[][] boardArray = game.getBoard().getBoardArray();
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                Color color = boardArray[row][col].isDark() ? DARK_TILE_COLOR : LIGHT_TILE_COLOR;
                g2.setColor(color);
                g2.fillRect(col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }
    }

    private void drawSelectionHighlight(Graphics2D g2) {
        if (selectedRow != null && selectedCol != null) {
            g2.setColor(HIGHLIGHT_COLOR);
            g2.fillRect(selectedCol * TILE_SIZE, selectedRow * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        }
    }

    /**
     * Draws a blue circle in the center of every tile the selected piece
     * is currently allowed to move to.
     */
    private void drawValidMoveHints(Graphics2D g2) {
        g2.setColor(MOVE_HINT_COLOR);
        int dotSize = TILE_SIZE / 3;
        int offset = (TILE_SIZE - dotSize) / 2;
        for (int[] dest : validDestinations) {
            int x = dest[1] * TILE_SIZE + offset;
            int y = dest[0] * TILE_SIZE + offset;
            g2.fillOval(x, y, dotSize, dotSize);
        }
    }

    private void drawPieces(Graphics2D g2) {
        Tile[][] boardArray = game.getBoard().getBoardArray();
        int margin = 10;
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                Tile tile = boardArray[row][col];
                if (tile.isEmpty()) {
                    continue;
                }
                Piece piece = tile.getPiece();
                Color pieceColor = (piece.getColor() == Piece.PieceColor.RED)
                        ? RED_PIECE_COLOR : BLACK_PIECE_COLOR;

                int x = col * TILE_SIZE + margin;
                int y = row * TILE_SIZE + margin;
                int diameter = TILE_SIZE - margin * 2;

                g2.setColor(pieceColor);
                g2.fillOval(x, y, diameter, diameter);

                if (piece.isKing()) {
                    g2.setColor(KING_MARK_COLOR);
                    int markSize = diameter / 3;
                    int markOffset = (diameter - markSize) / 2;
                    g2.fillOval(x + markOffset, y + markOffset, markSize, markSize);
                }
            }
        }
    }

    private void updateStatusLabel(String text) {
        statusLabel.setText(text);
    }

    /**
     * Undoes the last move via the Game object, clears any in-progress
     * selection, and repaints the board. Called by the Undo button.
     */
    public void undoLastMove() {
        boolean undone = game.undo();
        selectedRow = null;
        selectedCol = null;
        validDestinations.clear();
        if (undone) {
            updateStatusLabel("Move undone. | Turn: " + game.getCurrentPlayer().getId());
        } else {
            updateStatusLabel("Nothing to undo.");
        }
        repaint();
    }

    private class BoardMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            int col = e.getX() / TILE_SIZE;
            int row = e.getY() / TILE_SIZE;

            if (!game.getBoard().isInBounds(row, col)) {
                return;
            }

            if (selectedRow == null) {
                trySelectPiece(row, col);
            } else {
                tryMovePiece(row, col);
            }
            repaint();
        }
    }

    private void trySelectPiece(int row, int col) {
        Tile tile = game.getBoard().getTile(row, col);
        if (tile.isEmpty()) {
            updateStatusLabel("This tile is empty. Select a piece.");
            return;
        }
        if (tile.getPiece().getColor() != game.getCurrentPlayer().getColor()) {
            updateStatusLabel("This piece does not belong to you.");
            return;
        }
        selectedRow = row;
        selectedCol = col;
        validDestinations = game.getBoard().getValidDestinations(row, col, game.getCurrentPlayer());
        updateStatusLabel("Piece selected. Click the destination tile.");
    }

    private void tryMovePiece(int toRow, int toCol) {
        Game.MoveResult result = game.makeMove(selectedRow, selectedCol, toRow, toCol);

        if (!result.isSuccess()) {
            updateStatusLabel(result.getMessage());

            return;
        }

        if (result.getStatus() == Game.GameStatus.WIN) {
            updateStatusLabel(result.getMessage() + " Game over! Winner: " + game.getCurrentPlayer().getId());
            selectedRow = null;
            selectedCol = null;
            validDestinations.clear();
            return;
        }

        if (game.isChainCaptureInProgress()) {

            selectedRow = toRow;
            selectedCol = toCol;
            validDestinations = game.getBoard().getValidDestinations(toRow, toCol, game.getCurrentPlayer());
            updateStatusLabel(result.getMessage());
            return;
        }

        selectedRow = null;
        selectedCol = null;
        validDestinations.clear();
        updateStatusLabel(result.getMessage() + " | Turn: " + game.getCurrentPlayer().getId());
    }
}
