package net.mehvahdjukaar.amendments.client.gui;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.font.TextFieldHelper.CursorStep;
import org.jetbrains.annotations.Nullable;

public class StyledTextFieldHelper extends TextFieldHelper {
   private static final char TOKEN = '§';
   private final Supplier<String> getMessageFn;

   public StyledTextFieldHelper(
      Supplier<String> supplier, Consumer<String> consumer, Supplier<String> supplier2, Consumer<String> consumer2, Predicate<String> predicate
   ) {
      super(supplier, consumer, supplier2, consumer2, predicate);
      this.getMessageFn = supplier;
   }

   public void removeFromCursor(int direction, CursorStep cursorStep) {
      String msg = this.getMessageFn.get();
      int cursorPos = this.getCursorPos();
      boolean hasTokenAtCursor = cursorPos < msg.length() && msg.charAt(cursorPos) == 167;
      if (direction < 0) {
         int k = getIndexBeforeToken(direction, msg, cursorPos);
         if (cursorPos == msg.length() || hasTokenAtCursor) {
            direction = k;
         }

         super.removeFromCursor(direction, cursorStep);
         if (k != direction) {
            this.moveBy(k + 1, false, cursorStep);
         }
      } else if (hasTokenAtCursor) {
         this.moveBy(direction, false, CursorStep.CHARACTER);
         this.removeFromCursor(-1, cursorStep);
      } else {
         super.removeFromCursor(direction, cursorStep);
      }
   }

   private static int getIndexBeforeToken(int i, String msg, int cursorPos) {
      int p = cursorPos - 3;
      if (p >= 0 && msg.length() > p && msg.charAt(p) == 167) {
         i = -3;
         int p1 = cursorPos - 5;
         if (p1 >= 0 && msg.charAt(p1) == 167) {
            i = -5;
         }
      }

      return i;
   }

   public void moveBy(int i, boolean keepSelection, CursorStep cursorStep) {
      String msg = this.getMessageFn.get();
      int cursorPos = this.getCursorPos();
      if (i < 0) {
         i = getIndexBeforeToken(i, msg, cursorPos);
         super.moveBy(i, keepSelection, cursorStep);
      } else {
         if (cursorPos < msg.length() && msg.charAt(cursorPos) == 167) {
            i = 3;
            int p = cursorPos + 2;
            if (p < msg.length() && msg.charAt(p) == 167) {
               i = 5;
            }
         }

         super.moveBy(i, keepSelection, CursorStep.CHARACTER);
      }
   }

   public void insertStyledText(String text, ChatFormatting color, ChatFormatting style) {
      String currentMod = this.getModifier(color, style);
      String lastMod = this.getPreviousModifier();
      if (!Objects.equals(currentMod, lastMod)) {
         String s = currentMod + text;
         this.insertText(s);
         int j = this.getCursorPos();
         if (this.getCursorPos() != this.getMessageFn.get().length() && lastMod != null) {
            this.insertText(lastMod);
            super.setCursorPos(j, false);
         }
      } else {
         this.insertText(text);
      }
   }

   public void setCursorPos(int textIndex, boolean keepSelection) {
      String text = this.getMessageFn.get();
      super.setCursorPos(textIndex, keepSelection);
   }

   private String getModifier(ChatFormatting color, ChatFormatting style) {
      String s = color.toString();
      if (style != ChatFormatting.RESET) {
         s = s + style.toString();
      }

      return s.replace('§', '§');
   }

   @Nullable
   private String getPreviousModifier() {
      String text = this.getMessageFn.get();
      int cursorPos = this.getCursorPos() - 1;

      for (int i = cursorPos; i >= 0 && i < text.length(); i--) {
         if (text.charAt(i) == 167) {
            int start = i;
            int end = i + 2;
            if (i >= 2 && text.charAt(i - 2) == 167) {
               start = i - 2;
            }

            if (end <= text.length()) {
               return text.substring(start, end);
            }
         }
      }

      return null;
   }

   public void formatSelected(@Nullable ChatFormatting ink, @Nullable ChatFormatting quill) {
   }
}
