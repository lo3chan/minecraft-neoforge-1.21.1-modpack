package com.github.alexthe666.citadel.client.gui.data;

import net.minecraft.world.item.ItemStack;

public class LinkData {
   private String linked_page;
   private String text;
   private int x;
   private int y;
   private int page;
   private ItemStack stack;

   public LinkData(String linkedPage, String titleText, int x, int y, int page) {
      this(linkedPage, titleText, x, y, page, ItemStack.EMPTY);
   }

   public LinkData(String linkedPage, String titleText, int x, int y, int page, ItemStack stack) {
      this.linked_page = linkedPage;
      this.text = titleText;
      this.x = x;
      this.y = y;
      this.page = page;
      this.stack = stack;
   }

   public String getLinkedPage() {
      return this.linked_page;
   }

   public void setLinkedPage(String linkedPage) {
      this.linked_page = linkedPage;
   }

   public String getTitleText() {
      return this.text;
   }

   public void setTitleText(String titleText) {
      this.text = titleText;
   }

   public int getPage() {
      return this.page;
   }

   public int getX() {
      return this.x;
   }

   public void setX(int x) {
      this.x = x;
   }

   public int getY() {
      return this.y;
   }

   public void setY(int y) {
      this.y = y;
   }

   public ItemStack getDisplayItem() {
      return this.stack.copy();
   }
}
