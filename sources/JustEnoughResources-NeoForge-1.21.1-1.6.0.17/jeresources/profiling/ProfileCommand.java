package jeresources.profiling;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class ProfileCommand {
   private static final String COMMAND_NAME = "jer_profile";
   private static final String CHUNK_PARAM = "chunk count";
   private static final String DIM_PARAM = "all dimensions";

   public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
      LiteralArgumentBuilder<CommandSourceStack> profileCommand = (LiteralArgumentBuilder<CommandSourceStack>)Commands.literal("jer_profile")
         .requires(source -> source.hasPermission(4) && source.getServer().isSingleplayer());
      profileCommand.then(
         Commands.argument("chunk count", IntegerArgumentType.integer(1))
            .then(
               Commands.argument("all dimensions", BoolArgumentType.bool())
                  .executes(
                     context -> Profiler.init(
                           ((CommandSourceStack)context.getSource()).getEntity(),
                           IntegerArgumentType.getInteger(context, "chunk count"),
                           BoolArgumentType.getBool(context, "all dimensions")
                        )
                        ? 1
                        : 0
                  )
            )
      );
      profileCommand.then(Commands.literal("stop").executes(context -> Profiler.stop(((CommandSourceStack)context.getSource()).getEntity()) ? 1 : 0));
      dispatcher.register(profileCommand);
   }
}
