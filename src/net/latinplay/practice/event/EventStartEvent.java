package net.latinplay.practice.event;

import net.latinplay.practice.events.PracticeEvent;

public class EventStartEvent extends BaseEvent
{
    private final PracticeEvent event;

    public PracticeEvent getEvent() {
        return this.event;
    }

    public EventStartEvent(final PracticeEvent event) {
        this.event = event;
    }
}
