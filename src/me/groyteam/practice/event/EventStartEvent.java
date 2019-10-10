package me.groyteam.practice.event;

import me.groyteam.practice.events.PracticeEvent;

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
