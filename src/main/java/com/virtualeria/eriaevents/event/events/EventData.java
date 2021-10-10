package com.virtualeria.eriaevents.event.events;

import com.virtualeria.eriaevents.api.EriaGivable;
import com.virtualeria.eriaevents.event.behaviour.EventBehaviour;
import java.util.Deque;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.server.network.ServerPlayerEntity;

public record EventData(
    List<ServerPlayerEntity> participants,
    String uid,
    EriaGivable prize,
    Deque<EventBehaviour> appliedBehaviours,
    Deque<EventBehaviour> toApplyBehaviours,
    Predicate<Deque<EventBehaviour>> winConditions,
    long duration) {
}
