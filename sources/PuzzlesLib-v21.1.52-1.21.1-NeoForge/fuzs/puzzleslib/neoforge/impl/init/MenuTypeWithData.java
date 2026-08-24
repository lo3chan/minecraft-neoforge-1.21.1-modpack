package fuzs.puzzleslib.neoforge.impl.init;

import fuzs.puzzleslib.api.init.v3.registry.MenuSupplierWithData;
import java.util.Objects;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.network.IContainerFactory;

public final class MenuTypeWithData<T extends AbstractContainerMenu, S> extends MenuType<T> {
   private final StreamCodec<? super RegistryFriendlyByteBuf, S> streamCodec;

   public MenuTypeWithData(MenuSupplierWithData<T, S> menuSupplier, StreamCodec<? super RegistryFriendlyByteBuf, S> streamCodec) {
      super(
         (IContainerFactory)(containerId, inventory, buf) -> menuSupplier.create(containerId, inventory, (S)streamCodec.decode(buf)),
         FeatureFlags.DEFAULT_FLAGS
      );
      Objects.requireNonNull(menuSupplier, "menu supplier is null");
      Objects.requireNonNull(streamCodec, "stream codec is null");
      this.streamCodec = streamCodec;
   }

   public T create(int containerId, Inventory inventory) {
      throw new UnsupportedOperationException();
   }

   public StreamCodec<? super RegistryFriendlyByteBuf, S> getStreamCodec() {
      return this.streamCodec;
   }

   public static <T> void encodeMenuData(AbstractContainerMenu containerMenu, RegistryFriendlyByteBuf buf, T data) {
      Objects.requireNonNull(containerMenu, "container menu is null");
      if (containerMenu.getType() instanceof MenuTypeWithData<?, ?> menuType) {
         menuType.getStreamCodec().encode(buf, data);
      } else {
         throw new IllegalArgumentException("Menu type " + containerMenu.getType() + " does not support data");
      }
   }
}
