package com.aetherteam.aether.event.hooks;

import com.aetherteam.aether.AetherConfig;
import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.attachment.AetherDataAttachments;
import com.aetherteam.aether.attachment.AetherPlayerAttachment;
import com.aetherteam.aether.attachment.AetherTimeAttachment;
import com.aetherteam.aether.block.portal.AetherPortalShape;
import com.aetherteam.aether.data.resources.registries.AetherDimensions;
import com.aetherteam.aether.mixin.mixins.common.accessor.ServerGamePacketListenerImplAccessor;
import com.aetherteam.aether.mixin.mixins.common.accessor.ServerLevelAccessor;
import com.aetherteam.aether.network.packet.clientbound.AetherTravelPacket;
import com.aetherteam.aether.network.packet.clientbound.LeavingAetherPacket;
import com.aetherteam.aether.network.packet.clientbound.PortalInteractPacket;
import com.aetherteam.aether.world.AetherLevelData;
import com.aetherteam.aether.world.LevelUtil;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.Plane;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.network.PacketDistributor;

public class DimensionHooks {
   public static boolean playerLeavingAether;
   public static boolean displayAetherTravel;
   public static int teleportationTimer;

   public static void startInAether(Player player) {
      AetherPlayerAttachment aetherPlayer = (AetherPlayerAttachment)player.getData(AetherDataAttachments.AETHER_PLAYER);
      if ((Boolean)AetherConfig.SERVER.spawn_in_aether.get()) {
         if (aetherPlayer.canSpawnInAether() && player instanceof ServerPlayer serverPlayer) {
            MinecraftServer server = serverPlayer.level().getServer();
            if (server != null) {
               ServerLevel aetherLevel = server.getLevel(AetherDimensions.AETHER_LEVEL);
               if (aetherLevel != null && serverPlayer.level().dimension() == Level.OVERWORLD) {
                  DimensionTransition transition = new DimensionTransition(
                     aetherLevel,
                     checkPositionsForInitialSpawn(aetherLevel, serverPlayer.blockPosition()).getCenter(),
                     Vec3.ZERO,
                     serverPlayer.getYRot(),
                     serverPlayer.getXRot(),
                     false,
                     DimensionTransition.DO_NOTHING
                  );
                  if (serverPlayer.changeDimension(transition) != null) {
                     serverPlayer.setRespawnPosition(AetherDimensions.AETHER_LEVEL, serverPlayer.blockPosition(), serverPlayer.getYRot(), true, false);
                     aetherPlayer.setCanSpawnInAether(false);
                  }
               }
            }
         }
      } else {
         aetherPlayer.setCanSpawnInAether(false);
      }
   }

   private static BlockPos checkPositionsForInitialSpawn(Level level, BlockPos origin) {
      if (!isSafe(level, origin)) {
         for (int i = 0; i <= 750; i += 5) {
            for (Direction facing : Plane.HORIZONTAL) {
               BlockPos offsetPosition = origin.offset(facing.getNormal().multiply(i));
               if (isSafeAround(level, offsetPosition)) {
                  return offsetPosition;
               }

               BlockPos heightmapPosition = level.getHeightmapPos(Types.MOTION_BLOCKING, offsetPosition);
               if (isSafeAround(level, heightmapPosition)) {
                  return heightmapPosition;
               }
            }
         }
      }

      return origin;
   }

   public static boolean isSafeAround(Level level, BlockPos pos) {
      BlockPos belowPos = pos.below();
      if (!isSafe(level, belowPos)) {
         return false;
      } else {
         for (Direction facing : Plane.HORIZONTAL) {
            if (!isSafe(level, belowPos.relative(facing, 2))) {
               return false;
            }
         }

         return true;
      }
   }

   private static boolean isSafe(Level level, BlockPos pos) {
      return level.getWorldBorder().isWithinBounds(pos)
         && level.getBlockState(pos).is(AetherTags.Blocks.AETHER_DIRT)
         && level.getBlockState(pos.above()).isAir()
         && level.getBlockState(pos.above(2)).isAir();
   }

   public static boolean createPortal(Player player, Level level, BlockPos pos, @Nullable Direction direction, ItemStack stack, InteractionHand hand) {
      if (!level.isClientSide()
         && (!ModList.get().isLoaded("immersive_portals_core") || !(Boolean)AetherConfig.COMMON.enable_immersive_portals_compatibility.get())
         && direction != null) {
         BlockPos relativePos = pos.relative(direction);
         if (stack.is(AetherTags.Items.AETHER_PORTAL_ACTIVATION_ITEMS)
            && (level.dimension() == LevelUtil.returnDimension() || level.dimension() == LevelUtil.destinationDimension())) {
            Optional<AetherPortalShape> optional = AetherPortalShape.findEmptyAetherPortalShape(level, relativePos, Axis.X);
            if (optional.isPresent()) {
               PacketDistributor.sendToAllPlayers(new PortalInteractPacket(player.getId(), hand == InteractionHand.MAIN_HAND), new CustomPacketPayload[0]);
               optional.get().createPortalBlocks();
               if (!player.isCreative()) {
                  if (stack.getCount() > 1) {
                     stack.shrink(1);
                     player.addItem(stack.hasCraftingRemainingItem() ? stack.getCraftingRemainingItem() : ItemStack.EMPTY);
                  } else if (stack.isDamageableItem()) {
                     stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
                  } else {
                     player.setItemInHand(hand, stack.hasCraftingRemainingItem() ? stack.getCraftingRemainingItem() : ItemStack.EMPTY);
                  }
               }

               return true;
            }
         }
      }

      return false;
   }

   public static boolean detectWaterInFrame(LevelAccessor levelAccessor, BlockPos pos, BlockState blockState, FluidState fluidState) {
      if (levelAccessor instanceof Level level
         && !level.isClientSide()
         && (!ModList.get().isLoaded("immersive_portals_core") || !(Boolean)AetherConfig.COMMON.enable_immersive_portals_compatibility.get())
         && fluidState.is(Fluids.WATER)
         && fluidState.createLegacyBlock().getBlock() == blockState.getBlock()
         && (level.dimension() == LevelUtil.returnDimension() || level.dimension() == LevelUtil.destinationDimension())
         && !(Boolean)AetherConfig.SERVER.disable_aether_portal.get()) {
         Optional<AetherPortalShape> optional = AetherPortalShape.findEmptyAetherPortalShape(level, pos, Axis.X);
         if (optional.isPresent()) {
            optional.get().createPortalBlocks();
            return true;
         }
      }

      return false;
   }

   public static void tickTime(Level level) {
      if (level.dimensionType().effectsLocation().equals(AetherDimensions.AETHER_DIMENSION_TYPE.location()) && level instanceof ServerLevel serverLevel) {
         ServerLevelAccessor serverLevelAccessor = (ServerLevelAccessor)serverLevel;
         com.aetherteam.aether.mixin.mixins.common.accessor.LevelAccessor levelAccessor = (com.aetherteam.aether.mixin.mixins.common.accessor.LevelAccessor)level;
         long i = levelAccessor.aether$getLevelData().getGameTime() + 1L;
         serverLevelAccessor.aether$getServerLevelData().setGameTime(i);
         if (serverLevelAccessor.aether$getServerLevelData().getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)) {
            serverLevel.setDayTime(((AetherTimeAttachment)serverLevel.getData(AetherDataAttachments.AETHER_TIME)).tickTime(level));
         }
      }
   }

   public static void checkEternalDayConfig(Level level) {
      if (!level.isClientSide() && level.hasData(AetherDataAttachments.AETHER_TIME)) {
         AetherTimeAttachment aetherTime = (AetherTimeAttachment)level.getData(AetherDataAttachments.AETHER_TIME);
         boolean eternalDay = aetherTime.isEternalDay();
         if ((Boolean)AetherConfig.SERVER.disable_eternal_day.get() && eternalDay) {
            aetherTime.setEternalDay(false);
            aetherTime.updateEternalDay(level);
         }
      }
   }

   public static void dimensionTravel(Entity entity, ResourceKey<Level> dimension) {
      if (entity instanceof Player player && !player.level().isClientSide()) {
         AetherPlayerAttachment aetherPlayer = (AetherPlayerAttachment)player.getData(AetherDataAttachments.AETHER_PLAYER);
         if ((!(Boolean)AetherConfig.SERVER.spawn_in_aether.get() || !aetherPlayer.canSpawnInAether())
            && entity.level().getBiome(entity.blockPosition()).is(AetherTags.Biomes.DISPLAY_TRAVEL_TEXT)) {
            if (entity.level().dimension() == LevelUtil.destinationDimension() && dimension == LevelUtil.returnDimension()) {
               displayAetherTravel = true;
               playerLeavingAether = true;
               PacketDistributor.sendToAllPlayers(new AetherTravelPacket(true), new CustomPacketPayload[0]);
               PacketDistributor.sendToAllPlayers(new LeavingAetherPacket(true), new CustomPacketPayload[0]);
            } else if (entity.level().dimension() == LevelUtil.returnDimension() && dimension == LevelUtil.destinationDimension()) {
               displayAetherTravel = true;
               playerLeavingAether = false;
               PacketDistributor.sendToAllPlayers(new AetherTravelPacket(true), new CustomPacketPayload[0]);
               PacketDistributor.sendToAllPlayers(new LeavingAetherPacket(false), new CustomPacketPayload[0]);
            } else {
               displayAetherTravel = false;
               PacketDistributor.sendToAllPlayers(new AetherTravelPacket(false), new CustomPacketPayload[0]);
            }
         }
      }
   }

   public static void removePlayerAerbunny(Entity entity) {
      if (entity instanceof Player player) {
         ((AetherPlayerAttachment)player.getData((AttachmentType)AetherDataAttachments.AETHER_PLAYER.get())).removeAerbunny();
      }
   }

   public static void remountPlayerAerbunny(Player player) {
      ((AetherPlayerAttachment)player.getData((AttachmentType)AetherDataAttachments.AETHER_PLAYER.get())).remountAerbunny(player);
   }

   public static void travelling(Player player) {
      if (player instanceof ServerPlayer serverPlayer) {
         if (teleportationTimer > 0) {
            ServerGamePacketListenerImplAccessor serverGamePacketListenerImplAccessor = (ServerGamePacketListenerImplAccessor)serverPlayer.connection;
            serverGamePacketListenerImplAccessor.aether$setAboveGroundTickCount(0);
            serverGamePacketListenerImplAccessor.aether$setAboveGroundVehicleTickCount(0);
            teleportationTimer--;
         }

         if (teleportationTimer < 0 || serverPlayer.verticalCollisionBelow) {
            teleportationTimer = 0;
         }
      }
   }

   public static void initializeLevelData(LevelAccessor level) {
      if (level instanceof ServerLevel serverLevel && serverLevel.dimensionType().effectsLocation().equals(AetherDimensions.AETHER_DIMENSION_TYPE.location())) {
         AetherLevelData levelData = new AetherLevelData(
            serverLevel,
            serverLevel.getServer().getWorldData(),
            serverLevel.getServer().getWorldData().overworldData(),
            ((AetherTimeAttachment)serverLevel.getData(AetherDataAttachments.AETHER_TIME)).getDayTime()
         );
         ServerLevelAccessor serverLevelAccessor = (ServerLevelAccessor)serverLevel;
         com.aetherteam.aether.mixin.mixins.common.accessor.LevelAccessor levelAccessor = (com.aetherteam.aether.mixin.mixins.common.accessor.LevelAccessor)level;
         serverLevelAccessor.aether$setServerLevelData(levelData);
         levelAccessor.aether$setLevelData(levelData);
      }
   }

   @Nullable
   public static Long finishSleep(LevelAccessor level, long newTime) {
      if (level instanceof ServerLevel && level.dimensionType().effectsLocation().equals(AetherDimensions.AETHER_DIMENSION_TYPE.location())) {
         ServerLevelAccessor serverLevelAccessor = (ServerLevelAccessor)level;
         serverLevelAccessor.aether$getServerLevelData().setRainTime(0);
         serverLevelAccessor.aether$getServerLevelData().setRaining(false);
         serverLevelAccessor.aether$getServerLevelData().setThunderTime(0);
         serverLevelAccessor.aether$getServerLevelData().setThundering(false);
         long time = newTime + 24000L * AetherTimeAttachment.getTicksPerDayMultiplier();
         return time - time % AetherTimeAttachment.getTicksPerDay();
      } else {
         return null;
      }
   }

   public static boolean isEternalDay(Player player) {
      return player.level().dimensionType().effectsLocation().equals(AetherDimensions.AETHER_DIMENSION_TYPE.location())
         ? ((AetherTimeAttachment)player.level().getData(AetherDataAttachments.AETHER_TIME)).isEternalDay()
         : false;
   }
}
