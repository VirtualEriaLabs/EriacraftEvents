package com.virtualeria.eriaevents;

import com.virtualeria.eriaevents.basic.DiamondCallback;
import net.fabricmc.api.ModInitializer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.registry.Registry;

public class EriaEvents implements ModInitializer {
  @Override
  public void onInitialize() {
    System.out.println("Hello Fabric world!");
    DiamondCallback.EVENT.register(((player,stack) -> {
      player.getInventory().insertStack(new ItemStack(Items.STONE,5));
      boolean x = stack.getItem() == Items.DIAMOND_PICKAXE;
      System.out.println(String.format("Is this is a ore? %b", x));
      if (x) {
        System.out.println("Hellow!! Conditions met, event was triggered.");
        stack.setCount(50);
        return ActionResult.SUCCESS;
      }
      return ActionResult.PASS;
    }));
  }
}
