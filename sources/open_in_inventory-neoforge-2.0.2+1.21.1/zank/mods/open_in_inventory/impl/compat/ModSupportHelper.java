package zank.mods.open_in_inventory.impl.compat;

import dev.architectury.platform.Platform;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import zank.mods.open_in_inventory.OpenInInventory;
import zank.mods.open_in_inventory.api.OpenAction;
import zank.mods.open_in_inventory.api.OpenActionRegistry;

public final class ModSupportHelper {
   private final OpenActionRegistry registry;
   private String mod;

   public ModSupportHelper(OpenActionRegistry registry) {
      this.registry = registry;
   }

   public boolean check(String mod) {
      this.mod = mod;
      return Platform.isModLoaded(mod);
   }

   public ResourceLocation id(String path) {
      return ResourceLocation.tryBuild(this.mod, path);
   }

   public Collection<OpenAction> tryRegister(String path, boolean sneak) {
      ArrayList<OpenAction> registered = new ArrayList<>();

      for (String applied : this.registry.findAndApplyTemplate(path)) {
         Optional<OpenAction> result = this.registry.registerIfPresent(this.id(applied), sneak);
         if (result.isEmpty()) {
            OpenInInventory.LOGGER.error("Cannot find item ith id: {}", this.id(path));
         } else {
            registered.add(result.get());
         }
      }

      return registered;
   }

   public Collection<OpenAction> tryRegister(String path) {
      return this.tryRegister(path, false);
   }
}
