package com.virtualeria.eriaevents.event;

import com.virtualeria.eriaevents.api.EriaGivable;
import com.virtualeria.eriaevents.event.behaviour.EventBehaviour;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import lombok.Builder;
import lombok.Value;
import net.minecraft.server.network.ServerPlayerEntity;

/*
 * Can events interact with other events?
 * Events with timer (ticks)?
 * Can events add players after start?
 * How to check if a player can cancel events?
 * An event might be Unique (only 1 of this kind can be in the world at the same time)
 * Are players allowed to be in multiple events at the same time? And of the same type?
 * If we need a TYPE of event we ned typed events, they need to be objects
 * Types of events: Unique, Global(for every player), Positioned, WithEntities, WithPVP, EventWithRounds, EventsWithBuffs, WithDimensions
 * A boss fight might be an event that one player should be able to reconnect after it disconnected
 * To check client actual events it needs to perform a server query
 *
 * What might need an event?
 * - Position
 * - Players
 * - A identifier
 * - A way to check if it is active or not -> do we really need this? If we have a list of active events that should be enough
 * - A prize
 */
@Builder
@Value
public class Event {
  List<ServerPlayerEntity> participants;
  String uid;
  EriaGivable prize;
  Deque<EventBehaviour> appliedBehaviours = new ArrayDeque<>();
  Deque<EventBehaviour> toApplyBehaviours;

  /*
   * This performs actions necessary for the event to start
   * We can upgrade this by adding some Predicate that needs to be checked to be able to be started
   * */
  public final void start() {
    toApplyBehaviours.forEach(e -> {
      e.execute();
      appliedBehaviours.add(e);
    });
  }

  /*
   * We need to check if event win conditions are met
   * If win conditions met reward players
   * */
  public final void finish() {
    // TODO add conditions
    appliedBehaviours.forEach(e -> e.undo());
  }

  public enum EventDifficulty {
    EASY,
    NORMAL,
    HARD,
    EXTREME
  }

}
