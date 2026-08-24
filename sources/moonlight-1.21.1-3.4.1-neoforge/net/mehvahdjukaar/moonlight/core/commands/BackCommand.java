package net.mehvahdjukaar.moonlight.core.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Set;
import java.util.WeakHashMap;
import net.mehvahdjukaar.moonlight.api.misc.CircularList;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class BackCommand {
   private static final int MAX_HISTORY = 10;
   private static final WeakHashMap<ServerPlayer, CircularList<GlobalPos>> HISTORY = new WeakHashMap<>();

   public static ArgumentBuilder<CommandSourceStack, ?> register(CommandBuildContext context) {
      return ((LiteralArgumentBuilder)Commands.literal("back").requires(p -> p.hasPermission(2))).executes(BackCommand::teleportBack);
   }

   public static void onTeleported(Entity entity, BlockPos oldPos, ResourceKey<Level> oldDim) {
      if (entity instanceof ServerPlayer player) {
         record(player, GlobalPos.of(oldDim, oldPos));
      }
   }

   public static void onPlayerCloned(Player oldPlayer, Player newPlayer) {
      if (oldPlayer instanceof ServerPlayer old && newPlayer instanceof ServerPlayer fresh) {
         CircularList<GlobalPos> history = HISTORY.remove(old);
         if (history != null) {
            HISTORY.put(fresh, history);
         }

         record(fresh, GlobalPos.of(old.level().dimension(), old.blockPosition()));
      }
   }

   private static void record(ServerPlayer player, GlobalPos pos) {
      CircularList<GlobalPos> list = HISTORY.computeIfAbsent(player, p -> new CircularList<>(10));
      if (list.isEmpty() || !list.getLast().equals(pos)) {
         list.addLast(pos);
      }
   }

   private static int teleportBack(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      CommandSourceStack source = (CommandSourceStack)context.getSource();
      if (source.getEntity() instanceof ServerPlayer player) {
         CircularList<GlobalPos> list = HISTORY.get(player);
         if (list != null && !list.isEmpty()) {
            GlobalPos last = list.removeLast();
            ServerLevel targetLevel = source.getServer().getLevel(last.dimension());
            if (targetLevel == null) {
               source.sendFailure(Component.translatable("commands.moonlight.back.invalid_dimension"));
               return 0;
            } else {
               BlockPos pos = last.pos();
               double x = pos.getX() + 0.5;
               double y = pos.getY();
               double z = pos.getZ() + 0.5;
               Set<RelativeMovement> set = EnumSet.of(RelativeMovement.X_ROT, RelativeMovement.Y_ROT);
               performTeleport(source, player, targetLevel, x, y, z, set);
               source.sendSuccess(
                  () -> Component.translatable(
                     "commands.teleport.success.location.single", new Object[]{player.getDisplayName(), formatDouble(x), formatDouble(y), formatDouble(z)}
                  ),
                  true
               );
               return 1;
            }
         } else {
            source.sendFailure(Component.translatable("commands.moonlight.back.empty"));
            return 0;
         }
      } else {
         source.sendFailure(Component.translatable("commands.moonlight.back.only_players"));
         return 0;
      }
   }

   private static void performTeleport(
      CommandSourceStack source, Entity entity, ServerLevel level, double x, double y, double z, Set<RelativeMovement> relativeList
   ) throws CommandSyntaxException {
      BlockPos blockPos = BlockPos.containing(x, y, z);
      if (!Level.isInSpawnableBounds(blockPos)) {
         throw new CommandSyntaxException(null, Component.translatable("commands.teleport.invalidPosition"));
      } else {
         float f = Mth.wrapDegrees(entity.getYRot());
         float g = Mth.wrapDegrees(entity.getXRot());
         if (entity.teleportTo(level, x, y, z, relativeList, f, g)) {
            if (!(entity instanceof LivingEntity livingEntity && livingEntity.isFallFlying())) {
               entity.setDeltaMovement(entity.getDeltaMovement().multiply(1.0, 0.0, 1.0));
               entity.setOnGround(true);
            }

            if (entity instanceof PathfinderMob pathfinderMob) {
               pathfinderMob.getNavigation().stop();
            }
         }
      }
   }

   private static String formatDouble(double d) {
      return String.format(Locale.ROOT, "%f", d);
   }
}
