import model.GameMessage;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import model.MessageType;

public class GameClient {

    public void run(String ipAddress) {
        try (Socket socket = new Socket(ipAddress, 2048);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            Scanner input = new Scanner(System.in);

            while (true) {
                GameMessage message = (GameMessage) in.readObject();

                System.out.println(message.text());

                if (message.type() == MessageType.REQUEST_INPUT) {
                    out.writeObject(input.nextLine());
                } else if (message.type() == MessageType.GAME_OVER) {
                    return;
                }
            }

        } catch (IOException e) {
            System.err.println("Connection error: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("Unknown message type: " + e.getMessage());
        }
    }
}