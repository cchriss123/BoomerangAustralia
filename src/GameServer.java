import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.io.IOException;

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

    public void acceptPlayers(List<Player> players, int numberPlayers, int numberOfBots) throws IOException {

        // No remote players? No server socket needed.
        if (numberPlayers <= 1) {
            return;
        }

        try (ServerSocket serverSocket = new ServerSocket(2048)) {
            for (int i = numberOfBots + 1; i < numberPlayers + numberOfBots; i++) {
                Socket connectionSocket = serverSocket.accept();

                ObjectInputStream inFromClient = new ObjectInputStream(connectionSocket.getInputStream());

                ObjectOutputStream outToClient = new ObjectOutputStream(connectionSocket.getOutputStream());

                Player player = new Player(i, false);
                players.add(player);

                connections.put(player, connectionSocket);
                inputStreams.put(player, inFromClient);
                outputStreams.put(player, outToClient);

                System.out.println("Connected to player " + i);
                outToClient.writeObject("You connected to the server as player " + i + "\n");
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