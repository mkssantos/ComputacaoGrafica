/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

/**
 *
 * @author Marcus Santos
 */
import view.GameWindow;
import java.util.*;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;


public class GameController {
    private GameWindow gameWindow;
    private NetworkManager networkManager;
    private String playerName;
    private List<String> players;
    private String currentTurn;
    private Map<String, Integer> scores;
    private boolean canPlay;

    public GameController() {
        this.networkManager = new NetworkManager(this);
        this.players = new ArrayList<>();
        this.scores = new HashMap<>();
        this.canPlay = false;
    }

    public void setPlayerName(String name) {
        this.playerName = name;
    }

    public NetworkManager getNetworkManager() {
        return networkManager;
    }

    public void handleCardClick(int position) {
        if (canPlay && currentTurn.equals(playerName)) {
            networkManager.sendMessage("FLIP " + position);
        }
    }

     public void handleNetworkMessage(String message) {
    System.out.println("Mensagem recebida: " + message); // Debug
        SwingUtilities.invokeLater(() -> {
            try {
                if (message.startsWith("PLAYERS")) {
                    String[] playersList = message.substring(8).split(",");
                    players.clear();
                    players.addAll(Arrays.asList(playersList));
                    updatePlayersList();
                }
                else if (message.startsWith("START")) {
                    int pairs = Integer.parseInt(message.split(" ")[1]);
                    startGame(pairs);
                }
                else if (message.startsWith("TURN")) {
                    currentTurn = message.split(" ")[1];
                    canPlay = true;
                    updateStatus();
                }
                else if (message.startsWith("CARD")) {
                    String[] parts = message.split(" ");
                    int position = Integer.parseInt(parts[1]);
                    int value = Integer.parseInt(parts[2]);
                    gameWindow.showCard(position, value);
                }
                else if (message.startsWith("MATCH")) {
                    String[] parts = message.split(" ");
                    int pos1 = Integer.parseInt(parts[1]);
                    int pos2 = Integer.parseInt(parts[2]);
                    canPlay = false;
                    
                    // Usar timer para dar tempo de ver as cartas antes de travar
                    Timer timer = new Timer(500, e -> {
                        gameWindow.lockCards(pos1, pos2);
                        canPlay = true;
                    });
                    timer.setRepeats(false);
                    timer.start();
                }
                if (message.startsWith("NOMATCH")) {
                String[] parts = message.split(" ");
                int pos1 = Integer.parseInt(parts[1]);
                int pos2 = Integer.parseInt(parts[2]);
                System.out.println("Escondendo cartas: " + pos1 + " e " + pos2); // Debug
                
                // Desabilitar interação temporariamente
                canPlay = false;
                
                // Usar Timer para dar um delay antes de esconder as cartas
                new Timer(1500, e -> {
                    if (gameWindow != null) {
                        gameWindow.hideCards(pos1, pos2);
                        canPlay = true;
                        updateStatus();
                    }
                }).start();
            }
                else if (message.startsWith("SCORE")) {
                    String[] parts = message.split(" ");
                    String player = parts[1];
                    int score = Integer.parseInt(parts[2]);
                    scores.put(player, score);
                    updatePlayersList();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void startGame(int pairs) {
        if (gameWindow == null) {
            gameWindow = new GameWindow(this, pairs);
            gameWindow.setVisible(true);
        }
    }

    private void updatePlayersList() {
        if (gameWindow != null) {
            gameWindow.updatePlayers(players, scores);
        }
    }

    private void updateStatus() {
        if (gameWindow != null) {
            boolean isMyTurn = playerName.equals(currentTurn);
            gameWindow.updateStatus(isMyTurn ? "Seu turno!" : "Turno de " + currentTurn);
        }
    }
}