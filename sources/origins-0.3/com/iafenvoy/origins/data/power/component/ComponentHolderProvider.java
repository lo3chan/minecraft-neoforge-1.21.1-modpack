package com.iafenvoy.origins.data.power.component;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import net.minecraft.resources.ResourceLocation;

public interface ComponentHolderProvider<T> {
   T constructHolder(OriginDataHolder var1, ResourceLocation var2);
}
