package net.blay09.mods.balm.world.entity.ai.village.poi;

import java.util.function.Supplier;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.village.poi.PoiType;

public interface BalmPoiTypeRegistrar {
   <T extends PoiType> Holder<T> register(String var1, Supplier<T> var2);
}
