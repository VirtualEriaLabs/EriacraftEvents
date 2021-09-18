package com.virtualeria.eriaevents.event.behaviour.callback;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.ActionResult;

public interface ClearAreaEventOnDeathCallback {

  Event<ClearAreaEventOnDeathCallback> EVENT = EventFactory.createArrayBacked(ClearAreaEventOnDeathCallback.class,
      (listeners) -> (entity, source) -> {
        for (ClearAreaEventOnDeathCallback listener : listeners) {
          ActionResult result = listener.updateEvent(entity, source);

          if(result != ActionResult.PASS) {
            return result;
          }
        }
        return ActionResult.PASS;
      });

  ActionResult updateEvent(LivingEntity entity, DamageSource source);

  static void register() {
    ClearAreaEventOnDeathCallback.EVENT.register(
        (entity, source) -> {
          //x = entity.readNbt();
          return ActionResult.SUCCESS;

          //if this entity belongs to an clearArea event that is active
            //is this the last entity to die ?
              // trigger finish
        }
    );
  }

}
