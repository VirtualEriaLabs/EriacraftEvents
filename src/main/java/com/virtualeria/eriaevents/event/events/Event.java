package com.virtualeria.eriaevents.event.events;

import static net.minecraft.util.Util.getMeasuringTimeMs;

import com.virtualeria.eriaevents.event.BaseEvent;
import com.virtualeria.eriaevents.event.behaviour.EventBehaviour;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.NonFinal;
import net.minecraft.text.LiteralText;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
@Value
@AllArgsConstructor
public class Event implements BaseEvent {
  private static final Logger LOGGER = LogManager.getLogger();
  EventData eventData;
  @NonFinal
  long startedAt;

  @Builder
  public Event(EventData eventData) {
    this.eventData = eventData;
  }

  /*
   * This performs actions necessary for the event to start
   * We can upgrade this by adding some Predicate that needs to be checked to be able to be started
   * */
  public void start() {
    LOGGER.debug("Starting event %s with behaviours: \n%s"
        .formatted(this.eventData.uid(), this.eventData.toApplyBehaviours().stream()
            .map(EventBehaviour::toString)
            .collect(Collectors.joining(") (", "(", ")"))));
    this.getEventData().participants().forEach(serverPlayerEntity -> {
      serverPlayerEntity.sendMessage(new LiteralText("Event started."), false);
    });
    var toApplySize = eventData.toApplyBehaviours().size();
    for (int i = 0; i < toApplySize; i++) {
      EventBehaviour eventBehaviour = eventData.toApplyBehaviours().poll();
      LOGGER.trace("Starting behaviour: %s".formatted(eventBehaviour.toString()));
      eventBehaviour.execute();
      this.eventData.appliedBehaviours().add(eventBehaviour);
    }
    startedAt = getMeasuringTimeMs();
  }

  public boolean canContinue() {
    return eventData.duration() == 0 || startedAt + eventData.duration() > getMeasuringTimeMs();
  }

  /*
   * We need to check if event win conditions are met
   * If win conditions met reward players
   * */
  public void tryToFinish(Consumer<Event> rewarderAction) {
    LOGGER.debug("Trying to finish event with uid: %s".formatted(this.eventData.uid()));
    if (this.eventData.appliedBehaviours().stream()
        .allMatch(EventBehaviour::behaviourWinConditionsMet)) {
      rewarderAction.accept(this);
      this.finish();
      return;
    }
    LOGGER.debug("Unable to finish event, win conditions not met");
  }

  public void finish() {
    LOGGER.debug("Finished event with uid: %s".formatted(this.eventData.uid()));
    this.eventData.appliedBehaviours().forEach(EventBehaviour::undo);
  }

  @Override
  public Event getEvent() {
    return this;
  }

  public enum EventDifficulty {
    EASY,
    NORMAL,
    HARD,
    EXTREME
  }

}
