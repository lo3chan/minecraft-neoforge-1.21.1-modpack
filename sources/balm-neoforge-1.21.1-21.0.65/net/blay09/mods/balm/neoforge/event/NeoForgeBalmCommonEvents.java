package net.blay09.mods.balm.neoforge.event;

import net.blay09.mods.balm.api.event.BreakBlockEvent;
import net.blay09.mods.balm.api.event.ChunkLoadingEvent;
import net.blay09.mods.balm.api.event.ChunkTrackingEvent;
import net.blay09.mods.balm.api.event.CommandEvent;
import net.blay09.mods.balm.api.event.CropGrowEvent;
import net.blay09.mods.balm.api.event.DigSpeedEvent;
import net.blay09.mods.balm.api.event.EntityAddedEvent;
import net.blay09.mods.balm.api.event.ItemCraftedEvent;
import net.blay09.mods.balm.api.event.LevelLoadingEvent;
import net.blay09.mods.balm.api.event.LivingDamageEvent;
import net.blay09.mods.balm.api.event.LivingDeathEvent;
import net.blay09.mods.balm.api.event.LivingFallEvent;
import net.blay09.mods.balm.api.event.LivingHealEvent;
import net.blay09.mods.balm.api.event.PlayerAttackEvent;
import net.blay09.mods.balm.api.event.PlayerChangedDimensionEvent;
import net.blay09.mods.balm.api.event.PlayerLoginEvent;
import net.blay09.mods.balm.api.event.PlayerLogoutEvent;
import net.blay09.mods.balm.api.event.PlayerOpenMenuEvent;
import net.blay09.mods.balm.api.event.PlayerRespawnEvent;
import net.blay09.mods.balm.api.event.TickPhase;
import net.blay09.mods.balm.api.event.TickType;
import net.blay09.mods.balm.api.event.TossItemEvent;
import net.blay09.mods.balm.api.event.UseBlockEvent;
import net.blay09.mods.balm.api.event.UseItemEvent;
import net.blay09.mods.balm.api.event.server.ServerStartedEvent;
import net.blay09.mods.balm.api.event.server.ServerStartingEvent;
import net.blay09.mods.balm.api.event.server.ServerStoppedEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.neoforged.neoforge.event.level.block.CropGrowEvent.Pre.Result;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public class NeoForgeBalmCommonEvents {
   public static void registerEvents(NeoForgeBalmEvents events) {
      events.registerTickEvent(
         TickType.Server, TickPhase.Start, handler -> NeoForge.EVENT_BUS.addListener(orig -> handler.handle(ServerLifecycleHooks.getCurrentServer()))
      );
      events.registerTickEvent(
         TickType.Server, TickPhase.End, handler -> NeoForge.EVENT_BUS.addListener(orig -> handler.handle(ServerLifecycleHooks.getCurrentServer()))
      );
      events.registerTickEvent(TickType.ServerLevel, TickPhase.Start, handler -> NeoForge.EVENT_BUS.addListener(orig -> {
         if (orig.getLevel() instanceof ServerLevel serverLevel) {
            handler.handle(serverLevel);
         }
      }));
      events.registerTickEvent(TickType.ServerLevel, TickPhase.End, handler -> NeoForge.EVENT_BUS.addListener(orig -> {
         if (orig.getLevel() instanceof ServerLevel serverLevel) {
            handler.handle(serverLevel);
         }
      }));
      events.registerTickEvent(TickType.ServerPlayer, TickPhase.Start, handler -> NeoForge.EVENT_BUS.addListener(orig -> {
         if (orig.getEntity() instanceof ServerPlayer serverPlayer) {
            handler.handle(serverPlayer);
         }
      }));
      events.registerTickEvent(TickType.ServerPlayer, TickPhase.End, handler -> NeoForge.EVENT_BUS.addListener(orig -> {
         if (orig.getEntity() instanceof ServerPlayer serverPlayer) {
            handler.handle(serverPlayer);
         }
      }));
      events.registerTickEvent(TickType.Entity, TickPhase.Start, handler -> NeoForge.EVENT_BUS.addListener(orig -> handler.handle(orig.getEntity())));
      events.registerTickEvent(TickType.Entity, TickPhase.End, handler -> NeoForge.EVENT_BUS.addListener(orig -> handler.handle(orig.getEntity())));
      events.registerEvent(ServerStartingEvent.class, priority -> NeoForge.EVENT_BUS.addListener(NeoForgeBalmEvents.toForge(priority), orig -> {
         ServerStartingEvent event = new ServerStartingEvent(orig.getServer());
         events.fireEventHandlers(priority, event);
      }));
      events.registerEvent(ServerStartedEvent.class, priority -> NeoForge.EVENT_BUS.addListener(NeoForgeBalmEvents.toForge(priority), orig -> {
         ServerStartedEvent event = new ServerStartedEvent(orig.getServer());
         events.fireEventHandlers(priority, event);
      }));
      events.registerEvent(ServerStoppedEvent.class, priority -> NeoForge.EVENT_BUS.addListener(NeoForgeBalmEvents.toForge(priority), orig -> {
         ServerStoppedEvent event = new ServerStoppedEvent(orig.getServer());
         events.fireEventHandlers(priority, event);
      }));
      events.registerEvent(UseBlockEvent.class, priority -> NeoForge.EVENT_BUS.addListener(NeoForgeBalmEvents.toForge(priority), orig -> {
         UseBlockEvent event = new UseBlockEvent(orig.getEntity(), orig.getLevel(), orig.getHand(), orig.getHitVec());
         events.fireEventHandlers(priority, event);
         if (event.isCanceled()) {
            orig.setCancellationResult(event.getInteractionResult());
            orig.setCanceled(true);
         }
      }));
      events.registerEvent(UseItemEvent.class, priority -> NeoForge.EVENT_BUS.addListener(NeoForgeBalmEvents.toForge(priority), orig -> {
         UseItemEvent event = new UseItemEvent(orig.getEntity(), orig.getLevel(), orig.getHand());
         events.fireEventHandlers(priority, event);
         if (event.isCanceled()) {
            orig.setCancellationResult(event.getInteractionResult());
            orig.setCanceled(true);
         }
      }));
      events.registerEvent(PlayerLoginEvent.class, priority -> NeoForge.EVENT_BUS.addListener(NeoForgeBalmEvents.toForge(priority), orig -> {
         PlayerLoginEvent event = new PlayerLoginEvent((ServerPlayer)orig.getEntity());
         events.fireEventHandlers(priority, event);
      }));
      events.registerEvent(PlayerLogoutEvent.class, priority -> NeoForge.EVENT_BUS.addListener(NeoForgeBalmEvents.toForge(priority), orig -> {
         PlayerLogoutEvent event = new PlayerLogoutEvent((ServerPlayer)orig.getEntity());
         events.fireEventHandlers(priority, event);
      }));
      events.registerEvent(BreakBlockEvent.class, priority -> NeoForge.EVENT_BUS.addListener(NeoForgeBalmEvents.toForge(priority), orig -> {
         BlockEntity blockEntity = orig.getLevel().getBlockEntity(orig.getPos());
         BreakBlockEvent event = new BreakBlockEvent((Level)orig.getLevel(), orig.getPlayer(), orig.getPos(), orig.getState(), blockEntity);
         events.fireEventHandlers(priority, event);
         if (event.isCanceled()) {
            orig.setCanceled(true);
         }
      }));
      events.registerEvent(PlayerRespawnEvent.class, priority -> NeoForge.EVENT_BUS.addListener(NeoForgeBalmEvents.toForge(priority), orig -> {
         PlayerRespawnEvent event = new PlayerRespawnEvent((ServerPlayer)orig.getEntity(), (ServerPlayer)orig.getEntity());
         events.fireEventHandlers(priority, event);
      }));
      events.registerEvent(LivingFallEvent.class, priority -> NeoForge.EVENT_BUS.addListener(NeoForgeBalmEvents.toForge(priority), orig -> {
         LivingFallEvent event = new LivingFallEvent(orig.getEntity());
         events.fireEventHandlers(priority, event);
         if (event.getFallDamageOverride() != null) {
            orig.setDamageMultiplier(0.0F);
            event.getEntity().hurt(event.getEntity().level().damageSources().fall(), event.getFallDamageOverride());
         }

         if (event.isCanceled()) {
            orig.setCanceled(true);
         }
      }));
      events.registerEvent(LivingDamageEvent.class, priority -> NeoForge.EVENT_BUS.addListener(NeoForgeBalmEvents.toForge(priority), orig -> {
         DamageContainer damageContainer = orig.getContainer();
         LivingDamageEvent event = new LivingDamageEvent(orig.getEntity(), damageContainer.getSource(), damageContainer.getNewDamage());
         events.fireEventHandlers(priority, event);
         damageContainer.setNewDamage(event.getDamageAmount());
         if (event.isCanceled()) {
            orig.getContainer().setNewDamage(0.0F);
         }
      }));
      events.registerEvent(CropGrowEvent.Pre.class, priority -> NeoForge.EVENT_BUS.addListener(NeoForgeBalmEvents.toForge(priority), orig -> {
         if (orig.getLevel() instanceof Level level) {
            CropGrowEvent.Pre event = new CropGrowEvent.Pre(level, orig.getPos(), orig.getState());
            events.fireEventHandlers(priority, event);
            if (event.isCanceled()) {
               orig.setResult(Result.DO_NOT_GROW);
            }
         }
      }));
      events.registerEvent(CropGrowEvent.Post.class, priority -> NeoForge.EVENT_BUS.addListener(NeoForgeBalmEvents.toForge(priority), orig -> {
         if (orig.getLevel() instanceof Level level) {
            CropGrowEvent.Post event = new CropGrowEvent.Post(level, orig.getPos(), orig.getState());
            events.fireEventHandlers(priority, event);
         }
      }));
      events.registerEvent(ChunkTrackingEvent.Start.class, priority -> NeoForge.EVENT_BUS.addListener(NeoForgeBalmEvents.toForge(priority), orig -> {
         ChunkTrackingEvent.Start event = new ChunkTrackingEvent.Start(orig.getLevel(), orig.getPlayer(), orig.getPos());
         events.fireEventHandlers(priority, event);
      }));
      events.registerEvent(ChunkTrackingEvent.Stop.class, priority -> NeoForge.EVENT_BUS.addListener(NeoForgeBalmEvents.toForge(priority), orig -> {
         ChunkTrackingEvent.Stop event = new ChunkTrackingEvent.Stop(orig.getLevel(), orig.getPlayer(), orig.getPos());
         events.fireEventHandlers(priority, event);
      }));
      events.registerEvent(TossItemEvent.class, priority -> NeoForge.EVENT_BUS.addListener(NeoForgeBalmEvents.toForge(priority), orig -> {
         TossItemEvent event = new TossItemEvent(orig.getPlayer(), orig.getEntity().getItem());
         events.fireEventHandlers(priority, event);
         if (event.isCanceled()) {
            orig.setCanceled(true);
         }
      }));
      events.registerEvent(PlayerAttackEvent.class, priority -> NeoForge.EVENT_BUS.addListener(NeoForgeBalmEvents.toForge(priority), orig -> {
         PlayerAttackEvent event = new PlayerAttackEvent(orig.getEntity(), orig.getTarget());
         events.fireEventHandlers(priority, event);
         if (event.isCanceled()) {
            orig.setCanceled(true);
         }
      }));
      events.registerEvent(LivingHealEvent.class, priority -> NeoForge.EVENT_BUS.addListener(NeoForgeBalmEvents.toForge(priority), orig -> {
         LivingHealEvent event = new LivingHealEvent(orig.getEntity(), orig.getAmount());
         events.fireEventHandlers(priority, event);
         if (event.isCanceled()) {
            orig.setCanceled(true);
         }
      }));
      events.registerEvent(DigSpeedEvent.class, priority -> NeoForge.EVENT_BUS.addListener(NeoForgeBalmEvents.toForge(priority), orig -> {
         DigSpeedEvent event = new DigSpeedEvent(orig.getEntity(), orig.getState(), orig.getOriginalSpeed());
         events.fireEventHandlers(priority, event);
         if (event.getSpeedOverride() != null) {
            orig.setNewSpeed(event.getSpeedOverride());
         }

         if (event.isCanceled()) {
            orig.setCanceled(true);
         }
      }));
      events.registerEvent(PlayerChangedDimensionEvent.class, priority -> NeoForge.EVENT_BUS.addListener(NeoForgeBalmEvents.toForge(priority), orig -> {
         PlayerChangedDimensionEvent event = new PlayerChangedDimensionEvent((ServerPlayer)orig.getEntity(), orig.getFrom(), orig.getTo());
         events.fireEventHandlers(priority, event);
      }));
      events.registerEvent(ItemCraftedEvent.class, priority -> NeoForge.EVENT_BUS.addListener(NeoForgeBalmEvents.toForge(priority), orig -> {
         ItemCraftedEvent event = new ItemCraftedEvent(orig.getEntity(), orig.getCrafting(), orig.getInventory());
         events.fireEventHandlers(priority, event);
      }));
      events.registerEvent(CommandEvent.class, priority -> NeoForge.EVENT_BUS.addListener(NeoForgeBalmEvents.toForge(priority), orig -> {
         CommandEvent event = new CommandEvent(orig.getParseResults());
         events.fireEventHandlers(priority, event);
         if (event.isCanceled()) {
            orig.setCanceled(true);
         }
      }));
      events.registerEvent(LivingDeathEvent.class, priority -> NeoForge.EVENT_BUS.addListener(NeoForgeBalmEvents.toForge(priority), orig -> {
         LivingDeathEvent event = new LivingDeathEvent(orig.getEntity(), orig.getSource());
         events.fireEventHandlers(priority, event);
         if (event.isCanceled()) {
            orig.setCanceled(true);
         }
      }));
      events.registerEvent(EntityAddedEvent.class, priority -> NeoForge.EVENT_BUS.addListener(NeoForgeBalmEvents.toForge(priority), orig -> {
         EntityAddedEvent event = new EntityAddedEvent(orig.getEntity(), orig.getLevel());
         events.fireEventHandlers(priority, event);
         if (event.isCanceled()) {
            orig.setCanceled(true);
         }
      }));
      events.registerEvent(PlayerOpenMenuEvent.class, priority -> NeoForge.EVENT_BUS.addListener(NeoForgeBalmEvents.toForge(priority), orig -> {
         if (orig.getEntity() instanceof ServerPlayer serverPlayer) {
            PlayerOpenMenuEvent event = new PlayerOpenMenuEvent(serverPlayer, orig.getContainer());
            events.fireEventHandlers(priority, event);
         }
      }));
      events.registerEvent(ChunkLoadingEvent.Load.class, priority -> NeoForge.EVENT_BUS.addListener(NeoForgeBalmEvents.toForge(priority), orig -> {
         ChunkLoadingEvent.Load event = new ChunkLoadingEvent.Load(orig.getLevel(), orig.getChunk());
         events.fireEventHandlers(priority, event);
      }));
      events.registerEvent(ChunkLoadingEvent.Unload.class, priority -> NeoForge.EVENT_BUS.addListener(NeoForgeBalmEvents.toForge(priority), orig -> {
         ChunkLoadingEvent.Unload event = new ChunkLoadingEvent.Unload(orig.getLevel(), orig.getChunk());
         events.fireEventHandlers(priority, event);
      }));
      events.registerEvent(LevelLoadingEvent.Load.class, priority -> NeoForge.EVENT_BUS.addListener(NeoForgeBalmEvents.toForge(priority), orig -> {
         LevelLoadingEvent.Load event = new LevelLoadingEvent.Load(orig.getLevel());
         events.fireEventHandlers(priority, event);
      }));
      events.registerEvent(LevelLoadingEvent.Unload.class, priority -> NeoForge.EVENT_BUS.addListener(NeoForgeBalmEvents.toForge(priority), orig -> {
         LevelLoadingEvent.Unload event = new LevelLoadingEvent.Unload(orig.getLevel());
         events.fireEventHandlers(priority, event);
      }));
   }
}
