package com.virtualeria.eriaevents.event;

import com.virtualeria.eriaevents.command.CancelEventCommand;
import com.virtualeria.eriaevents.command.CreateEventCommand;
import com.virtualeria.eriaevents.command.GetUidCommand;
import com.virtualeria.eriaevents.event.behaviour.callback.ClearAreaEventOnDeathCallback;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;

public final class EventRegistry {

  public static void init() {
    CommandRegistrationCallback.EVENT.register(CreateEventCommand::register);
    CommandRegistrationCallback.EVENT.register(GetUidCommand::register);
    CommandRegistrationCallback.EVENT.register(CancelEventCommand::register);
    ClearAreaEventOnDeathCallback.register();
  }
}
