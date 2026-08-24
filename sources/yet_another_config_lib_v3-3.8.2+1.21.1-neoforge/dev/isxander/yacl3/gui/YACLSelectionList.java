package dev.isxander.yacl3.gui;

import net.minecraft.client.Minecraft;

public abstract class YACLSelectionList<E extends YACLSelectionList.Entry<E>> extends LegacySelectionList<E> {
   public YACLSelectionList(Minecraft minecraft, int width, int height, int y) {
      super(minecraft, y, width, height);
   }

   public static <T extends YACLSelectionList<?>> WidgetAndType<T> asWidget(T list) {
      return new LegacySelectionList.Holder<>(list);
   }

   public abstract static class Entry<E extends LegacySelectionList.Entry<E>> extends LegacySelectionList.Entry<E> {
      public Entry(LegacySelectionList<E> parent) {
         super(parent);
      }
   }
}
