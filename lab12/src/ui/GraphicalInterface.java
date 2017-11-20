package src.ui;

import src.client.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.logging.Level;
import java.util.logging.Logger;


public class GraphicalInterface extends JPanel implements KeyListener {
    private static Logger logger = Logger.getLogger(GraphicalInterface.class.getName());

    private JTextPane chatBox;
    private JTextField messageBox;
    private transient Client.InputHandler client;
    private static final int WINDOW_HEIGHT = 300;
    private static final int WINDOW_WIDTH = 400;

    public GraphicalInterface(Client.InputHandler client) {
        this.client = client;

        JFrame mainFrame = new JFrame("TCP-based chat");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setContentPane(this);

        mainFrame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);

        this.setLayout(new BorderLayout());

        chatBox = new JTextPane();
        messageBox = new JTextField();
        messageBox.setSize(WIDTH, 30);

        this.add(chatBox, BorderLayout.CENTER);
        this.add(messageBox, BorderLayout.PAGE_END);
        messageBox.addKeyListener(this);
        chatBox.setEditable(false);

        mainFrame.setVisible(true);
        mainFrame.requestFocus();
    }


    private void sendMessage() {
        String text = messageBox.getText();

        if (!text.equals("")) {
            client.send(text);
        }
        messageBox.setText("");
    }

    public void writeText(String text) {
        chatBox.setText(chatBox.getText() + text + "\n");
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {/**/}

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
            try {
                sendMessage();
            } catch (Exception e) {
                String exceptionLiteral = "Exception";
                logger.log(Level.SEVERE, exceptionLiteral, e);
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {/**/}
}
