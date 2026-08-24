package io.wispforest.owo.mixin.offline;

import java.util.Map;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(
   targets = {"net/minecraft/server/PlayerAdvancements$Data"}
)
public interface ProgressMapAccessor {
   @Accessor("map")
   Map<ResourceLocation, AdvancementProgress> getMap();
}
