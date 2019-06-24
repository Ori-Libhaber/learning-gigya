package com.ted.fileService.service;

import java.time.LocalDateTime;

public class ExecutionDecider {

    public boolean canExecute() {
        switch (GetTimeOfDay()) {
            case EVENING:
            case NIGHT:
                return true;
            case MORNING:
            case AFTERNOON:
                return false;
        }
        return false;
    }

    private TimeOfDay GetTimeOfDay() {
        LocalDateTime time = LocalDateTime.now();
        if (time.getHour() >= 0 && time.getHour() < 6) {
            return TimeOfDay.NIGHT;
        }
        if (time.getHour() >= 6 && time.getHour() < 12) {
            return TimeOfDay.MORNING;
        }
        if (time.getHour() >= 12 && time.getHour() < 18) {
            return TimeOfDay.AFTERNOON;
        }
        return TimeOfDay.EVENING;
    }

    private enum TimeOfDay {
        MORNING, AFTERNOON, EVENING, NIGHT
    }

}
