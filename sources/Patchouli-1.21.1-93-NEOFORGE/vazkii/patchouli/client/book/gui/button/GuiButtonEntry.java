package vazkii.patchouli.client.book.gui.button;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Button.OnPress;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import vazkii.patchouli.client.base.ClientTicker;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.gui.GuiBook;

public class GuiButtonEntry extends Button {
   private static final int ANIM_TIME = 5;
   private final GuiBook parent;
   private final BookEntry entry;
   private float timeHovered;

   public GuiButtonEntry(GuiBook parent, int x, int y, BookEntry entry, OnPress onPress) {
      super(x, y, 116, 10, entry.getName(), onPress, DEFAULT_NARRATION);
      this.parent = parent;
      this.entry = entry;
   }

   protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      if (this.active) {
         if (this.isHoveredOrFocused()) {
            this.timeHovered = Math.min(5.0F, this.timeHovered + ClientTicker.delta);
         } else {
            this.timeHovered = Math.max(0.0F, this.timeHovered - ClientTicker.delta);
         }

         float time = Math.max(0.0F, Math.min(5.0F, this.timeHovered + (this.isHoveredOrFocused() ? partialTicks : -partialTicks)));
         float widthFract = time / 5.0F;
         boolean locked = this.entry.isLocked();
         graphics.pose().scale(0.5F, 0.5F, 0.5F);
         graphics.fill(this.getX() * 2, this.getY() * 2, (this.getX() + (int)(this.width * widthFract)) * 2, (this.getY() + this.height) * 2, 570425344);
         RenderSystem.enableBlend();
         if (locked) {
            graphics.setColor(1.0F, 1.0F, 1.0F, 0.7F);
            GuiBook.drawLock(graphics, this.parent.book, this.getX() * 2 + 2, this.getY() * 2 + 2);
         } else {
            this.entry.getIcon().render(graphics, this.getX() * 2 + 2, this.getY() * 2 + 2);
         }

         graphics.pose().scale(2.0F, 2.0F, 2.0F);
         MutableComponent name;
         if (locked) {
            name = Component.translatable("patchouli.gui.lexicon.locked");
         } else {
            name = this.entry.getName();
            if (this.entry.isPriority()) {
               name = name.withStyle(ChatFormatting.ITALIC);
            }
         }

         name = name.withStyle(this.entry.getBook().getFontStyle());
         graphics.drawString(Minecraft.getInstance().font, name, this.getX() + 12, this.getY(), this.getColor(), false);
         if (!this.entry.isLocked()) {
            GuiBook.drawMarking(graphics, this.parent.book, this.getX() + this.width - 5, this.getY() + 1, this.entry.hashCode(), this.entry.getReadState());
         }
      }
   }

   private int getColor() {
      if (this.entry.isSecret()) {
         return -1442840576 | this.parent.book.textColor & 16777215;
      } else {
         return this.entry.isLocked() ? 1996488704 | this.parent.book.textColor & 16777215 : this.entry.getEntryColor();
      }
   }

   public void playDownSound(SoundManager soundHandlerIn) {
      if (this.entry != null && !this.entry.isLocked()) {
         GuiBook.playBookFlipSound(this.parent.book);
      }
   }

   public BookEntry getEntry() {
      return this.entry;
   }
}
