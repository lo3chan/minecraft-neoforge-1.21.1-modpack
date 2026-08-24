package io.github.razordevs.deep_aether.item.gear.skyjade;

import io.github.razordevs.deep_aether.DeepAetherConfig;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;

public class SkyjadeArmorItem extends ArmorItem {
   public SkyjadeArmorItem(Holder<ArmorMaterial> material, Type type, Properties properties) {
      super(material, type, properties);
   }

   public boolean isEnchantable(ItemStack itemStack) {
      return (Boolean)DeepAetherConfig.SERVER.skyjade_enchant.get() && !(Boolean)DeepAetherConfig.SERVER.enable_skyjade_rework.get();
   }

   public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
      return (Boolean)DeepAetherConfig.SERVER.skyjade_enchant.get() && !(Boolean)DeepAetherConfig.SERVER.enable_skyjade_rework.get();
   }
}
