package mezz.jei.api.recipe.transfer;

import java.util.Optional;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.Nullable;

public interface IUniversalRecipeTransferHandler<C extends AbstractContainerMenu> {
   Class<? extends C> getContainerClass();

   Optional<MenuType<C>> getMenuType();

   @Nullable
   IRecipeTransferError transferRecipe(C var1, Object var2, IRecipeSlotsView var3, Player var4, boolean var5, boolean var6);
}
