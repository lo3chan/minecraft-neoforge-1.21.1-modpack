package mezz.jei.library.plugins.vanilla.ingredients.subtypes;

import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class InstrumentSubtypeInterpreter implements ISubtypeInterpreter<ItemStack> {
   public static final InstrumentSubtypeInterpreter INSTANCE = new InstrumentSubtypeInterpreter();

   private InstrumentSubtypeInterpreter() {
   }

   @Nullable
   public Object getSubtypeData(ItemStack ingredient, UidContext context) {
      return ingredient.get(DataComponents.INSTRUMENT);
   }

   public String getLegacyStringSubtypeInfo(ItemStack itemStack, UidContext context) {
      Holder<Instrument> instrument = (Holder<Instrument>)itemStack.get(DataComponents.INSTRUMENT);
      return instrument == null ? "" : instrument.getRegisteredName();
   }
}
