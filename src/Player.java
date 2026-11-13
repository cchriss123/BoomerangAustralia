import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.*;

public class Player {
    public int playerID;
    public boolean online;
    public boolean isBot;
    public Socket connection;
    public ObjectInputStream inFromClient;
    public ObjectOutputStream outToClient;
    public List<String> region = new ArrayList<>();
    Scanner scanner = new Scanner(System.in);
    public List<Card> nextHand = new ArrayList<>();
    public List<Card> hand = new ArrayList<>();
    public List<Card> draft = new ArrayList<>();
    HashMap<String, String> sites = new HashMap<>(); //letter, region
    List<Map<String, Integer>> rScore = new ArrayList<>();
    HashMap<String, Integer> activitiesScore = new HashMap<>();
    public int regionRoundScore = 0;
    public int finalScore = 0;


    public Player(int playerID, boolean isBot, Socket connection, ObjectInputStream inFromClient, ObjectOutputStream outToClient) {
        this.playerID = playerID;
        this.connection = connection;
        this.inFromClient = inFromClient;
        this.outToClient = outToClient;
        this.isBot = isBot;
        this.online = connection != null;
    }

    public void sendMessage(Object message) {
        if (isBot) {
            return;
        }

        if (!online) {
            System.out.println(message);
            return;
        }

        try {
            outToClient.writeObject(message);
        } catch (IOException e) {
            System.err.println("Failed to send message: " + e.getMessage());
        }
    }

    public String readMessage() {
        if (!online) return scanner.nextLine();

        try {
            return (String) inFromClient.readObject();

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Failed to read message: " + e.getMessage());
            return "";
        }
    }


}

//import java.util.*;
//
//public class Player {
//    public final int playerID;
//    public final boolean isBot;
//
//    public final List<String> region = new ArrayList<>();
//    public final List<Card> nextHand = new ArrayList<>();
//    public final List<Card> hand = new ArrayList<>();
//    public final List<Card> draft = new ArrayList<>();
//
//    final Map<String, String> sites = new HashMap<>(); //letter, region
//    final List<Map<String, Integer>> rScore = new ArrayList<>();
//    final Map<String, Integer> activitiesScore = new HashMap<>();
//
//    public int regionRoundScore = 0;
//    public int finalScore = 0;
//
//    public Player(int playerID, boolean isBot) {
//        this.playerID = playerID;
//        this.isBot = isBot;
//    }
//}