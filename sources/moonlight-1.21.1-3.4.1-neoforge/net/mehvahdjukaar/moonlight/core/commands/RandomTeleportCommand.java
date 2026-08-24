package net.mehvahdjukaar.moonlight.core.commands;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.Vec3;

public class RandomTeleportCommand {
   private static final SimpleCommandExceptionType INVALID_POSITION = new SimpleCommandExceptionType(
      Component.translatable("commands.teleport.invalidPosition")
   );

   public static ArgumentBuilder<CommandSourceStack, ?> register(CommandBuildContext context) {
      return ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("tpr").requires(p -> p.hasPermission(2)))
               .executes(c -> teleportRandom(c, List.of(((CommandSourceStack)c.getSource()).getEntityOrException()), Optional.empty())))
            .then(
               ((RequiredArgumentBuilder)Commands.argument("radius", DoubleArgumentType.doubleArg(0.0))
                     .executes(
                        c -> teleportRandom(
                           c, List.of(((CommandSourceStack)c.getSource()).getEntityOrException()), Optional.of(DoubleArgumentType.getDouble(c, "radius"))
                        )
                     ))
                  .then(
                     Commands.argument("targets", EntityArgument.entities())
                        .executes(c -> teleportRandom(c, EntityArgument.getEntities(c, "targets"), Optional.of(DoubleArgumentType.getDouble(c, "radius"))))
                  )
            ))
         .then(
            Commands.argument("targets", EntityArgument.entities())
               .executes(c -> teleportRandom(c, EntityArgument.getEntities(c, "targets"), Optional.empty()))
         );
   }

   private static int teleportRandom(CommandContext<CommandSourceStack> context, Collection<? extends Entity> targets, Optional<Double> optRadius) throws CommandSyntaxException {
      CommandSourceStack source = (CommandSourceStack)context.getSource();
      ServerLevel level = source.getLevel();
      RandomSource random = level.getRandom();
      WorldBorder border = level.getWorldBorder();
      double xMin;
      double xMax;
      double zMin;
      double zMax;
      if (optRadius.isPresent()) {
         double radius = optRadius.get();

         double centerX;
         double centerZ;
         try {
            Entity src = source.getEntityOrException();
            centerX = src.getX();
            centerZ = src.getZ();
         } catch (CommandSyntaxException var31) {
            centerX = border.getCenterX();
            centerZ = border.getCenterZ();
         }

         xMin = centerX - radius;
         xMax = centerX + radius;
         zMin = centerZ - radius;
         zMax = centerZ + radius;
      } else {
         double centerXx = border.getCenterX();
         double centerZ = border.getCenterZ();
         double half = border.getSize() / 2.0;
         xMin = centerXx - half;
         xMax = centerXx + half;
         zMin = centerZ - half;
         zMax = centerZ + half;
      }

      Set<RelativeMovement> set = EnumSet.noneOf(RelativeMovement.class);
      set.add(RelativeMovement.X_ROT);
      set.add(RelativeMovement.Y_ROT);

      for (Entity entity : targets) {
         double sampledX = xMin + random.nextDouble() * (xMax - xMin);
         double sampledZ = zMin + random.nextDouble() * (zMax - zMin);
         BlockPos clamped = border.clampToBounds(sampledX, 0.0, sampledZ);
         double x = clamped.getX();
         double z = clamped.getZ();
         int xi = Mth.floor(x);
         int zi = Mth.floor(z);
         int finalY = 70;
         BlockPos blockPos = BlockPos.containing(x, finalY, z);
         if (!Level.isInSpawnableBounds(blockPos)) {
            throw INVALID_POSITION.create();
         }

         performTeleport(source, entity, level, x, finalY, z, set);
      }

      Vec3 example = targets.iterator().next().position();
      if (targets.size() == 1) {
         source.sendSuccess(
            () -> Component.translatable(
               "commands.teleport.success.location.single",
               new Object[]{targets.iterator().next().getDisplayName(), formatDouble(example.x), formatDouble(example.y), formatDouble(example.z)}
            ),
            true
         );
      } else {
         source.sendSuccess(
            () -> Component.translatable(
               "commands.teleport.success.location.multiple",
               new Object[]{targets.size(), formatDouble(example.x), formatDouble(example.y), formatDouble(example.z)}
            ),
            true
         );
      }

      return targets.size();
   }

   private static void performTeleport(
      CommandSourceStack source, Entity entity, ServerLevel level, double x, double y, double z, Set<RelativeMovement> relativeList
   ) throws CommandSyntaxException {
      BlockPos blockPos = BlockPos.containing(x, y, z);
      if (!Level.isInSpawnableBounds(blockPos)) {
         throw INVALID_POSITION.create();
      } else {
         float f = Mth.wrapDegrees(entity.getYRot());
         float g = Mth.wrapDegrees(entity.getXRot());
         BlockPos oldPos = entity.blockPosition();
         ResourceKey<Level> oldDim = entity.level().dimension();
         if (entity.teleportTo(level, x, y, z, relativeList, f, g)) {
            BackCommand.onTeleported(entity, oldPos, oldDim);
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
