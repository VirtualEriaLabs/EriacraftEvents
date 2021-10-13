package com.virtualeria.eriaevents.command;

import com.virtualeria.eriaevents.event.events.Event.EventDifficulty;
import java.util.List;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public record CreateEventData(
    ServerWorld world,
    EventDifficulty difficulty,
    long duration,
    BlockPos position,
    List<ServerPlayerEntity> participants
) {
}
