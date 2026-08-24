package snownee.jade.api;

import java.util.List;
import java.util.function.Function;
import snownee.jade.api.config.IWailaConfig;
import snownee.jade.api.ui.IElement;

public interface AccessorClientHandler<T extends Accessor<?>> {
   boolean shouldDisplay(T var1);

   List<IServerDataProvider<T>> shouldRequestData(T var1);

   void requestData(T var1, List<IServerDataProvider<T>> var2);

   IElement getIcon(T var1);

   void gatherComponents(T var1, Function<IJadeProvider, ITooltip> var2);

   default boolean isEnabled(IToggleableProvider provider) {
      return !IWailaConfig.get().getGeneral().getEnableAccessibilityPlugin() && JadeIds.isAccess(provider.getUid())
         ? false
         : IWailaConfig.get().getPlugin().get(provider);
   }
}
