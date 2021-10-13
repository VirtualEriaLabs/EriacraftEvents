package com.virtualeria.eriaevents.event.events.cleararea;

import com.virtualeria.eriaevents.api.EriaGivable;
import com.virtualeria.eriaevents.command.CreateEventData;
import com.virtualeria.eriaevents.event.BaseEvent;
import com.virtualeria.eriaevents.event.EventHandler;
import com.virtualeria.eriaevents.event.EventHandler.EventException;
import com.virtualeria.eriaevents.event.behaviour.EventBehaviour;
import com.virtualeria.eriaevents.event.behaviour.SpawnEntityEventBehaviour;
import com.virtualeria.eriaevents.event.events.Event;
import com.virtualeria.eriaevents.event.events.EventData;
import com.virtualeria.eriaevents.event.reward.GivableItemStack;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;
import java.util.Queue;
import java.util.function.Consumer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.EntityList;
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
  public boolean canContinue() {
    return this.event.canContinue();
  }


  @Override
  public void finish() {
    event.finish();
  }

  @Override
  public Event getEvent() {
    return this.event;
  }

  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  public static class Creator {

    public static Optional<BaseEvent> simpleEvent(CreateEventData createEventData) {
      Deque<EventBehaviour> deque = new ArrayDeque();
      EntityList entityList = new EntityList();
      var quantity = 1;
      for (int i = 0; i < quantity; i++) {
        SkeletonEntity skeletonEntity =
            new SkeletonEntity(EntityType.SKELETON, createEventData.world());
        skeletonEntity.setPos(createEventData.position().getX(),
            createEventData.position().getY() + 1, createEventData.position().getZ() + i);
        entityList.add(skeletonEntity);
      }
      deque.add(SpawnEntityEventBehaviour.builder()
          .winConditions(SpawnEntityEventBehaviour.getDefaultWinConditions())
          .entityList(entityList)
          .build());

      EriaGivable eriaGivable = GivableItemStack.builder()
          .itemStack(new ItemStack(Items.DIAMOND, 5))
          .build();

      EventData eventData =
          new EventData(createEventData.participants(), EventHandler.generateRandomUID(),
              eriaGivable,
              new ArrayDeque<>(), deque, null, createEventData.duration(),
              createEventData.position());

      return Optional.of(new ClearAreaEvent(new Event(eventData)));
    }

    public static Optional<BaseEvent> eventWithRounds(CreateEventData createEventData) {
      Deque<EventBehaviour> behaviours = new ArrayDeque();
      EntityList skeletonEntities = new EntityList();
      var quantity = 1;
      for (int i = 0; i < quantity; i++) {
        SkeletonEntity skeletonEntity =
            new SkeletonEntity(EntityType.SKELETON, createEventData.world());
        skeletonEntity.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
        skeletonEntity.setPos(createEventData.position().getX(),
            createEventData.position().getY() + 1, createEventData.position().getZ() + i);
        skeletonEntities.add(skeletonEntity);
      }

      EntityList zombiesEntities = new EntityList();
      for (int i = 0; i < quantity; i++) {
        ZombieEntity skeletonEntity =
            new ZombieEntity(EntityType.ZOMBIE, createEventData.world());
        skeletonEntity.setPos(createEventData.position().getX(),
            createEventData.position().getY() + 1, createEventData.position().getZ() + i);
        zombiesEntities.add(skeletonEntity);
      }

      behaviours.add(SpawnEntityEventBehaviour.builder()
          .winConditions(SpawnEntityEventBehaviour.getDefaultWinConditions())
          .entityList(zombiesEntities)
          .build());
      behaviours.add(SpawnEntityEventBehaviour.builder()
          .winConditions(SpawnEntityEventBehaviour.getDefaultWinConditions())
          .entityList(skeletonEntities)
          .build());

      EriaGivable eriaGivable = GivableItemStack.builder()
          .itemStack(new ItemStack(Items.EMERALD, 20))
          .build();

      EventData eventData =
          new EventData(createEventData.participants(), EventHandler.generateRandomUID(),
              eriaGivable,
              new ArrayDeque<>(), behaviours, null, createEventData.duration(),
              createEventData.position());

      return Optional.of(new ClearAreaEvent(new Event(eventData)));
    }

  }
}
