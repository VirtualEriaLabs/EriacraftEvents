package com.virtualeria.eriaevents.event.behaviour.cleararea;

import com.virtualeria.eriaevents.event.behaviour.EventBehaviour;
import java.util.function.Predicate;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.world.EntityList;

@SuperBuilder
@Getter
@EqualsAndHashCode(callSuper = true)
public class SpawnEntityEventBehaviour extends EventBehaviour {

  final EntityList entityList;
  int totalEntities;
  int entitiesKilled;

  @Override
  public void execute() {
    entityList.forEach(entity -> {
      totalEntities++;
      entity.getEntityWorld().spawnEntity(entity);
    });
  }

  @Override
  public void undo() {
    entityList.forEach(Entity::discard);
  }

  @Override
  public boolean isFinished() {
    return areAllDead();
  }

  @Override
  public boolean behaviourWinConditionsMet() {
    return winConditions.test(this);
  }

  public boolean areAllDead() {
    return totalEntities == entitiesKilled && entitiesKilled > 0;
  }

  public void entityKilled() {
    entitiesKilled++;
  }

  public static Predicate<SpawnEntityEventBehaviour> getDefaultWinConditions() {
    return SpawnEntityEventBehaviour::areAllDead;
  }

}