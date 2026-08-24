package dev.latvian.mods.kubejs.level;

import dev.latvian.mods.kubejs.plugin.builtin.event.LevelEvents;
import dev.latvian.mods.kubejs.script.ScriptType;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.ExplosionEvent.Detonate;
import net.neoforged.neoforge.event.level.ExplosionEvent.Start;
import net.neoforged.neoforge.event.level.LevelEvent.Load;
import net.neoforged.neoforge.event.level.LevelEvent.Unload;
import net.neoforged.neoforge.event.tick.LevelTickEvent.Post;

@EventBusSubscriber(
   modid = "kubejs"
)
public class KubeJSWorldEventHandler {
   @SubscribeEvent
   public static void serverLevelLoad(Load event) {
      if (event.getLevel() instanceof ServerLevel level && LevelEvents.LOADED.hasListeners(level.dimension())) {
         LevelEvents.LOADED.post(new SimpleLevelKubeEvent(level), level.dimension());
      }
   }

   @SubscribeEvent
   public static void serverLevelUnload(Unload event) {
      if (event.getLevel() instanceof ServerLevel level && LevelEvents.UNLOADED.hasListeners(level.dimension())) {
         LevelEvents.UNLOADED.post(new SimpleLevelKubeEvent(level), level.dimension());
      }
   }

   @SubscribeEvent
   public static void serverTickEvent(Post event) {
      if (event.getLevel() instanceof ServerLevel level && LevelEvents.TICK.hasListeners(level.dimension())) {
         LevelEvents.TICK.post(ScriptType.SERVER, level.dimension(), new SimpleLevelKubeEvent(level));
      }
   }

   @SubscribeEvent
   public static void preExplosion(Start event) {
      if (event.getLevel() instanceof ServerLevel level && LevelEvents.BEFORE_EXPLOSION.hasListeners(level.dimension())) {
         LevelEvents.BEFORE_EXPLOSION.post(level, level.dimension(), new ExplosionKubeEvent.Before(level, event.getExplosion())).applyCancel(event);
      }
   }

   @SubscribeEvent
   public static void detonateExplosion(Detonate event) {
      if (event.getLevel() instanceof ServerLevel level && LevelEvents.AFTER_EXPLOSION.hasListeners(level.dimension())) {
         LevelEvents.AFTER_EXPLOSION.post(level, level.dimension(), new ExplosionKubeEvent.After(level, event.getExplosion(), event.getAffectedEntities()));
      }
   }
}
