import model.Activities;
import model.Card;
import model.Player;
import model.Regions;

import java.util.*;
import java.io.*;
import java.util.concurrent.*;

public class BoomerangAustralia {

    public final List<Player> players = new ArrayList<>();
    private final GameServer gameServer = new GameServer();
    private static final int CARDS_PER_HAND = 7;
    private static final int DRAFTING_TURNS = CARDS_PER_HAND - 1;
    private static final int NUMBER_OF_ROUNDS = 4;

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

            game.runGame(numPlayers, numBots);
        } catch (NumberFormatException _) {
            System.err.println("model.Player counts must be valid integers.");
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
            gameServer.sendMessage(player, "You haven't drafted any cards yet");
        } else {
            gameServer.sendMessage(player, "%n********************************%nYour current draft: %n%s"
                    .formatted(GameFormatter.formatCards(player.getDraft())));
        }

        if (!player.getSites().isEmpty()) {
            TreeMap<String, String> sortedSites = new TreeMap<>(player.getSites());
            gameServer.sendMessage(player, "Sites from previous rounds:%n%s%n"
                    .formatted(sortedSites));
        }

        // Print the list of cards in the hand
        gameServer.sendMessage(player, "%nYour current hand: %n%s"
                .formatted(GameFormatter.formatCards(player.getHand())));
    }

    @SuppressWarnings("java:S2245")
    private String chooseCard(Player player) {
        // Prompt the user for which cards to keep (remove from hand and add to draft)
        if (player.isBot()) {
            return player.getHand()
                    .get(ThreadLocalRandom.current().nextInt(player.getHand().size()))
                    .letter();
        }

        gameServer.sendMessage(player, "Type the letter of the card to draft");

        while (true) {
            String response = gameServer.readMessage(player);

            for (Card card : player.getHand()) {
                if (card.letter().equalsIgnoreCase(response)) {
                    return response;
                }
            }

            gameServer.sendMessage(player, "Invalid card. Choose a card from your hand.");
        }
    }


    private void scoreRound(Player player) {
        HashMap<String, Integer> score = new HashMap<>();

        //Requirement 10a
        int throwCatchScore = ScoreCalculator.calculateThrowCatchScore(player.getDraft());

        gameServer.sendMessage(player, "This round you scored %d as your Throw and catch score".formatted(throwCatchScore));
        score.put("Throw and Catch score", throwCatchScore);

        //Requirement 10b
        int thisRoundSites = player.countNewSites();
        gameServer.sendMessage(player, "This round you scored %d new sites points and %d points for completing regions"
                .formatted(thisRoundSites, player.getRegionRoundScore()));
        score.put("Tourist sites score", thisRoundSites);

        //Requirement 10c - Calculate score for CollectionTypes
        int countRoundCollections = ScoreCalculator.calculateCollectionScore(player.getDraft());
        gameServer.sendMessage(player, "This round you scored %d collection points".formatted(countRoundCollections));
        score.put("Collection score", countRoundCollections);

        //Requirement 10d Calculate score for
        int countRoundAnimals = ScoreCalculator.calculateAnimalScore(player.getDraft());
        gameServer.sendMessage(player, "This round you scored %d animal points".formatted(countRoundAnimals));
        score.put("Animals score", countRoundAnimals);

        //Requirement 10e Calculate score for the Activities if the player wants to score it
        Map<String, Integer> availableActivities = getAvailableActivities(player);

        gameServer.sendMessage(player, "This round you have gathered the following new activities:");
        gameServer.sendMessage(player, "%s\nSelect if you wish to score one of them"
                .formatted(GameFormatter.formatActivities(availableActivities)));

        scoreActivities(player, availableActivities);

        player.getRScore().add(score);
        player.setRegionRoundScore(0); //it has been added to the region list, and will be summed up at the end of the 4 rounds.

    }

    private Map<String, Integer> getAvailableActivities(Player player) {
        Map<String, Integer> newActivitiesMap = new LinkedHashMap<>();

        for (String thisAct : Activities.ALL) { //Requirement 10e(ii)
            if (!player.getActivitiesScore().containsKey(thisAct)) {
                int frequency = ScoreCalculator.countMatchingCards(
                        player.getDraft(), thisAct, Card::activities);

                newActivitiesMap.put(thisAct, frequency);
            }
        }

        return newActivitiesMap;
    }

    private int finalScore(Player player) {
        int regionBonus = player.getCompletedRegions().size() * 3;
        int totalScore = ScoreCalculator.calculateFinalScore(
                player.getRScore(), player.getActivitiesScore(), regionBonus);

        //Requirement 12
        String scoreboard = GameFormatter.formatScore(
                player.getRScore(), player.getActivitiesScore(), regionBonus);
        gameServer.sendMessage(player, "\n*********************************************\n" + scoreboard);

        return totalScore;
    }

    private void scoreActivities(Player player, Map<String, Integer> newActivitiesMap) {
        for (Map.Entry<String, Integer> activity : newActivitiesMap.entrySet()) {
            int activityScore = ScoreCalculator.calculateActivityScore(activity.getValue());

            gameServer.sendMessage(player, "Want to keep %s(%d) [%d points]? (Y/N)"
                    .formatted(activity.getKey(), activity.getValue(), activityScore));
            String response = player.isBot() ? "Y" : gameServer.readMessage(player);

            if (response.equalsIgnoreCase("Y")) {
                player.getActivitiesScore().put(activity.getKey(), activityScore);
                gameServer.sendMessage(player, "This round you scored this activity: %s[%d points]"
                        .formatted(activity.getKey(), activityScore));

                return; //Requirement 10e(i) Only one activity can be scored per round
            }
        }
    }

    private List<Player> createLocalPlayers(int numBots) {
        List<Player> localPlayers = new ArrayList<>();

        localPlayers.add(new Player(0, false));

        for (int i = 0; i < numBots; i++) {
            localPlayers.add(new Player(i + 1, true));
        }

        return localPlayers;
    }



    public void runGame(int numPlayers, int numBots) {

        List<String> regions = Regions.ALL;

        Set<String> finishedRegions = new LinkedHashSet<>();

        try {
            List<Card> deck = DeckFactory.createDeck();  //Requirement 2

            Collections.shuffle(deck); //Requirement 3

            players.addAll(createLocalPlayers(numBots));
            gameServer.acceptPlayers(players, numPlayers, numBots);
            dealCardsToPlayers(deck);

            //Requirement 4
            //Requirement 12 - play 4 rounds
            for (int round = 0; round < NUMBER_OF_ROUNDS; round++) {
                draftRound();
                updateCompletedRegions(regions, finishedRegions);
                scorePlayers();
                announceCompletedRegions(finishedRegions);

                if (round < NUMBER_OF_ROUNDS - 1) {
                    prepareNextRound();
                }
            }

            calculateFinalScores();
            announceWinner();

        } catch (IllegalStateException e) {
            System.err.println("Game ended: " + e.getMessage());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } finally {
            gameServer.closeConnections();
        }

    }

    private void announceWinner() {
        Player highScore = ScoreCalculator.getHighestScoringPlayer(players);

        for (Player player : players) {
            gameServer.sendMessage(player, "The winner is player: %d with %d points"
                    .formatted(players.indexOf(highScore), highScore.getFinalScore()));
        }
    }

    private void prepareNextRound() {
        List<Card> deck = DeckFactory.createDeck(); //Requirement 11 & Requirement 2
        Collections.shuffle(deck); //Requirement 3
        //Requirement 4
        for (Player player : players) {
            player.getDraft().clear();
            player.getHand().clear();
        }
        dealCardsToPlayers(deck);
    }

    private void dealCardsToPlayers(List<Card> deck) {
        for (Player player : players) {
            for (int i = 0; i < CARDS_PER_HAND; i++) {
                player.getHand().add(deck.removeFirst());
            }
        }
    }

    private void draftRound(){
        for (int i = 0; i < DRAFTING_TURNS; i++) { //Requirement 8
            executeDraftingTurn(i);
            passRemainingCards();

            // Requirement 5 (Keep throw card hidden)
            if (i > 0) {
                showDraftedCards();
            }
        }
    }

    private void executeDraftingTurn(int turn) {
        try (ExecutorService executor = Executors.newFixedThreadPool(players.size())) {
            for (int p = 0; p < players.size(); p++) {
                Player player = players.get(p);
                Player receiver = getReceivingPlayer(p, turn);

                executor.execute(() -> addCardToDraft(player, receiver));
            }
        }
    }


    private void passRemainingCards() {
        for (Player sendToPlayer : players) {
            sendToPlayer.getHand().clear();
            // grab the cards passed on from the previous player
            sendToPlayer.getHand().addAll(sendToPlayer.getNextHand());
        }
    }

    private void showDraftedCards() {
        // Requirement 7 (Show cards)
        for (int i = 0; i < players.size(); i++) {
            List<Card> draft = players.get(i).getDraft();
            List<Card> visibleCards = draft.subList(1, draft.size());

            String message = "%nPlayer %d has drafted%n%s"
                    .formatted(i, GameFormatter.formatCards(visibleCards));

            players.forEach(player -> gameServer.sendMessage(player, message));
        }
    }

    private Player getReceivingPlayer(int playerIndex, int turn) {
        // Requirement 6 (Pass remaining cards to the next player)
        // Requirement 9 (The final card is passed to the previous player)

        // If this isn't the last drafting turn, pass forward. Otherwise, pass backward.
        return turn < DRAFTING_TURNS -1
                ? players.get((playerIndex + 1) % players.size())
                : players.get((playerIndex - 1 + players.size()) % players.size());
    }

    private void updateCompletedRegions(List<String> regions, Set<String> finishedRegions) {
        for (String region : regions) {
            if (finishedRegions.contains(region)) {
                continue;
            }

            boolean regionComplete = false;

            for (Player player : players) {
                if (RegionChecker.isRegionComplete(region, player.getSites(), player.getDraft())) {
                    player.getCompletedRegions().add(region);
                    player.setRegionRoundScore(player.getRegionRoundScore() + 3);
                    regionComplete = true;
                }
            }

            if (regionComplete) {
                finishedRegions.add(region); //Requirement 10b(ii)
            }
        }
    }

    private void calculateFinalScores() {
        for (Player player : players) {
            player.setFinalScore(finalScore(player));
        }
    }

    private void scorePlayers() {
        for (Player player : players) {
            player.getDraft().add(player.getHand().removeFirst()); // Catch card

            gameServer.sendMessage(player, """
            ********************************
            Your draft this round:
            """ + GameFormatter.formatCards(player.getDraft()) + "\n");

            scoreRound(player);
        }
    }

    private void announceCompletedRegions(Set<String> finishedRegions) {
        for (Player player : players) {
            gameServer.sendMessage(player,
                    "The following regions have now been completed: " + finishedRegions);
        }
    }
}