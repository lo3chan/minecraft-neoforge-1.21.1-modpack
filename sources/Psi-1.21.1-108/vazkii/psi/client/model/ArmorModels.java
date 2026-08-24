package vazkii.psi.client.model;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import vazkii.psi.common.item.armor.ItemPsimetalArmor;

public class ArmorModels {
   private static Map<EquipmentSlot, ModelArmor> exosuit = Collections.emptyMap();

   private static Map<EquipmentSlot, ModelArmor> make(Context ctx) {
      Map<EquipmentSlot, ModelArmor> ret = new EnumMap<>(EquipmentSlot.class);

      for (EquipmentSlot slot : EquipmentSlot.values()) {
         ModelPart mesh = ctx.bakeLayer(slot == EquipmentSlot.LEGS ? ModModelLayers.PSIMETAL_EXOSUIT_INNER_ARMOR : ModModelLayers.PSIMETAL_EXOSUIT_OUTER_ARMOR);
         ret.put(slot, new ModelArmor(mesh));
      }

      return ret;
   }

   public static void init(Context ctx) {
      exosuit = make(ctx);
   }

   @Nullable
   public static ModelArmor get(ItemStack stack) {
      return stack.getItem() instanceof ItemPsimetalArmor armor ? exosuit.get(armor.getEquipmentSlot()) : null;
   }
}
