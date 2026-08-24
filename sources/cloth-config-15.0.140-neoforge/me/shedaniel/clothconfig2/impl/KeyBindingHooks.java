package me.shedaniel.clothconfig2.impl;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface KeyBindingHooks {
   void cloth_setId(String var1);
}
