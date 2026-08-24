package com.alonie.brbe.neoforge.Mixins.Accessors;

import java.util.List;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionBrewing.Mix;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({PotionBrewing.class})
public interface NeoForgePotionBrewingAccessor {
   @Accessor("potionMixes")
   List<Mix<Potion>> getPotionMixes();
}
