package fuzs.puzzleslib.api.init.v3.registry;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

@FunctionalInterface
public interface MenuSupplierWithData<T extends AbstractContainerMenu, S> {
   T create(int var1, Inventory var2, S var3);
}
