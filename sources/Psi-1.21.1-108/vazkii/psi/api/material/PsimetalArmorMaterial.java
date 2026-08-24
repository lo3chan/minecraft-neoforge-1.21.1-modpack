package vazkii.psi.api.material;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.ArmorMaterial.Layer;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.registries.DeferredRegister;
import vazkii.psi.api.PsiAPI;

public class PsimetalArmorMaterial {
   public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS = DeferredRegister.create(Registries.ARMOR_MATERIAL, "psi");
   public static final Holder<ArmorMaterial> PSIMETAL_ARMOR_MATERIAL = ARMOR_MATERIALS.register(
      "psimetal",
      () -> new ArmorMaterial(
         (Map)Util.make(new EnumMap(Type.class), map -> {
            map.put(Type.BOOTS, 2);
            map.put(Type.LEGGINGS, 5);
            map.put(Type.CHESTPLATE, 6);
            map.put(Type.HELMET, 2);
            map.put(Type.BODY, 5);
         }),
         12,
         SoundEvents.ARMOR_EQUIP_IRON,
         () -> Ingredient.of(new ItemLike[]{(ItemLike)BuiltInRegistries.ITEM.get(PsiAPI.location("psimetal"))}),
         List.of(new Layer(ResourceLocation.withDefaultNamespace(""), "", true)),
         0.0F,
         0.0F
      )
   );
}
