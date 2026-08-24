package zank.mods.open_in_inventory.impl.crt;

import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;
import zank.mods.open_in_inventory.api.OpenActionRegistry;
import zank.mods.open_in_inventory.api.OpenInInventoryPlugin;

public class ProvideCraftTweakerOpenAction implements OpenInInventoryPlugin {
   @Override
   public void registerAction(OpenActionRegistry registry) {
      for (Consumer<OpenActionRegistry> provider : OpenInInventoryCrt.ACTION_PROVIDERS.view()) {
         provider.accept(registry);
      }
   }

   @Override
   public void registerReplaceTemplate(Map<String, Collection<String>> registry) {
      for (Consumer<Map<String, Collection<String>>> handler : OpenInInventoryCrt.REPLACE_TEMPLATE_PROVIDERS.view()) {
         handler.accept(registry);
      }
   }
}
