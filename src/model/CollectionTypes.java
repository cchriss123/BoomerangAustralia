package model;

import java.util.Map;

public final class CollectionTypes {

    public static final String LEAVES = "Leaves";
    public static final String SHELLS = "Shells";
    public static final String WILDFLOWERS = "Wildflowers";
    public static final String SOUVENIRS = "Souvenirs";

    public static final Map<String, Integer> POINTS = Map.of(
            LEAVES, 1,
            WILDFLOWERS, 2,
            SHELLS, 3,
            SOUVENIRS, 5
    );

    private CollectionTypes() {}
}