package com.seibel.distanthorizons.common.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.seibel.distanthorizons.common.wrappers.world.ServerLevelWrapper_neoforge;
import com.seibel.distanthorizons.core.api.internal.SharedApi;
import com.seibel.distanthorizons.core.generation.PregenManager;
import com.seibel.distanthorizons.core.pos.blockPos.DhBlockPos2D;
import com.seibel.distanthorizons.core.world.DhServerWorld;
import java.util.Objects;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.coordinates.ColumnPosArgument;
import net.minecraft.server.level.ColumnPos;
import net.minecraft.server.level.ServerLevel;

public class PregenCommand_neoforge extends AbstractCommand_neoforge {
   private PregenManager getPregenManager() {
      DhServerWorld world = Objects.requireNonNull(SharedApi.getAbstractDhWorld());
      return world.getPregenManager();
   }

   @Override
   public LiteralArgumentBuilder<CommandSourceStack> buildCommand() {
      LiteralArgumentBuilder<CommandSourceStack> statusCommand = (LiteralArgumentBuilder<CommandSourceStack>)Commands.literal("status")
         .executes(this::pregenStatus);
      LiteralArgumentBuilder<CommandSourceStack> startCommand = (LiteralArgumentBuilder<CommandSourceStack>)Commands.literal("start")
         .then(
            Commands.argument("dimension", DimensionArgument.dimension())
               .then(
                  Commands.argument("origin", ColumnPosArgument.columnPos())
                     .then(Commands.argument("chunkRadius", IntegerArgumentType.integer(32)).executes(this::pregenStart))
               )
         );
      LiteralArgumentBuilder<CommandSourceStack> stopCommand = (LiteralArgumentBuilder<CommandSourceStack>)Commands.literal("stop").executes(this::pregenStop);
      return (LiteralArgumentBuilder<CommandSourceStack>)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("pregen").then(statusCommand))
            .then(startCommand))
         .then(stopCommand);
   }

   private int pregenStatus(CommandContext<CommandSourceStack> c) {
      String statusString = this.getPregenManager().getStatusString();
      return statusString != null ? this.sendSuccessResponse(c, statusString, false) : this.sendSuccessResponse(c, "Pregen is not running", false);
   }

   private int pregenStart(CommandContext<CommandSourceStack> c) throws CommandSyntaxException {
      this.sendSuccessResponse(c, "Starting pregen. Progress will be in the server console.", true);
      ServerLevel level = DimensionArgument.getDimension(c, "dimension");
      ColumnPos origin = ColumnPosArgument.getColumnPos(c, "origin");
      int chunkRadius = IntegerArgumentType.getInteger(c, "chunkRadius");
      CompletableFuture<Void> future = this.getPregenManager()
         .startPregen(ServerLevelWrapper_neoforge.getWrapper(level), new DhBlockPos2D(origin.x(), origin.z()), chunkRadius);
      future.whenComplete((result, throwable) -> {
         if (throwable instanceof CancellationException) {
            this.sendSuccessResponse(c, "Pregen is cancelled", true);
         } else if (throwable != null) {
            this.sendFailureResponse(c, "Pregen failed: " + throwable.getMessage() + "\n Check the logs for more details.");
         } else {
            this.sendSuccessResponse(c, "Pregen is complete", true);
         }
      });
      return 1;
   }

   private int pregenStop(CommandContext<CommandSourceStack> c) {
      CompletableFuture<Void> runningPregen = this.getPregenManager().getRunningPregen();
      if (runningPregen == null) {
         return this.sendFailureResponse(c, "Pregen is not running");
      } else {
         runningPregen.cancel(true);
         return 1;
      }
   }
}
