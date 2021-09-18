package com.virtualeria.eriaevents.event.behaviour.model;

import lombok.Builder;
import lombok.Value;
import net.minecraft.world.EntityList;
import net.minecraft.world.World;

@Value
@Builder
public class SpawnEntityEventBehaviourArgs {
  World world;
  EntityList entityList;
}