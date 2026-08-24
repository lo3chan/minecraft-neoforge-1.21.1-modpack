package dev.isxander.yacl3.api;

import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.impl.PlaceholderCategoryImpl;
import java.util.function.BiFunction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public interface PlaceholderCategory extends ConfigCategory {
   BiFunction<Minecraft, YACLScreen, Screen> screen();

   static PlaceholderCategory.Builder createBuilder() {
      return new PlaceholderCategoryImpl.BuilderImpl();
   }

   public interface Builder {
      PlaceholderCategory.Builder name(@NotNull Component var1);

      PlaceholderCategory.Builder tooltip(@NotNull Component... var1);

      PlaceholderCategory.Builder screen(@NotNull BiFunction<Minecraft, YACLScreen, Screen> var1);

      PlaceholderCategory build();
   }
}
