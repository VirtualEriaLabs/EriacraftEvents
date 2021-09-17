package com.virtualeria.eriaevents.command;

import static com.virtualeria.eriaevents.command.CommandValues.BASE_ALIAS_NAME;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.virtualeria.eriaevents.EriaEvents;
import com.virtualeria.eriaevents.event.Event;
import com.virtualeria.eriaevents.event.Event.EventDifficulty;
import com.virtualeria.eriaevents.event.EventFactory;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;

public class CreateEventCommand {

   public final static String BY_NAME_FIRST_ARG = "name";
   public final static String BY_NAME_SECOND_ARG = "difficulty";

  public static void register(CommandDispatcher<ServerCommandSource> dispatcher, boolean b) {
    var eventBuilderByName =
        literal(CommandValues.BASE_COMMAND_NAME).requires((source) -> source.hasPermissionLevel(2));

    var nameArg = argument(BY_NAME_FIRST_ARG, StringArgumentType.word())
        .executes((context -> execute(context.getSource(),
            StringArgumentType.getString(context, BY_NAME_FIRST_ARG), EventDifficulty.EASY)));

    var requiredDifficultyAfterNameArg =
        argument(BY_NAME_SECOND_ARG, StringArgumentType.word())
            .executes((context ->
                execute(
                    context.getSource(),
                    StringArgumentType.getString(context, BY_NAME_FIRST_ARG),
                    StringArgumentType.getString(context, BY_NAME_SECOND_ARG))));

    var testo =
        argument("testo", EntityArgumentType.entities()).executes(context -> 1);

    var command =
        dispatcher.register(eventBuilderByName.then(nameArg.then(requiredDifficultyAfterNameArg).then(testo)));

    dispatcher
        .register(literal(BASE_ALIAS_NAME).requires((source) -> source.hasPermissionLevel(2))
            .redirect(command));
  }

  private static int execute(ServerCommandSource source, String name, String difficulty) {
    try {
      return execute(source, name, EventDifficulty.valueOf(difficulty.toUpperCase()));
    } catch (IllegalArgumentException argumentException) {
      source.sendError(new LiteralText("Error, difficulty values  - %s - ".formatted(
          Arrays.stream(EventDifficulty.values()).map(eventDifficulty -> eventDifficulty.name())
              .collect(
                  Collectors.joining(", ")))));
      return 0;
    }
  }

  private static int execute(ServerCommandSource source, String name,
                             EventDifficulty eventDifficulty) {
    Optional<Event> event =
        EventFactory.buildEvent(name, source.getWorld(), eventDifficulty);

    if (!event.isPresent()) {
      source.sendError(new LiteralText("Error, event  %s not found".formatted(name)));
      return 0;
    }

    EriaEvents.eventHandler.startEvent(event.get());
    source.sendFeedback(new LiteralText("Event -  %s -  executed.".formatted(name)), false);
    return Command.SINGLE_SUCCESS;
  }
}
