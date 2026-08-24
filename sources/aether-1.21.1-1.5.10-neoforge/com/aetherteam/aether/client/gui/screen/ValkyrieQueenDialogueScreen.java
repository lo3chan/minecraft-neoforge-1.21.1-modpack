package com.aetherteam.aether.client.gui.screen;

import com.aetherteam.aether.client.gui.component.dialogue.DialogueAnswerComponent;
import com.aetherteam.aether.client.gui.component.dialogue.DialogueChoiceComponent;
import com.aetherteam.aether.entity.monster.dungeon.boss.ValkyrieQueen;
import com.aetherteam.aether.item.AetherItems;
import com.aetherteam.aether.network.packet.serverbound.NpcPlayerInteractPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.network.PacketDistributor;

public class ValkyrieQueenDialogueScreen extends Screen {
   private final DialogueAnswerComponent dialogueAnswer;
   private final ValkyrieQueen valkyrieQueen;

   public ValkyrieQueenDialogueScreen(ValkyrieQueen valkyrieQueen) {
      super(valkyrieQueen.getBossName());
      this.dialogueAnswer = new DialogueAnswerComponent(this.buildDialogueAnswerName(valkyrieQueen.getBossName()));
      this.valkyrieQueen = valkyrieQueen;
   }

   protected void init() {
      if (this.getMinecraft().player != null) {
         this.setupDialogueChoices(
            new DialogueChoiceComponent(this.buildDialogueChoice("question"), button -> this.finishChat((byte)0)),
            new DialogueChoiceComponent(
               this.buildDialogueChoice("challenge"),
               button -> {
                  this.setDialogueAnswer(Component.translatable("gui.aether.queen.dialog.challenge"));
                  int medals = this.getMinecraft().player.getInventory().countItem((Item)AetherItems.VICTORY_MEDAL.get());
                  DialogueChoiceComponent startFightChoice = medals >= 10
                     ? new DialogueChoiceComponent(this.buildDialogueChoice("have_medals"), button1 -> this.finishChat((byte)1))
                     : new DialogueChoiceComponent(this.buildDialogueChoice("no_medals").append(" (" + medals + "/10)"), button1 -> this.finishChat((byte)1));
                  this.setupDialogueChoices(
                     startFightChoice, new DialogueChoiceComponent(this.buildDialogueChoice("deny_fight"), button1 -> this.finishChat((byte)2))
                  );
               }
            ),
            new DialogueChoiceComponent(this.buildDialogueChoice("leave"), pButton -> this.finishChat((byte)3))
         );
         this.positionDialogue();
      }
   }

   public void setupDialogueChoices(DialogueChoiceComponent... options) {
      this.clearWidgets();

      for (DialogueChoiceComponent option : options) {
         this.addRenderableWidget(option);
      }

      this.positionDialogue();
   }

   private void positionDialogue() {
      this.dialogueAnswer.reposition(this.width, this.height);
      int lineNumber = this.dialogueAnswer.height / 12 + 1;

      for (Renderable renderable : this.renderables) {
         if (renderable instanceof DialogueChoiceComponent option) {
            option.setX(this.width / 2 - option.getWidth() / 2);
            option.setY(this.height / 2 + 12 * lineNumber);
            lineNumber++;
         }
      }
   }

   private void setDialogueAnswer(Component component) {
      this.dialogueAnswer.updateDialogue(this.buildDialogueAnswerName(this.valkyrieQueen.getBossName()).append(": ").append(component));
   }

   public MutableComponent buildDialogueAnswerName(Component component) {
      return Component.literal("[").append(component.copy().withStyle(ChatFormatting.YELLOW)).append("]");
   }

   public MutableComponent buildDialogueChoice(String key) {
      return Component.translatable("gui.aether.player.dialog." + key);
   }

   private void finishChat(byte interactionID) {
      PacketDistributor.sendToServer(new NpcPlayerInteractPacket(this.valkyrieQueen.getId(), interactionID), new CustomPacketPayload[0]);
      super.onClose();
   }

   public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
      this.renderBackground(guiGraphics, mouseX, mouseY, partialTicks);
      this.dialogueAnswer.render(guiGraphics);
      super.render(guiGraphics, mouseX, mouseY, partialTicks);
   }

   public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
   }

   public void resize(Minecraft minecraft, int width, int height) {
      this.width = width;
      this.height = height;
      this.positionDialogue();
   }

   public boolean isPauseScreen() {
      return false;
   }

   public void onClose() {
      this.finishChat((byte)3);
   }
}
