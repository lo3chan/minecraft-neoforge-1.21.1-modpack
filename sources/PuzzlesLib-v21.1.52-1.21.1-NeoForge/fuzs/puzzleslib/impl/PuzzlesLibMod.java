package fuzs.puzzleslib.impl;

import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import fuzs.puzzleslib.api.network.v3.NetworkHandler;
import fuzs.puzzleslib.impl.capability.ClientboundEntityCapabilityMessage;
import fuzs.puzzleslib.impl.core.proxy.ProxyImpl;
import net.minecraft.resources.ResourceLocation;

public class PuzzlesLibMod extends PuzzlesLib implements ModConstructor {
   public static final NetworkHandler NETWORK = NetworkHandler.builder("puzzleslib").optional().registerClientbound(ClientboundEntityCapabilityMessage.class);

   @Override
   public void onConstructMod() {
      ProxyImpl.get().registerEventHandlers();
   }

   public static ResourceLocation id(String path) {
      return ResourceLocationHelper.fromNamespaceAndPath("puzzleslib", path);
   }
}
