package fuzs.puzzleslib.api.init.v3.registry;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

@Deprecated
@FunctionalInterface
public interface ExtendedMenuSupplier<T extends AbstractContainerMenu> {
   T create(int var1, Inventory var2, RegistryFriendlyByteBuf var3);
}
