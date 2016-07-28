package com.rafaelkarlo.workmode.mainscreen.service.time;

public enum Workday {

    SUNDAY(7),
    MONDAY(1),
    TUESDAY(2),
    WEDNESDAY(3),
    THURSDAY(4),
    FRIDAY(5),
    SATURDAY(6),
    UNKNOWN(-1);

    private final int value;

    Workday(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static Workday getDayFromValue(int dayValue) {
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
}
