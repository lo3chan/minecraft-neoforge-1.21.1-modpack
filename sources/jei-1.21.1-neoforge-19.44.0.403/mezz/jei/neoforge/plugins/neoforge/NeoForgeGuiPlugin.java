package mezz.jei.neoforge.plugins.neoforge;

import java.util.Optional;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRuntimeRegistration;
import mezz.jei.api.runtime.IJeiFeatures;
import mezz.jei.gui.startup.JeiEventHandlers;
import mezz.jei.gui.startup.JeiGuiStarter;
import mezz.jei.gui.startup.ResourceReloadHandler;
import mezz.jei.neoforge.events.RuntimeEventSubscriptions;
import mezz.jei.neoforge.startup.EventRegistration;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

@JeiPlugin
public class NeoForgeGuiPlugin implements IModPlugin {
   private static final Logger LOGGER = LogManager.getLogger();
   @Nullable
   private static ResourceReloadHandler resourceReloadHandler;
   @Nullable
   private IJeiFeatures jeiFeatures;
   private final RuntimeEventSubscriptions runtimeSubscriptions = new RuntimeEventSubscriptions(NeoForge.EVENT_BUS);

   @Override
   public ResourceLocation getPluginUid() {
      return ResourceLocation.fromNamespaceAndPath("jei", "neoforge_gui");
   }

   @Override
   public void configureJei(IJeiFeatures jeiFeatures) {
      this.jeiFeatures = jeiFeatures;
   }

   @Override
   public void registerRuntime(IRuntimeRegistration registration) {
      if (this.isJeiGuiEnabled()) {
         if (!this.runtimeSubscriptions.isEmpty()) {
            LOGGER.error("JEI GUI is already running.");
            this.runtimeSubscriptions.clear();
         }

         JeiEventHandlers eventHandlers = JeiGuiStarter.start(registration);
         resourceReloadHandler = eventHandlers.resourceReloadHandler();
         EventRegistration.registerEvents(this.runtimeSubscriptions, eventHandlers);
      }
   }

   @Override
   public void onRuntimeUnavailable() {
      LOGGER.info("Stopping JEI GUI");
      this.runtimeSubscriptions.clear();
      resourceReloadHandler = null;
   }

   public static Optional<ResourceReloadHandler> getResourceReloadHandler() {
      return Optional.ofNullable(resourceReloadHandler);
   }

   private boolean isJeiGuiEnabled() {
      IJeiFeatures jeiFeatures = this.jeiFeatures;
      return jeiFeatures == null || jeiFeatures.isJeiGuiEnabled();
   }
}
