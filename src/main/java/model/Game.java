/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author Marcus Santos
 */

import java.util.List;
import java.util.ArrayList;

public class Game {
    private List<Card> cards;
    private List<Player> players;
    private int currentPlayerIndex;
    private Player currentPlayer;

    public Game() {
        cards = new ArrayList<>();
        players = new ArrayList<>();
        currentPlayerIndex = 0;
    }

    // Métodos do jogo
    public void addPlayer(Player player) {
        players.add(player);
        if (currentPlayer == null) {
            currentPlayer = player;
        }
    }

    public void nextTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        currentPlayer = players.get(currentPlayerIndex);
    }

    // Getters
    public List<Card> getCards() { return cards; }
    public List<Player> getPlayers() { return players; }
    public Player getCurrentPlayer() { return currentPlayer; }
}