package com.virtualeria.eriaevents.event;

import com.virtualeria.eriaevents.event.events.Event;
import java.util.function.Consumer;

public interface BaseEvent {
  void start();

  void tryToFinish(Consumer<Event> rewarderAction);

  void finish();

  Event getEvent();
}
