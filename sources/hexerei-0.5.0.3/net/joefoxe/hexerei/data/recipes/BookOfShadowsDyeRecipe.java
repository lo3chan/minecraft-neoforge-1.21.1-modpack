package net.joefoxe.hexerei.data.recipes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.joefoxe.hexerei.data.books.HexereiBookItem;
import net.joefoxe.hexerei.item.ModDataComponents;
import net.joefoxe.hexerei.item.data_components.BookColorData;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.Tags.Items;

public class BookOfShadowsDyeRecipe extends CustomRecipe {
   protected BookOfShadowsDyeRecipe(CraftingBookCategory category) {
      super(category);
   }

   public boolean matches(CraftingInput inv, Level worldIn) {
      Map<Tuple<Integer, Integer>, List<DyeColor>> posDyes = new HashMap<>();
      Tuple<Tuple<Integer, Integer>, ItemStack> posBook = null;

      for (int slot = 0; slot < inv.size(); slot++) {
         ItemStack slotStack = inv.getItem(slot);
         int column = slot % inv.width();
         int row = slot / inv.width();
         if (!slotStack.isEmpty()) {
            if (slotStack.getItem() instanceof HexereiBookItem) {
               if (posBook != null) {
                  return false;
               }

               posBook = new Tuple(new Tuple(column, row), slotStack);
            } else {
               if (!slotStack.is(Items.DYES)) {
                  return false;
               }

               DyeColor dyeColor = DyeColor.getColor(slotStack);
               if (dyeColor == null) {
                  return false;
               }

               posDyes.computeIfAbsent(new Tuple(column, row), c -> new ArrayList<>()).add(dyeColor);
            }
         }
      }

      if (posBook != null && !posDyes.isEmpty()) {
         boolean mainDye = false;
         boolean trimDye = false;

         for (Entry<Tuple<Integer, Integer>, List<DyeColor>> entry : posDyes.entrySet()) {
            int dyeCol = (Integer)entry.getKey().getA();
            int dyeRow = (Integer)entry.getKey().getB();
            int bookCol = (Integer)((Tuple)posBook.getA()).getA();
            int bookRow = (Integer)((Tuple)posBook.getA()).getB();
            if (dyeCol != bookCol + 1 || dyeRow != bookRow) {
               if (dyeCol != bookCol - 1 || dyeRow != bookRow) {
                  if (dyeRow != bookRow + 1 || dyeCol != bookCol) {
                     if (dyeRow != bookRow - 1 || dyeCol != bookCol) {
                        return false;
                     }

                     if (trimDye) {
                        return false;
                     }

                     trimDye = true;
                  } else {
                     if (trimDye) {
                        return false;
                     }

                     trimDye = true;
                  }
               } else {
                  if (mainDye) {
                     return false;
                  }

                  mainDye = true;
               }
            } else {
               if (mainDye) {
                  return false;
               }

               mainDye = true;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public ItemStack assemble(CraftingInput inv, Provider registryAccess) {
      Map<Tuple<Integer, Integer>, List<DyeColor>> posDyes = new HashMap<>();
      Tuple<Tuple<Integer, Integer>, ItemStack> posBook = null;

      for (int slot = 0; slot < inv.size(); slot++) {
         ItemStack slotStack = inv.getItem(slot);
         if (!slotStack.isEmpty()) {
            int column = slot % inv.width();
            int row = slot / inv.width();
            if (slotStack.getItem() instanceof HexereiBookItem) {
               if (posBook != null) {
                  return ItemStack.EMPTY;
               }

               posBook = new Tuple(new Tuple(column, row), slotStack);
            } else {
               if (!slotStack.is(Items.DYES)) {
                  return ItemStack.EMPTY;
               }

               DyeColor dyeColor = DyeColor.getColor(slotStack);
               if (dyeColor == null) {
                  return ItemStack.EMPTY;
               }

               posDyes.computeIfAbsent(new Tuple(column, row), c -> new ArrayList<>()).add(dyeColor);
            }
         }
      }

      if (posBook == null) {
         return ItemStack.EMPTY;
      } else {
         ItemStack book = ((ItemStack)posBook.getB()).copy();
         book.setCount(1);
         this.applyColors(posDyes, book, (Tuple<Integer, Integer>)posBook.getA());
         return book;
      }
   }

   private void applyColors(Map<Tuple<Integer, Integer>, List<DyeColor>> posDyes, ItemStack book, Tuple<Integer, Integer> posBook) {
      List<DyeColor> mainDyes = new ArrayList<>();
      List<DyeColor> trimDyes = new ArrayList<>();

      for (Entry<Tuple<Integer, Integer>, List<DyeColor>> entry : posDyes.entrySet()) {
         if ((Integer)entry.getKey().getA() == (Integer)posBook.getA() + 1 && ((Integer)entry.getKey().getB()).equals(posBook.getB())) {
            mainDyes.addAll(entry.getValue());
         }

         if ((Integer)entry.getKey().getA() == (Integer)posBook.getA() - 1 && ((Integer)entry.getKey().getB()).equals(posBook.getB())) {
            mainDyes.addAll(entry.getValue());
         }

         if ((Integer)entry.getKey().getB() == (Integer)posBook.getB() + 1 && ((Integer)entry.getKey().getA()).equals(posBook.getA())) {
            trimDyes.addAll(entry.getValue());
         }

         if ((Integer)entry.getKey().getB() == (Integer)posBook.getB() - 1 && ((Integer)entry.getKey().getA()).equals(posBook.getA())) {
            trimDyes.addAll(entry.getValue());
         }
      }

      BookColorData bookColorData = (BookColorData)book.get(ModDataComponents.BOOK_COLORS);
      if (bookColorData != null) {
         int dye1 = bookColorData.color1();
         int dye2 = bookColorData.color2();
         if (!mainDyes.isEmpty()) {
            dye1 = mainDyes.get(0).getTextureDiffuseColor();
         }

         if (!trimDyes.isEmpty()) {
            dye2 = trimDyes.get(0).getTextureDiffuseColor();
         }

         bookColorData = new BookColorData(dye1, dye2);
      }

      book.set(ModDataComponents.BOOK_COLORS, bookColorData);
   }

   public boolean canCraftInDimensions(int pWidth, int pHeight) {
      return false;
   }

   public ItemStack getOutput() {
      return this.getResultItem(null);
   }

   public RecipeSerializer<?> getSerializer() {
      return (RecipeSerializer<?>)ModRecipeTypes.BOOK_OF_SHADOWS_DYE_SERIALIZER.get();
   }
}
