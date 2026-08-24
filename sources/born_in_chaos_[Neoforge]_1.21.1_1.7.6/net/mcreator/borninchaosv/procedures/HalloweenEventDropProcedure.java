package net.mcreator.borninchaosv.procedures;

import java.util.Calendar;
import javax.annotation.Nullable;
import net.mcreator.borninchaosv.init.BornInChaosV1ModBlocks;
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
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

@EventBusSubscriber
public class HalloweenEventDropProcedure {
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
               Calendar.getInstance().get(2) == 9 && Calendar.getInstance().get(5) >= 25 && Calendar.getInstance().get(5) <= 31
                  || Calendar.getInstance().get(2) == 10 && Calendar.getInstance().get(5) >= 1 && Calendar.getInstance().get(5) <= 7
                  || world.getLevelData().getGameRules().getBoolean(BornInChaosV1ModGameRules.HALLOWEEN_EVENT)
            )
            && (entity instanceof Zombie || entity instanceof Skeleton)
            && (
               (entity instanceof LivingEntity _entGetArmor ? _entGetArmor.getItemBySlot(EquipmentSlot.HEAD) : ItemStack.EMPTY).getItem()
                     == ((Block)BornInChaosV1ModBlocks.ROTTEN_INFERNAL_PUMPKIN.get()).asItem()
                  || (entity instanceof LivingEntity _livEntx ? _livEntx.getMainHandItem() : ItemStack.EMPTY).getItem()
                     == BornInChaosV1ModItems.SWEET_SWORD.get()
                  || (entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == BornInChaosV1ModItems.SWEET_AXE.get()
            )
            && Math.random() < 0.5) {
            for (int index0 = 0; index0 < Mth.nextInt(RandomSource.create(), 1, 2); index0++) {
               if (world instanceof ServerLevel _level) {
                  ItemEntity entityToSpawn = new ItemEntity(
                     _level,
                     entity.getX(),
                     entity.getY() + 0.5,
                     entity.getZ(),
                     new ItemStack(
                        (ItemLike)BuiltInRegistries.ITEM
                           .getOrCreateTag(ItemTags.create(ResourceLocation.parse("born_in_chaos_v1:sweets")))
                           .getRandomElement(RandomSource.create())
                           .orElseGet(() -> BuiltInRegistries.ITEM.wrapAsHolder(Items.AIR))
                           .value()
                     )
                  );
                  entityToSpawn.setPickUpDelay(10);
                  _level.addFreshEntity(entityToSpawn);
               }
            }

            if (world instanceof ServerLevel _level) {
               _level.sendParticles(
                  (SimpleParticleType)BornInChaosV1ModParticleTypes.CANDY_ORANGE.get(),
                  entity.getX(),
                  entity.getY() + 1.0,
                  entity.getZ(),
                  3,
                  0.3,
                  0.3,
                  0.3,
                  0.2
               );
            }

            if (world instanceof ServerLevel _level) {
               _level.sendParticles(
                  (SimpleParticleType)BornInChaosV1ModParticleTypes.CANDYGREN.get(), entity.getX(), entity.getY() + 1.0, entity.getZ(), 3, 0.3, 0.3, 0.3, 0.2
               );
            }

            if (world instanceof ServerLevel _level) {
               _level.sendParticles(
                  (SimpleParticleType)BornInChaosV1ModParticleTypes.CANDYPURPLE.get(), entity.getX(), entity.getY() + 1.0, entity.getZ(), 3, 0.3, 0.3, 0.3, 0.2
               );
            }

            if (world instanceof ServerLevel _level) {
               _level.addFreshEntity(new ExperienceOrb(_level, x, y, z, 2));
            }
         }
      }
   }
}
