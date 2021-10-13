package com.virtualeria.eriaevents.event;

import static com.virtualeria.eriaevents.event.EventFactory.EventType.CLEAR_AREA;
import static com.virtualeria.eriaevents.event.EventFactory.EventType.CLEAR_AREA_WITH_ROUNDS;

import com.google.common.collect.ImmutableMap;
import com.virtualeria.eriaevents.command.CreateEventData;
import com.virtualeria.eriaevents.event.events.cleararea.ClearAreaEvent;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class EventFactory {

  public static final Map<EventType, Function<CreateEventData, Optional<BaseEvent>>> eventMap =
      new ImmutableMap.Builder<EventType, Function<CreateEventData, Optional<BaseEvent>>>()
          .put(CLEAR_AREA, ClearAreaEvent.Creator::simpleEvent)
          .put(CLEAR_AREA_WITH_ROUNDS, ClearAreaEvent.Creator::eventWithRounds)
          .build();

  public enum EventType {
    CLEAR_AREA,
    CLEAR_AREA_WITH_ROUNDS
  }

}
