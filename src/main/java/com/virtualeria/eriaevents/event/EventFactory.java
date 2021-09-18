package com.virtualeria.eriaevents.event;

import com.virtualeria.eriaevents.event.Event.EventDifficulty;
import com.virtualeria.eriaevents.event.behaviour.EventBehaviour;
import com.virtualeria.eriaevents.event.behaviour.SpawnEntityEventBehaviour;
import com.virtualeria.eriaevents.event.behaviour.model.SpawnEntityEventBehaviourArgs;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Optional;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.EntityList;
import net.minecraft.world.World;

public class EventFactory {

  public static Optional<Event> buildEvent(String name, World world, EventDifficulty eventDifficulty) {
    if (name.equals("clearArea")) {
      return Optional.ofNullable(ClearAreaEvents.clearAreaWithSkeletons(world, eventDifficulty));
    }
    return Optional.empty();
  }

  public class ClearAreaEvents {
    public static Event clearAreaWithSkeletons(World world, EventDifficulty difficulty) {
      switch (difficulty) {
        case EASY -> {
          return build(world, 5);
        }
        case NORMAL -> {
          return build(world, 10);
        }
        case HARD -> {
          return build(world, 15);
        }
        case EXTREME -> {
          return build(world, 20);
        }
        default -> {
          return build(world, 1);
        }
      }
    }

    /*
    * Make a build for them
    * Restrict entities to an Area
    * Define time of event
    * When an entity dies must check if it is clearea entity and if it this check if all entities of event died
    * If all players of event left event must vbe canceled
    * Where entities spawn matter, they must spawn at night and outside
    * */
    private static Event build(World world, int quantity) {
      Deque<EventBehaviour> deque = new ArrayDeque();
      EntityList entityList = new EntityList();
      for (int i = 0; i < quantity; i++) {
        SkeletonEntity skeletonEntity =
            new SkeletonEntity(EntityType.SKELETON, world);
        var soyUnSupplier = new NbtCompound();
        var soyUnSupplierCustom = new NbtCompound();
        soyUnSupplier.putString("EriaEventID", "Misa");
        skeletonEntity.writeNbt(soyUnSupplier);
        soyUnSupplierCustom.putString("EriaEventID", "MisaCustom");
        skeletonEntity.writeCustomDataToNbt(soyUnSupplierCustom);

        skeletonEntity.setPos(-250, 67, 103 + i);
        entityList.add(skeletonEntity);
      }
      deque.add(SpawnEntityEventBehaviour.builder()
          .args(
              SpawnEntityEventBehaviourArgs.builder()
                  .entityList(entityList)
                  .world(world)
                  .build())
          .build());

      return Event.builder()
          .uid(EventHandler.generateRandomUID())
          .toApplyBehaviours(deque)
          .participants(new ArrayList<>())
          .build();
    }
  }
}
