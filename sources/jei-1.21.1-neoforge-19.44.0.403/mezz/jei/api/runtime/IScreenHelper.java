package mezz.jei.api.runtime;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import mezz.jei.api.gui.handlers.IGhostIngredientHandler;
import mezz.jei.api.gui.handlers.IGuiClickableArea;
import mezz.jei.api.gui.handlers.IGuiProperties;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import org.jetbrains.annotations.ApiStatus.NonExtendable;

@NonExtendable
public interface IScreenHelper {
   Stream<IClickableIngredient<?>> getClickableIngredientUnderMouse(Screen var1, double var2, double var4);

   <T extends Screen> Optional<IGuiProperties> getGuiProperties(T var1);

   Stream<IGuiClickableArea> getGuiClickableArea(AbstractContainerScreen<?> var1, double var2, double var4);

   Stream<Rect2i> getGuiExclusionAreas(Screen var1);

   <T extends Screen> List<IGhostIngredientHandler<T>> getGhostIngredientHandlers(T var1);

   @Deprecated(
      since = "19.8.2",
      forRemoval = true
   )
   default <T extends Screen> Optional<IGhostIngredientHandler<T>> getGhostIngredientHandler(T guiScreen) {
      List<IGhostIngredientHandler<T>> handlers = this.getGhostIngredientHandlers(guiScreen);
      return handlers.isEmpty() ? Optional.empty() : Optional.of((IGhostIngredientHandler<T>)handlers.getFirst());
   }
}
