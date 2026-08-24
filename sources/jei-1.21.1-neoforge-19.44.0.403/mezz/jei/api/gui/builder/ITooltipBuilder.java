package mezz.jei.api.gui.builder;

import com.mojang.datafixers.util.Either;
import java.util.Collection;
import java.util.List;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.runtime.IJeiKeyMapping;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

public interface ITooltipBuilder {
   void add(FormattedText var1);

   void addAll(Collection<? extends FormattedText> var1);

   void add(TooltipComponent var1);

   void addKeyUsageComponent(String var1, IJeiKeyMapping var2);

   void setIngredient(ITypedIngredient<?> var1);

   default void clear() {
      this.clearIngredient();
      this.getLines().clear();
   }

   void clearIngredient();

   List<Either<FormattedText, TooltipComponent>> getLines();

   @Deprecated(
      since = "19.8.4",
      forRemoval = true
   )
   List<Component> toLegacyToComponents();

   @Deprecated(
      since = "19.8.4",
      forRemoval = true
   )
   void removeAll(List<Component> var1);
}
