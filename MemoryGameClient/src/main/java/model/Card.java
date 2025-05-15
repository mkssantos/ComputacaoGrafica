/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author Marcus Santos
 */
public class Card {
    private int id;
    private String image;
    private boolean isRevealed;
    private boolean isMatched;

    public Card(int id, String image) {
        this.id = id;
        this.image = image;
        this.isRevealed = false;
        this.isMatched = false;
    }

    // Getters e setters
    public int getId() { return id; }
    public String getImage() { return image; }
    public boolean isRevealed() { return isRevealed; }
    public boolean isMatched() { return isMatched; }
    public void setRevealed(boolean revealed) { isRevealed = revealed; }
    public void setMatched(boolean matched) { isMatched = matched; }
}