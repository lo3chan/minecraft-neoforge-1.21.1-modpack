package at.petrak.hexcasting.common.items.magic;

import at.petrak.hexcasting.api.item.VariantItem;
import at.petrak.hexcasting.api.mod.HexConfig;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;

public class ItemTrinket extends ItemPackagedHex implements VariantItem {
   public ItemTrinket(Properties pProperties) {
      super(pProperties);
   }

   @Override
   public boolean canDrawMediaFromInventory(ItemStack stack) {
      return false;
   }

   @Override
   public boolean breakAfterDepletion() {
      return false;
   }

   @Override
   public int cooldown() {
      return HexConfig.common().trinketCooldown();
   }

   @Override
   public int numVariants() {
      return 8;
   }
}
