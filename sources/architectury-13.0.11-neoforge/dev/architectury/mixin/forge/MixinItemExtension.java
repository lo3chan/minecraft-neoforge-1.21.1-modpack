package dev.architectury.mixin.forge;

import dev.architectury.extensions.ItemExtension;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.extensions.IItemExtension;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ItemExtension.class})
public interface MixinItemExtension extends IItemExtension {
   @Nullable
   default EquipmentSlot getEquipmentSlot(ItemStack stack) {
      return ((ItemExtension)this).getCustomEquipmentSlot(stack);
   }
}
