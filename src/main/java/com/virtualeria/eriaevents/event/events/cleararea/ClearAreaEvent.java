package com.virtualeria.eriaevents.event.events.cleararea;

import com.virtualeria.eriaevents.event.EventHandler;
import com.virtualeria.eriaevents.event.EventHandler.EventException;
import com.virtualeria.eriaevents.event.behaviour.EventBehaviour;
import com.virtualeria.eriaevents.event.behaviour.SpawnEntityEventBehaviour;
import com.virtualeria.eriaevents.event.BaseEvent;
import com.virtualeria.eriaevents.event.events.Event;
import java.util.ArrayDeque;
import java.util.Optional;
import java.util.Queue;
import java.util.function.Consumer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClearAreaEvent implements BaseEvent {
  private final Event event;
  private final int maxRounds;
  private final Queue<SpawnEntityEventBehaviour> behaviourRounds = new ArrayDeque<>();
  private static final Logger LOGGER = LogManager.getLogger();

  public ClearAreaEvent(Event event) throws EventException {
    this.event = event;
    setUpRounds(event.getEventData().toApplyBehaviours());
    this.maxRounds = this.behaviourRounds.size();
    LOGGER.info("Rounds set up, max rounds %d".formatted(this.maxRounds));
  }

  private void setUpRounds(Queue<EventBehaviour> eventBehaviours) throws EventException {
    var size = eventBehaviours.size();
    if (size == 0) {
      LOGGER.info("SetUp failed, not entity rounds for event %d".formatted(this.maxRounds));
      throw new EventException();
    }
    for (var i = 0; i < size; i++) {
      EventBehaviour eventBehaviour = eventBehaviours.poll();
      if (eventBehaviour instanceof SpawnEntityEventBehaviour spawnEntityEventBehaviour) {
        this.behaviourRounds.add(spawnEntityEventBehaviour);
      } else {
        eventBehaviours.add(eventBehaviour);
      }
    }
  }

  public void startRound() {
    Optional.ofNullable(this.behaviourRounds.poll())
        .ifPresent(behaviour -> {
              behaviour.execute();
              this.event.getEventData().appliedBehaviours().add(behaviour);
            }
        );
  }

  public void finishRound() {
    LOGGER.info("Round %d/%d finished".formatted(this.behaviourRounds.size() - this.maxRounds,
        this.maxRounds));

    if (this.behaviourRounds.size() > 0) {
      startRound();
    } else {
      EventHandler.finishEvent(this);
    }
  }

  @Override
  public void start() {
    event.start();
    startRound();
  }


  @Override
  public void tryToFinish(Consumer<Event> rewarderAction) {
    event.tryToFinish(rewarderAction);
  }


  @Override
  public void finish() {
    event.finish();
  }

  @Override
  public Event getEvent() {
    return this.event;
  }
}
