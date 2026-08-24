package net.mcreator.borninchaosv.procedures;

import java.util.Calendar;
import javax.annotation.Nullable;
import net.mcreator.borninchaosv.init.BornInChaosV1ModBlocks;
import net.mcreator.borninchaosv.init.BornInChaosV1ModGameRules;
import net.mcreator.borninchaosv.init.BornInChaosV1ModItems;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

@EventBusSubscriber
public class HalloweenEventSpawnProcedure {
   @SubscribeEvent
   public static void onEntitySpawned(EntityJoinLevelEvent event) {
      execute(event, event.getLevel(), event.getEntity());
   }

   public static void execute(LevelAccessor world, Entity entity) {
      execute(null, world, entity);
   }

   private static void execute(@Nullable Event event, LevelAccessor world, Entity entity) {
      if (entity != null) {
         if ((entity instanceof Zombie || entity instanceof Skeleton)
            && world.getLevelData().getGameRules().getBoolean(BornInChaosV1ModGameRules.SEASONAL_EVENTS)
            && (
               Calendar.getInstance().get(2) == 9 && Calendar.getInstance().get(5) >= 25 && Calendar.getInstance().get(5) <= 31
                  || Calendar.getInstance().get(2) == 10 && Calendar.getInstance().get(5) >= 1 && Calendar.getInstance().get(5) <= 7
                  || world.getLevelData().getGameRules().getBoolean(BornInChaosV1ModGameRules.HALLOWEEN_EVENT)
            )) {
            if (entity instanceof Zombie && Math.random() < 0.35 && !world.isClientSide()) {
               if (entity instanceof Player _player) {
                  _player.getInventory().armor.set(3, new ItemStack((ItemLike)BornInChaosV1ModBlocks.ROTTEN_INFERNAL_PUMPKIN.get()));
                  _player.getInventory().setChanged();
               } else if (entity instanceof LivingEntity _living) {
                  _living.setItemSlot(EquipmentSlot.HEAD, new ItemStack((ItemLike)BornInChaosV1ModBlocks.ROTTEN_INFERNAL_PUMPKIN.get()));
               }

               if (Math.random() < 0.2) {
                  if (entity instanceof LivingEntity _entity) {
                     ItemStack _setstack = new ItemStack((ItemLike)BornInChaosV1ModItems.SWEET_AXE.get()).copy();
                     _setstack.setCount(1);
                     _entity.setItemInHand(InteractionHand.MAIN_HAND, _setstack);
                     if (_entity instanceof Player _player) {
                        _player.getInventory().setChanged();
                     }
                  }
               } else if (entity instanceof LivingEntity _entityx) {
                  ItemStack _setstack = new ItemStack((ItemLike)BornInChaosV1ModItems.SWEET_SWORD.get()).copy();
                  _setstack.setCount(1);
                  _entityx.setItemInHand(InteractionHand.MAIN_HAND, _setstack);
                  if (_entityx instanceof Player _player) {
                     _player.getInventory().setChanged();
                  }
               }
            }

            if (entity instanceof Skeleton && Math.random() < 0.25 && !world.isClientSide()) {
               if (Math.random() < 0.2) {
                  if (entity instanceof LivingEntity _entityxx) {
                     ItemStack _setstack = new ItemStack((ItemLike)BornInChaosV1ModItems.SWEET_AXE.get()).copy();
                     _setstack.setCount(1);
                     _entityxx.setItemInHand(InteractionHand.MAIN_HAND, _setstack);
                     if (_entityxx instanceof Player _player) {
                        _player.getInventory().setChanged();
                     }
                  }
               } else if (entity instanceof LivingEntity _entityxxx) {
                  ItemStack _setstack = new ItemStack((ItemLike)BornInChaosV1ModItems.SWEET_SWORD.get()).copy();
                  _setstack.setCount(1);
                  _entityxxx.setItemInHand(InteractionHand.MAIN_HAND, _setstack);
                  if (_entityxxx instanceof Player _player) {
                     _player.getInventory().setChanged();
                  }
               }
            }
         }
      }
   }
}
