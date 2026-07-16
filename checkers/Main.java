package checkers;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            String player1Id = askForPlayerId("Player 1 (Red)", "Player1");
            String player2Id = askForPlayerId("Player 2 (Black)", "Player2");

            Game game = new Game(player1Id, player2Id);
            GameFrame frame = new GameFrame(game);
            frame.setVisible(true);
        });
    }

    private static String askForPlayerId(String promptLabel, String defaultId) {
        String input = JOptionPane.showInputDialog(
                null,
                "Enter the Id for " + promptLabel + ":",
                "Start Checkers Game",
                JOptionPane.QUESTION_MESSAGE
        );

        if (input == null || input.trim().isEmpty()) {
            return defaultId;
        }
        return input.trim();
    }
}
