/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servidor;

/**
 *
 * @author Marcus Santos
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ServerGUI extends JFrame {
    private JButton startButton;
    private JTextArea logArea;
    private Servidor server;
    private boolean serverRunning;
    private JLabel statusLabel;
    private SimpleDateFormat timeFormat;
    private Thread serverThread;
    private JComboBox<String> pairsSelector;
    private JComboBox<String> playersSelector; // Novo componente

    public ServerGUI() {
        super("Servidor do Jogo da Memória");
        serverRunning = false;
        timeFormat = new SimpleDateFormat("HH:mm:ss");
        initComponents();
        setupGUI();
    }

    private void initComponents() {
        startButton = new JButton("Iniciar Servidor");
        statusLabel = new JLabel("Servidor Parado");
        statusLabel.setForeground(Color.RED);
        logArea = new JTextArea(10, 40);
        logArea.setEditable(false);
        
        pairsSelector = new JComboBox<>(new String[]{"20 pares", "30 pares"});
        playersSelector = new JComboBox<>(new String[]{"2 jogadores", "3 jogadores", "4 jogadores"}); // Novo selector
        
        startButton.addActionListener(e -> toggleServer());
    }

    private void setupGUI() {
        setLayout(new BorderLayout(10, 10));
        
        // Painel superior
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        topPanel.add(new JLabel("Número de pares:"));
        topPanel.add(pairsSelector);
        topPanel.add(new JLabel("Número de jogadores:")); // Nova label
        topPanel.add(playersSelector); // Adiciona selector de jogadores
        topPanel.add(startButton);
        topPanel.add(statusLabel);
        
        // Área de log com scroll
        JScrollPane scrollPane = new JScrollPane(logArea);
        
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
    }

    private void startServer() {
        server = new Servidor();
        
        int selectedPairs = pairsSelector.getSelectedIndex() == 0 ? 20 : 30;
        int selectedPlayers = playersSelector.getSelectedIndex() + 2; // Converte índice para número de jogadores
        
        server.setNumberOfPairs(selectedPairs);
        server.setExpectedPlayers(selectedPlayers); // Novo método
        
        serverThread = new Thread(() -> {
            server.start();
        });
        serverThread.start();
        
        serverRunning = true;
        startButton.setText("Parar Servidor");
        statusLabel.setText("Servidor Rodando");
        statusLabel.setForeground(Color.GREEN);
        pairsSelector.setEnabled(false);
        playersSelector.setEnabled(false);
        log("Servidor iniciado com " + selectedPairs + " pares e aguardando " + selectedPlayers + " jogadores");
    }

    private void toggleServer() {
        if (!serverRunning) {
            startServer();
        } else {
            stopServer();
        }
    }

    private void stopServer() {
        if (server != null) {
            server.shutdown();
            server = null;
        }
        if (serverThread != null) {
            serverThread.interrupt();
            serverThread = null;
        }
        serverRunning = false;
        startButton.setText("Iniciar Servidor");
        statusLabel.setText("Servidor Parado");
        statusLabel.setForeground(Color.RED);
        pairsSelector.setEnabled(true); // Reabilitar seleção após parar
        log("Servidor parado");
    }

    private void shutdown() {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Deseja realmente fechar o servidor?",
            "Confirmar Fechamento",
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (serverRunning) {
                stopServer();
            }
            dispose();
            System.exit(0);
        }
    }

    private void log(String message) {
        SwingUtilities.invokeLater(() -> {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            logArea.append(sdf.format(new Date()) + " - " + message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new ServerGUI().setVisible(true);
        });
    }
}