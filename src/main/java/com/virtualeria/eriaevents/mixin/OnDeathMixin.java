package com.virtualeria.eriaevents.mixins;

import com.virtualeria.eriaevents.event.behaviour.callback.ClearAreaEventOnDeathCallback;
import net.minecraft.block.BlockState;
import net.minecraft.block.OreBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class OnDeathMixin {


  @Inject(at = @At("HEAD"), method = "onDeath")
  public void onDeath(DamageSource source, CallbackInfo ci) {
    ClearAreaEventOnDeathCallback.EVENT.invoker().updateEvent((LivingEntity)(Object) this,source);
  }
}
