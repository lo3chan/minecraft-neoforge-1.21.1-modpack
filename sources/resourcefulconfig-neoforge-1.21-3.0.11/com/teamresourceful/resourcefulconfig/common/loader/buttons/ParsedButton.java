package com.teamresourceful.resourcefulconfig.common.loader.buttons;

import com.teamresourceful.resourcefulconfig.api.annotations.Comment;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigButton;
import com.teamresourceful.resourcefulconfig.api.types.ResourcefulConfigButton;
import com.teamresourceful.resourcefulconfig.api.types.options.Position;
import java.lang.reflect.Field;
import net.minecraft.Optionull;

public record ParsedButton(String title, String description, String target, Position position, Runnable runnable, String text)
   implements ResourcefulConfigButton {
   public static ParsedButton of(Field field, String after) {
      ConfigButton button = field.getAnnotation(ConfigButton.class);
      String description = (String)Optionull.mapOrDefault(field.getAnnotation(Comment.class), Comment::value, "");

      try {
         Runnable runnable = (Runnable)field.get(null);
         return new ParsedButton(button.title(), description, after, Position.AFTER, runnable, button.text());
      } catch (IllegalAccessException var5) {
         throw new RuntimeException(var5);
      }
   }

   @Override
   public boolean invoke() {
      try {
         this.runnable.run();
         return true;
      } catch (Exception var2) {
         return false;
      }
   }
}
