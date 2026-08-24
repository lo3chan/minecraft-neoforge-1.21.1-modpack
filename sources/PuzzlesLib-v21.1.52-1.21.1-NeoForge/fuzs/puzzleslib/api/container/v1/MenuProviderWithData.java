package fuzs.puzzleslib.api.container.v1;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;

public interface MenuProviderWithData<T> extends MenuProvider {
   T getMenuData(ServerPlayer var1);
}
