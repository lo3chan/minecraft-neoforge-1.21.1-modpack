package snownee.jade.impl.ui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.Nullable;
import snownee.jade.api.ui.Element;
import snownee.jade.overlay.DisplayHelper;

public class ItemStackElement extends Element {
   private final ItemStack item;
   private final float scale;
   private final String text;
   public static final ItemStackElement EMPTY = new ItemStackElement(ItemStack.EMPTY, 1.0F, null);

   private ItemStackElement(ItemStack item, float scale, @Nullable String text) {
      this.item = item;
      this.scale = scale == 0.0F ? 1.0F : scale;
      this.text = text;
   }

   public static ItemStackElement of(ItemStack stack) {
      return of(stack, 1.0F);
   }

   public static ItemStackElement of(ItemStack stack, float scale) {
      return of(stack, scale, null);
   }

   public static ItemStackElement of(ItemStack stack, float scale, @Nullable String text) {
      return scale == 1.0F && stack.isEmpty() ? EMPTY : new ItemStackElement(stack, scale, text);
   }

   @Override
   public Vec2 getSize() {
      int size = Mth.floor(18.0F * this.scale);
      return new Vec2(size, size);
   }

   @Override
   public void render(GuiGraphics guiGraphics, float x, float y, float maxX, float maxY) {
      if (!this.item.isEmpty()) {
         DisplayHelper.INSTANCE.drawItem(guiGraphics, x + 1.0F, y + 1.0F, this.item, this.scale, this.text);
      }
   }

   @Nullable
   @Override
   public String getMessage() {
      return this.item.isEmpty() ? null : "%s %s".formatted(this.item.getCount(), this.item.getHoverName().getString());
   }

   public ItemStack getItem() {
      return this.item;
   }
}
