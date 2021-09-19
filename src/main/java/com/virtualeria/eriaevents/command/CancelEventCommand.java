package com.virtualeria.eriaevents.command;

import static com.virtualeria.eriaevents.command.CommandValues.BASE_ALIAS_NAME;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.virtualeria.eriaevents.event.EventHandler;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;

public class CancelEventCommand {
  public final static String ACTION = "cancel";
  public final static String BY_UID_SECOND_ARG = "uid";

  public static void register(CommandDispatcher<ServerCommandSource> dispatcher, boolean b) {
    var eventBuilderByName =
        literal(CommandValues.BASE_COMMAND_NAME).requires((source) -> source.hasPermissionLevel(2));
    var action = literal(ACTION);
    var eventUid =
        argument(BY_UID_SECOND_ARG, StringArgumentType.word())
            .executes((context ->
                execute(
                    context.getSource(),
                    StringArgumentType.getString(context, BY_UID_SECOND_ARG))));

    var command =
        dispatcher.register(
            eventBuilderByName.then(action.then(eventUid)));

    dispatcher
        .register(literal(BASE_ALIAS_NAME).requires((source) -> source.hasPermissionLevel(2))
            .redirect(command));
  }

  private static int execute(ServerCommandSource source, String uid) {
    EventHandler.activeEventByUid(uid).ifPresentOrElse(
        (event) -> {
          EventHandler.forceFinish(event);
          source.sendFeedback(new LiteralText("Deleted event  - %s - ".formatted(uid)), false);
        },
        () -> source.sendError(new LiteralText("No event with uid  - %s - ".formatted(uid)))
    );
    return Command.SINGLE_SUCCESS;
  }
}
