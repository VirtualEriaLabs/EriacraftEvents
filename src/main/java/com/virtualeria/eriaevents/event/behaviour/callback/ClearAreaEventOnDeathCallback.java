package com.virtualeria.eriaevents.event.behaviour.callback;

import com.virtualeria.eriaevents.event.EventHandler;
import com.virtualeria.eriaevents.event.behaviour.EventBehaviour;
import com.virtualeria.eriaevents.event.behaviour.SpawnEntityEventBehaviour;
import java.util.function.Predicate;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.ActionResult;

public interface ClearAreaEventOnDeathCallback {

  Event<ClearAreaEventOnDeathCallback> EVENT =
      EventFactory.createArrayBacked(ClearAreaEventOnDeathCallback.class,
          (listeners) -> (entity, source) -> {
            for (ClearAreaEventOnDeathCallback listener : listeners) {
              ActionResult result = listener.updateEvent(entity, source);

              if (result != ActionResult.PASS) {
                return result;
              }
            }
            return ActionResult.PASS;
          });

  ActionResult updateEvent(LivingEntity entity, DamageSource source);

  static void register() {
    ClearAreaEventOnDeathCallback.EVENT.register(
        (entity, source) -> {
          Predicate<EventBehaviour> entityOnActiveEvent = (eventBehaviour ->
              eventBehaviour instanceof SpawnEntityEventBehaviour spawnEntityBehaviour
                  && spawnEntityBehaviour.getEntityList().has(entity));

          EventHandler.activeEvents().stream()
              .forEach(event -> event.getAppliedBehaviours().stream()
                  .filter(entityOnActiveEvent)
                  .findFirst()
                  .ifPresent(eventBehaviour ->
                      entityOfClearAreaKilled((SpawnEntityEventBehaviour) eventBehaviour, event)
                  ));

          return ActionResult.SUCCESS;
        }
    );
  }


  static void entityOfClearAreaKilled(SpawnEntityEventBehaviour eventBehaviour,
                                      com.virtualeria.eriaevents.event.Event event) {
    eventBehaviour.entityKilled();
    if (eventBehaviour.areAllDead()) {
      EventHandler.finishEvent(event);
    }
  }

}
