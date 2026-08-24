package net.blay09.mods.balm.api.resources;

import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;

@Deprecated
public interface BalmResources {
   @Deprecated
   <T extends BalmResourceCondition> void registerResourceCondition(ResourceLocation var1, MapCodec<T> var2);
}
