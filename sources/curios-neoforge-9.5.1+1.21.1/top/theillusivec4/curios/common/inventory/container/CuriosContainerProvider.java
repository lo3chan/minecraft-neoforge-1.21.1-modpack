package top.theillusivec4.curios.common.inventory.container;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class CuriosContainerProvider implements MenuProvider {
   @Nonnull
   public Component getDisplayName() {
      return Component.translatable("container.crafting");
   }

   @Nullable
   public AbstractContainerMenu createMenu(int i, @Nonnull Inventory playerInventory, @Nonnull Player playerEntity) {
      return new CuriosContainer(i, playerInventory);
   }
}
