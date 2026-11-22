import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class GameClient {

    public void run(String ipAddress) {
        try (Socket socket = new Socket(ipAddress, 2048);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            Scanner input = new Scanner(System.in);
            String message = "";

            while (!message.contains("winner")) {
                message = (String) in.readObject();
                System.out.println(message);

                if (message.contains("Type") || message.contains("keep")) {
                    out.writeObject(input.nextLine());
                }
            }

        } catch (IOException e) {
            System.err.println("Connection error: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("Unknown message type: " + e.getMessage());
        }
    }
}