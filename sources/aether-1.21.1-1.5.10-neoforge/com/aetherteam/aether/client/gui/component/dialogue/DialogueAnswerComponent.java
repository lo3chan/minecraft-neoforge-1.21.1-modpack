package com.aetherteam.aether.client.gui.component.dialogue;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

public class DialogueAnswerComponent {
   private final List<DialogueAnswerComponent.NpcDialogueElement> splitLines = new ArrayList<>();
   public int height;

   public DialogueAnswerComponent(Component message) {
      this.updateDialogue(message);
   }

   public void render(GuiGraphics guiGraphics) {
      this.splitLines.forEach(element -> element.render(guiGraphics));
   }

   public void reposition(int width, int height) {
      int i = 0;

      for (DialogueAnswerComponent.NpcDialogueElement dialogue : this.splitLines) {
         dialogue.width = Minecraft.getInstance().font.width(dialogue.text) + 2;
         dialogue.x = width / 2 - dialogue.width / 2;
         dialogue.y = height / 2 + i * 12;
         i++;
      }

      this.height = this.splitLines.size() * 12;
   }

   public void updateDialogue(Component message) {
      this.splitLines.clear();
      List<FormattedCharSequence> list = Minecraft.getInstance().font.split(message, 300);
      this.height = list.size() * 12;
      list.forEach(text -> this.splitLines.add(new DialogueAnswerComponent.NpcDialogueElement(0, 0, 0, text)));
   }

   public static class NpcDialogueElement {
      private final FormattedCharSequence text;
      private int x;
      private int y;
      private int width;

      public NpcDialogueElement(int x, int y, int width, FormattedCharSequence text) {
         this.text = text;
         this.x = x;
         this.y = y;
         this.width = width;
      }

      public void render(GuiGraphics guiGraphics) {
         guiGraphics.fillGradient(this.x, this.y, this.x + this.width, this.y + 12, 1711276032, 1711276032);
         int var10003 = this.x + 1;
         int var10004 = this.y + 1;
         guiGraphics.drawString(Minecraft.getInstance().font, this.text, var10003, var10004, 16777215);
      }
   }
}
