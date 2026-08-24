package com.teamresourceful.resourcefulconfig.common.jsonc;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class JsoncPrimitive implements JsoncElement {
   private String comment = "";
   private final JsonElement element;

   public JsoncPrimitive(JsonElement element) {
      this.element = element;
   }

   public JsoncPrimitive(Number element) {
      this(new JsonPrimitive(element));
   }

   public JsoncPrimitive(boolean element) {
      this(new JsonPrimitive(element));
   }

   public JsoncPrimitive(String element) {
      this(new JsonPrimitive(element));
   }

   @Override
   public void comment(String comment) {
      this.comment = comment;
   }

   @Override
   public String toString(int indentation) {
      return this.element.toString();
   }

   @Override
   public String comment() {
      return this.comment;
   }

   @Override
   public String toString() {
      return this.toString(0);
   }
}
