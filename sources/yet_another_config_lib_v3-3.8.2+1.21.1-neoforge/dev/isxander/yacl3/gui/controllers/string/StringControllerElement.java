package dev.isxander.yacl3.gui.controllers.string;

import dev.isxander.yacl3.api.OptionEventListener;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.ControllerWidget;
import dev.isxander.yacl3.gui.utils.GuiUtils;
import dev.isxander.yacl3.gui.utils.KeyUtils;
import dev.isxander.yacl3.gui.utils.UndoRedoHelper;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class StringControllerElement extends ControllerWidget<IStringController<?>> {
   protected final boolean instantApply;
   protected String inputField;
   protected Dimension<Integer> inputFieldBounds;
   protected boolean inputFieldFocused;
   protected int caretPos;
   protected int previousCaretPos;
   protected int selectionLength;
   protected int renderOffset;
   protected UndoRedoHelper undoRedoHelper;
   protected float ticks;
   protected float caretTicks;
   private final Component emptyText;

   public StringControllerElement(IStringController<?> control, YACLScreen screen, Dimension<Integer> dim, boolean instantApply) {
      super(control, screen, dim);
      this.instantApply = instantApply;
      this.inputField = control.getString();
      this.inputFieldFocused = false;
      this.selectionLength = 0;
      this.emptyText = Component.literal("Click to type...").withStyle(ChatFormatting.GRAY);
      control.option().addEventListener((opt, event) -> {
         if (event == OptionEventListener.Event.STATE_CHANGE) {
            this.inputField = control.getString();
         }
      });
      this.setDimension(dim);
   }

   @Override
   protected void drawHoveredControl(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
   }

   @Override
   protected void drawValueText(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
      Component valueText = this.getValueText();
      if (!this.isHovered()) {
         valueText = Component.literal(GuiUtils.shortenString(valueText.getString(), this.textRenderer, this.getMaxUnwrapLength(), "..."))
            .setStyle(valueText.getStyle());
      }

      int textX = this.getDimension().xLimit() - this.textRenderer.width(valueText) + this.renderOffset - this.getXPadding();
      graphics.enableScissor(this.inputFieldBounds.x(), this.inputFieldBounds.y() - 2, this.inputFieldBounds.xLimit() + 1, this.inputFieldBounds.yLimit() + 4);
      graphics.drawString(this.textRenderer, valueText, textX, this.getTextY(), this.getValueColor(), true);
      if (this.isHovered()) {
         this.ticks += delta;
         String text = this.getValueText().getString();
         graphics.fill(this.inputFieldBounds.x(), this.inputFieldBounds.yLimit(), this.inputFieldBounds.xLimit(), this.inputFieldBounds.yLimit() + 1, -1);
         graphics.fill(
            this.inputFieldBounds.x() + 1,
            this.inputFieldBounds.yLimit() + 1,
            this.inputFieldBounds.xLimit() + 1,
            this.inputFieldBounds.yLimit() + 2,
            -12566464
         );
         if (this.inputFieldFocused || this.focused) {
            if (this.caretPos > text.length()) {
               this.caretPos = text.length();
            }

            int caretX = textX + this.textRenderer.width(text.substring(0, this.caretPos));
            if (text.isEmpty()) {
               caretX = this.inputFieldBounds.x() + this.inputFieldBounds.width() / 2;
            }

            if (this.selectionLength != 0) {
               int selectionX = textX + this.textRenderer.width(text.substring(0, this.caretPos + this.selectionLength));
               graphics.fill(caretX, this.inputFieldBounds.y() - 2, selectionX, this.inputFieldBounds.yLimit() - 1, -2144325377);
            }

            if (this.caretPos != this.previousCaretPos) {
               this.previousCaretPos = this.caretPos;
               this.caretTicks = 0.0F;
            }

            if ((this.caretTicks += delta) % 20.0F <= 10.0F) {
               graphics.fill(caretX, this.inputFieldBounds.y() - 2, caretX + 1, this.inputFieldBounds.yLimit() - 1, -1);
            }
         }
      }

      graphics.disableScissor();
   }

   private boolean isHoveredInputField(double mouseX, double mouseY) {
      return this.inputFieldBounds.isPointInside((int)mouseX, (int)mouseY);
   }

   @Override
   public boolean onMouseClicked(double mouseX, double mouseY, int button) {
      if (this.isAvailable() && this.getDimension().isPointInside((int)mouseX, (int)mouseY)) {
         this.inputFieldFocused = true;
         if (!this.isHoveredInputField(mouseX, mouseY)) {
            this.caretPos = this.getDefaultCaretPos();
         } else {
            int textX = (int)mouseX - (this.inputFieldBounds.xLimit() - this.textRenderer.width(this.getValueText()));
            int pos = -1;
            int currentWidth = 0;

            for (char ch : this.inputField.toCharArray()) {
               pos++;
               int charLength = this.textRenderer.width(String.valueOf(ch));
               if (currentWidth + charLength / 2 > textX) {
                  this.caretPos = pos;
                  break;
               }

               if (pos == this.inputField.length() - 1) {
                  this.caretPos = pos + 1;
               }

               currentWidth += charLength;
            }

            this.selectionLength = 0;
         }

         return true;
      } else {
         this.unfocus();
         return false;
      }
   }

   protected int getDefaultCaretPos() {
      return this.inputField.length();
   }

   @Override
   public boolean onKeyPressed(int keyCode, int scanCode, int modifiers) {
      if (!this.inputFieldFocused) {
         return false;
      } else {
         switch (keyCode) {
            case 256:
            case 257:
               this.unfocus();
               return true;
            case 258:
            case 260:
            case 264:
            case 265:
            case 266:
            case 267:
            default:
               if (KeyUtils.isPaste(keyCode, modifiers)) {
                  return this.doPaste();
               } else if (KeyUtils.isCopy(keyCode, modifiers)) {
                  return this.doCopy();
               } else if (KeyUtils.isCut(keyCode, modifiers)) {
                  return this.doCut();
               } else {
                  if (KeyUtils.isSelectAll(keyCode, modifiers)) {
                     return this.doSelectAll();
                  }

                  return false;
               }
            case 259:
               this.doBackspace();
               return true;
            case 261:
               this.doDelete();
               return true;
            case 262:
               if (KeyUtils.hasShiftDown(modifiers)) {
                  if (KeyUtils.hasControlDown(modifiers)) {
                     int spaceChar = this.findSpaceIndex(false);
                     this.selectionLength = this.selectionLength - (spaceChar - this.caretPos);
                     this.caretPos = spaceChar;
                  } else if (this.caretPos < this.inputField.length()) {
                     this.caretPos++;
                     this.selectionLength--;
                  }

                  this.checkRenderOffset();
               } else {
                  if (this.caretPos < this.inputField.length()) {
                     if (KeyUtils.hasControlDown(modifiers)) {
                        this.caretPos = this.findSpaceIndex(false);
                     } else if (this.selectionLength != 0) {
                        this.caretPos = this.caretPos + Math.max(this.selectionLength, 0);
                     } else {
                        this.caretPos++;
                     }

                     this.checkRenderOffset();
                  }

                  this.selectionLength = 0;
               }

               return true;
            case 263:
               if (KeyUtils.hasShiftDown(modifiers)) {
                  if (KeyUtils.hasControlDown(modifiers)) {
                     int spaceChar = this.findSpaceIndex(true);
                     this.selectionLength = this.selectionLength + (this.caretPos - spaceChar);
                     this.caretPos = spaceChar;
                  } else if (this.caretPos > 0) {
                     this.caretPos--;
                     this.selectionLength++;
                  }

                  this.checkRenderOffset();
               } else {
                  if (this.caretPos > 0) {
                     if (KeyUtils.hasControlDown(modifiers)) {
                        this.caretPos = this.findSpaceIndex(true);
                     } else if (this.selectionLength != 0) {
                        this.caretPos = this.caretPos + Math.min(this.selectionLength, 0);
                     } else {
                        this.caretPos--;
                     }
                  }

                  this.checkRenderOffset();
                  this.selectionLength = 0;
               }

               return true;
            case 268:
               if (KeyUtils.hasShiftDown(modifiers)) {
                  this.selectionLength = this.selectionLength + this.caretPos;
                  this.caretPos = 0;
               } else {
                  this.caretPos = 0;
                  this.selectionLength = 0;
               }

               this.checkRenderOffset();
               return true;
            case 269:
               if (KeyUtils.hasShiftDown(modifiers)) {
                  this.selectionLength = this.selectionLength - (this.inputField.length() - this.caretPos);
               } else {
                  this.selectionLength = 0;
               }

               this.caretPos = this.inputField.length();
               this.checkRenderOffset();
               return true;
         }
      }
   }

   protected boolean doPaste() {
      this.write(this.client.keyboardHandler.getClipboard());
      this.updateUndoHistory();
      return true;
   }

   protected boolean doCopy() {
      if (this.selectionLength != 0) {
         this.client.keyboardHandler.setClipboard(this.getSelection());
         return true;
      } else {
         return false;
      }
   }

   protected boolean doCut() {
      if (this.selectionLength != 0) {
         this.client.keyboardHandler.setClipboard(this.getSelection());
         this.write("");
         this.updateUndoHistory();
         return true;
      } else {
         return false;
      }
   }

   protected boolean doSelectAll() {
      this.caretPos = this.inputField.length();
      this.checkRenderOffset();
      this.selectionLength = -this.caretPos;
      return true;
   }

   protected void checkRenderOffset() {
      if (this.textRenderer.width(this.inputField) < this.getUnshiftedLength()) {
         this.renderOffset = 0;
      } else {
         int textX = this.getDimension().xLimit() - this.textRenderer.width(this.inputField) - this.getXPadding();
         int caretX = textX + this.textRenderer.width(this.inputField.substring(0, this.caretPos));
         int minX = this.getDimension().xLimit() - this.getXPadding() - this.getUnshiftedLength();
         int maxX = minX + this.getUnshiftedLength();
         if (caretX + this.renderOffset < minX) {
            this.renderOffset = minX - caretX;
         } else if (caretX + this.renderOffset > maxX) {
            this.renderOffset = maxX - caretX;
         }
      }
   }

   @Override
   public boolean onCharTyped(char ch, String cpStr, int modifiers) {
      if (!this.inputFieldFocused) {
         return false;
      } else if (!KeyUtils.hasControlDown(modifiers)) {
         this.write(cpStr);
         this.updateUndoHistory();
         return true;
      } else {
         return false;
      }
   }

   protected void doBackspace() {
      if (this.selectionLength != 0) {
         this.write("");
      } else if (this.caretPos > 0 && this.modifyInput(builder -> builder.deleteCharAt(this.caretPos - 1))) {
         this.caretPos--;
         this.checkRenderOffset();
      }

      this.updateUndoHistory();
   }

   protected void doDelete() {
      if (this.selectionLength != 0) {
         this.write("");
      } else if (this.caretPos < this.inputField.length()) {
         this.modifyInput(builder -> builder.deleteCharAt(this.caretPos));
      }

      this.updateUndoHistory();
   }

   public void write(String string) {
      if (this.selectionLength == 0) {
         if (this.modifyInput(builder -> builder.insert(this.caretPos, string))) {
            this.caretPos = this.caretPos + string.length();
            this.checkRenderOffset();
         }
      } else {
         int start = this.getSelectionStart();
         int end = this.getSelectionEnd();
         if (this.modifyInput(builder -> builder.replace(start, end, string))) {
            this.caretPos = start + string.length();
            this.selectionLength = 0;
            this.checkRenderOffset();
         }
      }
   }

   public boolean modifyInput(Consumer<StringBuilder> consumer) {
      StringBuilder temp = new StringBuilder(this.inputField);
      consumer.accept(temp);
      if (!this.control.isInputValid(temp.toString())) {
         return false;
      } else {
         this.inputField = temp.toString();
         if (this.instantApply) {
            this.updateControl();
         }

         return true;
      }
   }

   protected void updateUndoHistory() {
   }

   public int getUnshiftedLength() {
      return this.optionNameString.isEmpty() ? this.getDimension().width() - this.getXPadding() * 2 : this.getDimension().width() / 8 * 5;
   }

   public int getMaxUnwrapLength() {
      return this.optionNameString.isEmpty() ? this.getDimension().width() - this.getXPadding() * 2 : this.getDimension().width() / 2;
   }

   public int getSelectionStart() {
      return Math.min(this.caretPos, this.caretPos + this.selectionLength);
   }

   public int getSelectionEnd() {
      return Math.max(this.caretPos, this.caretPos + this.selectionLength);
   }

   protected String getSelection() {
      return this.inputField.substring(this.getSelectionStart(), this.getSelectionEnd());
   }

   protected int findSpaceIndex(boolean reverse) {
      int fromIndex = this.caretPos;
      int i;
      if (reverse) {
         if (this.caretPos > 0) {
            fromIndex -= 2;
         }

         i = this.inputField.lastIndexOf(" ", fromIndex) + 1;
      } else {
         if (this.caretPos < this.inputField.length()) {
            fromIndex++;
         }

         i = this.inputField.indexOf(" ", fromIndex) + 1;
         if (i == 0) {
            i = this.inputField.length();
         }
      }

      return i;
   }

   @Override
   public void setFocused(boolean focused) {
      super.setFocused(focused);
      this.inputFieldFocused = focused;
   }

   @Override
   public void unfocus() {
      super.unfocus();
      this.inputFieldFocused = false;
      this.renderOffset = 0;
      if (!this.instantApply) {
         this.updateControl();
      }
   }

   @Override
   public void setDimension(Dimension<Integer> dim) {
      super.setDimension(dim);
      int width = Math.max(6, Math.min(this.textRenderer.width(this.getValueText()), this.getUnshiftedLength()));
      this.inputFieldBounds = Dimension.ofInt(dim.xLimit() - this.getXPadding() - width, dim.centerY() - 9 / 2, width, 9);
   }

   @Override
   public boolean isHovered() {
      return super.isHovered() || this.inputFieldFocused;
   }

   protected void updateControl() {
      this.control.setFromString(this.inputField);
   }

   @Override
   protected int getUnhoveredControlWidth() {
      return !this.isHovered() ? Math.min(this.getHoveredControlWidth(), this.getMaxUnwrapLength()) : this.getHoveredControlWidth();
   }

   @Override
   protected int getHoveredControlWidth() {
      return Math.min(this.textRenderer.width(this.getValueText()), this.getUnshiftedLength());
   }

   @Override
   protected Component getValueText() {
      if (!this.inputFieldFocused && this.inputField.isEmpty()) {
         return this.emptyText;
      } else {
         return (Component)(!this.instantApply && this.inputFieldFocused ? Component.literal(this.inputField) : this.control.formatValue());
      }
   }
}
