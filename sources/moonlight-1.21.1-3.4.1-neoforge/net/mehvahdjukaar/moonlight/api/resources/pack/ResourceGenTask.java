package net.mehvahdjukaar.moonlight.api.resources.pack;

import java.util.function.BiConsumer;
import net.minecraft.server.packs.resources.ResourceManager;

public interface ResourceGenTask extends BiConsumer<ResourceManager, ResourceSink> {
   void accept(ResourceManager var1, ResourceSink var2);
}
