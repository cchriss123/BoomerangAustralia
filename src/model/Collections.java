package model;

import java.util.List;

public final class Collections {

    public static final String LEAVES = "Leaves";
    public static final String SHELLS = "Shells";
    public static final String WILDFLOWERS = "Wildflowers";
    public static final String SOUVENIRS = "Souvenirs";

    public static final List<String> ALL = List.of(
            LEAVES,
            WILDFLOWERS,
            SHELLS,
            SOUVENIRS
    );

    private Collections() {}
}