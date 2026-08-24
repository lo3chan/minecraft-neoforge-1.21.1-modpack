package net.cibernet.alchemancy.util;

import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.Property;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;

public class InfusionPropertyDispenseBehavior implements DispenseItemBehavior {
   final DispenseItemBehavior parent;

   public InfusionPropertyDispenseBehavior(DispenseItemBehavior parent) {
      this.parent = parent;
   }

   public ItemStack dispense(BlockSource blockSource, ItemStack item) {
      InfusionPropertyDispenseBehavior.DispenseResult[] result = new InfusionPropertyDispenseBehavior.DispenseResult[]{
         InfusionPropertyDispenseBehavior.DispenseResult.PASS
      };
      Direction direction = (Direction)blockSource.state().getValue(DispenserBlock.FACING);
      InfusedPropertiesHelper.forEachProperty(
         item,
         propertyHolder -> {
            InfusionPropertyDispenseBehavior.DispenseResult currentResult = ((Property)propertyHolder.value())
               .onItemDispense(blockSource, direction, item, result[0]);
            if (currentResult.ordinal() < result[0].ordinal()) {
               result[0] = currentResult;
            }
         }
      );
      if (result[0] == InfusionPropertyDispenseBehavior.DispenseResult.CONSUME) {
         item.shrink(1);
      }

      return result[0] != InfusionPropertyDispenseBehavior.DispenseResult.PASS ? item : this.parent.dispense(blockSource, item);
   }

   public static InfusionPropertyDispenseBehavior.DispenseResult executeItemBehavior(BlockSource blockSource, ItemStack stack, Item parentItem) {
      DispenseItemBehavior parentBehavior = (DispenseItemBehavior)DispenserBlock.DISPENSER_REGISTRY.get(parentItem);
      parentBehavior.dispense(blockSource, stack);
      return parentBehavior instanceof OptionalDispenseItemBehavior optionalDispenseItemBehavior && !optionalDispenseItemBehavior.isSuccess()
         ? InfusionPropertyDispenseBehavior.DispenseResult.PASS
         : InfusionPropertyDispenseBehavior.DispenseResult.SUCCESS;
   }

   public static void playDefaultEffects(BlockSource blockSource, Direction direction) {
      playDefaultSound(blockSource);
      playDefaultParticles(blockSource, direction);
   }

   public static void playDefaultSound(BlockSource blockSource) {
      blockSource.level().levelEvent(1000, blockSource.pos(), 0);
   }

   public static void playDefaultParticles(BlockSource blockSource, Direction direction) {
      blockSource.level().levelEvent(2000, blockSource.pos(), direction.get3DDataValue());
   }

   public static enum DispenseResult {
      CONSUME,
      SUCCESS,
      PASS;
   }
}
