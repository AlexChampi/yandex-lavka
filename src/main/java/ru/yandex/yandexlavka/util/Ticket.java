package ru.yandex.yandexlavka.util;

import ru.yandex.yandexlavka.model.TimeInterval;

public class Ticket {
    TimeInterval timeInterval;
    boolean isUsed;

    public Ticket(TimeInterval timeInterval) {
        this.timeInterval = timeInterval;
        this.isUsed = false;
    }

    public TimeInterval getTimeInterval() {
        return timeInterval;
    }

    public boolean isUsed() {
        return isUsed;
    }

    public void setUsed(boolean used) {
        isUsed = used;
    }
}
