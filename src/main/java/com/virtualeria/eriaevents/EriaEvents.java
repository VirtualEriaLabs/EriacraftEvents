package com.virtualeria.eriaevents;

import com.virtualeria.eriaevents.event.EventHandler;
import com.virtualeria.eriaevents.event.EventRegistry;
import net.fabricmc.api.ModInitializer;

public class EriaEvents implements ModInitializer {

  public static EventHandler eventHandler = new EventHandler();

  @Override
  public void onInitialize() {
    System.out.println("Hello Fabric world!");
    EventRegistry.init();
  }
}
