package mezz.jei.api.gui.handlers;

import java.util.Optional;
import java.util.function.Function;
import mezz.jei.api.gui.builder.IClickableIngredientFactory;
import mezz.jei.api.runtime.IClickableIngredient;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface IScreenHandler<T extends Screen> extends Function<T, IGuiProperties> {
   @Nullable
   IGuiProperties apply(T var1);

   default Optional<? extends IClickableIngredient<?>> getClickableIngredientUnderMouse(
      IClickableIngredientFactory factory, T screen, double mouseX, double mouseY
   ) {
      return Optional.empty();
   }
}
