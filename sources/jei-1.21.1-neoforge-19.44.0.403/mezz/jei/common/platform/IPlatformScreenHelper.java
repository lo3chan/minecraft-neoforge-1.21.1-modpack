package mezz.jei.common.platform;

import java.util.List;
import java.util.Optional;
import mezz.jei.common.util.ImmutableRect2i;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeBookTabButton;
import net.minecraft.world.inventory.Slot;

public interface IPlatformScreenHelper {
   Optional<Slot> getSlotUnderMouse(AbstractContainerScreen<?> var1);

   int getGuiLeft(AbstractContainerScreen<?> var1);

   int getGuiTop(AbstractContainerScreen<?> var1);

   int getXSize(AbstractContainerScreen<?> var1);

   int getYSize(AbstractContainerScreen<?> var1);

   ImmutableRect2i getBookArea(RecipeBookComponent var1);

   ImmutableRect2i getToastsArea();

   List<RecipeBookTabButton> getTabButtons(RecipeBookComponent var1);

   boolean canLoseFocus(EditBox var1);
}
