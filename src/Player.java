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

    public void addCardToDraft(Player sendToPlayer) {
        // Print the list of cards in the draft
        if (draft.isEmpty()) {
            sendMessage("You haven't drafted any cards yet");
        } else {
            sendMessage("\n*****************************\nYour current draft: \n" + CardFormatter.format(draft));
        }
        if (!sites.isEmpty()) {
            TreeMap<String, String> sortedSites = new TreeMap<>(sites);
            sendMessage("Sites from previous rounds:\n" + sortedSites + "\n");

        }
        // Print the list of cards in the hand
        sendMessage("\nYour current hand: \n" + CardFormatter.format(hand));
        // Prompt the user for which cards to keep (remove from hand and add to draft)
        sendMessage("Type the letter of the card to draft");
        String response = "";
        if (!isBot) {
            response = readMessage();
        } else {
            Random rnd = new Random();
            response = hand.get(rnd.nextInt(hand.size() - 1)).letter();
        }
        for (Card c : hand) {
            if (c.letter().equalsIgnoreCase(response)) {
                draft.add(hand.remove(hand.indexOf(c)));
                break;
            }
        }
        sendToPlayer.nextHand.clear();
        //pass my hand to the next player
        sendToPlayer.nextHand.addAll(hand);
    }
}