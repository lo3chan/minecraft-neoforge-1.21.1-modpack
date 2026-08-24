package com.aetherteam.aether.entity;

import javax.annotation.Nullable;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public interface NpcDialogue {
   @OnlyIn(Dist.CLIENT)
   void openDialogueScreen();

   void handleNpcInteraction(Player var1, byte var2);

   void setConversingPlayer(@Nullable Player var1);

   @Nullable
   Player getConversingPlayer();
}
