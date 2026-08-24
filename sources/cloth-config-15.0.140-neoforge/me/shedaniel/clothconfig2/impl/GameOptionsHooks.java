package me.shedaniel.clothconfig2.impl;

import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface GameOptionsHooks {
   void cloth_setKeysAll(KeyMapping[] var1);
}
