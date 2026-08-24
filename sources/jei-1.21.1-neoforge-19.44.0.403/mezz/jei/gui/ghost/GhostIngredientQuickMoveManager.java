package mezz.jei.gui.ghost;

import java.util.Optional;
import mezz.jei.api.gui.handlers.IGhostIngredientHandler;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.runtime.IScreenHelper;
import mezz.jei.gui.input.IDraggableIngredientInternal;
import mezz.jei.gui.input.IRecipeFocusSource;
import mezz.jei.gui.input.UserInput;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;

public class GhostIngredientQuickMoveManager {
   private final IRecipeFocusSource source;
   private final IScreenHelper screenHelper;

   public GhostIngredientQuickMoveManager(IRecipeFocusSource source, IScreenHelper screenHelper) {
      this.source = source;
      this.screenHelper = screenHelper;
   }

   private <T extends Screen, V> boolean quickMoveInternal(T currentScreen, UserInput input, IDraggableIngredientInternal<V> clicked) {
      for (IGhostIngredientHandler<T> handler : this.screenHelper.getGhostIngredientHandlers(currentScreen)) {
         if (input.isSimulate()) {
            return true;
         }

         ITypedIngredient<V> ingredient = clicked.getTypedIngredient();
         if (handler.quickMove(currentScreen, ingredient)) {
            return true;
         }
      }

      return false;
   }

   public <T extends Screen> boolean quickMove(T screen, UserInput input) {
      Minecraft minecraft = Minecraft.getInstance();
      LocalPlayer player = minecraft.player;
      return player == null ? false : this.source.getDraggableIngredientUnderMouse(input.getMouseX(), input.getMouseY()).findFirst().flatMap(clicked -> {
         ItemStack mouseItem = player.containerMenu.getCarried();
         return mouseItem.isEmpty() && this.quickMoveInternal(screen, input, (IDraggableIngredientInternal<?>)clicked) ? Optional.of(true) : Optional.empty();
      }).isPresent();
   }
}
