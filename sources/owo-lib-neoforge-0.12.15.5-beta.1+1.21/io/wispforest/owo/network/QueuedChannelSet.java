package io.wispforest.owo.network;

import java.util.Set;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.ApiStatus.Internal;

@OnlyIn(Dist.CLIENT)
@Internal
public class QueuedChannelSet {
   public static Set<ResourceLocation> channels;
}
