package com.finndog.moogs_structures.mixins.resources;

import java.util.List;
import net.minecraft.server.packs.resources.FallbackResourceManager;
import net.minecraft.server.packs.resources.FallbackResourceManager.PackEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({FallbackResourceManager.class})
public interface NamespaceResourceManagerAccessor {
   @Accessor("fallbacks")
   List<PackEntry> moogs_structures_getFallbacks();
}
