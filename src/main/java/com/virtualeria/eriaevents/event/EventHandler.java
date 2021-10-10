package com.virtualeria.eriaevents.event;

import com.virtualeria.eriaevents.api.EriaRewarder;
import com.virtualeria.eriaevents.event.events.Event;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/*
 * Features:
 * Make event creation more intuitive using suggestions of brigadier
 * Craete a command that handles player groups to be able to start events referencing groups
 * Make the clear area event accept a range and a positions to spawn an area in which the event appear
 * Make a cancel event comand
 * Make a command to create givables
 * Make events handle uids that are readable
 * Make a command to retrieve event information by uid
 * Make a command to create default events with suggestions
 * Create observables in Event class on certain parts (onStart, onFinish, onCancel, etc..)
 * Make a resource multiplier event
 * Make a airdrop event
 * Handle pleayer leaving server while in event
 * Make a buff event
 * */
@NoArgsConstructor
public final class EventHandler {
  private static final Logger LOGGER = LogManager.getLogger();
  final static Map<String, BaseEvent> activeEvents = new ConcurrentHashMap<>();
  final static EriaRewarder eriaRewarder = (player, reward) -> {
    player.forEach(reward::apply);
  };

  public void startEvent(BaseEvent baseEvent) {
    Event event = baseEvent.getEvent();
    if (activeEvents.containsKey(event.getEventData().uid())) {
      throw new EventException();
    }
    baseEvent.start();
    activeEvents.put(event.getEventData().uid(), baseEvent);
  }

  public static List<BaseEvent> activeEvents() {
    return activeEvents.values().stream().toList();
  }

  public static void checkForConcludedEvents() {
    Collections.unmodifiableMap(activeEvents).values().stream().forEach(event -> {
          if (!event.canContinue()) {
            LOGGER.debug("Event timed out: %s".formatted(event.getEvent().getEventData().uid()));
            forceFinish(event);
          }
        }
    );
  }

  public static void finishEvent(BaseEvent event) {
    event.tryToFinish(
        (e) -> eriaRewarder.reward(e.getEventData().participants(), e.getEventData().prize()));
    activeEvents.remove(event.getEvent().getEventData().uid());
  }

  public static Optional<BaseEvent> activeEventByUid(String uid) {
    return activeEvents.values().stream()
        .filter(event -> event.getEvent().getEventData().uid().equals(uid)).findFirst();
  }

  public static void forceFinish(BaseEvent event) {
    event.finish();
    activeEvents.remove(event.getEvent().getEventData().uid());
  }

  public static Set<String> activeEventUids() {
    return activeEvents.keySet();
  }

  public static String generateRandomUID() {
    return UUID.randomUUID().toString();
  }

  public static class EventException extends RuntimeException {
  }
}
