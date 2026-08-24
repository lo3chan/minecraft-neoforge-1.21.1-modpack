package io.wispforest.owo.client.screens;

import java.util.function.Consumer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public final class SlotGenerator {
   private int anchorX;
   private int anchorY;
   private int horizontalSpacing = 0;
   private int verticalSpacing = 0;
   private SlotGenerator.SlotFactory slotFactory = Slot::new;
   private Consumer<Slot> slotConsumer;

   private SlotGenerator(Consumer<Slot> slotConsumer, int anchorX, int anchorY) {
      this.anchorX = anchorX;
      this.anchorY = anchorY;
      this.slotConsumer = slotConsumer;
   }

   public static SlotGenerator begin(Consumer<Slot> slotConsumer, int anchorX, int anchorY) {
      return new SlotGenerator(slotConsumer, anchorX, anchorY);
   }

   public SlotGenerator moveTo(int anchorX, int anchorY) {
      this.anchorX = anchorX;
      this.anchorY = anchorY;
      return this;
   }

   public SlotGenerator spacing(int spacing) {
      this.horizontalSpacing = spacing;
      this.verticalSpacing = spacing;
      return this;
   }

   public SlotGenerator horizontalSpacing(int horizontalSpacing) {
      this.horizontalSpacing = horizontalSpacing;
      return this;
   }

   public SlotGenerator verticalSpacing(int verticalSpacing) {
      this.verticalSpacing = verticalSpacing;
      return this;
   }

   public SlotGenerator slotConsumer(Consumer<Slot> slotConsumer) {
      this.slotConsumer = slotConsumer;
      return this;
   }

   public SlotGenerator defaultSlotFactory() {
      this.slotFactory = Slot::new;
      return this;
   }

   public SlotGenerator slotFactory(SlotGenerator.SlotFactory slotFactory) {
      this.slotFactory = slotFactory;
      return this;
   }

   public SlotGenerator grid(Container inventory, int startIndex, int width, int height) {
      for (int row = 0; row < height; row++) {
         for (int column = 0; column < width; column++) {
            this.slotConsumer
               .accept(
                  this.slotFactory
                     .create(
                        inventory,
                        startIndex + row * width + column,
                        this.anchorX + column * (18 + this.horizontalSpacing),
                        this.anchorY + row * (18 + this.verticalSpacing)
                     )
               );
         }
      }

      return this;
   }

   public SlotGenerator playerInventory(Inventory playerInventory) {
      this.grid(playerInventory, 9, 9, 3);
      this.anchorY += 58;
      this.grid(playerInventory, 0, 9, 1);
      this.anchorY -= 58;
      return this;
   }

   @FunctionalInterface
   public interface SlotFactory {
      Slot create(Container var1, int var2, int var3, int var4);
   }
}
