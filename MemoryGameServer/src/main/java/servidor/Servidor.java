/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servidor;

import java.io.*;
import java.net.*;
import java.util.*;

public class Servidor {
    private static final int PORT = 12345;
    private List<ClientHandler> clients;
    private List<Card> cards;
    private int currentPlayerIndex;
    private boolean gameStarted;
    private int selectedPairs;
    private Map<String, Integer> scores;
    private Card firstCard;
    private Card secondCard;
    private ServerSocket serverSocket;
    private volatile boolean running;
    private int expectedPlayers; // Novo campo

    public Servidor() {
        clients = new ArrayList<>();
        scores = new HashMap<>();
        currentPlayerIndex = 0;
        gameStarted = false;
        selectedPairs = 20;
        expectedPlayers = 2; // Valor padrão
    }
    
    public void setExpectedPlayers(int players) {
        this.expectedPlayers = players;
    }

    private void handleNewConnection(Socket clientSocket) {
        if (clients.size() >= expectedPlayers) {
            try {
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                out.println("ERROR Jogo já possui " + expectedPlayers + " jogadores");
                clientSocket.close();
                return;
            } catch (IOException e) {
                System.err.println("Erro ao rejeitar conexão: " + e.getMessage());
            }
        } else {
            ClientHandler clientHandler = new ClientHandler(clientSocket, this);
            clients.add(clientHandler);
            new Thread(clientHandler).start();
            
            // Informa o novo cliente sobre o número total esperado de jogadores
            clientHandler.sendMessage("PLAYERS_INFO " + clients.size() + "/" + expectedPlayers);
            broadcastPlayers();

            // Se atingiu o número esperado de jogadores, inicia o jogo automaticamente
            if (clients.size() == expectedPlayers) {
                startGame();
            }
        }
    }
    
    public void start() {
        running = true;
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Servidor iniciado na porta " + PORT + " aguardando " + expectedPlayers + " jogadores");
            
            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    handleNewConnection(clientSocket);
                } catch (SocketException e) {
                    if (!running) break;
                }
            }
        } catch (IOException e) {
            System.err.println("Erro no servidor: " + e.getMessage());
        }
    }

    public void shutdown() {
        running = false;
        try {
            // Fechar todas as conexões de clientes
            for (ClientHandler client : new ArrayList<>(clients)) {
                client.disconnect();
            }
            clients.clear();
            
            // Fechar o ServerSocket
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Erro ao parar o servidor: " + e.getMessage());
        }
    }
       
    private void broadcastPlayers() {
        StringBuilder playersList = new StringBuilder("PLAYERS ");
        for (ClientHandler client : clients) {
            if (client.getPlayerName() != null) {
                playersList.append(client.getPlayerName()).append(",");
            }
        }
        broadcastMessage(playersList.toString());
    }

    private void broadcastMessage(String message) {
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    public void setNumberOfPairs(int pairs) {
        if (!gameStarted && (pairs == 20 || pairs == 30)) {
            this.selectedPairs = pairs;
            initializeCards();
            broadcastMessage("PAIRS " + pairs);
        }
    }

        private void initializeCards() {
        cards = new ArrayList<>();
        for (int i = 0; i < selectedPairs; i++) {
            // Criar duas cartas com o mesmo valor (par)
            Card card1 = new Card(i, cards.size());  // posição será o tamanho atual da lista
            Card card2 = new Card(i, cards.size() + 1);  // posição será o tamanho atual + 1
            cards.add(card1);
            cards.add(card2);
        }
        Collections.shuffle(cards);

        // Atualizar as posições após o embaralhamento
        for (int i = 0; i < cards.size(); i++) {
            cards.get(i).setPosition(i);
        }
    }

    public void startGame() {
        if (clients.size() >= 2 && !gameStarted) {
            gameStarted = true;
            initializeCards();
            broadcastMessage("START " + selectedPairs);
            nextTurn();
        }
    }

    private void nextTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % clients.size();
        while (clients.get(currentPlayerIndex).getPlayerName() == null) {
            currentPlayerIndex = (currentPlayerIndex + 1) % clients.size();
        }
        broadcastMessage("TURN " + clients.get(currentPlayerIndex).getPlayerName());
    }

    public void handleCardFlip(int position, String playerName) {
        if (!gameStarted || position >= cards.size()) return;
        
        Card card = cards.get(position);
        if (card.isMatched() || card.isRevealed()) return;
        
        ClientHandler currentPlayer = clients.get(currentPlayerIndex);
        if (!currentPlayer.getPlayerName().equals(playerName)) return;
        
        // Revelar a carta atual
        card.setRevealed(true);
        broadcastMessage("CARD " + position + " " + card.getValue());
        
        if (firstCard == null) {
            firstCard = card;
        } else {
            secondCard = card;
            
            // Aguardar um pouco antes de processar o par
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    processCardPair();
                }
            }, 1000); // 1 segundo de delay
        }
    }
    
    private void processCardPair() {
    if (firstCard != null && secondCard != null) {
        if (firstCard.getValue() == secondCard.getValue()) {
            // Par encontrado
            firstCard.setMatched(true);
            secondCard.setMatched(true);
            broadcastMessage("MATCH " + firstCard.getPosition() + " " + secondCard.getPosition());
            
            // Atualizar pontuação
            String currentPlayer = clients.get(currentPlayerIndex).getPlayerName();
            scores.put(currentPlayer, scores.getOrDefault(currentPlayer, 0) + 1);
            broadcastMessage("SCORE " + currentPlayer + " " + scores.get(currentPlayer));
            
            checkGameEnd();
        } else {
            // Não é um par
            firstCard.setRevealed(false);
            secondCard.setRevealed(false);
            broadcastMessage("NOMATCH " + firstCard.getPosition() + " " + secondCard.getPosition());
            
            // Próximo jogador
            nextTurn();
        }
        
        // Resetar as cartas selecionadas
        firstCard = null;
        secondCard = null;
    }
}
     
    private void checkMatch() {
        if (firstCard.getValue() == secondCard.getValue()) {
            firstCard.setMatched(true);
            secondCard.setMatched(true);
            broadcastMessage("MATCH " + firstCard.getPosition() + " " + secondCard.getPosition());
            
            String currentPlayer = clients.get(currentPlayerIndex).getPlayerName();
            scores.put(currentPlayer, scores.getOrDefault(currentPlayer, 0) + 1);
            broadcastMessage("SCORE " + currentPlayer + " " + scores.get(currentPlayer));
            
            checkGameEnd();
        } else {
            broadcastMessage("NOMATCH " + firstCard.getPosition() + " " + secondCard.getPosition());
            nextTurn();
        }
        
        firstCard = null;
        secondCard = null;
    }

    private void checkGameEnd() {
        boolean allMatched = true;
        for (Card card : cards) {
            if (!card.isMatched()) {
                allMatched = false;
                break;
            }
        }
        
        if (allMatched) {
            String winner = findWinner();
            broadcastMessage("GAMEOVER " + winner);
            gameStarted = false;
        }
    }

    private String findWinner() {
        String winner = "";
        int maxScore = -1;
        
        for (Map.Entry<String, Integer> entry : scores.entrySet()) {
            if (entry.getValue() > maxScore) {
                maxScore = entry.getValue();
                winner = entry.getKey();
            }
        }
        return winner;
    }

    public static void main(String[] args) {
        new Servidor().start();
    }

    class ClientHandler implements Runnable {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String playerName;
        private Servidor server;

        public ClientHandler(Socket socket, Servidor server) {
            this.socket = socket;
            this.server = server;
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    if (message.startsWith("LOGIN")) {
                        playerName = message.substring(6);
                        server.broadcastPlayers();
                    } 
                    else if (message.startsWith("SELECT_PAIRS")) {
                        int pairs = Integer.parseInt(message.substring(12));
                        server.setNumberOfPairs(pairs);
                    }
                    else if (message.startsWith("FLIP")) {
                        int position = Integer.parseInt(message.substring(5));
                        server.handleCardFlip(position, playerName);
                    }
                    else if (message.equals("START_GAME")) {
                        server.startGame();
                    }
                }
            } catch (IOException e) {
                System.err.println("Erro no cliente: " + e.getMessage());
            } finally {
                disconnect();
            }
        }

        public void sendMessage(String message) {
            out.println(message);
        }

        public String getPlayerName() {
            return playerName;
        }

        private void disconnect() {
            try {
                clients.remove(this);
                if (socket != null) socket.close();
                if (playerName != null) {
                    broadcastPlayers();
                }
            } catch (IOException e) {
                System.err.println("Erro ao desconectar cliente: " + e.getMessage());
            }
        }
    }
    
    

class Card {
    private int value;
    private int position;
    private boolean revealed;
    private boolean matched;

    public Card(int value, int position) {
        this.value = value;
        this.position = position;
        this.revealed = false;
        this.matched = false;
    }

    public int getValue() { return value; }
    public int getPosition() { return position; }
    public boolean isRevealed() { return revealed; }
    public boolean isMatched() { return matched; }
    public void setRevealed(boolean revealed) { this.revealed = revealed; }
    public void setMatched(boolean matched) { this.matched = matched; }
    public void setPosition(int position) { this.position = position; } // Método adicionado
}
}