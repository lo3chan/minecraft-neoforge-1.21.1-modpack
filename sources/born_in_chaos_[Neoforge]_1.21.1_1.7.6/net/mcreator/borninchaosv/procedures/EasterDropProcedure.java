package net.mcreator.borninchaosv.procedures;

import javax.annotation.Nullable;
import net.mcreator.borninchaosv.init.BornInChaosV1ModItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

@EventBusSubscriber
public class EasterDropProcedure {
   @SubscribeEvent
   public static void onEntityDeath(LivingDeathEvent event) {
      if (event.getEntity() != null) {
         execute(event, event.getEntity().level(), event.getEntity());
      }
   }

   public static void execute(LevelAccessor world, Entity entity) {
      execute(null, world, entity);
   }

   private static void execute(@Nullable Event event, LevelAccessor world, Entity entity) {
      if (entity != null) {
         double easterbonus = 0.0;
         if (entity instanceof Rabbit && Math.random() < 0.02) {
            easterbonus = Mth.nextInt(RandomSource.create(), 1, 3);
            if (easterbonus == 1.0) {
               if (world instanceof ServerLevel _level) {
                  ItemEntity entityToSpawn = new ItemEntity(
                     _level, entity.getX(), entity.getY() + 1.0, entity.getZ(), new ItemStack((ItemLike)BornInChaosV1ModItems.ROTTEN_EASTER_EGG.get())
                  );
                  entityToSpawn.setPickUpDelay(10);
                  _level.addFreshEntity(entityToSpawn);
               }
            } else if (easterbonus == 2.0) {
               if (world instanceof ServerLevel _level) {
                  ItemEntity entityToSpawn = new ItemEntity(
                     _level, entity.getX(), entity.getY() + 1.0, entity.getZ(), new ItemStack((ItemLike)BornInChaosV1ModItems.SPIRITUAL_EASTER_EGG.get())
                  );
                  entityToSpawn.setPickUpDelay(10);
                  _level.addFreshEntity(entityToSpawn);
               }
            } else if (easterbonus == 3.0 && world instanceof ServerLevel _level) {
               ItemEntity entityToSpawn = new ItemEntity(
                  _level, entity.getX(), entity.getY() + 1.0, entity.getZ(), new ItemStack((ItemLike)BornInChaosV1ModItems.MONSTROUS_EASTER_EGG.get())
               );
               entityToSpawn.setPickUpDelay(10);
               _level.addFreshEntity(entityToSpawn);
            }
         }
      }
   }
}
