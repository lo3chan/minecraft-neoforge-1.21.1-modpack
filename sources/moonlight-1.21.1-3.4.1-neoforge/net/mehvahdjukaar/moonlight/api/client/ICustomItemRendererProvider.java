package net.mehvahdjukaar.moonlight.api.client;

import java.util.function.Supplier;
import net.minecraft.world.level.ItemLike;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@Deprecated(
   forRemoval = true
)
public interface ICustomItemRendererProvider extends ItemLike {
   @OnlyIn(Dist.CLIENT)
   Supplier<ItemStackRenderer> getRendererFactory();
}
