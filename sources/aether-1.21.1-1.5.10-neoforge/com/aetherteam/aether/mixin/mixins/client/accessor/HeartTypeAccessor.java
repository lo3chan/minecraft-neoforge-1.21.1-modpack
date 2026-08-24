package com.aetherteam.aether.mixin.mixins.client.accessor;

import net.minecraft.client.gui.Gui.HeartType;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({HeartType.class})
public interface HeartTypeAccessor {
   @Invoker
   static HeartType callForPlayer(Player player) {
      throw new AssertionError();
   }
}
