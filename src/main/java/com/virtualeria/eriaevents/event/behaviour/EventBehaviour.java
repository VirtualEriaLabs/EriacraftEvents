package com.virtualeria.eriaevents.event.behaviour;


import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@EqualsAndHashCode
public abstract class EventBehaviour<T> {
  final T args;

  public abstract void execute();

  public abstract void undo();
}