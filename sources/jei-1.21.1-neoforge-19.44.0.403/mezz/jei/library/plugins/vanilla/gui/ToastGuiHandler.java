package mezz.jei.library.plugins.vanilla.gui;

import java.util.Collection;
import java.util.List;
import mezz.jei.api.gui.handlers.IGlobalGuiHandler;
import mezz.jei.common.Internal;
import mezz.jei.common.config.IClientConfig;
import mezz.jei.common.platform.IPlatformScreenHelper;
import mezz.jei.common.platform.Services;
import mezz.jei.common.util.ImmutableRect2i;
import net.minecraft.client.renderer.Rect2i;

public class ToastGuiHandler implements IGlobalGuiHandler {
   @Override
   public Collection<Rect2i> getGuiExtraAreas() {
      IClientConfig clientConfig = Internal.getJeiClientConfigs().getClientConfig();
      if (!clientConfig.toastReflowEnabled().getValue()) {
         return List.of();
      } else {
         IPlatformScreenHelper screenHelper = Services.PLATFORM.getScreenHelper();
         ImmutableRect2i toastsArea = screenHelper.getToastsArea();
         return toastsArea.isEmpty() ? List.of() : List.of(toastsArea.toMutable());
      }
   }
}
