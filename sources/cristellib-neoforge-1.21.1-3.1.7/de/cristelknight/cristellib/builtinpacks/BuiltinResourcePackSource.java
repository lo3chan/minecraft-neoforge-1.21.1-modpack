package de.cristelknight.cristellib.builtinpacks;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.repository.PackSource;
import org.jetbrains.annotations.NotNull;

public record BuiltinResourcePackSource() implements PackSource {
   public boolean shouldAddAutomatically() {
      return true;
   }

   @NotNull
   public Component decorate(@NotNull Component packName) {
      return Component.translatable("cristellib.nameAndSource", new Object[]{packName}).withStyle(ChatFormatting.GRAY);
   }
}
