package com.virtualeria.eriaevents.event;

import com.virtualeria.eriaevents.api.EriaGivable;
import com.virtualeria.eriaevents.event.events.cleararea.ClearAreaEvent;
import com.virtualeria.eriaevents.event.events.Event;
import com.virtualeria.eriaevents.event.events.Event.EventDifficulty;
import com.virtualeria.eriaevents.event.behaviour.EventBehaviour;
import com.virtualeria.eriaevents.event.behaviour.SpawnEntityEventBehaviour;
import com.virtualeria.eriaevents.event.events.EventData;
import com.virtualeria.eriaevents.event.reward.GivableItemStack;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.EntityList;
import net.minecraft.world.World;

public class EventFactory {

  public static Optional<BaseEvent> buildEvent(String name, World world,
                                               EventDifficulty eventDifficulty,
                                               List<ServerPlayerEntity> players) {
    if (name.equals("clearArea")) {
      return Optional
          .ofNullable(ClearAreaEvents.clearAreaWithSkeletons(world, eventDifficulty, players));
    } else if (name.equals("clearAreaWithRounds")) {
      return Optional.ofNullable(
          ClearAreaEvents.clearAreaWithSkeletonsAndRounds(world, eventDifficulty, players));
    }
    return Optional.empty();
  }

  public static Optional<BaseEvent> buildEvent(String name, World world,
                                               EventDifficulty eventDifficulty) {
    return buildEvent(name, world, eventDifficulty, new ArrayList<>());
  }

  public class ClearAreaEvents {
    public static BaseEvent clearAreaWithSkeletons(World world, EventDifficulty difficulty,
                                                   List<ServerPlayerEntity> players) {
      return switch (difficulty) {
        case EASY -> build(world, 1, players);
        case NORMAL -> build(world, 10, players);
        case HARD -> build(world, 15, players);
        case EXTREME -> build(world, 20, players);
      };
    }

    public static BaseEvent clearAreaWithSkeletonsAndRounds(World world, EventDifficulty difficulty,
                                                            List<ServerPlayerEntity> players) {
      return switch (difficulty) {
        case EASY -> buildWithRounds(world, 1, players);
        case NORMAL -> buildWithRounds(world, 10, players);
        case HARD -> buildWithRounds(world, 15, players);
        case EXTREME -> buildWithRounds(world, 20, players);
      };
    }

    private static BaseEvent build(World world, int quantity, List<ServerPlayerEntity> players) {
      Deque<EventBehaviour> deque = new ArrayDeque();
      ServerPlayerEntity player = players.get(0);
      EntityList entityList = new EntityList();
      for (int i = 0; i < quantity; i++) {
        SkeletonEntity skeletonEntity =
            new SkeletonEntity(EntityType.SKELETON, world);
        skeletonEntity.setPos(player.getX(), player.getY()+1, player.getZ() + 10 + i);
        entityList.add(skeletonEntity);
      }
      deque.add(SpawnEntityEventBehaviour.builder()
          .winConditions(SpawnEntityEventBehaviour.getDefaultWinConditions())
          .entityList(entityList)
          .build());

      EriaGivable eriaGivable = GivableItemStack.builder()
          .itemStack(new ItemStack(Items.DIAMOND, 5))
          .build();

      EventData eventData = new EventData(players, EventHandler.generateRandomUID(), eriaGivable,
          new ArrayDeque<EventBehaviour>(), deque, null,60000);

      return new Event(eventData);
    }

    private static BaseEvent buildWithRounds(World world, int quantity,
                                             List<ServerPlayerEntity> players) {
      Deque<EventBehaviour> deque = new ArrayDeque();
      ServerPlayerEntity player = players.get(0);
      EntityList entityList = new EntityList();
      for (int i = 0; i < quantity; i++) {
        SkeletonEntity skeletonEntity =
            new SkeletonEntity(EntityType.SKELETON, world);
        skeletonEntity.equipStack(EquipmentSlot.MAINHAND,new ItemStack(Items.BOW));
        skeletonEntity.setPos(player.getX(), player.getY() + 1, player.getZ() + 10 + i);
        entityList.add(skeletonEntity);
      }
      EntityList entityList1 = new EntityList();
      for (int i = 0; i < quantity; i++) {
        ZombieEntity skeletonEntity =
            new ZombieEntity(EntityType.ZOMBIE, world);
        skeletonEntity.setPos(player.getX(), player.getY()+1, player.getZ() + 10 + i);
        entityList1.add(skeletonEntity);
      }

      SpawnEntityEventBehaviour eventBehaviour = SpawnEntityEventBehaviour.builder()
          .winConditions(SpawnEntityEventBehaviour.getDefaultWinConditions())
          .entityList(entityList)
          .build();

      SpawnEntityEventBehaviour eventBehaviour2 = SpawnEntityEventBehaviour.builder()
          .winConditions(SpawnEntityEventBehaviour.getDefaultWinConditions())
          .entityList(entityList1)
          .build();

      deque.add(eventBehaviour);
      deque.add(eventBehaviour2);

      EriaGivable eriaGivable = GivableItemStack.builder()
          .itemStack(new ItemStack(Items.EMERALD, 20))
          .build();

      EventData eventData = new EventData(players, EventHandler.generateRandomUID(), eriaGivable,
          new ArrayDeque<>(), deque, null, 5000);

      Event event = new Event(eventData);

      return new ClearAreaEvent(event);
    }
  }
}
