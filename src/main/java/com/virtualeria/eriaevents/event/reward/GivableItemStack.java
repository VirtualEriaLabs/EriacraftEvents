package com.virtualeria.eriaevents.event.reward;

import com.virtualeria.eriaevents.api.EriaGivable;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;

@Builder
@Value
public class GivableItemStack implements EriaGivable {
  @NonNull
  ItemStack itemStack;

  @Override
  public EriaGivable apply(ServerPlayerEntity spe) {
    spe.getInventory()
        .insertStack(new ItemStack(this.itemStack.getItem(), this.getItemStack().getCount()));
    this.successFeedback(spe, new TranslatableText(""));
    return this;
  }
}