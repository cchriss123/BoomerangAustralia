import java.util.*;
import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class BoomerangAustralia {

    public static void main(String[] args) {

        BoomerangAustralia game = new BoomerangAustralia();

        if (args.length != 1 && args.length != 2) {
            System.out.println("""
            This game is for a total of 2-4 players/bots
            Server syntax: java BoomerangAustralia numPlayers numBots
            Client syntax: java BoomerangAustralia IP
            """);
            return;
        }

        try {

            if (args.length == 1) {
                game.client(args[0]);
                return;
            }

            int numPlayers = Integer.parseInt(args[0]);
            int numBots = Integer.parseInt(args[1]);

            if (numPlayers < 1 || numBots < 0 || numPlayers + numBots < 2 || numPlayers + numBots > 4) {
                System.out.println("This game requires 2-4 players/bots");
                return;
            }

            game.initGame(numPlayers, numBots);
        } catch (NumberFormatException _) {
            System.err.println("Player counts must be valid integers.");
        }
    }

    public List<Player> players = new ArrayList<>();

    private int roundScore(Player player, int roundNr) {
        HashMap<String, Integer> score = new HashMap<>();
        //Fix total score and round score... Also, sum up the round score according to the scoring sheet

        //Requirement 10a
        int throwCatchScore = Math.abs(
                player.draft.get(0).number() - player.draft.get(6).number());

        player.sendMessage("This round you scored " + throwCatchScore + " as your Throw and catch score");
        score.put("Throw and Catch score", throwCatchScore);

        //Requirement 10b
        int thisRoundSites = 0;
        for (Card draftCard : player.draft) {
            if (!player.sites.containsKey(draftCard.letter())) {
                thisRoundSites++;
                player.sites.put(draftCard.letter(), draftCard.region());
            }
        }
        player.sendMessage("This round you scored " + thisRoundSites + " new sites points and " + player.regionRoundScore + " points for completing regions");
        score.put("Tourist sites score", thisRoundSites);

        //Requirement 10c - Calculate score for Collections
        int countRoundCollections = ScoreCalculator.calculateCollectionScore(player.draft);
        player.sendMessage("This round you scored " + countRoundCollections + " collection points");
        score.put("Collections score", countRoundCollections);

        //Requirement 10d Calculate score for
        int countRoundAnimals = ScoreCalculator.calculateAnimalScore(player.draft);
        player.sendMessage("This round you scored " + countRoundAnimals + " animal points");
        score.put("Animals score", countRoundAnimals);

        //TODO refactor from

        //Requirement 10e Calculate score for the Activities if the player wants to score it
        String[] act = {"Indigenous Culture", "Bushwalking", "Swimming", "Sightseeing"};
        player.sendMessage("This round you have gathered the following new activities:");
        Map<String, Integer> newActivitiesMap = new LinkedHashMap<>();
        StringBuilder newActivities = new StringBuilder();

        for (String thisAct : act) { //Requirement 10e(ii)
            if (!player.activitiesScore.containsKey(thisAct)) {
                int frequency = ScoreCalculator.countMatchingCards(player.draft, thisAct, Card::activities);
                newActivities.append(thisAct).append("(# ").append(frequency).append(")\t");
                newActivitiesMap.put(thisAct, frequency);
            }
        }

        player.sendMessage(newActivities + "\nSelect if you wish to score one of them");
        int countRoundActivities = 0;
        int[] scoreTable = {0, 2, 4, 7, 10, 15};

        for (Map.Entry<String, Integer> entry : newActivitiesMap.entrySet()) {
            int frequency = entry.getValue();
            int scoret = (frequency > 0) ? scoreTable[frequency - 1] : 0;
            player.sendMessage("Want to keep " + entry.getKey() + "(" + entry.getValue()
                    + ") [" + scoret + " points]? (Y/N)");
            String response = player.isBot ? "Y" : player.readMessage();

            if (response.equalsIgnoreCase("Y")) {
                player.activitiesScore.put(entry.getKey(), scoret);
                player.sendMessage("This round you scored this activity: "
                        + entry.getKey() + "[" + scoret + " points]");
                //We do not need to add the Activity score to the score variable, since it's stored separately in the activitiesScore hashmap
                countRoundActivities = scoret;
                break; //Requirement 10e(i) exit the loop since you are only allowed to score one activity per round
            }
        }

        //TODO refactor from
        player.rScore.add(score);

        if (roundNr == 3) {//last round
            int regionBonus = player.region.size() * 3;
            int totalScore = ScoreCalculator.calculateFinalScore(
                    player.rScore, player.activitiesScore, regionBonus);

            //Requirement 12
            String scoreboard = ScoreFormatter.format(
                    player.rScore, player.activitiesScore, regionBonus);
            player.sendMessage("\n*********************************************\n" + scoreboard);
            return totalScore;
        }

        int totalRoundScore = player.regionRoundScore + throwCatchScore + thisRoundSites
                + countRoundCollections + countRoundAnimals + countRoundActivities;

        player.regionRoundScore = 0; //it has been added to the region list, and will be summed up at the end of the 4 rounds.
        return totalRoundScore;
    }




    public void initGame(int numPlayers, int numBots) {


        String[] regions = {
                "Wester Australia", "Northern Territory", "Queensland", "South Australia", "New South Whales", "Victoria", "Tasmania"
        };

        ArrayList<String> finishedRegions = new ArrayList<>();

        try {
            List<Card> deck = DeckFactory.createDeck();  //Requirement 2

            Collections.shuffle(deck); //Requirement 3
            server(numPlayers, numBots);
            //Requirement 4
            for (Player player : players) {
                for (int i = 0; i < 7; i++) {
                    player.hand.add(deck.removeFirst());
                }
            }
            //Requirement 12 - play 4 rounds
            for (int round = 0; round < 4; round++) { //Requirement 11
                //Draft 6 cards, the 7th card is sent to the previous player's hand
                for (int i = 0; i < 6; i++) { //Requirement 8
                    ExecutorService threadpool = Executors.newFixedThreadPool(players.size());
                    for (int p = 0; p < players.size(); p++) {
                        final int currentPlayerIndex = p;
                        Player sendHand;
                        if (i < 5) {
                            // Requirement 6 (Pass remaining cards to the next player)
                            sendHand = (p < (players.size() - 1)) ? players.get(p + 1) : players.getFirst();
                        } else {
                            // Requirement 9 (The final card is passed to the previous player)
                            sendHand = (p > 0) ? players.get(p - 1) : players.getLast();
                        }
                        //Requirement 7 - Make sure every player can draft their card at the same time
                        Runnable task = () -> players.get(currentPlayerIndex).addCardToDraft(sendHand);
                        threadpool.execute(task);
                    }
                    threadpool.shutdown();

                    //wait for all the hands to switch places
                    while (!threadpool.isTerminated()) {
                        Thread.sleep(100);
                    }

                    for (Player sendToPlayer : players) {
                        sendToPlayer.hand.clear();
                        // grab the cards passed on from the previous player
                        sendToPlayer.hand.addAll(sendToPlayer.nextHand);
                    }
                    // Requirement 5 (Keep throw card hidden)
                    if (i > 0) {
                        // Requirement 7 (Show cards)
                        for (int pID = 0; pID < players.size(); pID++) {
                            for (Player sendToPlayer : players) {
                                Player id = players.get(pID);
                                sendToPlayer.sendMessage("\nPlayer " + pID + " has drafted\n" + CardFormatter.format(id.draft.subList(1, id.draft.size())));
                            }
                        }
                    }
                }
                // Check if I'm the first to complete a region, add regionBonusScore if that's the case
                //Requirement 10b(i+ii)
                for (int r = 0; r < regions.length; r++) {
                    boolean regionComplete = false;
                    for (Player player : players) {
                        if (!finishedRegions.contains(regions[r]) &&
                                RegionChecker.isRegionComplete(regions[r], player.sites, player.draft)) {
                            player.region.add(regions[r]);
                            player.regionRoundScore += 3;
                            regionComplete = true;
                        }
                    }
                    if (regionComplete) {
                        finishedRegions.add(regions[r]); //Requirement 10b(ii)
                    }
                }

                for (Player player : players) {
                    player.draft.add(player.hand.removeFirst()); //Requirement 9 - last card on hand is added to draft
                    //Requirement 10 - scoring
                    player.sendMessage("********************************\nYour draft this round: \n" + CardFormatter.format(player.draft) + "\n");
                    player.finalScore = roundScore(player, round);
                    player.sendMessage("The following regions have now been completed: " + finishedRegions);
                }

                deck = DeckFactory.createDeck(); //Requirement 11 & Requirement 2
                Collections.shuffle(deck); //Requirement 3
                //Requirement 4
                for (Player player : players) {
                    player.draft.clear();
                    player.hand.clear();
                    for (int i = 0; i < 7; i++) {
                        player.hand.add(deck.removeFirst());
                    }
                }
            }
            Player highScore = players.getFirst();
            for (Player player : players) {
                if (player.finalScore > highScore.finalScore) {
                    highScore = player;
                }
            }
            for (Player player : players) {
                player.sendMessage("The winner is player: " + players.indexOf(highScore) + " with " + highScore.finalScore + " points");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void client(String ipAddress) {
        try (Socket socket = new Socket(ipAddress, 2048);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            Scanner scanner = new Scanner(System.in);
            String message = "";

            while (!message.contains("winner")) {
                message = (String) in.readObject();
                System.out.println(message);

                if (message.contains("Type") || message.contains("keep")) {
                    out.writeObject(scanner.nextLine());
                }
            }

        } catch (IOException e) {
            System.err.println("Connection error: " + e.getMessage());

        } catch (ClassNotFoundException e) {
            System.err.println("Unknown message type: " + e.getMessage());
        }
    }

    public void server(int numberPlayers, int numberOfBots) throws IOException {

        players.add(new Player(0, false, null, null, null));

        for (int i = 0; i < numberOfBots; i++) {
            players.add(new Player(i + 1, true, null, null, null));
        }

        // No remote players? No server socket needed.
        if (numberPlayers <= 1) {
            return;
        }

        try (ServerSocket serverSocket = new ServerSocket(2048)) {
            for (int i = numberOfBots + 1; i < numberPlayers + numberOfBots; i++) {
                Socket connectionSocket = serverSocket.accept();

                ObjectInputStream inFromClient = new ObjectInputStream(connectionSocket.getInputStream());

                ObjectOutputStream outToClient = new ObjectOutputStream(connectionSocket.getOutputStream());

                players.add(new Player(i, false, connectionSocket, inFromClient, outToClient));

                System.out.println("Connected to player " + i);
                outToClient.writeObject("You connected to the server as player " + i + "\n");
            }
        }
    }
}


