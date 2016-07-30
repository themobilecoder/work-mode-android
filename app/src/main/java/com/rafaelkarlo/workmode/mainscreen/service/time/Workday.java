package com.rafaelkarlo.workmode.mainscreen.service.time;

public enum WorkDay {

    SUNDAY(7, "Su"),
    MONDAY(1, "M"),
    TUESDAY(2, "T"),
    WEDNESDAY(3, "W"),
    THURSDAY(4, "Th"),
    FRIDAY(5, "F"),
    SATURDAY(6, "Sa"),
    UNKNOWN(-1, "");

    private final int value;
    private final String shortenDayString;

    WorkDay(int value, String shortenDayString) {
        this.value = value;
        this.shortenDayString = shortenDayString;
    }

    public int getValue() {
        return value;
    }

    public static WorkDay getDayFromValue(int dayValue) {
        switch (dayValue) {
            case 7:
                return SUNDAY;
            case 1:
                return MONDAY;
            case 2:
                return TUESDAY;
            case 3:
                return WEDNESDAY;
            case 4:
                return THURSDAY;
            case 5:
                return FRIDAY;
            case 6:
                return SATURDAY;
            default:
                return UNKNOWN;
        }
    }

    public String getShortenDayString() {
        return shortenDayString;
    }
}
