package com.aetherteam.aether.event.listeners.abilities;

import com.aetherteam.aether.event.hooks.AbilityHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.neoforged.neoforge.event.level.BlockEvent.BlockToolModificationEvent;
import net.neoforged.neoforge.event.level.BlockEvent.BreakEvent;

public class ToolAbilityListener {
   public static void listen(IEventBus bus) {
      bus.addListener(ToolAbilityListener::setupToolModifications);
      bus.addListener(ToolAbilityListener::doHolystoneAbility);
      bus.addListener(ToolAbilityListener::modifyBreakSpeed);
      bus.addListener(ToolAbilityListener::setupDebuffToolState);
      bus.addListener(ToolAbilityListener::doGoldenOakStripping);
   }

   public static void setupToolModifications(BlockToolModificationEvent event) {
      LevelAccessor levelAccessor = event.getLevel();
      BlockPos pos = event.getPos();
      BlockState oldState = event.getState();
      ItemAbility ItemAbility = event.getItemAbility();
      BlockState newState = AbilityHooks.ToolHooks.setupItemAbilities(levelAccessor, pos, oldState, ItemAbility);
      if (newState != oldState && !event.isSimulated() && !event.isCanceled()) {
         event.setFinalState(newState);
      }
   }

   public static void doHolystoneAbility(BreakEvent event) {
      Player player = event.getPlayer();
      Level level = player.level();
      BlockPos blockPos = event.getPos();
      ItemStack itemStack = player.getMainHandItem();
      BlockState blockState = event.getState();
      if (!event.isCanceled()) {
         AbilityHooks.ToolHooks.handleHolystoneToolAbility(player, level, blockPos, itemStack, blockState);
      }
   }

   public static void modifyBreakSpeed(BreakSpeed event) {
      BlockState blockState = event.getState();
      Player player = event.getEntity();
      ItemStack itemStack = player.getMainHandItem();
      if (!event.isCanceled()) {
         event.setNewSpeed(AbilityHooks.ToolHooks.handleZaniteToolAbility(itemStack, event.getNewSpeed()));
         event.setNewSpeed(AbilityHooks.ToolHooks.reduceToolEffectiveness(player, blockState, itemStack, event.getNewSpeed()));
      }
   }

   public static void setupDebuffToolState(PlayerLoggedInEvent event) {
      AbilityHooks.ToolHooks.setDebuffToolsState((ServerPlayer)event.getEntity());
   }

   public static void doGoldenOakStripping(BlockToolModificationEvent event) {
      LevelAccessor levelAccessor = event.getLevel();
      BlockState oldState = event.getState();
      ItemStack itemStack = event.getHeldItemStack();
      ItemAbility ItemAbility = event.getItemAbility();
      UseOnContext context = event.getContext();
      if (!event.isSimulated() && !event.isCanceled()) {
         AbilityHooks.ToolHooks.stripGoldenOak(levelAccessor, oldState, itemStack, ItemAbility, context);
      }
   }
}
