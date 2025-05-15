/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

/**
 *
 * @author Marcus Santos
 */
import controller.NetworkManager;
import controller.GameController;
import javax.swing.*;
import java.awt.*;

public class LoginScreen extends JFrame {
    private JTextField nameField;
    private JButton connectButton;
    private NetworkManager networkManager;
    private GameController gameController;
    private JLabel statusLabel; // Nova label para mostrar status

    public LoginScreen() {
        super("Login - Jogo da Memória");
        this.gameController = new GameController();
        this.networkManager = gameController.getNetworkManager();
        initComponents();
        setupGUI();
    }

    private void initComponents() {
        nameField = new JTextField(20);
        connectButton = new JButton("Conectar");
        statusLabel = new JLabel("Digite seu nome e conecte-se ao jogo");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        connectButton.addActionListener(e -> tryConnect());
    }

    private void setupGUI() {
        setLayout(new BorderLayout(10, 10));
        
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(new JLabel("Nome:"), gbc);
        
        gbc.gridx = 1;
        mainPanel.add(nameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 2;
        mainPanel.add(connectButton, gbc);
        
        gbc.gridy = 2;
        mainPanel.add(statusLabel, gbc);
        
        add(mainPanel, BorderLayout.CENTER);
        
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void updatePlayersStatus(String status) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText(status);
        });
    }

    private void tryConnect() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Por favor, digite seu nome!", 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        connectButton.setEnabled(false);
        connectButton.setText("Conectando...");
        nameField.setEnabled(false);

        new Thread(() -> {
            boolean connected = networkManager.connect();
            
            SwingUtilities.invokeLater(() -> {
                if (connected) {
                    gameController.setPlayerName(name);
                    networkManager.sendMessage("LOGIN " + name);
                    connectButton.setText("Conectado");
                    statusLabel.setText("Aguardando outros jogadores...");
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Não foi possível conectar ao servidor.\nVerifique se o servidor está rodando ou se o jogo já está cheio.",
                        "Erro de Conexão",
                        JOptionPane.ERROR_MESSAGE);
                    connectButton.setEnabled(true);
                    connectButton.setText("Conectar");
                    nameField.setEnabled(true);
                }
            });
        }).start();
    }
    
    public static void main(String[] args) {
        System.out.println("Iniciando aplicação...");
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                LoginScreen loginScreen = new LoginScreen();
                loginScreen.setVisible(true);
                System.out.println("Janela de login criada e exibida");
            } catch (Exception e) {
                System.err.println("Erro ao criar janela de login: ");
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, 
                    "Erro ao iniciar o jogo: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}