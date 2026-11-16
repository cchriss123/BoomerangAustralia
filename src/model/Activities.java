package model;

import java.util.List;

public final class Activities {

    public static final String INDIGENOUS_CULTURE = "Indigenous Culture";
    public static final String BUSHWALKING = "Bushwalking";
    public static final String SWIMMING = "Swimming";
    public static final String SIGHTSEEING = "Sightseeing";

    public static final List<String> ALL = List.of(
            INDIGENOUS_CULTURE,
            BUSHWALKING,
            SWIMMING,
            SIGHTSEEING
    );

    private Activities() {}
}