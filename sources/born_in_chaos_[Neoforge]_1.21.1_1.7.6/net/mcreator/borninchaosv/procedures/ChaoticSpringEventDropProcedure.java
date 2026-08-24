package net.mcreator.borninchaosv.procedures;

import java.util.Calendar;
import javax.annotation.Nullable;
import net.mcreator.borninchaosv.init.BornInChaosV1ModGameRules;
import net.mcreator.borninchaosv.init.BornInChaosV1ModItems;
import net.mcreator.borninchaosv.init.BornInChaosV1ModParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

@EventBusSubscriber
public class ChaoticSpringEventDropProcedure {
   @SubscribeEvent
   public static void onEntityDeath(LivingDeathEvent event) {
      if (event.getEntity() != null) {
         execute(event, event.getEntity().level(), event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(), event.getEntity());
      }
   }

   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      execute(null, world, x, y, z, entity);
   }

   private static void execute(@Nullable Event event, LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if ((entity instanceof Zombie || entity instanceof Skeleton)
            && world.getLevelData().getGameRules().getBoolean(BornInChaosV1ModGameRules.SEASONAL_EVENTS)
            && (
               Calendar.getInstance().get(2) == 3 && Calendar.getInstance().get(5) >= 10 && Calendar.getInstance().get(5) <= 22
                  || world.getLevelData().getGameRules().getBoolean(BornInChaosV1ModGameRules.CHAOTIC_SPRING_EVENT)
            )
            && (entity instanceof Zombie || entity instanceof Skeleton)
            && (
               (entity instanceof LivingEntity _entGetArmor ? _entGetArmor.getItemBySlot(EquipmentSlot.HEAD) : ItemStack.EMPTY).getItem()
                     == BornInChaosV1ModItems.KILLER_RABBIT_EARS_HELMET.get()
                  || (entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem()
                     == BornInChaosV1ModItems.CARROT_SWORD.get()
            )) {
            if (Math.random() < 0.5) {
               if (world instanceof ServerLevel _level) {
                  ItemEntity entityToSpawn = new ItemEntity(
                     _level,
                     entity.getX(),
                     entity.getY() + 0.5,
                     entity.getZ(),
                     new ItemStack(
                        (ItemLike)BuiltInRegistries.ITEM
                           .getOrCreateTag(ItemTags.create(ResourceLocation.parse("born_in_chaos_v1:easter_eggs")))
                           .getRandomElement(RandomSource.create())
                           .orElseGet(() -> BuiltInRegistries.ITEM.wrapAsHolder(Items.AIR))
                           .value()
                     )
                  );
                  entityToSpawn.setPickUpDelay(10);
                  _level.addFreshEntity(entityToSpawn);
               }

               if (world instanceof ServerLevel _level) {
                  _level.sendParticles(
                     (SimpleParticleType)BornInChaosV1ModParticleTypes.LITTLE_CARROT.get(),
                     entity.getX(),
                     entity.getY() + 1.0,
                     entity.getZ(),
                     Mth.nextInt(RandomSource.create(), 4, 6),
                     0.3,
                     0.3,
                     0.3,
                     0.2
                  );
               }

               if (world instanceof ServerLevel _level) {
                  _level.addFreshEntity(new ExperienceOrb(_level, x, y, z, 2));
               }
            } else if (Math.random() < 0.1) {
               if (world instanceof ServerLevel _level) {
                  ItemEntity entityToSpawn = new ItemEntity(
                     _level, entity.getX(), entity.getY() + 0.5, entity.getZ(), new ItemStack((ItemLike)BornInChaosV1ModItems.TRANSFORMING_EASTER_CAKE.get())
                  );
                  entityToSpawn.setPickUpDelay(10);
                  _level.addFreshEntity(entityToSpawn);
               }

               if (world instanceof ServerLevel _level) {
                  _level.sendParticles(
                     (SimpleParticleType)BornInChaosV1ModParticleTypes.LITTLE_CARROT.get(),
                     entity.getX(),
                     entity.getY() + 1.0,
                     entity.getZ(),
                     Mth.nextInt(RandomSource.create(), 4, 6),
                     0.3,
                     0.3,
                     0.3,
                     0.2
                  );
               }

               if (world instanceof ServerLevel _level) {
                  _level.addFreshEntity(new ExperienceOrb(_level, x, y, z, 2));
               }
            }
         }
      }
   }
}
