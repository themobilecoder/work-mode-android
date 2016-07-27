package com.rafaelkarlo.workmode.mainscreen.service.alarm;

import org.joda.time.LocalDateTime;

public interface AlarmService {
    void setRepeatingAlarmForDateTimeWithIdentifier(LocalDateTime triggerDateTime, String identifier);
    void cancelAlarmWithIdentifier(String identifier);
}
