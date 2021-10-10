package com.virtualeria.eriaevents;

import com.virtualeria.eriaevents.command.CancelEventCommand;
import com.virtualeria.eriaevents.command.CreateEventCommand;
import com.virtualeria.eriaevents.command.GetUidCommand;
import com.virtualeria.eriaevents.event.BaseEvent;
import com.virtualeria.eriaevents.event.EventHandler;
import com.virtualeria.eriaevents.event.behaviour.callback.ClearAreaEventOnDeathCallback;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;

public class EriaEvents implements ModInitializer, ClientModInitializer,
    DedicatedServerModInitializer {

  public static EventHandler eventHandler = new EventHandler();

  @Override
  public void onInitialize() {
    System.out.println("Hello Fabric world!");
    CommandRegistrationCallback.EVENT.register(CreateEventCommand::register);
    CommandRegistrationCallback.EVENT.register(GetUidCommand::register);
    CommandRegistrationCallback.EVENT.register(CancelEventCommand::register);
    ClearAreaEventOnDeathCallback.register();
    BaseEvent.tickRegister();
  }

  @Override
  public void onInitializeClient() {

  }

  @Override
  public void onInitializeServer() {
  }
}
