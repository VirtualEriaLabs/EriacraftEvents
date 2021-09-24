package com.virtualeria.eriaevents;


import static com.virtualeria.eriaevents.event.EventRegistry.CUSTOM_TEST_PARTICLE;

import com.virtualeria.eriaevents.event.EventHandler;
import com.virtualeria.eriaevents.event.EventRegistry;
import com.virtualeria.eriaevents.event.behaviour.particle.CustomTestParticle;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;

public class EriaEvents implements ModInitializer, ClientModInitializer,
    DedicatedServerModInitializer {

  public static EventHandler eventHandler = new EventHandler();

  @Override
  public void onInitialize() {
    System.out.println("Hello Fabric world!");
    EventRegistry.init();
  }

  @Override
  @Environment(EnvType.CLIENT)
  public void onInitializeClient() {
    ClientSpriteRegistryCallback.event(
        PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).register(((atlasTexture, registry) -> {
      registry.register(new Identifier("eriaevents", "particle/custom"));
    }));

    ParticleFactoryRegistry.getInstance()
        .register(CUSTOM_TEST_PARTICLE, CustomTestParticle.Factory::new);
  }

  @Override
  @Environment(EnvType.SERVER)
  public void onInitializeServer() {

  }
}
