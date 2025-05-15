/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

/**
 *
 * @author Marcus Santos
 */

import javax.swing.*;
import controller.GameController;
import java.util.List;
import java.util.Map;
import java.awt.BorderLayout;
import java.awt.GridLayout;

public class GameWindow extends JFrame {
    private GameBoard gameBoard;
    private JPanel playerPanel;
    private JLabel statusLabel;
    private GameController controller;

    public GameWindow(GameController controller, int pairs) {
        super("Jogo da Memória");
        this.controller = controller;
        initComponents(pairs);
        setupGUI();
    }
     private void setupGUI() {
        setLayout(new BorderLayout(10, 10));
        
        // Configurar o painel de jogadores
        playerPanel.setBorder(BorderFactory.createTitledBorder("Jogadores"));
        
        // Adicionar componentes
        add(gameBoard, BorderLayout.CENTER);
        add(playerPanel, BorderLayout.EAST);
        add(statusLabel, BorderLayout.SOUTH);
        
        // Configurar a janela
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    private void initComponents(int pairs) {
        gameBoard = new GameBoard(controller, pairs);
        playerPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        statusLabel = new JLabel("Aguardando jogadores...");
    }

    public void showCard(int position, int value) {
        gameBoard.showCard(position, value);
    }

    public void hideCards(int pos1, int pos2) {
    SwingUtilities.invokeLater(() -> {
        gameBoard.hideCard(pos1);
        gameBoard.hideCard(pos2);
        gameBoard.revalidate();
        gameBoard.repaint();
    });
}

    public void lockCards(int pos1, int pos2) {
        gameBoard.lockCard(pos1);
        gameBoard.lockCard(pos2);
    }

    public void updatePlayers(List<String> players, Map<String, Integer> scores) {
        playerPanel.removeAll();
        for (String player : players) {
            int score = scores.getOrDefault(player, 0);
            JLabel playerLabel = new JLabel(player + ": " + score + " pontos");
            playerLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            playerPanel.add(playerLabel);
        }
        playerPanel.revalidate();
        playerPanel.repaint();
    }

    public void updateStatus(String status) {
        statusLabel.setText(status);
    }
}