package dev.latvian.mods.kubejs.entity;

import dev.latvian.mods.kubejs.event.EventResult;
import dev.latvian.mods.kubejs.plugin.builtin.event.EntityEvents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Post;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Pre;

@EventBusSubscriber(
   modid = "kubejs"
)
public class KubeJSEntityEventHandler {
   @SubscribeEvent
   public static void checkSpawn(FinalizeSpawnEvent event) {
      ResourceKey<EntityType<?>> key = event.getEntity().getType().kjs$getKey();
      if (event.getLevel() instanceof ServerLevel level && EntityEvents.CHECK_SPAWN.hasListeners(key)) {
         EventResult result = EntityEvents.CHECK_SPAWN
            .post(
               level,
               key,
               new CheckLivingEntitySpawnKubeEvent(event.getEntity(), level, event.getX(), event.getY(), event.getZ(), event.getSpawnType(), event.getSpawner())
            );
         if (result.interruptFalse() || result.interruptTrue()) {
            event.setSpawnCancelled(result.interruptFalse());
            event.setCanceled(true);
         }
      }
   }

   @SubscribeEvent
   public static void livingDeath(LivingDeathEvent event) {
      ResourceKey<EntityType<?>> key = event.getEntity().getType().kjs$getKey();
      if (EntityEvents.DEATH.hasListeners(key)) {
         EntityEvents.DEATH.post(event.getEntity(), key, new LivingEntityDeathKubeEvent(event.getEntity(), event.getSource())).applyCancel(event);
      }
   }

   @SubscribeEvent
   public static void beforeLivingHurt(Pre event) {
      ResourceKey<EntityType<?>> key = event.getEntity().getType().kjs$getKey();
      if (EntityEvents.BEFORE_HURT.hasListeners(key)
         && EntityEvents.BEFORE_HURT.post(event.getEntity(), key, new BeforeLivingEntityHurtKubeEvent(event)).interruptFalse()) {
         event.getContainer().setNewDamage(0.0F);
      }
   }

   @SubscribeEvent
   public static void afterLivingHurt(Post event) {
      ResourceKey<EntityType<?>> key = event.getEntity().getType().kjs$getKey();
      if (EntityEvents.AFTER_HURT.hasListeners(key)) {
         EntityEvents.AFTER_HURT.post(event.getEntity(), key, new AfterLivingEntityHurtKubeEvent(event));
      }
   }

   @SubscribeEvent
   public static void entitySpawned(EntityJoinLevelEvent event) {
      ResourceKey<EntityType<?>> key = event.getEntity().getType().kjs$getKey();
      if (EntityEvents.SPAWNED.hasListeners(key) && event.getLevel() instanceof ServerLevel level) {
         EntityEvents.SPAWNED.post(level, key, new EntitySpawnedKubeEvent(event.getEntity(), level)).applyCancel(event);
      }
   }

   @SubscribeEvent
   public static void livingDrops(LivingDropsEvent event) {
      ResourceKey<EntityType<?>> key = event.getEntity().getType().kjs$getKey();
      if (EntityEvents.ENTITY_DROPS.hasListeners(key)) {
         LivingEntityDropsKubeEvent e = new LivingEntityDropsKubeEvent(event);
         if (!EntityEvents.ENTITY_DROPS.post(event.getEntity(), key, e).applyCancel(event) && e.eventDrops != null) {
            event.getDrops().clear();
            event.getDrops().addAll(e.eventDrops);
         }
      }
   }
}
