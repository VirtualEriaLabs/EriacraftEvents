package com.virtualeria.eriaevents.api;

import java.util.function.Function;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public interface EriaGivable extends Function<ServerPlayerEntity, EriaGivable> {
  String name = "Givable";

  EriaGivable apply(ServerPlayerEntity spe);

  default void successFeedback(ServerPlayerEntity target, Text text) {
    target.sendMessage(text, false);
  }

  default void errorFeedback(ServerPlayerEntity target) {
    target.sendMessage(new TranslatableText("text.givable.error"), false);
  }
}
