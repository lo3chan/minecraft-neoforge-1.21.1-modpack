package fuzs.puzzleslib.api.client.init.v1;

import fuzs.puzzleslib.impl.client.core.proxy.ClientProxyImpl;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;

public interface ItemModelDisplayOverrides {
   ItemModelDisplayOverrides INSTANCE = ClientProxyImpl.get().getItemModelDisplayOverrides();

   void register(ModelResourceLocation var1, ModelResourceLocation var2);

   void register(ModelResourceLocation var1, ModelResourceLocation var2, ItemDisplayContext... var3);

   void register(ModelResourceLocation var1, ResourceLocation var2);

   void register(ModelResourceLocation var1, ResourceLocation var2, ItemDisplayContext... var3);
}
