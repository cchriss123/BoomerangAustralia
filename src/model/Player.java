package model;

import java.util.*;

public class Player {
    private final int playerID;
    private final boolean isBot;
    private final List<String> region = new ArrayList<>();
    private final List<Card> nextHand = new ArrayList<>();
    private final List<Card> hand = new ArrayList<>();
    private final List<Card> draft = new ArrayList<>();
    private final Map<String, String> sites = new HashMap<>(); //letter, region
    private final List<Map<String, Integer>> rScore = new ArrayList<>();
    private final Map<String, Integer> activitiesScore = new HashMap<>();
    private int regionRoundScore = 0;
    private int finalScore = 0;

    public Player(int playerID, boolean isBot) {
        this.playerID = playerID;
        this.isBot = isBot;
    }

    public void draftCard(String letter) {
        for (Card card : hand) {
            if (card.letter().equalsIgnoreCase(letter)) {
                hand.remove(card);
                draft.add(card);
                return;
            }
        }
    }

    public int countNewSites() {
        int thisRoundSites = 0;

        for (Card draftCard : draft) {
            if (!sites.containsKey(draftCard.letter())) {
                thisRoundSites++;
                sites.put(draftCard.letter(), draftCard.region());
            }
        }

        return thisRoundSites;
    }

    // Getters and setters

    public int getPlayerID() {
        return playerID;
    }

    public boolean isBot() {
        return isBot;
    }

    public List<String> getRegion() {
        return region;
    }

    public List<Card> getNextHand() {
        return nextHand;
    }

    public List<Card> getHand() {
        return hand;
    }

    public List<Card> getDraft() {
        return draft;
    }

    public Map<String, String> getSites() {
        return sites;
    }

    public List<Map<String, Integer>> getRScore() {
        return rScore;
    }

    public Map<String, Integer> getActivitiesScore() {
        return activitiesScore;
    }

    public int getRegionRoundScore() {
        return regionRoundScore;
    }

    public void setRegionRoundScore(int regionRoundScore) {
        this.regionRoundScore = regionRoundScore;
    }

    public int getFinalScore() {
        return finalScore;
    }

    public void setFinalScore(int finalScore) {
        this.finalScore = finalScore;
    }


}
