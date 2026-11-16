import model.Card;

import java.util.ArrayList;
import java.util.List;

public final class DeckFactory {

    private DeckFactory() {}

    public static List<Card> createDeck() {
        return new ArrayList<>(List.of(
                new Card("The Bungle Bungles", "A", "Western Australia", 1, "Leaves", "", "Indigenous Culture"),
                new Card("The Pinnacles", "B", "Western Australia", 1, "", "Kangaroos", "Sightseeing"),
                new Card("Margaret River", "C", "Western Australia", 1, "Shells", "Kangaroos", ""),
                new Card("Kalbarri National Park", "D", "Western Australia", 1, "Wildflowers", "", "Bushwalking"),

                new Card("Uluru", "E", "Northern Territory", 4, "", "Emus", "Indigenous Culture"),
                new Card("Kakadu National Park", "F", "Northern Territory", 4, "", "Wombats", "Sightseeing"),
                new Card("Nitmiluk National Park", "G", "Northern Territory", 4, "Shells", "Platypuses", ""),
                new Card("King's Canyon", "H", "Northern Territory", 4, "", "Koalas", "Swimming"),

                new Card("The Great Barrier Reef", "I", "Queensland", 6, "Wildflowers", "", "Sightseeing"),
                new Card("The Whitsundays", "J", "Queensland", 6, "", "Kangaroos", "Indigenous Culture"),
                new Card("Daintree Rainforest", "K", "Queensland", 6, "Souvenirs", "", "Bushwalking"),
                new Card("Surfers Paradise", "L", "Queensland", 6, "Wildflowers", "", "Swimming"),

                new Card("Barossa Valley", "M", "South Australia", 3, "", "Koalas", "Bushwalking"),
                new Card("Lake Eyre", "N", "South Australia", 3, "", "Emus", "Swimming"),
                new Card("Kangaroo Island", "O", "South Australia", 3, "", "Kangaroos", "Bushwalking"),
                new Card("Mount Gambier", "P", "South Australia", 3, "Wildflowers", "", "Sightseeing"),

                new Card("Blue Mountains", "Q", "New South Whales", 5, "", "Wombats", "Indigenous Culture"),
                new Card("Sydney Harbour", "R", "New South Whales", 5, "", "Emus", "Sightseeing"),
                new Card("Bondi Beach", "S", "New South Whales", 5, "", "Wombats", "Swimming"),
                new Card("Hunter Valley", "T", "New South Whales", 5, "", "Emus", "Bushwalking"),

                new Card("Melbourne", "U", "Victoria", 2, "", "Wombats", "Bushwalking"),
                new Card("The MCG", "V", "Victoria", 2, "Leaves", "", "Indigenous Culture"),
                new Card("Twelve Apostles", "W", "Victoria", 2, "Shells", "", "Swimming"),
                new Card("Royal Exhibition Building", "X", "Victoria", 2, "Leaves", "Platypuses", ""),

                new Card("Salamanca Markets", "Y", "Tasmania", 7, "Leaves", "Emus", ""),
                new Card("Mount Wellington", "Z", "Tasmania", 7, "", "Koalas", "Sightseeing"),
                new Card("Port Arthur", "*", "Tasmania", 7, "Leaves", "", "Indigenous Culture"),
                new Card("Richmond", "-", "Tasmania", 7, "", "Kangaroos", "Swimming")
        ));
    }
}