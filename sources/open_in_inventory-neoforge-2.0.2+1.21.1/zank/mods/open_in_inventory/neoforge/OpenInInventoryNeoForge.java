package zank.mods.open_in_inventory.neoforge;

import java.util.List;
import net.neoforged.fml.common.Mod;
import zank.mods.open_in_inventory.OpenInInventory;
import zank.mods.open_in_inventory.api.OpenInInventoryPlugin;
import zank.mods.open_in_inventory.neoforge.kubejs.KubeJSOpenInInventoryPlugin;

@Mod("open_in_inventory")
public class OpenInInventoryNeoForge extends OpenInInventory {
   public OpenInInventoryNeoForge() {
      COMMON = this;
   }

   @Override
   protected void registerPlugin(List<OpenInInventoryPlugin> plugins) {
      super.registerPlugin(plugins);
      plugins.add(new KubeJSOpenInInventoryPlugin());
   }
}
