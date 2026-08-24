package mezz.jei.common.platform;

import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.Enchantment;

public interface IPlatformItemStackHelper {
   int getBurnTime(ItemStack var1);

   boolean isBookEnchantable(ItemStack var1, ItemStack var2);

   Optional<String> getCreatorModId(ItemStack var1);

   default ItemAttributeModifiers getItemAttributeModifiers(ItemStack stack) {
      return (ItemAttributeModifiers)stack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
   }

   boolean canEnchant(Holder<Enchantment> var1, ItemStack var2);
}
