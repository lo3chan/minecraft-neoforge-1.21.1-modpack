package fuzs.puzzleslib.api.client.init.v1;

import fuzs.puzzleslib.impl.client.core.proxy.ClientProxyImpl;
import java.util.Objects;
import net.minecraft.world.level.block.state.properties.WoodType;

public final class ClientWoodTypeRegistry {
   private ClientWoodTypeRegistry() {
   }

   public static void registerWoodType(WoodType woodType) {
      Objects.requireNonNull(woodType, "wood type is null");
      ClientProxyImpl.get().registerWoodType(woodType);
   }
}
