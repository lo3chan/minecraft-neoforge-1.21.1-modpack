package corgitaco.corgilib.shadow.blue.endless.jankson.impl;

import corgitaco.corgilib.shadow.blue.endless.jankson.JsonElement;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AnnotatedElement {
   protected String comment;
   protected JsonElement elem;

   public AnnotatedElement(@Nonnull JsonElement elem, @Nullable String comment) {
      this.comment = comment;
      this.elem = elem;
   }

   @Nullable
   public String getComment() {
      return this.comment;
   }

   @Nonnull
   public JsonElement getElement() {
      return this.elem;
   }
}
