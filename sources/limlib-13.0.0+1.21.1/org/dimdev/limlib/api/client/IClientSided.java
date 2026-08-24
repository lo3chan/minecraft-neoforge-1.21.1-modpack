package org.dimdev.limlib.api.client;

import java.util.function.Consumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.block.Block;

public interface IClientSided<T extends IClientSided<T>> {
   default T self() {
      return (T)this;
   }

   void register(RenderType var1, Block... var2);

   void onClientPlayerJoin(Runnable var1);

   void registerClientLoader(String var1, Consumer<ResourceManager> var2);
}
