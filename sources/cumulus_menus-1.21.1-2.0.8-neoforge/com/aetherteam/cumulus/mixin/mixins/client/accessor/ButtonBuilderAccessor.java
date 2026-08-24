package com.aetherteam.cumulus.mixin.mixins.client.accessor;

import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.Button.Builder;
import net.minecraft.client.gui.components.Button.CreateNarration;
import net.minecraft.client.gui.components.Button.OnPress;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({Builder.class})
public interface ButtonBuilderAccessor {
   @Accessor("message")
   Component cumulus$message();

   @Accessor("onPress")
   OnPress cumulus$onPress();

   @Accessor("tooltip")
   @Nullable
   Tooltip cumulus$tooltip();

   @Accessor("x")
   int cumulus$x();

   @Accessor("y")
   int cumulus$y();

   @Accessor("width")
   int cumulus$width();

   @Accessor("height")
   int cumulus$height();

   @Accessor("createNarration")
   CreateNarration cumulus$createNarration();
}
