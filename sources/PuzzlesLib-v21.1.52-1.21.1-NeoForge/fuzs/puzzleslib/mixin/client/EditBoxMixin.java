package fuzs.puzzleslib.mixin.client;

import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({EditBox.class})
abstract class EditBoxMixin extends AbstractWidget {
   @Shadow
   @Final
   private Font font;
   @Shadow
   private String value;
   @Shadow
   private boolean bordered;
   @Shadow
   private int displayPos;
   @Shadow
   private int cursorPos;
   @Shadow
   private int highlightPos;
   @Unique
   private long puzzleslib$lastClickTime;
   @Unique
   private boolean puzzleslib$doubleClick;
   @Unique
   private int puzzleslib$doubleClickHighlightPos;
   @Unique
   private int puzzleslib$doubleClickCursorPos;

   public EditBoxMixin(int x, int y, int width, int height, Component message) {
      super(x, y, width, height, message);
   }

   @Inject(
      method = {"deleteText(I)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   protected void deleteText(int charCount, CallbackInfo callback) {
      if (Screen.hasControlDown()) {
         if (charCount < 0) {
            this.deleteChars(-this.cursorPos);
         }
      } else if (Screen.hasAltDown()) {
         this.deleteWords(charCount);
      } else {
         this.deleteChars(charCount);
      }

      callback.cancel();
   }

   @Shadow
   public abstract void deleteWords(int var1);

   @Shadow
   public abstract void deleteChars(int var1);

   @Shadow
   public abstract int getWordPosition(int var1);

   @Shadow
   protected abstract int getWordPosition(int var1, int var2, boolean var3);

   @Inject(
      method = {"getWordPosition(IIZ)I"},
      at = {@At("HEAD")},
      cancellable = true
   )
   protected void getWordPosition(int numWords, int pos, boolean skipConsecutiveSpaces, CallbackInfoReturnable<Integer> callback) {
      int i = pos;
      boolean backwards = numWords < 0;
      int skippedWords = Math.abs(numWords);

      for (int k = 0; k < skippedWords; k++) {
         if (!backwards) {
            int l;
            for (l = this.value.length(); skipConsecutiveSpaces && i == pos && i < l && !puzzleslib$isWordChar(this.value.charAt(i)); pos++) {
               i++;
            }

            while (i < l && puzzleslib$isWordChar(this.value.charAt(i))) {
               i++;
            }
         } else {
            while (skipConsecutiveSpaces && i == pos && i > 0 && !puzzleslib$isWordChar(this.value.charAt(i - 1))) {
               i--;
               pos--;
            }

            while (i > 0 && puzzleslib$isWordChar(this.value.charAt(i - 1))) {
               i--;
            }
         }
      }

      callback.setReturnValue(i);
   }

   @Unique
   private static boolean puzzleslib$isWordChar(char charAt) {
      return charAt == '_' || Character.isAlphabetic(charAt) || Character.isDigit(charAt);
   }

   @Inject(
      method = {"keyPressed"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> callback) {
      if (this.isActive() && this.isFocused()) {
         if (keyCode == 262) {
            boolean allowedToMoveRight = true;
            if (!Screen.hasShiftDown() && this.highlightPos != this.cursorPos) {
               this.setCursorPosition(Math.max(this.getCursorPosition(), this.highlightPos));
               this.setHighlightPos(this.getCursorPosition());
               allowedToMoveRight = false;
            }

            if (Screen.hasControlDown()) {
               this.moveCursorToEnd(Screen.hasShiftDown());
            } else if (Screen.hasAltDown()) {
               this.moveCursorTo(this.getWordPosition(1), Screen.hasShiftDown());
            } else if (allowedToMoveRight) {
               this.moveCursor(1, Screen.hasShiftDown());
            }

            callback.setReturnValue(true);
         } else if (keyCode == 263) {
            boolean allowedToMoveLeft = true;
            if (!Screen.hasShiftDown() && this.highlightPos != this.cursorPos) {
               this.setCursorPosition(Math.min(this.getCursorPosition(), this.highlightPos));
               this.setHighlightPos(this.getCursorPosition());
               allowedToMoveLeft = false;
            }

            if (Screen.hasControlDown()) {
               this.moveCursorToStart(Screen.hasShiftDown());
            } else if (Screen.hasAltDown()) {
               this.moveCursorTo(this.getWordPosition(-1), Screen.hasShiftDown());
            } else if (allowedToMoveLeft) {
               this.moveCursor(-1, Screen.hasShiftDown());
            }

            callback.setReturnValue(true);
         }
      }
   }

   @Shadow
   public abstract void moveCursor(int var1, boolean var2);

   @Shadow
   public abstract void moveCursorTo(int var1, boolean var2);

   @Shadow
   public abstract void setCursorPosition(int var1);

   @Shadow
   public abstract void moveCursorToStart(boolean var1);

   @Shadow
   public abstract void moveCursorToEnd(boolean var1);

   @Shadow
   public abstract int getCursorPosition();

   @Shadow
   public abstract int getInnerWidth();

   @Shadow
   public abstract void setHighlightPos(int var1);

   @Inject(
      method = {"onClick"},
      at = {@At("TAIL")}
   )
   public void onClick(double mouseX, double mouseY, CallbackInfo callback) {
      long millis = Util.getMillis();
      boolean tripleClick = this.puzzleslib$doubleClick;
      this.puzzleslib$doubleClick = millis - this.puzzleslib$lastClickTime < 250L;
      if (this.puzzleslib$doubleClick) {
         if (tripleClick) {
            this.moveCursorToEnd(false);
            this.setHighlightPos(0);
         } else {
            this.puzzleslib$doubleClickHighlightPos = this.getWordPosition(1, this.getCursorPosition(), false);
            this.moveCursorTo(this.puzzleslib$doubleClickHighlightPos, false);
            this.puzzleslib$doubleClickCursorPos = this.getWordPosition(-1, this.getCursorPosition(), false);
            this.moveCursorTo(this.puzzleslib$doubleClickCursorPos, true);
         }
      }

      this.puzzleslib$lastClickTime = millis;
   }

   protected void onDrag(double mouseX, double mouseY, double dragX, double dragY) {
      int i = Mth.floor(mouseX) - this.getX();
      if (this.bordered) {
         i -= 4;
      }

      String string = this.font.plainSubstrByWidth(this.value.substring(this.displayPos), this.getInnerWidth());
      int mousePosition = this.font.plainSubstrByWidth(string, i).length() + this.displayPos;
      if (this.puzzleslib$doubleClick) {
         if (this.clicked(mouseX, mouseY)) {
            int rightBoundary = this.getWordPosition(1, mousePosition, false);
            this.moveCursorTo(Math.max(this.puzzleslib$doubleClickHighlightPos, rightBoundary), false);
            int leftBoundary = this.getWordPosition(-1, mousePosition, false);
            this.moveCursorTo(Math.min(this.puzzleslib$doubleClickCursorPos, leftBoundary), true);
         } else {
            if (mousePosition > this.puzzleslib$doubleClickHighlightPos) {
               this.moveCursorToEnd(false);
            } else {
               this.moveCursorTo(this.puzzleslib$doubleClickHighlightPos, false);
            }

            if (mousePosition < this.puzzleslib$doubleClickCursorPos) {
               this.moveCursorToStart(true);
            } else {
               this.moveCursorTo(this.puzzleslib$doubleClickCursorPos, true);
            }
         }
      } else if (this.clicked(mouseX, mouseY)) {
         this.moveCursorTo(mousePosition, true);
      } else if (this.highlightPos < mousePosition) {
         this.moveCursorToEnd(true);
      } else {
         this.moveCursorToStart(true);
      }
   }
}
