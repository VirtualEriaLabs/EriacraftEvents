package com.virtualeria.eriaevents.api;

import java.util.Collection;
import net.minecraft.server.network.ServerPlayerEntity;

@FunctionalInterface
public interface EriaRewarder {
  void reward(Collection<ServerPlayerEntity> player, EriaGivable reward);
}
