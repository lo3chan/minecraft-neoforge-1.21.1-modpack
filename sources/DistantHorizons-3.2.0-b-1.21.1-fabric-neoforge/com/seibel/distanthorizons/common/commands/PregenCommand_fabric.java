package com.seibel.distanthorizons.common.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.seibel.distanthorizons.common.wrappers.world.ServerLevelWrapper_fabric;
import com.seibel.distanthorizons.core.api.internal.SharedApi;
import com.seibel.distanthorizons.core.generation.PregenManager;
import com.seibel.distanthorizons.core.pos.blockPos.DhBlockPos2D;
import com.seibel.distanthorizons.core.world.DhServerWorld;
import java.util.Objects;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import net.minecraft.class_2168;
import net.minecraft.class_2170;
import net.minecraft.class_2181;
import net.minecraft.class_2264;
import net.minecraft.class_2265;
import net.minecraft.class_3218;

public class PregenCommand_fabric extends AbstractCommand_fabric {
   private PregenManager getPregenManager() {
      DhServerWorld world = Objects.requireNonNull(SharedApi.getAbstractDhWorld());
      return world.getPregenManager();
   }

   @Override
   public LiteralArgumentBuilder<class_2168> buildCommand() {
      LiteralArgumentBuilder<class_2168> statusCommand = (LiteralArgumentBuilder<class_2168>)class_2170.method_9247("status").executes(this::pregenStatus);
      LiteralArgumentBuilder<class_2168> startCommand = (LiteralArgumentBuilder<class_2168>)class_2170.method_9247("start")
         .then(
            class_2170.method_9244("dimension", class_2181.method_9288())
               .then(
                  class_2170.method_9244("origin", class_2264.method_9701())
                     .then(class_2170.method_9244("chunkRadius", IntegerArgumentType.integer(32)).executes(this::pregenStart))
               )
         );
      LiteralArgumentBuilder<class_2168> stopCommand = (LiteralArgumentBuilder<class_2168>)class_2170.method_9247("stop").executes(this::pregenStop);
      return (LiteralArgumentBuilder<class_2168>)((LiteralArgumentBuilder)((LiteralArgumentBuilder)class_2170.method_9247("pregen").then(statusCommand))
            .then(startCommand))
         .then(stopCommand);
   }

   private int pregenStatus(CommandContext<class_2168> c) {
      String statusString = this.getPregenManager().getStatusString();
      return statusString != null ? this.sendSuccessResponse(c, statusString, false) : this.sendSuccessResponse(c, "Pregen is not running", false);
   }

   private int pregenStart(CommandContext<class_2168> c) throws CommandSyntaxException {
      this.sendSuccessResponse(c, "Starting pregen. Progress will be in the server console.", true);
      class_3218 level = class_2181.method_9289(c, "dimension");
      class_2265 origin = class_2264.method_9702(c, "origin");
      int chunkRadius = IntegerArgumentType.getInteger(c, "chunkRadius");
      CompletableFuture<Void> future = this.getPregenManager()
         .startPregen(ServerLevelWrapper_fabric.getWrapper(level), new DhBlockPos2D(origin.comp_638(), origin.comp_639()), chunkRadius);
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

   private int pregenStop(CommandContext<class_2168> c) {
      CompletableFuture<Void> runningPregen = this.getPregenManager().getRunningPregen();
      if (runningPregen == null) {
         return this.sendFailureResponse(c, "Pregen is not running");
      } else {
         runningPregen.cancel(true);
         return 1;
      }
   }
}
