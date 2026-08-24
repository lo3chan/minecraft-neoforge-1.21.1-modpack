package net.joefoxe.hexerei.client.renderer.entity.model;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.joefoxe.hexerei.item.custom.MushroomWitchArmorItem;
import net.joefoxe.hexerei.item.custom.WitchArmorItem;
import net.joefoxe.hexerei.util.ClientProxy;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ArmorModels {
   private static Map<EquipmentSlot, ArmorModel> witchArmor = Collections.emptyMap();
   private static Map<EquipmentSlot, ArmorModel> mushroomWitchArmor = Collections.emptyMap();

   private static Map<EquipmentSlot, ArmorModel> make(Context ctx, ModelLayerLocation layer) {
      Map<EquipmentSlot, ArmorModel> ret = new EnumMap<>(EquipmentSlot.class);

      for (EquipmentSlot slot : EquipmentSlot.values()) {
         ret.put(slot, new ArmorModel(ctx.bakeLayer(layer), slot));
      }

      return ret;
   }

   public static void init(Context context) {
      witchArmor = make(context, ClientProxy.WITCH_ARMOR_LAYER);
      mushroomWitchArmor = make(context, ClientProxy.MUSHROOM_WITCH_ARMOR_LAYER);
   }

   @Nullable
   public static ArmorModel get(ItemStack stack) {
      Item item = stack.getItem();
      if (item instanceof MushroomWitchArmorItem armor) {
         return mushroomWitchArmor.get(armor.getType().getSlot());
      } else {
         return item instanceof WitchArmorItem armor ? witchArmor.get(armor.getType().getSlot()) : null;
      }
   }
}
