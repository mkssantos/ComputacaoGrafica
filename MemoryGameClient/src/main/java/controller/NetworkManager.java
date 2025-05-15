/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

/**
 *
 * @author Marcus Santos
 */

import java.io.*;
import java.net.*;

public class NetworkManager {
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 12345;
    
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private GameController gameController;
    
    public NetworkManager(GameController gameController) {
        this.gameController = gameController;
    }
    
    public boolean connect() {
        try {
            socket = new Socket(SERVER_IP, SERVER_PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            new Thread(this::listenToServer).start();
            return true;
        } catch (IOException e) {
            System.err.println("Erro ao conectar: " + e.getMessage());
            return false;
        }
    }
    
    private void listenToServer() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                gameController.handleNetworkMessage(message);
            }
        } catch (IOException e) {
            System.err.println("Erro na conexão: " + e.getMessage());
        }
    }
    
    public void sendMessage(String message) {
        if (out != null) {
            out.println(message);
        }
    }
    
    public void disconnect() {
        try {
            if (socket != null) socket.close();
            if (out != null) out.close();
            if (in != null) in.close();
        } catch (IOException e) {
            System.err.println("Erro ao desconectar: " + e.getMessage());
        }
    }

}