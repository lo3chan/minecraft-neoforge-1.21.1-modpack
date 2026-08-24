package net.cibernet.alchemancy.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.cibernet.alchemancy.util.InfusionPropertyDispenseBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({DispenserBlock.class})
public class DispenserBlockMixin {
   @WrapOperation(
      method = {"dispenseFrom"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/block/DispenserBlock;getDispenseMethod(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/core/dispenser/DispenseItemBehavior;"
      )}
   )
   public <V extends DispenseItemBehavior> V getDispenseBehaviour(
      DispenserBlock instance, Level level, ItemStack item, Operation<DispenseItemBehavior> original
   ) {
      return (V)(new InfusionPropertyDispenseBehavior((DispenseItemBehavior)original.call(new Object[]{instance, level, item})));
   }
}
