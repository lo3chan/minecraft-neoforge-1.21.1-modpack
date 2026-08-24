package mezz.jei.library.plugins.vanilla.ingredients.subtypes;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.item.component.FireworkExplosion.Shape;
import org.jetbrains.annotations.Nullable;

public class FireworkRocketSubtypeInterpreter implements ISubtypeInterpreter<ItemStack> {
   public static final FireworkRocketSubtypeInterpreter INSTANCE = new FireworkRocketSubtypeInterpreter();

   private FireworkRocketSubtypeInterpreter() {
   }

   @Nullable
   public Object getSubtypeData(ItemStack ingredient, UidContext context) {
      return ingredient.get(DataComponents.FIREWORKS);
   }

   public String getLegacyStringSubtypeInfo(ItemStack itemStack, UidContext context) {
      Fireworks fireworks = (Fireworks)itemStack.get(DataComponents.FIREWORKS);
      if (fireworks == null) {
         return "";
      } else {
         List<FireworkExplosion> explosions = fireworks.explosions();
         List<String> strings = new ArrayList<>();

         for (FireworkExplosion e : explosions) {
            Shape shape = e.shape();
            strings.add(shape.getSerializedName());
         }

         StringJoiner joiner = new StringJoiner(",", "[", "]");
         strings.sort(null);

         for (String s : strings) {
            joiner.add(s);
         }

         int flightDuration = fireworks.flightDuration();
         return flightDuration + ":" + joiner;
      }
   }
}
