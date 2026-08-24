package com.github.alexthe666.alexsmobs.misc;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AMBannerRegistry {
   public static final DeferredRegister<BannerPattern> DEF_REG = DeferredRegister.create(Registries.BANNER_PATTERN, "alexsmobs");
}
