package fuzs.eternalnether.world.item;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.Item.Properties;

public class UnrepairableShieldItem extends ShieldItem {
   public UnrepairableShieldItem(Properties properties) {
      super(properties);
   }

   public boolean isValidRepairItem(ItemStack itemStack, ItemStack repairCandidate) {
      return false;
   }
}
