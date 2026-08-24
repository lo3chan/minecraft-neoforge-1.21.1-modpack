package net.cibernet.alchemancy.crafting;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import net.cibernet.alchemancy.advancements.predicates.ForgeRecipePredicate;
import net.cibernet.alchemancy.blocks.blockentities.ItemStackHolderBlockEntity;
import net.cibernet.alchemancy.registries.AlchemancyRecipeTypes;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.component.WrittenBookContent;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.TriState;
import org.apache.commons.lang3.function.TriFunction;

public class ForgeLoreRecipe extends AbstractForgeRecipe<Object> {
   protected ForgeLoreRecipe() {
      super(Optional.empty(), Optional.empty(), List.of(), List.of());
   }

   @Override
   public TriFunction<ForgeRecipeGrid, Provider, ItemStack, ItemStack> processResult() {
      return (grid, provider, stack) -> {
         for (ItemStackHolderBlockEntity itemPedestal : grid.getItemPedestals()) {
            if (!itemPedestal.isEmpty() && itemPedestal.getItem().has(DataComponents.WRITTEN_BOOK_CONTENT)) {
               WrittenBookContent book = (WrittenBookContent)itemPedestal.getItem().get(DataComponents.WRITTEN_BOOK_CONTENT);
               List<Component> pages = book.getPages(true);
               if (!pages.isEmpty()) {
                  String str = ((Component)pages.getFirst()).tryCollapseToString();
                  if (str != null) {
                     stack.set(
                        DataComponents.LORE, new ItemLore(Arrays.stream(str.split("\n")).map(s -> Component.translationArg(Component.literal(s))).toList())
                     );
                     grid.consumeItem(itemPedestal);
                     break;
                  }
               }
            }
         }

         return stack;
      };
   }

   @Override
   public Object getResult() {
      return null;
   }

   @Override
   public TriState matches(ForgeRecipePredicate forgeRecipePredicate, ForgeRecipeGrid grid) {
      return TriState.DEFAULT;
   }

   @Override
   public boolean matches(ForgeRecipeGrid input, Level level) {
      for (ItemStackHolderBlockEntity itemPedestal : input.getItemPedestals()) {
         if (!itemPedestal.getItem().isEmpty() && itemPedestal.getItem().has(DataComponents.WRITTEN_BOOK_CONTENT)) {
            return true;
         }
      }

      return false;
   }

   public ItemStack getResultItem(Provider registries) {
      return ItemStack.EMPTY;
   }

   public RecipeSerializer<?> getSerializer() {
      return (RecipeSerializer<?>)AlchemancyRecipeTypes.Serializers.ALCHEMANCY_FORGE_CUSTOM_LORE.get();
   }

   public static class Serializer implements RecipeSerializer<ForgeLoreRecipe> {
      private final MapCodec<ForgeLoreRecipe> codec;
      private final StreamCodec<RegistryFriendlyByteBuf, ForgeLoreRecipe> streamCodec;
      private final ForgeLoreRecipe INSTANCE = new ForgeLoreRecipe();

      public Serializer() {
         this.codec = RecordCodecBuilder.mapCodec(p_311736_ -> p_311736_.point(this.INSTANCE));
         this.streamCodec = StreamCodec.unit(this.INSTANCE);
      }

      public MapCodec<ForgeLoreRecipe> codec() {
         return this.codec;
      }

      public StreamCodec<RegistryFriendlyByteBuf, ForgeLoreRecipe> streamCodec() {
         return this.streamCodec;
      }

      @FunctionalInterface
      public interface Factory {
         DormantPropertyInfusionRecipe create();
      }
   }
}
