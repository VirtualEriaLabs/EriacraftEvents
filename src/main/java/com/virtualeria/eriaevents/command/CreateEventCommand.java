package com.virtualeria.eriaevents.command;

import static com.virtualeria.eriaevents.command.CommandValues.BASE_ALIAS_NAME;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.virtualeria.eriaevents.EriaEvents;
import com.virtualeria.eriaevents.event.EventFactory;
import com.virtualeria.eriaevents.event.EventFactory.EventType;
import com.virtualeria.eriaevents.event.events.Event.EventDifficulty;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.TimeArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;

public class CreateEventCommand {

  public final static String EVENT_TYPE_FIRST_ARG = "name";
  public final static String DIFFICULTY_SECOND_ARG = "difficulty";
  public final static String DURATION_THIRD_ARG = "duration";
  public final static String POSITION_FOURTH_ARG = "position";
  public final static String PLAYERS_FIFTH_ARG = "players";
  public final static int PERMISSION_LEVEL = 2;

  public static void register(CommandDispatcher<ServerCommandSource> dispatcher, boolean b) {
    var eventBuilderByName =
        literal(CommandValues.BASE_COMMAND_NAME).requires((source) -> source.hasPermissionLevel(2));

    var nameArg = argument(EVENT_TYPE_FIRST_ARG, StringArgumentType.word())
        .executes((CreateEventCommand::execute));

    var requiredDifficultyAfterNameArg =
        argument(DIFFICULTY_SECOND_ARG, StringArgumentType.word())
            .executes((CreateEventCommand::execute));

    var eventDurationIntArg =
        argument(DURATION_THIRD_ARG, TimeArgumentType.time())
            .executes((CreateEventCommand::execute));

    var positionAfterDurationArg =
        argument(POSITION_FOURTH_ARG, BlockPosArgumentType.blockPos())
            .executes((CreateEventCommand::execute));

    var playersNameArg =
        argument(PLAYERS_FIFTH_ARG, EntityArgumentType.players())
            .executes((CreateEventCommand::execute));

    var command =
        dispatcher.register(
            eventBuilderByName
                .then(nameArg.then(requiredDifficultyAfterNameArg.then(
                    eventDurationIntArg.then(positionAfterDurationArg.then(playersNameArg))))));

    dispatcher
        .register(literal(BASE_ALIAS_NAME).requires(
                (source) -> source.hasPermissionLevel(PERMISSION_LEVEL))
            .redirect(command));
  }

  private static int execute(CommandContext<ServerCommandSource> sourceCommand) {
    var source = sourceCommand.getSource();
    try {
      EventType eventType = getType(sourceCommand);
      var createEventData = new CreateEventData(
          source.getWorld(),
          parseDifficulty(sourceCommand),
          parseDuration(sourceCommand),
          parsePosition(sourceCommand),
          parsePlayers(sourceCommand)
      );

      EventFactory.eventMap.get(eventType)
          .apply(createEventData)
          .ifPresentOrElse(
              (event) -> EriaEvents.eventHandler.startEvent(event),
              () -> source.sendError(
                  new TranslatableText("Error creating event %s".formatted(eventType.name())))
          );
    } catch (EEVException e) {
      sourceCommand.getSource().sendError(new LiteralText(e.getMessage()));
    } catch (CommandSyntaxException e) {
      sourceCommand.getSource()
          .sendError(new TranslatableText("Syntax error: %s".formatted(e.getMessage())));
    } catch (Exception e) {
      sourceCommand.getSource()
          .sendError(new TranslatableText("Unexpected error, unable to create event."));
    }

    return Command.SINGLE_SUCCESS;
  }

  private static EventType getType(CommandContext<ServerCommandSource> sourceCommand) {
    try {
      return EventType.valueOf(StringArgumentType.getString(sourceCommand, EVENT_TYPE_FIRST_ARG));
    } catch (IllegalArgumentException e) {
      sourceCommand.getSource().sendError(new TranslatableText("Error: event types: %s".formatted(
          Arrays.stream(EventType.values()).map(Enum::name)
              .collect(
                  Collectors.joining(", ")))));
      throw new EEVException();
    }
  }

  private static EventDifficulty parseDifficulty(
      CommandContext<ServerCommandSource> sourceCommand) {
    try {
      return EventDifficulty.valueOf(StringArgumentType.getString(sourceCommand,
          DIFFICULTY_SECOND_ARG));
    } catch (IllegalArgumentException e) {
        sourceCommand.getSource().sendError(new TranslatableText("Error: difficulty values: %s".formatted(
          Arrays.stream(EventDifficulty.values()).map(Enum::name)
              .collect(
                  Collectors.joining(", ")))));
      throw new EEVException();
    }
  }

  private static BlockPos parsePosition(CommandContext<ServerCommandSource> sourceCommand)
      throws CommandSyntaxException {
    return BlockPosArgumentType.getBlockPos(sourceCommand, POSITION_FOURTH_ARG);
  }


  private static long parseDuration(CommandContext<ServerCommandSource> sourceCommand) {
    return sourceCommand.getArgument(DURATION_THIRD_ARG, Integer.class) * 50;
  }

  private static List<ServerPlayerEntity> parsePlayers(
      CommandContext<ServerCommandSource> sourceCommand)
      throws CommandSyntaxException {
    return EntityArgumentType.getPlayers(sourceCommand, PLAYERS_FIFTH_ARG).stream().toList();
  }
}
