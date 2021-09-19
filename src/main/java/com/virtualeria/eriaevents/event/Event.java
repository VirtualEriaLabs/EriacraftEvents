package com.virtualeria.eriaevents.event;

import com.virtualeria.eriaevents.api.EriaGivable;
import com.virtualeria.eriaevents.event.behaviour.EventBehaviour;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Value;
import lombok.extern.log4j.Log4j2;
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
@Log4j2
public class Event {
  List<ServerPlayerEntity> participants;
  String uid;
  EriaGivable prize;
  Deque<EventBehaviour> appliedBehaviours = new ArrayDeque<>();
  Deque<EventBehaviour> toApplyBehaviours;
  Predicate<Deque<EventBehaviour>> winConditions;

  /*
   * This performs actions necessary for the event to start
   * We can upgrade this by adding some Predicate that needs to be checked to be able to be started
   * */
  public final void start() {
    log.debug("Starting event %s with behaviours: \n%s"
        .formatted(this.uid, this.toApplyBehaviours.stream()
            .map(EventBehaviour::toString)
            .collect(Collectors.joining(") (", "(", ")"))));
    toApplyBehaviours.forEach(e -> {
      log.trace("Starting behaviour: %s".formatted(e.toString()));
      e.execute();
      appliedBehaviours.add(e);
    });
  }

  /*
   * We need to check if event win conditions are met
   * If win conditions met reward players
   * */
  public final void tryToFinish(Consumer<Event> rewarderAction) {
    log.debug("Trying to finish event with uid: %s".formatted(this.uid));
    if (this.appliedBehaviours.stream().allMatch(EventBehaviour::behaviourWinConditionsMet)) {
      rewarderAction.accept(this);
      this.finish();
      return;
    }
    log.debug("Unable to finish event, win conditions not met");
  }

  public final void finish() {
    log.debug("Finished event with uid: %s".formatted(this.uid));
    appliedBehaviours.forEach(EventBehaviour::undo);
  }

  public enum EventDifficulty {
    EASY,
    NORMAL,
    HARD,
    EXTREME
  }

}
