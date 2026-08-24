package net.blay09.mods.balm.api.compat.hudinfo;

import net.minecraft.network.chat.Component;

public interface HudInfoOutput {
   void text(Component var1);

   void progress(float var1);

   default void progress(int progress, int maxProgress) {
      this.progress((float)progress / maxProgress);
   }
}
