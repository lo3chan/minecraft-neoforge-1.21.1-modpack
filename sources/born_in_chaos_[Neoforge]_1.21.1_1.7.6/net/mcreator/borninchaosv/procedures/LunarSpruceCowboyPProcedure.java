package net.mcreator.borninchaosv.procedures;

import javax.annotation.Nullable;
import net.mcreator.borninchaosv.init.BornInChaosV1ModItems;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

@EventBusSubscriber
public class LunarSpruceCowboyPProcedure {
   @SubscribeEvent
   public static void onEntityDeath(LivingDeathEvent event) {
      if (event.getEntity() != null) {
         execute(event, event.getEntity().level(), event.getEntity(), event.getSource().getEntity());
      }
   }

   public static void execute(LevelAccessor world, Entity entity, Entity sourceentity) {
      execute(null, world, entity, sourceentity);
   }

   private static void execute(@Nullable Event event, LevelAccessor world, Entity entity, Entity sourceentity) {
      if (entity != null && sourceentity != null) {
         if (sourceentity instanceof Player
            && entity instanceof Monster
            && !(world instanceof Level _lvl2 && _lvl2.isDay())
            && (sourceentity instanceof LivingEntity _entGetArmor ? _entGetArmor.getItemBySlot(EquipmentSlot.HEAD) : ItemStack.EMPTY).getItem()
               == BornInChaosV1ModItems.DAMNED_DEMOMANS_HAT_HELMET.get()
            && sourceentity.isPassenger()
            && (
               world.getBlockState(BlockPos.containing(sourceentity.getX(), sourceentity.getY() - 1.0, sourceentity.getZ())).getBlock() == Blocks.SPRUCE_LOG
                  || world.getBlockState(BlockPos.containing(sourceentity.getX(), sourceentity.getY() - 1.0, sourceentity.getZ())).getBlock()
                     == Blocks.STRIPPED_SPRUCE_LOG
                  || world.getBlockState(BlockPos.containing(sourceentity.getX(), sourceentity.getY() - 1.0, sourceentity.getZ())).getBlock()
                     == Blocks.SPRUCE_WOOD
                  || world.getBlockState(BlockPos.containing(sourceentity.getX(), sourceentity.getY() - 1.0, sourceentity.getZ())).getBlock()
                     == Blocks.SPRUCE_LEAVES
                  || world.getBlockState(BlockPos.containing(sourceentity.getX(), sourceentity.getY() - 2.0, sourceentity.getZ())).getBlock()
                     == Blocks.SPRUCE_LOG
                  || world.getBlockState(BlockPos.containing(sourceentity.getX(), sourceentity.getY() - 2.0, sourceentity.getZ())).getBlock()
                     == Blocks.STRIPPED_SPRUCE_LOG
                  || world.getBlockState(BlockPos.containing(sourceentity.getX(), sourceentity.getY() - 2.0, sourceentity.getZ())).getBlock()
                     == Blocks.SPRUCE_WOOD
                  || world.getBlockState(BlockPos.containing(sourceentity.getX(), sourceentity.getY() - 2.0, sourceentity.getZ())).getBlock()
                     == Blocks.SPRUCE_LEAVES
            )
            && sourceentity instanceof ServerPlayer _player) {
            AdvancementHolder _adv = _player.server.getAdvancements().get(ResourceLocation.parse("born_in_chaos_v1:spruce_cowboyinthe_moonlight"));
            if (_adv != null) {
               AdvancementProgress _ap = _player.getAdvancements().getOrStartProgress(_adv);
               if (!_ap.isDone()) {
                  for (String criteria : _ap.getRemainingCriteria()) {
                     _player.getAdvancements().award(_adv, criteria);
                  }
               }
            }
         }
      }
   }
}
