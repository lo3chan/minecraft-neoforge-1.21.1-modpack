package net.blay09.mods.balm.api.component;

import java.util.function.Supplier;
import net.blay09.mods.balm.api.DeferredObject;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.ResourceLocation;

@Deprecated
public interface BalmComponents {
   @Deprecated
   <TComponent> DeferredObject<DataComponentType<TComponent>> registerComponent(Supplier<DataComponentType<TComponent>> var1, ResourceLocation var2);
}
