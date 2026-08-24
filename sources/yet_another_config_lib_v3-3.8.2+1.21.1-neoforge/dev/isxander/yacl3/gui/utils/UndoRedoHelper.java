package dev.isxander.yacl3.gui.utils;

import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.Nullable;

public class UndoRedoHelper {
   private final List<UndoRedoHelper.FieldState> history = new ArrayList<>();
   private int index = 0;

   public UndoRedoHelper(String text, int cursorPos, int selectionLength) {
      this.history.add(new UndoRedoHelper.FieldState(text, cursorPos, selectionLength));
   }

   public void save(String text, int cursorPos, int selectionLength) {
      int max = this.history.size();
      this.history.subList(this.index, max).clear();
      this.history.add(new UndoRedoHelper.FieldState(text, cursorPos, selectionLength));
      this.index++;
   }

   @Nullable
   public UndoRedoHelper.FieldState undo() {
      this.index--;
      this.index = Math.max(this.index, 0);
      return this.history.isEmpty() ? null : this.history.get(this.index);
   }

   @Nullable
   public UndoRedoHelper.FieldState redo() {
      if (this.index < this.history.size() - 1) {
         this.index++;
         return this.history.get(this.index);
      } else {
         return null;
      }
   }

   public record FieldState(String text, int cursorPos, int selectionLength) {
   }
}
