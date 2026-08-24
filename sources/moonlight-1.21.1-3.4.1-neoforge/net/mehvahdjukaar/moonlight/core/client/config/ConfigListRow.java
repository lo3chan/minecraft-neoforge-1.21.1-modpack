package net.mehvahdjukaar.moonlight.core.client.config;

import net.minecraft.client.gui.components.ContainerObjectSelectionList.Entry;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

abstract class ConfigListRow extends Entry<ConfigListRow> {
   @Nullable
   abstract Component getTooltip(int var1, int var2);

   @Nullable
   Component getGutterTooltip(int mouseX, int mouseY) {
      return null;
   }
}
