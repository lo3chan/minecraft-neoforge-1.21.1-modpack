package com.alonie.brbe.book;

import com.alonie.brbe.config.AppContext;
import com.alonie.brbe.layout.BookGeometry;
import com.alonie.brbe.layout.BookLayout;

public final class RecipeBook {
   private final RecipeBookType type;
   private final BookLayout layout;
   private final AppContext ctx;
   private BookGeometry geometry;
   private boolean visible;

   RecipeBook(RecipeBookType type, AppContext ctx) {
      this.type = type;
      this.ctx = ctx;
      this.layout = ctx.bookLayout();
      this.visible = false;
   }

   public void layout(BookLayout.Rect available) {
      this.geometry = this.layout.compute(available, this.ctx.config().keepCentered, this.ctx.config().expandedRecipeBook);
   }

   public BookGeometry geometry() {
      return this.geometry;
   }

   public boolean isVisible() {
      return this.visible;
   }

   public void setVisible(boolean visible) {
      this.visible = visible;
   }

   public void toggleVisibility() {
      this.visible = !this.visible;
   }

   public RecipeBookType type() {
      return this.type;
   }

   public AppContext ctx() {
      return this.ctx;
   }
}
