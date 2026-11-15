import java.util.*;
import java.io.*;
import java.util.concurrent.*;

public class BoomerangAustralia {


    public final List<Player> players = new ArrayList<>();
    private final GameServer gameServer = new GameServer();

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
                game.gameServer.client(args[0]);
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


    private void addCardToDraft(Player player, Player sendToPlayer) {
        showDraftingOptions(player);
        String response = chooseCard(player);
        player.draftCard(response);

        sendToPlayer.getNextHand().clear();
        //pass my hand to the next player
        sendToPlayer.getNextHand().addAll(player.getHand());
    }

    private void showDraftingOptions(Player player) {
        // Print the list of cards in the draft
        if (player.getDraft().isEmpty()) {
            gameServer.sendMessage(player,"You haven't drafted any cards yet");
        } else {
            gameServer.sendMessage(player,"\n*****************************\nYour current draft: \n" + CardFormatter.format(player.getDraft()));
        }

        if (!player.getSites().isEmpty()) {
            TreeMap<String, String> sortedSites = new TreeMap<>(player.getSites());
            gameServer.sendMessage(player,"Sites from previous rounds:\n" + sortedSites + "\n");
        }

        // Print the list of cards in the hand
        gameServer.sendMessage(player,"\nYour current hand: \n" + CardFormatter.format(player.getHand()));
    }

    private String chooseCard(Player player) {
        // Prompt the user for which cards to keep (remove from hand and add to draft)
        if (player.isBot()) {
            return player.getHand()
                    .get(ThreadLocalRandom.current().nextInt(player.getHand().size()))
                    .letter();
        }

        gameServer.sendMessage(player,"Type the letter of the card to draft");

        while (true) {
            String response = gameServer.readMessage(player);

            for (Card card : player.getHand()) {
                if (card.letter().equalsIgnoreCase(response)) {
                    return response;
                }
            }

            gameServer.sendMessage(player,"Invalid card. Choose a card from your hand.");
        }
    }




    private int roundScore(Player player, int roundNr) {
        HashMap<String, Integer> score = new HashMap<>();
        //Fix total score and round score... Also, sum up the round score according to the scoring sheet

        //Requirement 10a
        int firstCardNumber = player.getDraft().get(0).number();
        int lastCardNumber = player.getDraft().get(6).number();
        int throwCatchScore = Math.abs(firstCardNumber - lastCardNumber);

        gameServer.sendMessage(player, "This round you scored %d as your Throw and catch score".formatted(throwCatchScore));
        score.put("Throw and Catch score", throwCatchScore);

        //Requirement 10b
        int thisRoundSites = player.countNewSites();
        gameServer.sendMessage(player, "This round you scored %d new sites points and %d points for completing regions"
                .formatted(thisRoundSites, player.getRegionRoundScore()));
        score.put("Tourist sites score", thisRoundSites);

        //Requirement 10c - Calculate score for Collections
        int countRoundCollections = ScoreCalculator.calculateCollectionScore(player.getDraft());
        gameServer.sendMessage(player, "This round you scored %d collection points".formatted(countRoundCollections));
        score.put("Collections score", countRoundCollections);

        //Requirement 10d Calculate score for
        int countRoundAnimals = ScoreCalculator.calculateAnimalScore(player.getDraft());
        gameServer.sendMessage(player, "This round you scored %d animal points".formatted(countRoundAnimals));
        score.put("Animals score", countRoundAnimals);

        //Requirement 10e Calculate score for the Activities if the player wants to score it
        Map<String, Integer> newActivitiesMap = getAvailableActivities(player);
        gameServer.sendMessage(player, "This round you have gathered the following new activities:");
        gameServer.sendMessage(player, "%s\nSelect if you wish to score one of them"
                .formatted(ActivityFormatter.format(newActivitiesMap)));

        int countRoundActivities = 0;
        for (Map.Entry<String, Integer> activity : newActivitiesMap.entrySet()) {
            int frequency = activity.getValue();
            int scoret = ScoreCalculator.calculateActivityScore(frequency);
            gameServer.sendMessage(player, "Want to keep %s(%d) [%d points]? (Y/N)"
                    .formatted(activity.getKey(), activity.getValue(), scoret));
            String response = player.isBot() ? "Y" : gameServer.readMessage(player);

            if (response.equalsIgnoreCase("Y")) {
                player.getActivitiesScore().put(activity.getKey(), scoret);
                gameServer.sendMessage(player, "This round you scored this activity: %s[%d points]"
                        .formatted(activity.getKey(), scoret));
                //We do not need to add the Activity score to the score variable, since it's stored separately in the activitiesScore hashmap
                countRoundActivities = scoret;
                break; //Requirement 10e(i) exit the loop since you are only allowed to score one activity per round
            }
        }

        player.getRScore().add(score);

        if (roundNr == 3) {//last round
            return finalScore(player);
        }

        int totalRoundScore = player.getRegionRoundScore()
                + throwCatchScore
                + thisRoundSites
                + countRoundCollections
                + countRoundAnimals
                + countRoundActivities;

        player.setRegionRoundScore(0); //it has been added to the region list, and will be summed up at the end of the 4 rounds.
        return totalRoundScore;
    }

    private Map<String, Integer> getAvailableActivities(Player player) {
        String[] act = {"Indigenous Culture", "Bushwalking", "Swimming", "Sightseeing"};
        Map<String, Integer> newActivitiesMap = new LinkedHashMap<>();

        for (String thisAct : act) { //Requirement 10e(ii)
            if (!player.getActivitiesScore().containsKey(thisAct)) {
                int frequency = ScoreCalculator.countMatchingCards(
                        player.getDraft(), thisAct, Card::activities);

                newActivitiesMap.put(thisAct, frequency);
            }
        }

        return newActivitiesMap;
    }

    private int finalScore(Player player) {
        int regionBonus = player.getRegion().size() * 3;
        int totalScore = ScoreCalculator.calculateFinalScore(
                player.getRScore(), player.getActivitiesScore(), regionBonus);

        //Requirement 12
        String scoreboard = ScoreFormatter.format(
                player.getRScore(), player.getActivitiesScore(), regionBonus);
        gameServer.sendMessage(player, "\n*********************************************\n" + scoreboard);

        return totalScore;
    }










    public void initGame(int numPlayers, int numBots) {


        String[] regions = {
                "Wester Australia", "Northern Territory", "Queensland", "South Australia", "New South Whales", "Victoria", "Tasmania"
        };

        ArrayList<String> finishedRegions = new ArrayList<>();

        try {
            List<Card> deck = DeckFactory.createDeck();  //Requirement 2

            Collections.shuffle(deck); //Requirement 3
            initializePlayers(numPlayers, numBots);
            //Requirement 4
            for (Player player : players) {
                for (int i = 0; i < 7; i++) {
                    player.getHand().add(deck.removeFirst());
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
                        Runnable task = () -> addCardToDraft(players.get(currentPlayerIndex), sendHand);
                        threadpool.execute(task);
                    }
                    threadpool.shutdown();

                    //wait for all the hands to switch places
                    while (!threadpool.isTerminated()) {
                        Thread.sleep(100);
                    }

                    for (Player sendToPlayer : players) {
                        sendToPlayer.getHand().clear();
                        // grab the cards passed on from the previous player
                        sendToPlayer.getHand().addAll(sendToPlayer.getNextHand());
                    }
                    // Requirement 5 (Keep throw card hidden)
                    if (i > 0) {
                        // Requirement 7 (Show cards)
                        for (int pID = 0; pID < players.size(); pID++) {
                            for (Player sendToPlayer : players) {
                                Player draftedPlayer = players.get(pID);
                                gameServer.sendMessage(sendToPlayer, "\nPlayer " + pID + " has drafted\n" + CardFormatter.format(draftedPlayer.getDraft().subList(1, draftedPlayer.getDraft().size())));
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
                                RegionChecker.isRegionComplete(regions[r], player.getSites(), player.getDraft())) {
                            player.getRegion().add(regions[r]);
                            player.setRegionRoundScore(player.getRegionRoundScore() + 3);
                            regionComplete = true;
                        }
                    }
                    if (regionComplete) {
                        finishedRegions.add(regions[r]); //Requirement 10b(ii)
                    }
                }

                for (Player player : players) {
                    player.getDraft().add(player.getHand().removeFirst()); //Requirement 9 - last card on hand is added to draft
                    //Requirement 10 - scoring
                    gameServer.sendMessage(player,"********************************\nYour draft this round: \n" + CardFormatter.format(player.getDraft()) + "\n");
                    player.setFinalScore(roundScore(player, round));
                    gameServer.sendMessage(player,"The following regions have now been completed: " + finishedRegions);
                }

                deck = DeckFactory.createDeck(); //Requirement 11 & Requirement 2
                Collections.shuffle(deck); //Requirement 3
                //Requirement 4
                for (Player player : players) {
                    player.getDraft().clear();
                    player.getHand().clear();
                    for (int i = 0; i < 7; i++) {
                        player.getHand().add(deck.removeFirst());
                    }
                }
            }
            Player highScore = players.getFirst();
            for (Player player : players) {
                if (player.getFinalScore() > highScore.getFinalScore()) {
                    highScore = player;
                }
            }
            for (Player player : players) {
                gameServer.sendMessage(player,"The winner is player: " + players.indexOf(highScore) + " with " + highScore.getFinalScore() + " points");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            gameServer.closeConnections();
        }
    }


    public void initializePlayers(int numberPlayers, int numberOfBots) throws IOException {
        players.add(new Player(0, false));

        for (int i = 0; i < numberOfBots; i++) {
            players.add(new Player(i + 1, true));
        }

        gameServer.acceptPlayers(players, numberPlayers, numberOfBots);
    }
}



