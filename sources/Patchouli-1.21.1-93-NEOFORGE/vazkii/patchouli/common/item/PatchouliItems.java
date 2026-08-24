package vazkii.patchouli.common.item;

import java.util.function.BiConsumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class PatchouliItems {
   public static final ResourceLocation BOOK_ID = ResourceLocation.fromNamespaceAndPath("patchouli", "guide_book");
   public static final Item BOOK = new ItemModBook();

   public static void submitItemRegistrations(BiConsumer<ResourceLocation, Item> consumer) {
      consumer.accept(BOOK_ID, BOOK);
   }
}
