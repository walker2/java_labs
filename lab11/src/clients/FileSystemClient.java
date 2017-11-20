package lab11.src.clients;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.InputMismatchException;
import java.util.Scanner;

import static java.lang.System.out;

public class FileSystemClient {

    private String clientName = "defaultName";
    private Integer port;
    private DatagramSocket datagramSocket;
    private InetAddress address;
    private byte[] dataBuffer;
    private boolean wasPrinted = false;

    private FileSystemClient(String address, int port) {
        try {
            this.address = InetAddress.getByName(address);
            this.port = port;
            datagramSocket = new DatagramSocket();
            dataBuffer = new byte[1024];
        } catch (Exception e) {
            out.print(e.getMessage());
        }
    }

    private void setName(String string) {
        clientName = string;
    }

    private String getName() {
        return clientName;
    }

    private void sendMessage() {
        Scanner sc = new Scanner(System.in);

        boolean running = true;
        while (running) {

            out.print(getName() + ": ");
            wasPrinted = true;
            String message = sc.nextLine();
            if (message.length() > 6) {
                String cmp = new String(message.getBytes(), 0, 6);
                if (cmp.compareTo("@name ") == 0) {
                    this.setName(new String(message.getBytes(), 6, message.length() - 6));
                    continue;
                }
            }

            if (message.compareTo("@quit") == 0) {
                datagramSocket.close();
                running = false;
                System.exit(0);
            }

            try {
                String fullMessage = clientName + ": " + message;
                byte[] data = fullMessage.getBytes();

                DatagramPacket outPacket = new DatagramPacket(data, data.length, address, port);
                datagramSocket.send(outPacket);
            } catch (IOException e) {
                out.print(e.getMessage());
                running = false;
            } catch (IllegalArgumentException e) {
                out.println("port must be > 0");
                System.exit(0);
            }
        }
    }

    private void recieveMessage() {
        try {
            DatagramPacket datagramPacket = new DatagramPacket
                    (dataBuffer, dataBuffer.length);
            boolean running = true;
            while (running) {
                datagramSocket.receive(datagramPacket);
                String answ = new String(datagramPacket.getData(), datagramPacket.getOffset(),
                        datagramPacket.getLength());

                if (wasPrinted)
                    out.println("");

                out.println("localhost:" + port + " : " + answ);
                wasPrinted = false;
                out.print(getName() + ": ");
            }
        } catch (IOException e) {
            datagramSocket.close();
            out.print(e.getMessage());
        } catch (IllegalArgumentException e) {
            out.println("port must be > 0");
            System.exit(0);
        }

    }

    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);
            String adr;
            Integer port;
            out.print("Enter server address: ");
            adr = scanner.next();
            out.print("Enter portNumber: ");
            port = scanner.nextInt();
            out.println("Available options: \n@ name [your name] to change your name in chat\n" +
                    "[message] + [enter] to send message\n@quit to close chat\n@ls to list contents of server\n" +
                    "@pwd print current directory\n@cd /path/ go to the directory");

            FileSystemClient client = new FileSystemClient(adr, port);

            new Thread(client::sendMessage).start();

            new Thread(client::recieveMessage).start();

        } catch (InputMismatchException e) {
            out.println("Try another port or address input");
        }
    }
}
