package top.theillusivec4.curios.common.integration.jei;

import javax.annotation.Nonnull;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import net.minecraft.resources.ResourceLocation;
import top.theillusivec4.curios.client.gui.CuriosScreen;

@JeiPlugin
public class CuriosJeiPlugin implements IModPlugin {
   @Nonnull
   public ResourceLocation getPluginUid() {
      return ResourceLocation.fromNamespaceAndPath("curios", "curios");
   }

   public void registerGuiHandlers(IGuiHandlerRegistration registration) {
      registration.addGuiContainerHandler(CuriosScreen.class, new CuriosContainerHandler());
   }
}
