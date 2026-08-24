package net.joefoxe.hexerei.item.custom;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.ArmorMaterial.Layer;
import net.minecraft.world.item.Item.Properties;
import org.jetbrains.annotations.Nullable;

public class MushroomWitchArmorItem extends WitchArmorItem {
   public MushroomWitchArmorItem(Holder<ArmorMaterial> materialIn, Type slot, Properties builder) {
      super(materialIn, slot, builder);
   }

   @Nullable
   @Override
   public ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, Layer layer, boolean innerModel) {
      return ResourceLocation.parse("hexerei:textures/models/armor/mushroom_witch_armor_layer1.png");
   }

   @Nullable
   @Override
   public EquipmentSlot getEquipmentSlot(ItemStack stack) {
      return super.getEquipmentSlot(stack);
   }
}
