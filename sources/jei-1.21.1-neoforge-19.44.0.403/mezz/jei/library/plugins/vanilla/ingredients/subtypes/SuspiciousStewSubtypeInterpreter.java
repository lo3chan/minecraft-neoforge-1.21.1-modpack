package mezz.jei.library.plugins.vanilla.ingredients.subtypes;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.SuspiciousStewEffects;
import net.minecraft.world.item.component.SuspiciousStewEffects.Entry;
import org.jetbrains.annotations.Nullable;

public class SuspiciousStewSubtypeInterpreter implements ISubtypeInterpreter<ItemStack> {
   public static final SuspiciousStewSubtypeInterpreter INSTANCE = new SuspiciousStewSubtypeInterpreter();

   private SuspiciousStewSubtypeInterpreter() {
   }

   @Nullable
   public Object getSubtypeData(ItemStack ingredient, UidContext context) {
      return ingredient.get(DataComponents.SUSPICIOUS_STEW_EFFECTS);
   }

   public String getLegacyStringSubtypeInfo(ItemStack itemStack, UidContext context) {
      SuspiciousStewEffects suspiciousStewEffects = (SuspiciousStewEffects)itemStack.get(DataComponents.SUSPICIOUS_STEW_EFFECTS);
      if (suspiciousStewEffects == null) {
         return "";
      } else {
         List<Entry> effects = suspiciousStewEffects.effects();
         List<String> strings = new ArrayList<>();

         for (Entry e : effects) {
            String effect = e.effect().getRegisteredName();
            int duration = e.duration();
            strings.add(effect + "." + duration);
         }

         StringJoiner joiner = new StringJoiner(",", "[", "]");
         strings.sort(null);

         for (String s : strings) {
            joiner.add(s);
         }

         return joiner.toString();
      }
   }
}
