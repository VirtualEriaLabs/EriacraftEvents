package com.virtualeria.eriaevents.event.behaviour;

import com.virtualeria.eriaevents.event.behaviour.models.SpawnEntityEventBehaviourArgs;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;

@Value
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class SpawnEntityEventBehaviour extends EventBehaviour<SpawnEntityEventBehaviourArgs> {

  @Override
  public void execute() {
    args.getEntityList().forEach(entity -> args.getWorld().spawnEntity(entity));
  }

  @Override
  public void undo() {
    args.getEntityList().forEach(entity -> entity.discard());
  }

}