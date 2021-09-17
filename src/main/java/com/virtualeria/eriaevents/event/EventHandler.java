package com.virtualeria.eriaevents.event;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public final class EventHandler {
  final static Map<String, Event> activeEvents = new HashMap<>();

  public void startEvent(Event event) {
    if(activeEvents.containsKey(event.getUid())) {
      throw new EventException();
    }
    event.start();
    activeEvents.put(event.getUid(),event);
  }

  public class EventException extends RuntimeException {}

  public static List<Event> activeEvents() {
    return activeEvents.values().stream().toList();
  }

  public static Set<String> activeEventUids() {
    return activeEvents.keySet();
  }

  public static String generateRandomUID() {
    return UUID.randomUUID().toString();
  }
}
