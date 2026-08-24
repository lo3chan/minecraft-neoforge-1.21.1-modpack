package dev.latvian.mods.kubejs.text.action;

import dev.latvian.mods.rhino.util.HideFromJS;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.chat.Component;

public class TextActionBuilder {
   @HideFromJS
   public List<TextAction> actions = new ArrayList<>(1);

   public void dynamic(String id) {
      this.actions.add(new DynamicTextAction(id));
   }

   public void add(List<Component> text) {
      this.actions.add(new AddTextAction(text));
   }

   public void insert(int line, List<Component> text) {
      this.actions.add(new InsertTextAction(line, text));
   }

   public void removeLine(int line) {
      this.actions.add(new RemoveLineTextAction(line));
   }

   public void removeText(Component match) {
      this.actions.add(new RemoveTextTextAction(match));
   }

   public void removeExactText(Component match) {
      this.actions.add(new RemoveExactTextTextAction(match));
   }

   public void clear() {
      this.actions.add(ClearTextAction.INSTANCE);
   }
}
