package com.aetherteam.aether.event.listeners;

import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.event.hooks.DimensionHooks;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Player.BedSleepingProblem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator.Context;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.EntityTravelToDimensionEvent;
import net.neoforged.neoforge.event.entity.player.CanPlayerSleepEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.neoforged.neoforge.event.level.AlterGroundEvent;
import net.neoforged.neoforge.event.level.SleepFinishedTimeEvent;
import net.neoforged.neoforge.event.level.AlterGroundEvent.StateProvider;
import net.neoforged.neoforge.event.level.BlockEvent.NeighborNotifyEvent;
import net.neoforged.neoforge.event.level.LevelEvent.Load;
import net.neoforged.neoforge.event.tick.LevelTickEvent.Post;

public class DimensionListener {
   public static void listen(IEventBus bus) {
      bus.addListener(DimensionListener::onPlayerLogin);
      bus.addListener(DimensionListener::onInteractWithPortalFrame);
      bus.addListener(DimensionListener::onWaterExistsInsidePortalFrame);
      bus.addListener(DimensionListener::onWorldTick);
      bus.addListener(DimensionListener::onEntityTravelToDimension);
      bus.addListener(DimensionListener::onPlayerChangedDimension);
      bus.addListener(DimensionListener::onPlayerTraveling);
      bus.addListener(DimensionListener::onWorldLoad);
      bus.addListener(DimensionListener::onSleepFinish);
      bus.addListener(DimensionListener::onTriedToSleep);
      bus.addListener(DimensionListener::onAlterGround);
   }

   public static void onPlayerLogin(PlayerLoggedInEvent event) {
      Player player = event.getEntity();
      DimensionHooks.startInAether(player);
   }

   public static void onInteractWithPortalFrame(RightClickBlock event) {
      Player player = event.getEntity();
      Level level = event.getLevel();
      BlockPos blockPos = event.getPos();
      Direction direction = event.getFace();
      ItemStack itemStack = event.getItemStack();
      InteractionHand interactionHand = event.getHand();
      if (DimensionHooks.createPortal(player, level, blockPos, direction, itemStack, interactionHand)) {
         event.setCanceled(true);
      }
   }

   public static void onWaterExistsInsidePortalFrame(NeighborNotifyEvent event) {
      LevelAccessor level = event.getLevel();
      BlockPos blockPos = event.getPos();
      BlockState blockState = level.getBlockState(blockPos);
      FluidState fluidState = level.getFluidState(blockPos);
      if (DimensionHooks.detectWaterInFrame(level, blockPos, blockState, fluidState)) {
         event.setCanceled(true);
      }
   }

   public static void onWorldTick(Post event) {
      Level level = event.getLevel();
      if (!level.isClientSide()) {
         DimensionHooks.tickTime(level);
         DimensionHooks.checkEternalDayConfig(level);
      }
   }

   public static void onEntityTravelToDimension(EntityTravelToDimensionEvent event) {
      Entity entity = event.getEntity();
      ResourceKey<Level> dimension = event.getDimension();
      DimensionHooks.dimensionTravel(entity, dimension);
      DimensionHooks.removePlayerAerbunny(entity);
   }

   public static void onPlayerChangedDimension(PlayerChangedDimensionEvent event) {
      Player player = event.getEntity();
      DimensionHooks.remountPlayerAerbunny(player);
   }

   public static void onPlayerTraveling(net.neoforged.neoforge.event.tick.PlayerTickEvent.Post event) {
      Player player = event.getEntity();
      DimensionHooks.travelling(player);
   }

   public static void onWorldLoad(Load event) {
      LevelAccessor level = event.getLevel();
      DimensionHooks.initializeLevelData(level);
   }

   public static void onSleepFinish(SleepFinishedTimeEvent event) {
      LevelAccessor level = event.getLevel();
      Long time = DimensionHooks.finishSleep(level, event.getNewTime());
      if (time != null) {
         event.setTimeAddition(time);
      }
   }

   public static void onTriedToSleep(CanPlayerSleepEvent event) {
      Player player = event.getEntity();
      if (DimensionHooks.isEternalDay(player)) {
         event.setProblem(BedSleepingProblem.NOT_POSSIBLE_NOW);
      }
   }

   public static void onAlterGround(AlterGroundEvent event) {
      Context context = event.getContext();
      StateProvider provider = event.getStateProvider();
      event.setStateProvider((rand, pos) -> {
         AtomicReference<BlockState> oldState = new AtomicReference<>();
         BlockState attemptedState = provider.getState(rand, pos);
         if (context.level().isStateAtPosition(pos, state -> {
            if (state.is(AetherTags.Blocks.AETHER_DIRT)) {
               oldState.set(state);
               return true;
            } else {
               return false;
            }
         })) {
            return attemptedState.is(Blocks.PODZOL) ? oldState.get() : attemptedState;
         } else {
            return attemptedState;
         }
      });
   }
}
