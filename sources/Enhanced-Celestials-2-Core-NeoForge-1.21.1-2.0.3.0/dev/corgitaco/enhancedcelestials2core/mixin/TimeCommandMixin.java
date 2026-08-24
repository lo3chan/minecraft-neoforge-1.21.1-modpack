package dev.corgitaco.enhancedcelestials2core.mixin;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.corgitaco.enhancedcelestials2core.util.TimeCommandUtil;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.TimeArgument;
import net.minecraft.server.commands.TimeCommand;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({TimeCommand.class})
public class TimeCommandMixin {
   @Shadow
   private static int getDayTime(ServerLevel level) {
      throw new AssertionError();
   }

   @Shadow
   private static int queryTime(CommandSourceStack source, int time) {
      throw new AssertionError();
   }

   @Overwrite
   public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
      dispatcher.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("time")
                     .requires(source -> source.hasPermission(2)))
                  .then(
                     ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("set")
                                    .then(TimeCommandUtil.setNode("day", 1000)))
                                 .then(TimeCommandUtil.setNode("noon", 6000)))
                              .then(TimeCommandUtil.setNode("night", 13000)))
                           .then(TimeCommandUtil.setNode("midnight", 18000)))
                        .then(TimeCommandUtil.setTimeArgumentNode())
                  ))
               .then(
                  Commands.literal("add")
                     .then(
                        Commands.argument("time", TimeArgument.time())
                           .executes(context -> TimeCommand.addTime((CommandSourceStack)context.getSource(), IntegerArgumentType.getInteger(context, "time")))
                     )
               ))
            .then(
               ((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("query")
                        .then(
                           Commands.literal("daytime")
                              .executes(
                                 context -> queryTime((CommandSourceStack)context.getSource(), getDayTime(((CommandSourceStack)context.getSource()).getLevel()))
                              )
                        ))
                     .then(
                        Commands.literal("gametime")
                           .executes(
                              context -> queryTime(
                                 (CommandSourceStack)context.getSource(),
                                 (int)(((CommandSourceStack)context.getSource()).getLevel().getGameTime() % 2147483647L)
                              )
                           )
                     ))
                  .then(
                     Commands.literal("day")
                        .executes(
                           context -> queryTime(
                              (CommandSourceStack)context.getSource(),
                              (int)(((CommandSourceStack)context.getSource()).getLevel().getDayTime() / 24000L % 2147483647L)
                           )
                        )
                  )
            )
      );
   }
}
