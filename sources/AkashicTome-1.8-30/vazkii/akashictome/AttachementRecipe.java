package vazkii.akashictome;

import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import vazkii.akashictome.data_components.ToolContentComponent;

public class AttachementRecipe extends CustomRecipe {
   public AttachementRecipe(CraftingBookCategory pCategory) {
      super(pCategory);
   }

   public boolean matches(CraftingInput input, Level level) {
      boolean foundTool = false;
      boolean foundTarget = false;

      for (int i = 0; i < input.size(); i++) {
         ItemStack stack = input.getItem(i);
         if (!stack.isEmpty()) {
            if (this.isTarget(stack)) {
               if (foundTarget) {
                  return false;
               }

               foundTarget = true;
            } else {
               if (!stack.is((Item)Registries.TOME.get())) {
                  return false;
               }

               if (foundTool) {
                  return false;
               }

               foundTool = true;
            }
         }
      }

      return foundTool && foundTarget;
   }

   public ItemStack assemble(CraftingInput input, Provider provider) {
      ItemStack tool = ItemStack.EMPTY;
      ItemStack target = ItemStack.EMPTY;

      for (int i = 0; i < input.size(); i++) {
         ItemStack stack = input.getItem(i);
         if (!stack.isEmpty()) {
            if (stack.is((Item)Registries.TOME.get())) {
               tool = stack;
            } else {
               target = stack;
            }
         }
      }

      if (!tool.has(Registries.TOOL_CONTENT)) {
         return ItemStack.EMPTY;
      } else {
         ItemStack copy = tool.copy();
         ToolContentComponent contents = (ToolContentComponent)copy.get(Registries.TOOL_CONTENT);
         if (contents == null) {
            return ItemStack.EMPTY;
         } else {
            String mod = MorphingHandler.getModFromStack(target);
            String modRoot = mod;

            for (int tries = 0; contents.hasDefinedMod(mod) && tries < 99; tries++) {
               mod = modRoot + "_" + tries;
            }

            target.set(Registries.DEFINED_MOD, mod);
            ToolContentComponent.Mutable mutable = new ToolContentComponent.Mutable(contents);
            if (!target.isEmpty()) {
               mutable.tryInsert(target);
            }

            copy.set(Registries.TOOL_CONTENT, mutable.toImmutable());
            return copy;
         }
      }
   }

   public boolean canCraftInDimensions(int width, int height) {
      return width * height >= 2;
   }

   public boolean isTarget(ItemStack stack) {
      if (!stack.isEmpty() && !MorphingHandler.isAkashicTome(stack)) {
         String mod = MorphingHandler.getModFromStack(stack);
         if (mod.equals("minecraft")) {
            return false;
         } else if ((Boolean)ConfigHandler.allItems.get()) {
            return true;
         } else if (((List)ConfigHandler.blacklistedMods.get()).contains(mod)) {
            return false;
         } else if (stack.getItem() instanceof IModdedBook) {
            return true;
         } else {
            ResourceLocation registryNameRL = BuiltInRegistries.ITEM.getKey(stack.getItem());
            String registryName = registryNameRL.toString();
            if (!((List)ConfigHandler.whitelistedItems.get()).contains(registryName)
               && !((List)ConfigHandler.whitelistedItems.get()).contains(registryName + ":" + stack.getDamageValue())) {
               if (!((List)ConfigHandler.blacklistedItems.get()).contains(registryName)
                  && !((List)ConfigHandler.blacklistedItems.get()).contains(registryName + ":" + stack.getDamageValue())) {
                  String itemName = registryNameRL.getPath().toLowerCase();

                  for (String s : (List)ConfigHandler.whitelistedNames.get()) {
                     if (itemName.contains(s.toLowerCase())) {
                        return true;
                     }
                  }

                  return false;
               } else {
                  return false;
               }
            } else {
               return true;
            }
         }
      } else {
         return false;
      }
   }

   public ItemStack getResultItem(Provider provider) {
      return ItemStack.EMPTY;
   }

   public NonNullList<ItemStack> getRemainingItems(CraftingInput craftingInput) {
      return NonNullList.withSize(craftingInput.size(), ItemStack.EMPTY);
   }

   public RecipeSerializer<?> getSerializer() {
      return Registries.ATTACHMENT.get();
   }
}
