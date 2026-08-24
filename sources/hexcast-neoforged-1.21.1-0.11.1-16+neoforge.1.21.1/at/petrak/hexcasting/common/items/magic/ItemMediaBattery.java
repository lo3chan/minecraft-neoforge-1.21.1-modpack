package at.petrak.hexcasting.common.items.magic;

import at.petrak.hexcasting.api.HexAPI;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;

public class ItemMediaBattery extends ItemMediaHolder {
   public static final ResourceLocation MEDIA_PREDICATE = HexAPI.modLoc("media");
   public static final ResourceLocation MAX_MEDIA_PREDICATE = HexAPI.modLoc("max_media");

   public ItemMediaBattery(Properties pProperties) {
      super(pProperties);
   }

   @Override
   public boolean canProvideMedia(ItemStack stack) {
      return true;
   }

   @Override
   public boolean canRecharge(ItemStack stack) {
      return true;
   }
}
