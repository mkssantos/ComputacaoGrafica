/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

/**
 *
 * @author Marcus Santos
 */
import controller.GameController;
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class GameBoard extends JPanel {
    private Map<Integer, CardButton> cardButtons;
    private GameController controller;
    private int rows;
    private int cols;

    public GameBoard(GameController controller, int pairs) {
        this.controller = controller;
        this.cardButtons = new HashMap<>();
        
        // Calcular linhas e colunas baseado no número de pares
        int totalCards = pairs * 2;
        this.rows = (int) Math.ceil(Math.sqrt(totalCards));
        this.cols = (int) Math.ceil(totalCards / (double) rows);
        
        setupBoard(totalCards);
    }

    private void setupBoard(int totalCards) {
        setLayout(new GridLayout(rows, cols, 5, 5));
        for (int i = 0; i < totalCards; i++) {
            // O índice i já é a posição correta, não precisamos calcular
            CardButton button = new CardButton(i);
            cardButtons.put(i, button);
            
            // Captura o índice final para usar no lambda
            final int position = i;
            button.addActionListener(e -> {
                System.out.println("Clicou na carta posição: " + position); // Debug
                controller.handleCardClick(position);
            });
            add(button);
        }
    }

     public void showCard(int position, int value) {
        System.out.println("Mostrando carta na posição: " + position + " com valor: " + value); // Debug
        SwingUtilities.invokeLater(() -> {
            CardButton button = cardButtons.get(position);
            if (button != null) {
                button.setValue(value);
                button.setRevealed(true);
                revalidate();
                repaint();
            } else {
                System.err.println("Botão não encontrado para posição: " + position); // Debug
            }
        });
    }

    public void hideCard(int position) {
        System.out.println("Escondendo carta na posição: " + position); // Debug
        SwingUtilities.invokeLater(() -> {
            CardButton button = cardButtons.get(position);
            if (button != null) {
                button.reset();
                revalidate();
                repaint();
            } else {
                System.err.println("Botão não encontrado para posição: " + position); // Debug
            }
        });
    }

    public void lockCard(int position) {
        CardButton button = cardButtons.get(position);
        if (button != null) {
            button.setMatched(true);
            button.updateAppearance();
            revalidate();
            repaint();
        }
    }
    
    
    
     private class CardButton extends JButton {
        private int position;
        private int value;
        private boolean revealed;
        private boolean matched;
        
        public CardButton(int position) {
            this.position = position;
            this.value = -1;
            this.revealed = false;
            this.matched = false;
            
            setPreferredSize(new Dimension(80, 80));
            setFont(new Font("Arial", Font.BOLD, 24));
            setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
            updateAppearance();
            
            // Debug na criação do botão
            System.out.println("Criado botão na posição: " + position);
        }
        
        public int getPosition() { 
            return position; 
        }
        
         public void setMatched(boolean matched) {
            this.matched = matched;
            updateAppearance();
        }
        
        public void setValue(int value) {
            this.value = value;
            System.out.println("Definindo valor " + value + " para posição " + position); // Debug
            updateAppearance();
        }
        
        public void setRevealed(boolean revealed) {
            this.revealed = revealed;
            System.out.println("Carta na posição " + position + " revelada: " + revealed); // Debug
            updateAppearance();
        }
        
        public void reset() {
            this.revealed = false;
            this.value = -1;
            System.out.println("Resetando carta na posição " + position); // Debug
            updateAppearance();
        }
        
        private void updateAppearance() {
            SwingUtilities.invokeLater(() -> {
                if (matched) {
                    setText(String.valueOf(value));
                    setEnabled(false);
                    setBackground(new Color(144, 238, 144));
                    setForeground(Color.BLACK);
                } else if (revealed) {
                    setText(String.valueOf(value));
                    setEnabled(false);
                    setBackground(new Color(200, 200, 200));
                    setForeground(Color.BLACK);
                } else {
                    setText("?");
                    setEnabled(true);
                    setBackground(UIManager.getColor("Button.background"));
                    setForeground(Color.BLUE);
                }
            });
        }
    }
}