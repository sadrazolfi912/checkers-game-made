package checkers;

import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame {

    public GameFrame(Game game) {
        super("Checkers");

        JLabel statusLabel = new JLabel("", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        BoardPanel boardPanel = new BoardPanel(game, statusLabel);

        JButton undoButton = new JButton("Undo");
        undoButton.addActionListener(e -> boardPanel.undoLastMove());
        JPanel controlsPanel = new JPanel();
        controlsPanel.add(undoButton);

        setLayout(new BorderLayout());
        add(statusLabel, BorderLayout.NORTH);
        add(boardPanel, BorderLayout.CENTER);
        add(controlsPanel, BorderLayout.SOUTH);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
    }
}
