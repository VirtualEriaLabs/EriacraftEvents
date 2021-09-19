package com.virtualeria.eriaevents.event;

import com.virtualeria.eriaevents.api.EriaGivable;
import com.virtualeria.eriaevents.event.Event.EventDifficulty;
import com.virtualeria.eriaevents.event.behaviour.EventBehaviour;
import com.virtualeria.eriaevents.event.behaviour.SpawnEntityEventBehaviour;
import com.virtualeria.eriaevents.event.reward.GivableItemStack;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.EntityList;
import net.minecraft.world.World;

public class EventFactory {

  public static Optional<Event> buildEvent(String name, World world, EventDifficulty eventDifficulty, List<ServerPlayerEntity> players) {
    if (name.equals("clearArea")) {
      return Optional.ofNullable(ClearAreaEvents.clearAreaWithSkeletons(world, eventDifficulty, players));
    }
    return Optional.empty();
  }

  public static Optional<Event> buildEvent(String name, World world, EventDifficulty eventDifficulty) {
    if (name.equals("clearArea")) {
      return Optional.ofNullable(ClearAreaEvents.clearAreaWithSkeletons(world, eventDifficulty, new ArrayList<>()));
    }
    return Optional.empty();
  }

  public class ClearAreaEvents {
    public static Event clearAreaWithSkeletons(World world, EventDifficulty difficulty, List<ServerPlayerEntity> players) {
      return switch (difficulty) {
        case EASY -> build(world, 1, players);
        case NORMAL -> build(world, 10, players);
        case HARD -> build(world, 15, players);
        case EXTREME -> build(world, 20, players);
      };
    }

    /*
    * Make a build for them
    * Restrict entities to an Area
    * Define time of event
    * When an entity dies must check if it is clearea entity and if it this check if all entities of event died
    * If all players of event left event must vbe canceled
    * Where entities spawn matter, they must spawn at night and outside
    * */
    private static Event build(World world, int quantity, List<ServerPlayerEntity> players) {
      Deque<EventBehaviour> deque = new ArrayDeque();
      EntityList entityList = new EntityList();
      for (int i = 0; i < quantity; i++) {
        SkeletonEntity skeletonEntity =
            new SkeletonEntity(EntityType.SKELETON, world);
        skeletonEntity.setPos(-250, 67, 103 + i);
        entityList.add(skeletonEntity);
      }
      deque.add(SpawnEntityEventBehaviour.builder()
          .winConditions(SpawnEntityEventBehaviour.getDefaultWinConditions())
          .entityList(entityList)
          .build());

      EriaGivable eriaGivable = GivableItemStack.builder()
          .itemStack(new ItemStack(Items.DIAMOND,5))
          .build();

      return Event.builder()
          .uid(EventHandler.generateRandomUID())
          .toApplyBehaviours(deque)
          .prize(eriaGivable)
          .participants(players)
          .build();
    }
  }
}
