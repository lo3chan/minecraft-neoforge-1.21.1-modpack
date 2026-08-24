package net.mehvahdjukaar.moonlight.core.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.Commands.CommandSelection;

public class ModCommands {
   public static void init() {
      RegHelper.addCommandRegistration(ModCommands::register);
   }

   public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context, CommandSelection selection) {
      LiteralCommandNode<CommandSourceStack> node = dispatcher.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal(
                                       "moonlight"
                                    )
                                    .then(ConfigCommand.register()))
                                 .then(RegistryCommand.register()))
                              .then(BlockStateStatsCommand.register(context)))
                           .then(IUsedToRollTheDice.register(context)))
                        .then(DebugRenderersCommand.register(context)))
                     .then(RandomTeleportCommand.register(context)))
                  .then(ChangeDimensionCommand.register(context)))
               .then(BackCommand.register(context)))
            .then(MapMarkerCommand.register(context))
      );
      dispatcher.register((LiteralArgumentBuilder)Commands.literal("mnl").redirect(node));
   }
}
