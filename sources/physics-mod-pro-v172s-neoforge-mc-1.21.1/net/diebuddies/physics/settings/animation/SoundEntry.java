package net.diebuddies.physics.settings.animation;

import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.settings.cloth.BaseEntry;
import net.diebuddies.physics.settings.gui.legacy.LegacyObjectSelectionList;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvent;

public class SoundEntry extends BaseEntry {
   private final String soundID;
   private SoundEvent sound;

   public SoundEntry(LegacyObjectSelectionList objectSelectionList, String soundID) {
      super(objectSelectionList, soundID);
      this.soundID = soundID;
      this.sound = PhysicsMod.registeredSounds.get(soundID);
   }

   @Override
   public boolean mouseClicked(double d, double e, int i) {
      Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(this.sound, 1.0F));
      if (!this.isSelected()) {
         this.objectSelectionList.setSelected(this);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void render(
      GuiGraphics guiGraphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta
   ) {
      Font font = Minecraft.getInstance().font;
      String text = this.soundID;
      if (font.width(Component.literal(text).withStyle(ChatFormatting.BOLD)) > this.objectSelectionList.getRowWidth() - 55) {
         String newText = font.plainSubstrByWidth(text, this.objectSelectionList.getRowWidth() - 58);
         if (!text.equalsIgnoreCase(newText)) {
            text = newText + "...";
         }
      }

      MutableComponent label = Component.literal(text);
      if (hovered) {
         label = label.withStyle(ChatFormatting.BOLD);
         guiGraphics.drawCenteredString(font, label, x + entryWidth / 2 - 2, y + (entryHeight - 11) / 2, 16777215);
      } else {
         guiGraphics.drawCenteredString(font, label, x + entryWidth / 2 - 2, y + (entryHeight - 11) / 2, 12763842);
      }
   }

   public String getText() {
      return this.soundID;
   }

   @Override
   public Component getNarration() {
      return Component.literal(this.soundID);
   }
}
