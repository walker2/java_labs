package server;

import java.net.*;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Random;

public class Server implements Runnable {
    private static Logger logger = Logger.getLogger(Server.class.getName());
    private static String exceptionLiteral = "Exception";

    private ServerSocket socket;
    private int connectionsNumber = 0;
    private HashMap<String, Socket> names;
    private HashMap<String, Integer> bets;
    private int roundNum = 0;
    private int pool = 1000;
    private boolean roundStarted = false;
    private int numberGuessed;

    private Server(int port) {
        try {
            socket = new ServerSocket(port);
            names = new HashMap<>();
            bets = new HashMap<>();
        } catch (IOException e) {
            logger.log(Level.SEVERE, exceptionLiteral, e);
        }
    }

    public void run() {
        while (true) {
            try {
                if (socket.isClosed()) {
                    break;
                }

                Socket tempSocket = socket.accept();
                names.put("unknown" + Integer.toString(connectionsNumber), tempSocket);
                new Thread(new ClientHandler(tempSocket, connectionsNumber)).start();

                connectionsNumber++;

            } catch (IOException e) {
                logger.log(Level.SEVERE, exceptionLiteral, e);
            }
        }
    }

    private void close() {
        try {
            socket.close();
        } catch (IOException e) {
            logger.log(Level.SEVERE, exceptionLiteral, e);
        }

        System.exit(0);
    }

    public class ClientHandler implements Runnable {
        private Socket clientSocket;
        private String name;

        ClientHandler(Socket sock, int num) {
            this.clientSocket = sock;
            this.name = "unknown" + Integer.toString(num);
            names.put(name, clientSocket);
        }

        private void closeAndRemove() throws IOException {
            clientSocket.close();
            names.remove(name);
        }

        @Override
        public void run() {
            try (BufferedReader socketInputReader =
                         new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
                String receivedString;
                while (true) {
                    receivedString = socketInputReader.readLine();

                    if (receivedString == null) {
                        closeAndRemove();
                        break;
                    }
                    handleInput(receivedString);
                }
            } catch (IOException e) {
                logger.log(Level.SEVERE, exceptionLiteral, e);
            }
        }

        private void startRound() throws IOException {
            StringBuilder allNames = new StringBuilder();
            for (Map.Entry<String, Socket> entry : names.entrySet()) {
                allNames.append(entry.getKey()).append(' ');
            }
            for (Map.Entry<String, Socket> entry : names.entrySet()) {
                send(entry.getValue(), "Someone have started casino lottery \n *************************** \n" +
                        "WIN B$$$G WIN B$$$G WIN B$$$G WIN B$$$G\nCurrent pool: " + pool
                        + "\nTYPE @bet number (between 1 and 10) \nAll participants: " + allNames
                        + "\nYou have 10 seconds");
            }
            roundNum++;
            roundStarted = true;

            Random rng = new Random();
            numberGuessed = rng.nextInt(10);
            long seconds = 10;

            new Timer().scheduleAtFixedRate(new CounterTask(seconds), 1000, 1000);
            new Timer().schedule(new EndCasinoTask(), seconds * 1000);
        }

        class CounterTask extends TimerTask {
            private long allSeconds = -1;

            CounterTask(long allSeconds) {this.allSeconds = allSeconds;}

            public void run() {
                try {
                    if (allSeconds <= 1) {
                        this.cancel();
                    }
                    for (Map.Entry<String, Socket> entry : names.entrySet()) {
                        send(entry.getValue(), "Only " + allSeconds + " seconds left to vote!");
                    }
                    allSeconds--;
                } catch (IOException e) {
                    logger.log(Level.SEVERE, exceptionLiteral, e);
                }
            }
        }
        class EndCasinoTask extends TimerTask {
            public void run() {
                try {
                    for (Map.Entry<String, Socket> entry : names.entrySet()) {
                        send(entry.getValue(), "Les jeux sont faits; rien ne va plus\n Number was: " + numberGuessed);
                    }

                    StringBuilder winner = new StringBuilder("And the winner is: ");
                    boolean wasWinner = false;
                    for (Map.Entry<String, Integer> entry : bets.entrySet()) {
                        if (entry.getValue() == numberGuessed) {
                            winner.append(entry.getKey()).append(" ");
                            wasWinner = true;
                        }
                    }

                    if (!wasWinner) {
                        winner.append("nobody");
                        pool *= bets.size();
                    } else {
                        pool = 1000;
                    }

                    for (Map.Entry<String, Socket> entry : names.entrySet()) {
                        send(entry.getValue(), winner.toString());
                    }
                    bets.clear();

                } catch (IOException e) {
                    logger.log(Level.SEVERE, exceptionLiteral, e);
                }
            }
        }

        private void handleInput(String receivedString) throws IOException {
            if (receivedString.startsWith("@")) {
                command(receivedString);
            } else {

                for (Map.Entry<String, Socket> entry : names.entrySet()) {
                    send(entry.getValue(), name + ":" + receivedString);
                }
            }
        }


        private void command(String receivedString) throws IOException {
            if (receivedString.equals("@stop")) {
                closeAndRemove();

            }
            else if (receivedString.startsWith("@name")) {
                String newName = receivedString.substring(6, receivedString.length());
                if (names.containsKey(newName)) {
                    send(clientSocket, ">>>Your desired name is taken");
                } else {
                    names.remove(name);
                    names.put(newName, clientSocket);
                    name = newName;
                    send(clientSocket, ">>>Your name is now " + name);
                }

            }
            else if (receivedString.startsWith("@senduser")) {

                int firstSpace = receivedString.indexOf(' ');
                String afterCommand = receivedString.substring(firstSpace + 1, receivedString.length());
                String targetName = afterCommand.substring(0, afterCommand.indexOf(' '));
                String message = afterCommand.substring(afterCommand.indexOf(' '), afterCommand.length());

                if (!names.containsKey(targetName)) {
                    send(clientSocket, ">>>No user with such name found");
                }
                else {
                    send(names.get(targetName), "[private]" + name + ":" + message);
                    send(clientSocket, String.format("[to %s] : %s", targetName, message));
                }
            }
            else if (receivedString.startsWith("@casino")) {
                startRound();
            }
            else if (receivedString.startsWith("@bet")) {
                if (roundStarted) {
                    Integer bettingNumber = Integer.parseInt(receivedString.substring(5, receivedString.length()));

                    if (bets.containsKey(name)) {
                        send(clientSocket, ">>>You can't place more bets");
                    }
                    else {
                        bets.put(name, bettingNumber);
                        send(clientSocket, ">>>Your bet of " + bettingNumber + " has been registered");
                    }
                }
                else {
                    send(clientSocket, ">>>You can bet right now");
                }

            }
            else {
                send(clientSocket, ">>>Invalid command");
            }
        }

        private void send(Socket socket, String string) throws IOException {
            if (!socket.isClosed()) {
                PrintStream socketOutputWriter = new PrintStream(socket.getOutputStream());

                socketOutputWriter.println(string);
            }
        }
    }

    public static void main(String[] args) {
        Server serverObj = new Server(5775);
        new Thread(serverObj).start();

        Scanner scanner = new Scanner(System.in);

        while (true) {
            String input = scanner.nextLine();

            if (input.equals("@stop")) {
                serverObj.close();
                break;
            }
        }
    }
}