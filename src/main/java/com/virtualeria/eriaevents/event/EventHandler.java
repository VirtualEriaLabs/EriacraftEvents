package com.virtualeria.eriaevents.event;

import com.virtualeria.eriaevents.api.EriaRewarder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.NoArgsConstructor;


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
  final static Map<String, Event> activeEvents = new HashMap<>();
  final static EriaRewarder eriaRewarder = (player, reward) -> {
    player.forEach(reward::apply);
  };

  public void startEvent(Event event) {
    if (activeEvents.containsKey(event.getUid())) {
      throw new EventException();
    }
    event.start();
    activeEvents.put(event.getUid(), event);
  }

  public static List<Event> activeEvents() {
    return activeEvents.values().stream().toList();
  }

  public static void finishEvent(Event event) {
    event.tryToFinish((e) -> eriaRewarder.reward(e.getParticipants(), e.getPrize()));
    activeEvents.remove(event.getUid());
  }

  public static Optional<Event> activeEventByUid(String uid) {
    return activeEvents.values().stream().filter(event -> event.getUid().equals(uid)).findFirst();
  }

  public static void forceFinish(Event event) {
    event.finish();
    activeEvents.remove(event.getUid());
  }

  public static Set<String> activeEventUids() {
    return activeEvents.keySet();
  }

  public static String generateRandomUID() {
    return UUID.randomUUID().toString();
  }

  public class EventException extends RuntimeException {
  }
}
