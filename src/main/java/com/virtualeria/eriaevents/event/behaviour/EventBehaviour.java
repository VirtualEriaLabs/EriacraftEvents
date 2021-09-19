package com.virtualeria.eriaevents.event.behaviour;

import java.util.function.Predicate;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@EqualsAndHashCode
public abstract class EventBehaviour {
  @Getter
  protected final Predicate winConditions;

  public abstract void execute();

  public abstract void undo();

  public abstract boolean isFinished();

  public abstract boolean behaviourWinConditionsMet();
}