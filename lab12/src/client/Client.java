/*
Написать текстовый многопользовательский чат.
Пользователь управляет клиентом. На сервере пользователя нет. Сервер занимается пересылкой сообщений между клиентами.
По умолчанию сообщение посылается всем участникам чата.
Есть команда послать сообщение конкретному пользователю (@senduser Vasya).
Программа работает по протоколу TCP.

+ Доп:
 Сервер представляет собой казино. Сервер объявляет начало тура. После этого в течении 10 секунд пользователи
 могут сделать ставку на число (@bet number). После этого сервер разыгрывает число и объявляет победителя.
*/
package lab12.src.client;


import lab12.src.ui.GraphicalInterface;

import java.net.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Client implements Runnable {
    private Socket socket;
    private GraphicalInterface ui;
    private static Logger logger = Logger.getLogger(Client.class.getName());
    private static String exceptionLiteral = "Exception";

    private Client(String address, int port) {
        try {
            this.socket = new Socket(InetAddress.getByName(address), port);
        } catch (Exception e) {

            logger.log(Level.SEVERE, exceptionLiteral, e);
        }

    }

    @Override
    public void run() {
        InputHandler client = new InputHandler(socket);
        this.ui = new GraphicalInterface(client);
        ui.writeText("Use \"@name %your-name%\" to define your username.\nType message and press enter to send it\n" +
                "Use @stop to exit program\n" + "You can type \"@casino\" to start casino lottery");
        new Thread(client).start();
    }

    public class InputHandler implements Runnable {
        private Socket socket;
        private PrintStream writer;

        InputHandler(Socket socket) {
            this.socket = socket;

            try {
                writer = new PrintStream(socket.getOutputStream());
            } catch (IOException e) {

                logger.log(Level.SEVERE, exceptionLiteral, e);
            }
        }

        @Override
        public void run() {
            try (BufferedReader socketInputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                while (true) {
                    String receivedString = receiveString(socketInputReader);
                    if (receivedString == null) {
                        close();
                        return;
                    }
                    ui.writeText(receivedString);
                }
            } catch (IOException e) {
                logger.log(Level.SEVERE, exceptionLiteral, e);
            }
        }


        public void send(String text) {
            writer.println(text);
        }


        void close() throws IOException {
            if (!socket.isClosed()) {
                socket.close();
            }

            System.exit(0);
        }


        private String receiveString(BufferedReader buff) throws IOException {
            String receivedString = null;

            try {
                receivedString = buff.readLine();
            } catch (SocketException e) {
                close();
                logger.log(Level.SEVERE, exceptionLiteral, e);
                System.exit(0);
            }

            return receivedString;
        }
    }

    public static void main(String[] args) {
        new Thread(new Client("localhost", 5000)).start();
    }
}
