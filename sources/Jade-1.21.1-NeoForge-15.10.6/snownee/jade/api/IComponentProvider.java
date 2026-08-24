package snownee.jade.api;

import org.jetbrains.annotations.Nullable;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElement;

public interface IComponentProvider<T extends Accessor<?>> extends IToggleableProvider {
   @Nullable
   default IElement getIcon(T accessor, IPluginConfig config, IElement currentIcon) {
      return null;
   }

   void appendTooltip(ITooltip var1, T var2, IPluginConfig var3);
}
