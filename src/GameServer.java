import model.Player;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class GameServer {

    private final Map<Player, Socket> connections = new HashMap<>();
    private final Map<Player, ObjectInputStream> inputStreams = new HashMap<>();
    private final Map<Player, ObjectOutputStream> outputStreams = new HashMap<>();
    private final Scanner scanner = new Scanner(System.in);


    public void sendMessage(Player player, Object message) {
        if (player.isBot()) {
            return;
        }

        ObjectOutputStream out = outputStreams.get(player);

        if (out == null) {
            System.out.println(message);
            return;
        }

        try {
            out.writeObject(message);
        } catch (IOException e) {
            System.err.println("Failed to send message: " + e.getMessage());
        }
    }

    public String readMessage(Player player) {
        ObjectInputStream in = inputStreams.get(player);

        if (in == null) {
            return scanner.nextLine();
        }

        try {
            return (String) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Failed to read message: " + e.getMessage());
            return "";
        }
    }

    public void acceptPlayers(List<Player> players, int numberPlayers, int numberOfBots, CountDownLatch serverReady) throws IOException {

        try (ServerSocket serverSocket = new ServerSocket(2048)) {
            serverReady.countDown();
            System.out.println("Server started on port 2048");
            System.out.printf("Waiting for %d human player(s)...%n", numberPlayers);

            for (int i = 0; i < numberPlayers; i++) {
                Socket socket = serverSocket.accept();

                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

                int playerId = i == 0 ? 0 : numberOfBots + i;

                Player player = new Player(playerId, false);

                if (i == 0) players.addFirst(player);
                else players.add(player);

                connections.put(player, socket);
                inputStreams.put(player, in);
                outputStreams.put(player, out);

                System.out.println("Connected to player " + playerId);
                out.writeObject("You connected as Player " + playerId);
            }
        }
    }

    public void closeConnections() {
        for (Socket connection : connections.values()) {
            try {
                connection.close();
            } catch (IOException e) {
                System.err.println("Failed to close connection: " + e.getMessage());
            }
        }

        connections.clear();
        inputStreams.clear();
        outputStreams.clear();
    }


}