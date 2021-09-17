package com.virtualeria.eriaevents.basic;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;

public interface DiamondCallback {
  Event<DiamondCallback> EVENT = EventFactory.createArrayBacked(DiamondCallback.class,
      (listeners) -> (player, itemStack) -> {
        for (DiamondCallback listener : listeners) {
            ActionResult result = listener.use(player, itemStack);
          System.out.println(String.format("Listener %d triggered", listener.hashCode()));
          if (result != ActionResult.PASS) {
            return result;
          }
        }
        return ActionResult.SUCCESS;
      });

  ActionResult use(ServerPlayerEntity serverPlayerEntity, ItemStack itemStack);
}
