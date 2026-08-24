package mezz.jei.library.plugins.vanilla.ingredients.subtypes;

import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.jetbrains.annotations.Nullable;

public class PaintingSubtypeInterpreter implements ISubtypeInterpreter<ItemStack> {
   public static final PaintingSubtypeInterpreter INSTANCE = new PaintingSubtypeInterpreter();

   private PaintingSubtypeInterpreter() {
   }

   @Nullable
   public Object getSubtypeData(ItemStack ingredient, UidContext context) {
      CustomData properties = (CustomData)ingredient.get(DataComponents.ENTITY_DATA);
      if (properties == null) {
         return null;
      } else {
         CompoundTag compoundTag = properties.copyTag();
         return compoundTag.get("variant");
      }
   }

   public String getLegacyStringSubtypeInfo(ItemStack ingredient, UidContext context) {
      CustomData properties = (CustomData)ingredient.get(DataComponents.ENTITY_DATA);
      if (properties == null) {
         return "";
      } else {
         CompoundTag compoundTag = properties.copyTag();
         Tag variant = compoundTag.get("variant");
         return variant == null ? "" : variant.getAsString();
      }
   }
}
