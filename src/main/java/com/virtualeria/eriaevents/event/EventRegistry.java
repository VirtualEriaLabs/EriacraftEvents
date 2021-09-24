package com.virtualeria.eriaevents.event;

import com.virtualeria.eriaevents.command.CancelEventCommand;
import com.virtualeria.eriaevents.command.CreateEventCommand;
import com.virtualeria.eriaevents.command.GetUidCommand;
import com.virtualeria.eriaevents.event.behaviour.callback.ClearAreaEventOnDeathCallback;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public final class EventRegistry {

  public static final DefaultParticleType CUSTOM_TEST_PARTICLE = FabricParticleTypes.simple();

  public static void init() {
    CommandRegistrationCallback.EVENT.register(CreateEventCommand::register);
    CommandRegistrationCallback.EVENT.register(GetUidCommand::register);
    CommandRegistrationCallback.EVENT.register(CancelEventCommand::register);
    ClearAreaEventOnDeathCallback.register();
    Registry.register(Registry.PARTICLE_TYPE, new Identifier("eriaevents", "custom"),
        CUSTOM_TEST_PARTICLE);
  }
}
