package model;

import java.util.List;

public final class Regions {

    public static final String WESTERN_AUSTRALIA = "Western Australia";
    public static final String NORTHERN_TERRITORY = "Northern Territory";
    public static final String QUEENSLAND = "Queensland";
    public static final String SOUTH_AUSTRALIA = "South Australia";
    public static final String NEW_SOUTH_WALES = "New South Whales";
    public static final String VICTORIA = "Victoria";
    public static final String TASMANIA = "Tasmania";

    public static final List<String> ALL = List.of(
            WESTERN_AUSTRALIA,
            NORTHERN_TERRITORY,
            QUEENSLAND,
            SOUTH_AUSTRALIA,
            NEW_SOUTH_WALES,
            VICTORIA,
            TASMANIA
    );




    private Regions() {}
}