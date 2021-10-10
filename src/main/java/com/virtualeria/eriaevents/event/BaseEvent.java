package com.virtualeria.eriaevents.event;

import static net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.END_SERVER_TICK;

import com.virtualeria.eriaevents.event.events.Event;
import java.util.function.Consumer;

public interface BaseEvent {
  void start();

  void tryToFinish(Consumer<Event> rewarderAction);

  boolean canContinue();

  void finish();

  Event getEvent();

  static void tickRegister() {
  END_SERVER_TICK.register(server -> {
      EventHandler.checkForConcludedEvents();
    });
  }
}
