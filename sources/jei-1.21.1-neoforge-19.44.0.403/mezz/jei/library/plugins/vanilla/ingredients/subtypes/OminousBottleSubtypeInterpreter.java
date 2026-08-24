package mezz.jei.library.plugins.vanilla.ingredients.subtypes;

import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class OminousBottleSubtypeInterpreter implements ISubtypeInterpreter<ItemStack> {
   public static final OminousBottleSubtypeInterpreter INSTANCE = new OminousBottleSubtypeInterpreter();

   private OminousBottleSubtypeInterpreter() {
   }

   @Nullable
   public Object getSubtypeData(ItemStack ingredient, UidContext context) {
      return ingredient.get(DataComponents.OMINOUS_BOTTLE_AMPLIFIER);
   }

   public String getLegacyStringSubtypeInfo(ItemStack itemStack, UidContext context) {
      Integer amplifier = (Integer)itemStack.get(DataComponents.OMINOUS_BOTTLE_AMPLIFIER);
      return amplifier == null ? "" : amplifier.toString();
   }
}
