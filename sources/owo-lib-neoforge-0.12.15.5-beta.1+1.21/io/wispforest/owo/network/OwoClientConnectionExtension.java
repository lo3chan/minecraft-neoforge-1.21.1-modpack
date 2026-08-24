package io.wispforest.owo.network;

import java.util.Set;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public interface OwoClientConnectionExtension {
   void owo$setChannelSet(Set<ResourceLocation> var1);

   Set<ResourceLocation> owo$getChannelSet();
}
