package com.aetherteam.cumulus.mixin.mixins.client.accessor;

import com.mojang.blaze3d.systems.TimerQuery.FrameProfile;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({Minecraft.class})
public interface MinecraftAccessor {
   @Accessor("isLocalServer")
   void cumulus$setIsLocalServer(boolean var1);

   @Accessor("currentFrameProfile")
   FrameProfile cumulus$getCurrentFrameProfile();

   @Accessor("currentFrameProfile")
   void cumulus$setCurrentFrameProfile(FrameProfile var1);
}
