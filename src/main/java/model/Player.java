/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author Marcus Santos
 */
public class Player {
    private String name;
    private int score;
    private int playerNumber;

    public Player(String name, int playerNumber) {
        this.name = name;
        this.playerNumber = playerNumber;
        this.score = 0;
    }

    // Getters e setters
    public String getName() { return name; }
    public int getScore() { return score; }
    public int getPlayerNumber() { return playerNumber; }
    public void incrementScore() { score++; }
}
