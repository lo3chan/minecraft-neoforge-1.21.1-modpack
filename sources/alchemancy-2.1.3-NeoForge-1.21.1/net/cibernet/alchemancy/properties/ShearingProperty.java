package net.cibernet.alchemancy.properties;

import java.util.List;
import java.util.Set;
import net.cibernet.alchemancy.util.InfusionPropertyDispenseBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.common.IShearable;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.EntityInteractSpecific;

public class ShearingProperty extends ToolProperty {
   public ShearingProperty(int color, TagKey<Block> allowedBlocks, Set<ItemAbility> abilities) {
      super(color, allowedBlocks, abilities);
   }

   public ShearingProperty(int color, List<ToolProperty.RuleFunc> toolRules, Set<ItemAbility> abilities) {
      super(color, toolRules, abilities);
   }

   @Override
   public InfusionPropertyDispenseBehavior.DispenseResult onItemDispense(
      BlockSource blockSource, Direction direction, ItemStack stack, InfusionPropertyDispenseBehavior.DispenseResult currentResult
   ) {
      return InfusionPropertyDispenseBehavior.executeItemBehavior(blockSource, stack, Items.SHEARS);
   }

   @Override
   public void onRightClickEntity(EntityInteractSpecific event) {
      Entity entity = event.getTarget();
      Player player = event.getEntity();
      ItemStack stack = entity.getWeaponItem();
      if (entity instanceof IShearable target) {
         if (entity.level().isClientSide()) {
            event.setCancellationResult(InteractionResult.CONSUME);
         } else {
            BlockPos pos = entity.blockPosition();
            if (target.isShearable(player, stack, entity.level(), pos)) {
               target.onSheared(player, stack, entity.level(), pos).forEach(drop -> target.spawnShearedDrop(entity.level(), pos, drop));
               entity.gameEvent(GameEvent.SHEAR, player);
               this.damageItem(player, stack, LivingEntity.getSlotForHand(event.getHand()), 1);
            }

            event.setCancellationResult(InteractionResult.SUCCESS);
         }

         event.setCanceled(true);
      }
   }
}
