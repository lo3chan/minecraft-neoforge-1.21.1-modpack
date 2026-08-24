package com.teamresourceful.resourcefulconfig.mixins.common;

import java.util.Properties;
import net.minecraft.server.dedicated.Settings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({Settings.class})
public interface SettingsAccessor {
   @Invoker
   String invokeGetStringRaw(String var1);

   @Invoker
   Properties invokeCloneProperties();
}
