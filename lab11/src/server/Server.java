package server;

/* Добавить команды @cd, @pwd, @ls для просмотра файловой системы собеседника.
В ответ на эти команды необходимо выполнить действие с файловой системой и вернуть назад результат.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

import static java.lang.System.out;

public class Server {

    private DatagramSocket datagramSocket;
    private InetAddress address;
    private byte[] dataBuffer;
    private Integer clientPort;
    private String fileSystemPath = "/";
    private int prevCommandLength = 0;

    private Server(int portNumber) {
        try {
            datagramSocket = new DatagramSocket(portNumber);
            dataBuffer = new byte[1024];
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    private int checkLs(String message) throws InterruptedException, IOException {
        int exitValue = 0;
        if (message.compareTo("@ls") == 0) {
            StringBuilder answer = new StringBuilder("\n");
            answer.append("Executing ls \n");
            out.println("Executing ls");
            String s;
            Process p = Runtime.getRuntime().exec("ls -aF " + fileSystemPath);
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(p.getInputStream()));

            while ((s = br.readLine()) != null) {
                out.println("line: " + s);
                answer.append(s).append("\n");
            }
            respondWithMessage(answer.toString());

            p.waitFor();
            out.println("exit: " + p.exitValue());
            exitValue = p.exitValue();
            p.destroy();
        }
        return exitValue;
    }

    private void checkPwd(String message) throws InterruptedException, IOException {
        if (message.compareTo("@pwd") == 0) {
            StringBuilder answer = new StringBuilder("\n");
            answer.append("Executing pwd \n");
            out.println("Executing pwd");
            Process p = Runtime.getRuntime().exec("pwd");
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(p.getInputStream()));

            answer.append(br.readLine());
            respondWithMessage(answer.toString());

            p.waitFor();
            p.destroy();
        }
    }

    private void checkCd(String message) throws InterruptedException, IOException {
        if (message.length() > 4) {
            String cmp = new String(message.getBytes(), 0, 4);
            if (cmp.compareTo("@cd ") == 0) {
                String path = new String(message.getBytes(), 4, message.length() - 4);

                if (path.substring(path.length() - 1).compareTo("/") != 0) {
                    out.println("Is not a directory. Please enter your path with /");
                    respondWithMessage("Is not a directory. Please enter your path with /");
                    return;
                }
                out.println("Executing cd with path " + path);
                if (path.compareTo("..") == 0) {
                    this.fileSystemPath = new String(this.fileSystemPath.getBytes(),
                            0, this.fileSystemPath.length() - prevCommandLength);
                } else if (path.compareTo(".") != 0) {
                    String prevFileSystemPath = this.fileSystemPath;
                    this.fileSystemPath += path;
                    if (checkLs("@ls") != 0) {
                        out.println("There's no such directory as " + this.fileSystemPath);
                        respondWithMessage("There's no such directory as " + this.fileSystemPath);
                        this.fileSystemPath = prevFileSystemPath;
                        return;
                    }
                    this.prevCommandLength = path.length();
                }
                out.println("Current path is " + this.fileSystemPath);
            }
        }
    }

    private void listener() {
        try {
            DatagramPacket datagramPacket = new DatagramPacket(dataBuffer, dataBuffer.length);
            boolean running = true;
            while (running) {
                datagramSocket.receive(datagramPacket);

                address = datagramPacket.getAddress();
                clientPort = datagramPacket.getPort();

                String receivedData = new String(datagramPacket.getData(),
                        datagramPacket.getOffset(), datagramPacket.getLength());

                out.println("\n" + receivedData);
                int indexOfCommand;
                indexOfCommand = receivedData.indexOf('@');
                if (indexOfCommand != -1) {
                    receivedData = receivedData.substring(indexOfCommand);
                }

                checkLs(receivedData);
                checkPwd(receivedData);
                checkCd(receivedData);

                out.print("localhost: ");
            }
        } catch (Exception e) {
            out.print(e.getMessage());
        }

    }

    private void respondWithMessage(String message) {
        try {
            byte[] sendData = message.getBytes();
            DatagramPacket outServerPacket = new DatagramPacket(sendData, sendData.length, address, clientPort);
            datagramSocket.send(outServerPacket);
        } catch (Exception e) {
            out.print(e.getMessage());
        }
    }

    private void respond() {
        boolean running = true;
        while (running) {
            try {
                out.print("localhost: ");
                Scanner sc = new Scanner(System.in);
                String answ;
                answ = sc.nextLine();
                byte[] sendData = answ.getBytes();
                DatagramPacket outServerPacket = new DatagramPacket(sendData, sendData.length,
                        address, clientPort);
                datagramSocket.send(outServerPacket);
            } catch (IOException e) {
                out.print(e.getMessage());
                running = false;
            }
        }
    }

    public static void main(String[] args) {
        try {
            Server server = new Server(Integer.parseInt(args[0]));

            new Thread(server::listener).start();

            new Thread(server::respond).start();

        } catch (Exception e) {
            out.print(e.getMessage());
        }
    }
}
