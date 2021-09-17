package com.virtualeria.eriaevents.command;

import static com.virtualeria.eriaevents.command.CommandValues.BASE_ALIAS_NAME;
import static net.minecraft.server.command.CommandManager.literal;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.virtualeria.eriaevents.event.EventHandler;
import java.util.stream.Collectors;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;

public class GetUidCommand {
  public static String FIRST_ARG = "uids";

   public static void register(CommandDispatcher<ServerCommandSource> dispatcher, boolean b) {
    var command = dispatcher.register(
        literal(CommandValues.BASE_COMMAND_NAME)
            .requires((source) -> source.hasPermissionLevel(2))
            .then(literal(FIRST_ARG).executes(context -> execute(context.getSource()))));
    dispatcher
        .register(literal(BASE_ALIAS_NAME).requires((source) -> source.hasPermissionLevel(2))
            .redirect(command));
  }

  private static int execute(ServerCommandSource source) {
    source.sendFeedback(new LiteralText("Event uids: %s ".formatted(EventHandler.activeEventUids().stream().collect(Collectors.joining(", ")))), false);
    return Command.SINGLE_SUCCESS;
  }
}
