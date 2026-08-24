package dev.shadowsoffire.placebo.events;

import net.minecraft.server.packs.resources.ResourceManager;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.LogicalSide;

public class ResourceReloadEvent extends Event {
   protected final ResourceManager resourceManager;
   protected final LogicalSide side;

   public ResourceReloadEvent(ResourceManager resourceManager, LogicalSide side) {
      this.resourceManager = resourceManager;
      this.side = side;
   }

   public ResourceManager getResourceManager() {
      return this.resourceManager;
   }

   public LogicalSide getSide() {
      return this.side;
   }
}
