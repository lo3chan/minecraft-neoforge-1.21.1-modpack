package com.teamresourceful.resourcefulconfig.common.jsonc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class JsoncArray implements JsoncElement, Iterable<JsoncElement> {
   private final List<JsoncElement> elements = new ArrayList<>();
   private String comment = "";

   @Override
   public void comment(String comment) {
      this.comment = comment;
   }

   @Override
   public String comment() {
      return this.comment;
   }

   public JsoncArray add(JsoncElement element) {
      this.elements.add(element);
      return this;
   }

   public JsoncArray remove(JsoncElement element) {
      this.elements.remove(element);
      return this;
   }

   public JsoncArray remove(int index) {
      this.elements.remove(index);
      return this;
   }

   @Override
   public String toString(int indentation) {
      if (this.elements.isEmpty()) {
         return "[]";
      } else {
         StringBuilder builder = new StringBuilder("[\n");

         for (JsoncElement element : this.elements) {
            JsoncElement.writeComment(builder, element, indentation + 1);
            builder.append(INDENT.repeat(indentation + 1));
            builder.append(element.toString(indentation + 1));
            builder.append(",\n");
         }

         builder.deleteCharAt(builder.length() - 2);
         builder.append(INDENT.repeat(indentation)).append("]");
         return builder.toString();
      }
   }

   @NotNull
   @Override
   public Iterator<JsoncElement> iterator() {
      return this.elements.iterator();
   }

   @Override
   public String toString() {
      return this.toString(0);
   }
}
