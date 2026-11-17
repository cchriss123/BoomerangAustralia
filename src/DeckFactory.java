import model.Card;

import java.util.ArrayList;
import java.util.List;

import static model.Regions.*;
import static model.Activities.*;
import static model.CollectionTypes.*;
import static model.Animals.*;

public final class DeckFactory {

    private DeckFactory() {}

    public static List<Card> createDeck() {
        return new ArrayList<>(List.of(
                new Card("The Bungle Bungles", "A", WESTERN_AUSTRALIA, 1, LEAVES, "", INDIGENOUS_CULTURE),
                new Card("The Pinnacles", "B", WESTERN_AUSTRALIA, 1, "", KANGAROOS, SIGHTSEEING),
                new Card("Margaret River", "C", WESTERN_AUSTRALIA, 1, SHELLS, KANGAROOS, ""),
                new Card("Kalbarri National Park", "D", WESTERN_AUSTRALIA, 1, WILDFLOWERS, "", BUSHWALKING),

                new Card("Uluru", "E", NORTHERN_TERRITORY, 4, "", EMUS, INDIGENOUS_CULTURE),
                new Card("Kakadu National Park", "F", NORTHERN_TERRITORY, 4, "", WOMBATS, SIGHTSEEING),
                new Card("Nitmiluk National Park", "G", NORTHERN_TERRITORY, 4, SHELLS, PLATYPUSES, ""),
                new Card("King's Canyon", "H", NORTHERN_TERRITORY, 4, "", KOALAS, SWIMMING),

                new Card("The Great Barrier Reef", "I", QUEENSLAND, 6, WILDFLOWERS, "", SIGHTSEEING),
                new Card("The Whitsundays", "J", QUEENSLAND, 6, "", KANGAROOS, INDIGENOUS_CULTURE),
                new Card("Daintree Rainforest", "K", QUEENSLAND, 6, SOUVENIRS, "", BUSHWALKING),
                new Card("Surfers Paradise", "L", QUEENSLAND, 6, WILDFLOWERS, "", SWIMMING),

                new Card("Barossa Valley", "M", SOUTH_AUSTRALIA, 3, "", KOALAS, BUSHWALKING),
                new Card("Lake Eyre", "N", SOUTH_AUSTRALIA, 3, "", EMUS, SWIMMING),
                new Card("Kangaroo Island", "O", SOUTH_AUSTRALIA, 3, "", KANGAROOS, BUSHWALKING),
                new Card("Mount Gambier", "P", SOUTH_AUSTRALIA, 3, WILDFLOWERS, "", SIGHTSEEING),

                new Card("Blue Mountains", "Q", NEW_SOUTH_WALES, 5, "", WOMBATS, INDIGENOUS_CULTURE),
                new Card("Sydney Harbour", "R", NEW_SOUTH_WALES, 5, "", EMUS, SIGHTSEEING),
                new Card("Bondi Beach", "S", NEW_SOUTH_WALES, 5, "", WOMBATS, SWIMMING),
                new Card("Hunter Valley", "T", NEW_SOUTH_WALES, 5, "", EMUS, BUSHWALKING),

                new Card("Melbourne", "U", VICTORIA, 2, "", WOMBATS, BUSHWALKING),
                new Card("The MCG", "V", VICTORIA, 2, LEAVES, "", INDIGENOUS_CULTURE),
                new Card("Twelve Apostles", "W", VICTORIA, 2, SHELLS, "", SWIMMING),
                new Card("Royal Exhibition Building", "X", VICTORIA, 2, LEAVES, PLATYPUSES, ""),

                new Card("Salamanca Markets", "Y", TASMANIA, 7, LEAVES, EMUS, ""),
                new Card("Mount Wellington", "Z", TASMANIA, 7, "", KOALAS, SIGHTSEEING),
                new Card("Port Arthur", "*", TASMANIA, 7, LEAVES, "", INDIGENOUS_CULTURE),
                new Card("Richmond", "-", TASMANIA, 7, "", KANGAROOS, SWIMMING)
        ));
    }
}