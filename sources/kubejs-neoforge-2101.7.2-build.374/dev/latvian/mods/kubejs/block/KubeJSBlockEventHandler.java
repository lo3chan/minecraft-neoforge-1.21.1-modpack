package dev.latvian.mods.kubejs.block;

import dev.latvian.mods.kubejs.plugin.builtin.event.BlockEvents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.event.level.BlockEvent.BreakEvent;
import net.neoforged.neoforge.event.level.BlockEvent.EntityPlaceEvent;
import net.neoforged.neoforge.event.level.BlockEvent.FarmlandTrampleEvent;

@EventBusSubscriber(
   modid = "kubejs"
)
public class KubeJSBlockEventHandler {
   @SubscribeEvent
   public static void rightClick(RightClickBlock event) {
      BlockState state = event.getLevel().getBlockState(event.getPos());
      ResourceKey<Block> key = state.getBlock().kjs$getKey();
      Level var4 = event.getLevel();
      if (var4 instanceof Level
         && BlockEvents.RIGHT_CLICKED.hasListeners(key)
         && !event.getEntity().getCooldowns().isOnCooldown(event.getEntity().getItemInHand(event.getHand()).getItem())) {
         BlockEvents.RIGHT_CLICKED
            .post(var4, key, new BlockRightClickedKubeEvent(null, event.getEntity(), event.getHand(), event.getPos(), event.getFace(), event.getHitVec()))
            .applyCancel(event);
      }
   }

   @SubscribeEvent
   public static void leftClick(LeftClickBlock event) {
      BlockState state = event.getLevel().getBlockState(event.getPos());
      ResourceKey<Block> key = state.getBlock().kjs$getKey();
      Level var4 = event.getLevel();
      if (var4 instanceof Level && BlockEvents.LEFT_CLICKED.hasListeners(key)) {
         BlockEvents.LEFT_CLICKED.post(var4, key, new BlockLeftClickedKubeEvent(event)).applyCancel(event);
      }
   }

   @SubscribeEvent
   public static void blockBreak(BreakEvent event) {
      ResourceKey<Block> key = event.getState().getBlock().kjs$getKey();
      if (event.getLevel() instanceof Level level && BlockEvents.BROKEN.hasListeners(key)) {
         BlockEvents.BROKEN.post(level, key, new BlockBrokenKubeEvent(event)).applyCancel(event);
      }
   }

   @SubscribeEvent
   public static void drops(BlockDropsEvent event) {
      ResourceKey<Block> key = event.getState().getBlock().kjs$getKey();
      ServerLevel var3 = event.getLevel();
      if (var3 instanceof ServerLevel && BlockEvents.DROPS.hasListeners(key)) {
         BlockEvents.DROPS.post(var3, key, new BlockDropsKubeEvent(event)).applyCancel(event);
      }
   }

   @SubscribeEvent
   public static void blockPlace(EntityPlaceEvent event) {
      ResourceKey<Block> key = event.getPlacedBlock().getBlock().kjs$getKey();
      if (event.getLevel() instanceof Level level && BlockEvents.PLACED.hasListeners(key)) {
         BlockEvents.PLACED.post(level, key, new BlockPlacedKubeEvent(event)).applyCancel(event);
      }
   }

   @SubscribeEvent
   public static void farmlandTrample(FarmlandTrampleEvent event) {
      ResourceKey<Block> key = event.getState().getBlock().kjs$getKey();
      if (event.getLevel() instanceof Level level && BlockEvents.FARMLAND_TRAMPLED.hasListeners(key)) {
         BlockEvents.FARMLAND_TRAMPLED.post(level, key, new FarmlandTrampledKubeEvent(event)).applyCancel(event);
      }
   }
}
