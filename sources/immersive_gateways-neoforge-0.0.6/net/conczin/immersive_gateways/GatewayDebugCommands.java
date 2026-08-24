package net.conczin.immersive_gateways;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.conczin.immersive_gateways.data.PortalDataManager;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.ClickEvent.Action;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;

public class GatewayDebugCommands {
   private static final Map<UUID, GatewayDebugCommands.PendingStart> STARTS = new ConcurrentHashMap<>();

   public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
      dispatcher.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("gateway")
                     .requires(source -> source.hasPermission(2)))
                  .then(Commands.literal("start").executes(context -> start((CommandSourceStack)context.getSource()))))
               .then(Commands.literal("finish").executes(context -> finish((CommandSourceStack)context.getSource()))))
            .then(Commands.literal("detect").executes(context -> detect((CommandSourceStack)context.getSource())))
      );
   }

   public static void reset() {
      STARTS.clear();
   }

   private static int start(CommandSourceStack source) throws CommandSyntaxException {
      ServerPlayer player = source.getPlayerOrException();
      BlockPos pos = getLookedGateway(player);
      if (pos == null) {
         source.sendFailure(Component.translatable("immersive_gateways.command.look_at_gateway"));
         return 0;
      } else {
         STARTS.put(player.getUUID(), new GatewayDebugCommands.PendingStart(player.level().dimension(), pos));
         source.sendSuccess(() -> Component.translatable("immersive_gateways.command.start", new Object[]{tp(pos)}), false);
         return 1;
      }
   }

   private static int finish(CommandSourceStack source) throws CommandSyntaxException {
      ServerPlayer player = source.getPlayerOrException();
      BlockPos finish = getLookedGateway(player);
      if (finish == null) {
         source.sendFailure(Component.translatable("immersive_gateways.command.look_at_gateway"));
         return 0;
      } else {
         GatewayDebugCommands.PendingStart start = STARTS.get(player.getUUID());
         if (start == null) {
            source.sendFailure(Component.translatable("immersive_gateways.command.start_required"));
            return 0;
         } else if (start.dimension() != player.level().dimension()) {
            source.sendFailure(Component.translatable("immersive_gateways.command.dimension_mismatch"));
            return 0;
         } else {
            PortalDataManager.addManualConnection(player.serverLevel(), start.pos(), finish);
            STARTS.remove(player.getUUID());
            source.sendSuccess(() -> Component.translatable("immersive_gateways.command.finish"), true);
            return 1;
         }
      }
   }

   private static int detect(CommandSourceStack source) throws CommandSyntaxException {
      ServerPlayer player = source.getPlayerOrException();
      ServerLevel level = player.serverLevel();
      BlockPos pos = getLookedGateway(player);
      if (pos == null) {
         source.sendFailure(Component.translatable("immersive_gateways.command.look_at_gateway"));
         return 0;
      } else {
         PortalDataManager.PortalPair pair = PortalDataManager.search(level, pos, false);
         if (pair == null) {
            source.sendFailure(Component.translatable("immersive_gateways.command.detect_missing", new Object[]{tp(pos)}));
            return 0;
         } else {
            BoundingBox target = pair.getTarget(pos).boundingBox();
            source.sendSuccess(() -> Component.translatable("immersive_gateways.command.detect", new Object[]{tp(pos), tp(target.getCenter())}), false);
            return 1;
         }
      }
   }

   private static BlockPos getLookedGateway(ServerPlayer player) {
      HitResult hit = player.pick(20.0, 0.0F, false);
      if (hit.getType() != Type.BLOCK) {
         return null;
      } else {
         BlockPos pos = ((BlockHitResult)hit).getBlockPos();
         return player.level().getBlockState(pos).is(Blocks.GATEWAY) ? pos : null;
      }
   }

   private static Component tp(BlockPos pos) {
      String command = "/tp @s " + pos.getX() + " " + pos.getY() + " " + pos.getZ();
      return ComponentUtils.wrapInSquareBrackets(Component.translatable("chat.coordinates", new Object[]{pos.getX(), pos.getY(), pos.getZ()}))
         .withStyle(
            style -> style.withColor(ChatFormatting.GREEN)
               .withClickEvent(new ClickEvent(Action.SUGGEST_COMMAND, command))
               .withHoverEvent(new HoverEvent(net.minecraft.network.chat.HoverEvent.Action.SHOW_TEXT, Component.translatable("chat.coordinates.tooltip")))
         );
   }

   private record PendingStart(ResourceKey<Level> dimension, BlockPos pos) {
   }
}
