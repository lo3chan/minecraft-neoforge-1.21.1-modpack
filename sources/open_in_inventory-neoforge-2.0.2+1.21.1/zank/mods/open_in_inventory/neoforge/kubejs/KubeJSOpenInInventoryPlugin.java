package zank.mods.open_in_inventory.neoforge.kubejs;

import java.util.Collection;
import java.util.Map;
import zank.mods.open_in_inventory.api.OpenActionRegistry;
import zank.mods.open_in_inventory.api.OpenInInventoryPlugin;

public class KubeJSOpenInInventoryPlugin implements OpenInInventoryPlugin {
   @Override
   public void registerAction(OpenActionRegistry registry) {
      OpenInInvEvents.REGISTER_ACTION.post(new ActionRegistryEventJS(registry));
   }

   @Override
   public void registerReplaceTemplate(Map<String, Collection<String>> registry) {
      OpenInInvEvents.REGISTER_REPLACE_TEMPLATE.post(new RegisterReplaceTemplateEventJS(registry));
   }
}
