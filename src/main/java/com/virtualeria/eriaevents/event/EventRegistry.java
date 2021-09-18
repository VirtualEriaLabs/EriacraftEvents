package com.virtualeria.eriaevents.event;

import com.virtualeria.eriaevents.command.CreateEventCommand;
import com.virtualeria.eriaevents.command.GetUidCommand;
import com.virtualeria.eriaevents.event.behaviour.callback.ClearAreaEventOnDeathCallback;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;

public final class EventRegistry {

  public static void init() {
    //var x = (arg) -> "si";
    CommandRegistrationCallback.EVENT.register(CreateEventCommand::register);
    CommandRegistrationCallback.EVENT.register(GetUidCommand::register);
    ClearAreaEventOnDeathCallback.register();
  }
}
