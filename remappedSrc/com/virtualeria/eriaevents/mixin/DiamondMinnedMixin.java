package com.virtualeria.eriaevents.mixin;

import com.virtualeria.eriaevents.basic.DiamondCallback;
import net.minecraft.block.BlockState;
import net.minecraft.block.OreBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OreBlock.class)
public class DiamondMinnedMixin {

  @Inject(method = "onStacksDropped", at = @At(value = "INVOKE", ordinal = 2))
  private void onStacksDropped(BlockState state, ServerWorld world, BlockPos pos, ItemStack stack,
                               CallbackInfo ci) {
    world.iterateEntities().forEach(it -> {
      if (it.isPlayer()) {
        DiamondCallback.EVENT.invoker().use((ServerPlayerEntity) it, stack);
      }
    });
  }
}
