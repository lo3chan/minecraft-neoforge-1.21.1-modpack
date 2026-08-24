package net.astralya.hexalia.gameplay.smallcauldron;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import net.astralya.hexalia.HexaliaConfig;
import net.astralya.hexalia.item.ModItems;
import net.astralya.hexalia.item.custom.BrewItem;
import net.astralya.hexalia.recipe.ModRecipeTypes;
import net.astralya.hexalia.recipe.SmallCauldronRecipe;
import net.astralya.hexalia.recipe.SmallCauldronRecipeInput;
import net.astralya.hexalia.util.FireStarterHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public final class SmallCauldronContents {
   public static final int MAX_INGREDIENTS = 4;
   public static final int SERVINGS_PER_MIXTURE = 2;
   public static final int DEFAULT_REQUIRED_WATER_MB = 1000;
   public static final int DEFAULT_COOK_TIME_TICKS = 400;
   public static final int DEFAULT_OVERCOOK_AFTER_TICKS = 2400;
   public static final int DEFAULT_CAPACITY_MB = 1000;
   public static final int BUCKET_MB = 1000;
   public static final int BOTTLE_MB = 250;
   private static final int WATER_RGB = 4159204;
   private static final int SPOILED_RGB = 4086330;
   private static final String TAG_WATER_MB = "WaterMb";
   private static final String TAG_COOK_PROGRESS = "CookProgress";
   private static final String TAG_STIRS_DONE = "StirsDone";
   private static final String TAG_INGREDIENTS = "Ingredients";
   private static final String TAG_CONTENTS_KIND = "ContentsKind";
   private static final String TAG_SERVINGS = "Servings";
   private static final String TAG_MIX_COLOR = "MixtureColor";
   private static final String TAG_LOCKED_RECIPE = "LockedRecipe";
   private static final String TAG_MIXTURE_AGE = "MixtureAge";
   private static final String TAG_OVERCOOKED = "Overcooked";
   private static final String TAG_MIXTURE_RESULT = "MixtureResult";
   private final NonNullList<ItemStack> ingredients = NonNullList.withSize(4, ItemStack.EMPTY);
   private final List<ItemStack> ingredientsView = Collections.unmodifiableList(this.ingredients);
   private int waterMb;
   private int ingredientCount;
   private int stirsDone;
   private int cookProgress;
   private int mixtureAgeTicks;
   private boolean overcooked;
   private SmallCauldronContents.ContentsKind kind = SmallCauldronContents.ContentsKind.EMPTY;
   private int servings;
   private int mixtureColor;
   private ItemStack mixtureResult = ItemStack.EMPTY;
   @Nullable
   private String lockedRecipeId;
   private boolean dirty;

   public boolean isDirty() {
      return this.dirty;
   }

   public void clearDirty() {
      this.dirty = false;
   }

   public boolean isSpoiled() {
      return this.kind == SmallCauldronContents.ContentsKind.SPOILED;
   }

   public boolean isCooking() {
      return this.kind == SmallCauldronContents.ContentsKind.COOKING;
   }

   public boolean hasMixture() {
      return this.kind == SmallCauldronContents.ContentsKind.MIXTURE;
   }

   public boolean isOvercooked() {
      return this.kind == SmallCauldronContents.ContentsKind.MIXTURE && this.overcooked;
   }

   public int getMixtureBaseColor() {
      return this.mixtureColor != 0 ? this.mixtureColor : 4159204;
   }

   public int getVisualLiquidColor() {
      if (this.kind == SmallCauldronContents.ContentsKind.SPOILED) {
         return 4086330;
      } else if (this.kind == SmallCauldronContents.ContentsKind.MIXTURE) {
         return this.overcooked ? darkenRgb(this.getMixtureBaseColor(), 0.55F) : this.getMixtureBaseColor();
      } else {
         return this.kind != SmallCauldronContents.ContentsKind.WATER && this.kind != SmallCauldronContents.ContentsKind.COOKING ? 0 : 4159204;
      }
   }

   public float getLiquidFill01() {
      return this.waterMb / 1000.0F;
   }

   public float getVisualLiquidFill01() {
      if (this.kind != SmallCauldronContents.ContentsKind.MIXTURE) {
         return this.kind == SmallCauldronContents.ContentsKind.COOKING ? 1.0F : this.getLiquidFill01();
      } else {
         return !this.overcooked && this.servings > 1 ? 1.0F : 0.5F;
      }
   }

   public List<ItemStack> getIngredientsForRender() {
      return this.ingredientsView;
   }

   public ItemStack getIngredient(int slot) {
      return slot >= 0 && slot < 4 ? (ItemStack)this.ingredients.get(slot) : ItemStack.EMPTY;
   }

   public void setIngredient(int slot, ItemStack stack) {
      if (slot >= 0 && slot < 4) {
         ItemStack previous = (ItemStack)this.ingredients.get(slot);
         if (previous.isEmpty() && !stack.isEmpty()) {
            this.ingredientCount++;
         } else if (!previous.isEmpty() && stack.isEmpty()) {
            this.ingredientCount = Math.max(0, this.ingredientCount - 1);
         }

         this.ingredients.set(slot, stack.isEmpty() ? ItemStack.EMPTY : stack.copyWithCount(1));
         this.stirsDone = Math.min(this.stirsDone, this.ingredientCount);
         if (this.kind == SmallCauldronContents.ContentsKind.EMPTY && this.ingredientCount > 0 && this.waterMb > 0) {
            this.kind = SmallCauldronContents.ContentsKind.WATER;
         }

         this.markDirty();
      }
   }

   public boolean canExtractOneIngredient(boolean blockLit) {
      return (this.kind == SmallCauldronContents.ContentsKind.WATER || this.kind == SmallCauldronContents.ContentsKind.EMPTY)
         && this.cookProgress == 0
         && this.stirsDone == 0
         && this.ingredientCount > 0;
   }

   public ItemStack extractOneIngredient() {
      int slot = this.lastFilledIngredientSlot();
      if (slot == -1) {
         return ItemStack.EMPTY;
      } else {
         ItemStack extracted = (ItemStack)this.ingredients.get(slot);
         this.ingredients.set(slot, ItemStack.EMPTY);
         this.ingredientCount = Math.max(0, this.ingredientCount - 1);
         this.markDirty();
         return extracted;
      }
   }

   public boolean canInsertOne(ItemStack stack) {
      return !stack.isEmpty()
            && !FireStarterHelper.isFireStarter(stack)
            && !this.isRusticBottle(stack)
            && !this.isLotusBlossom(stack)
            && !this.isWaterContainer(stack)
         ? (this.kind == SmallCauldronContents.ContentsKind.WATER || this.kind == SmallCauldronContents.ContentsKind.EMPTY)
            && this.firstEmptyIngredientSlot() != -1
         : false;
   }

   public boolean insertOne(ItemStack held) {
      if (!this.canInsertOne(held)) {
         return false;
      } else {
         if (this.kind == SmallCauldronContents.ContentsKind.EMPTY && this.waterMb > 0) {
            this.kind = SmallCauldronContents.ContentsKind.WATER;
         }

         int slot = this.firstEmptyIngredientSlot();
         ItemStack one = held.copyWithCount(1);
         this.ingredients.set(slot, one);
         this.ingredientCount++;
         this.stirsDone = Math.min(this.stirsDone, this.ingredientCount);
         this.markDirty();
         return true;
      }
   }

   public boolean canStir(boolean blockLit) {
      return this.kind == SmallCauldronContents.ContentsKind.WATER
         && blockLit
         && this.ingredientCount > 0
         && this.cookProgress == 0
         && this.stirsDone < this.ingredientCount;
   }

   public SmallCauldronContents.StirResult stir(ServerLevel level) {
      this.stirsDone++;
      this.markDirty();
      if (this.stirsDone >= this.ingredientCount) {
         return this.tryStartCooking(level) ? SmallCauldronContents.StirResult.STARTED_COOKING : SmallCauldronContents.StirResult.STIRRED;
      } else {
         return SmallCauldronContents.StirResult.STIRRED;
      }
   }

   public boolean canScoopMixtureWithRusticBottle() {
      return this.kind == SmallCauldronContents.ContentsKind.MIXTURE && this.servings > 0 && !this.mixtureResult.isEmpty();
   }

   public boolean tryScoopBottle(ServerLevel level, double x, double y, double z, Player player, InteractionHand hand, ItemStack held) {
      if (this.canScoopMixtureWithRusticBottle() && this.isRusticBottle(held)) {
         ItemStack resultBottle = this.mixtureResult.copy();
         if (!player.getAbilities().instabuild) {
            held.shrink(1);
         }

         if (!player.getInventory().add(resultBottle)) {
            level.addFreshEntity(new ItemEntity(level, x, y, z, resultBottle));
         }

         this.servings--;
         if (this.servings <= 0) {
            this.resetToEmpty();
         }

         this.markDirty();
         return true;
      } else {
         return false;
      }
   }

   public boolean canCleanseSpoiledWithLotus() {
      return this.kind == SmallCauldronContents.ContentsKind.SPOILED;
   }

   public boolean tryCleanseSpoiled(Player player, InteractionHand hand, ItemStack held) {
      if (this.canCleanseSpoiledWithLotus() && this.isLotusBlossom(held)) {
         if (!player.getAbilities().instabuild) {
            held.shrink(1);
         }

         this.resetToEmpty();
         this.markDirty();
         return true;
      } else {
         return false;
      }
   }

   public boolean canUseWaterContainer(ItemStack stack) {
      if (this.kind == SmallCauldronContents.ContentsKind.COOKING
         || this.kind == SmallCauldronContents.ContentsKind.MIXTURE
         || this.kind == SmallCauldronContents.ContentsKind.SPOILED) {
         return false;
      } else if (stack.is(Items.WATER_BUCKET)) {
         return this.waterMb + 1000 <= 1000;
      } else {
         return stack.is(Items.BUCKET) ? this.waterMb >= 1000 : stack.is(Items.GLASS_BOTTLE) && this.waterMb >= 250;
      }
   }

   public boolean tryUseWaterContainer(ServerLevel level, double x, double y, double z, Player player, InteractionHand hand, ItemStack held) {
      if (!this.canUseWaterContainer(held)) {
         return false;
      } else if (held.is(Items.WATER_BUCKET)) {
         this.fillWater(1000);
         if (!player.getAbilities().instabuild) {
            player.setItemInHand(hand, new ItemStack(Items.BUCKET));
         }

         level.playSound(null, x, y, z, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
         return true;
      } else if (held.is(Items.BUCKET)) {
         this.drainWater(1000);
         if (!player.getAbilities().instabuild) {
            player.setItemInHand(hand, new ItemStack(Items.WATER_BUCKET));
         }

         level.playSound(null, x, y, z, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
         return true;
      } else {
         this.drainWater(250);
         if (!player.getAbilities().instabuild) {
            held.shrink(1);
            ItemStack waterBottle = createWaterBottle();
            if (!player.getInventory().add(waterBottle)) {
               player.drop(waterBottle, false);
            }
         }

         level.playSound(null, x, y, z, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
         return true;
      }
   }

   public void tickServer(ServerLevel level, boolean blockLit) {
      if (this.kind == SmallCauldronContents.ContentsKind.COOKING) {
         if (!blockLit) {
            return;
         }

         this.cookProgress++;
         int duration = this.findLockedRecipe(level).map(holder -> ((SmallCauldronRecipe)holder.value()).getDuration()).orElse(400);
         if (this.cookProgress >= Math.max(1, duration)) {
            if (!this.finalizeCook(level)) {
               this.makeSpoiled();
            }

            this.markDirty();
         } else if ((this.cookProgress & 3) == 0) {
            this.markDirty();
         }
      } else if (this.kind == SmallCauldronContents.ContentsKind.MIXTURE && blockLit) {
         this.mixtureAgeTicks++;
         if (!this.overcooked && this.mixtureAgeTicks >= HexaliaConfig.overcookedDuration()) {
            this.overcooked = true;
            this.servings = Math.min(this.servings, 1);
            this.markDirty();
         }
      }
   }

   public void dropAll(Level level, double x, double y, double z) {
      for (ItemStack stack : this.ingredients) {
         if (!stack.isEmpty()) {
            Containers.dropItemStack(level, x, y, z, stack);
         }
      }

      this.resetToEmpty();
      this.markDirty();
   }

   public void save(CompoundTag tag, Provider registries) {
      tag.putInt("WaterMb", this.waterMb);
      tag.putInt("CookProgress", this.cookProgress);
      tag.putInt("StirsDone", this.stirsDone);
      tag.putInt("MixtureAge", this.mixtureAgeTicks);
      tag.putBoolean("Overcooked", this.overcooked);
      tag.putInt("ContentsKind", this.kind.ordinal());
      tag.putInt("Servings", this.servings);
      tag.putInt("MixtureColor", this.mixtureColor);
      if (this.lockedRecipeId != null) {
         tag.putString("LockedRecipe", this.lockedRecipeId);
      }

      ListTag list = new ListTag();

      for (int index = 0; index < 4; index++) {
         ItemStack stack = (ItemStack)this.ingredients.get(index);
         if (!stack.isEmpty()) {
            CompoundTag entry = new CompoundTag();
            entry.putInt("Slot", index);
            entry.put("Stack", stack.save(registries));
            list.add(entry);
         }
      }

      tag.put("Ingredients", list);
      if (!this.mixtureResult.isEmpty()) {
         tag.put("MixtureResult", this.mixtureResult.save(registries));
      }
   }

   public void load(CompoundTag tag, Provider registries) {
      this.waterMb = clamp(tag.getInt("WaterMb"), 0, 1000);
      this.cookProgress = Math.max(0, tag.getInt("CookProgress"));
      this.stirsDone = Math.max(0, tag.getInt("StirsDone"));
      this.mixtureAgeTicks = Math.max(0, tag.getInt("MixtureAge"));
      this.overcooked = tag.getBoolean("Overcooked");
      SmallCauldronContents.ContentsKind[] values = SmallCauldronContents.ContentsKind.values();
      int kindId = tag.getInt("ContentsKind");
      this.kind = kindId >= 0 && kindId < values.length ? values[kindId] : SmallCauldronContents.ContentsKind.EMPTY;
      this.servings = Math.max(0, tag.getInt("Servings"));
      this.mixtureColor = tag.getInt("MixtureColor");
      this.lockedRecipeId = tag.contains("LockedRecipe", 8) ? tag.getString("LockedRecipe") : null;
      this.mixtureResult = tag.contains("MixtureResult", 10) ? ItemStack.parseOptional(registries, tag.getCompound("MixtureResult")) : ItemStack.EMPTY;
      this.clearIngredientsAndCount();
      if (tag.contains("Ingredients", 9)) {
         ListTag list = tag.getList("Ingredients", 10);

         for (int index = 0; index < list.size(); index++) {
            CompoundTag entry = list.getCompound(index);
            int slot = entry.getInt("Slot");
            if (slot >= 0 && slot < 4 && entry.contains("Stack", 10)) {
               ItemStack stack = ItemStack.parseOptional(registries, entry.getCompound("Stack"));
               if (!stack.isEmpty()) {
                  this.ingredients.set(slot, stack);
                  this.ingredientCount++;
               }
            }
         }
      }

      this.dirty = false;
   }

   private boolean tryStartCooking(ServerLevel level) {
      if (this.kind == SmallCauldronContents.ContentsKind.WATER && this.waterMb >= 1000 && this.ingredientCount > 0) {
         Optional<RecipeHolder<SmallCauldronRecipe>> match = this.findRecipeMatch(level);
         this.lockedRecipeId = match.<String>map(holder -> holder.id().toString()).orElse(null);
         this.mixtureResult = match.<ItemStack>map(holder -> ((SmallCauldronRecipe)holder.value()).getResultItem(level.registryAccess()).copy())
            .orElse(ItemStack.EMPTY);
         this.clearIngredientsAndCount();
         this.stirsDone = 0;
         this.cookProgress = 0;
         this.kind = SmallCauldronContents.ContentsKind.COOKING;
         this.markDirty();
         return true;
      } else {
         return false;
      }
   }

   private boolean finalizeCook(ServerLevel level) {
      Optional<RecipeHolder<SmallCauldronRecipe>> locked = this.findLockedRecipe(level);
      if (locked.isEmpty()) {
         return false;
      } else {
         if (this.mixtureResult.isEmpty()) {
            this.mixtureResult = ((SmallCauldronRecipe)locked.get().value()).getResultItem(level.registryAccess()).copy();
         }

         this.drainWater(1000);
         this.kind = SmallCauldronContents.ContentsKind.MIXTURE;
         this.servings = 2;
         this.mixtureColor = getMixtureColor(this.mixtureResult);
         this.cookProgress = 0;
         this.mixtureAgeTicks = 0;
         this.overcooked = false;
         return true;
      }
   }

   private static int getMixtureColor(ItemStack result) {
      if (result.getItem() instanceof BrewItem brewItem) {
         int color = brewItem.getBrewColor();
         return color != 0 ? color : 4159204;
      } else {
         return 4159204;
      }
   }

   private void makeSpoiled() {
      this.clearIngredientsAndCount();
      this.stirsDone = 0;
      this.kind = SmallCauldronContents.ContentsKind.SPOILED;
      this.servings = 0;
      this.mixtureColor = 0;
      this.cookProgress = 0;
      this.mixtureAgeTicks = 0;
      this.overcooked = false;
      this.lockedRecipeId = null;
      this.mixtureResult = ItemStack.EMPTY;
   }

   private Optional<RecipeHolder<SmallCauldronRecipe>> findRecipeMatch(ServerLevel level) {
      return level.getRecipeManager()
         .getRecipeFor((RecipeType)ModRecipeTypes.SMALL_CAULDRON.get(), new SmallCauldronRecipeInput(this.getIngredientListForRecipe()), level);
   }

   private Optional<RecipeHolder<SmallCauldronRecipe>> findLockedRecipe(ServerLevel level) {
      if (this.lockedRecipeId == null) {
         return Optional.empty();
      } else {
         ResourceLocation key = ResourceLocation.tryParse(this.lockedRecipeId);
         if (key == null) {
            return Optional.empty();
         } else {
            Optional<? extends RecipeHolder<?>> recipe = level.getRecipeManager().byKey(key);
            if (!recipe.isEmpty() && recipe.get().value() instanceof SmallCauldronRecipe) {
               RecipeHolder<SmallCauldronRecipe> typed = (RecipeHolder<SmallCauldronRecipe>)recipe.get();
               return Optional.of(typed);
            } else {
               return Optional.empty();
            }
         }
      }
   }

   private List<ItemStack> getIngredientListForRecipe() {
      List<ItemStack> list = new ArrayList<>(this.ingredientCount);

      for (ItemStack stack : this.ingredients) {
         if (!stack.isEmpty()) {
            list.add(stack);
         }
      }

      return list;
   }

   private int firstEmptyIngredientSlot() {
      for (int index = 0; index < 4; index++) {
         if (((ItemStack)this.ingredients.get(index)).isEmpty()) {
            return index;
         }
      }

      return -1;
   }

   private int lastFilledIngredientSlot() {
      for (int index = 3; index >= 0; index--) {
         if (!((ItemStack)this.ingredients.get(index)).isEmpty()) {
            return index;
         }
      }

      return -1;
   }

   private int fillWater(int amountMb) {
      int accepted = Math.min(1000 - this.waterMb, amountMb);
      if (accepted > 0) {
         this.waterMb += accepted;
         if (this.kind == SmallCauldronContents.ContentsKind.EMPTY) {
            this.kind = SmallCauldronContents.ContentsKind.WATER;
         }

         this.markDirty();
      }

      return accepted;
   }

   private int drainWater(int amountMb) {
      int removed = Math.min(this.waterMb, amountMb);
      if (removed > 0) {
         this.waterMb -= removed;
         if (this.waterMb == 0 && this.ingredientCount == 0 && this.kind == SmallCauldronContents.ContentsKind.WATER) {
            this.kind = SmallCauldronContents.ContentsKind.EMPTY;
         }

         this.markDirty();
      }

      return removed;
   }

   public boolean isRusticBottle(ItemStack stack) {
      return stack.is((Item)ModItems.RUSTIC_BOTTLE.get());
   }

   public boolean isLotusBlossom(ItemStack stack) {
      return stack.is((Item)ModItems.LOTUS_BLOSSOM.get());
   }

   public boolean isWaterContainer(ItemStack stack) {
      return stack.is(Items.WATER_BUCKET) || stack.is(Items.BUCKET) || stack.is(Items.GLASS_BOTTLE);
   }

   private static ItemStack createWaterBottle() {
      ItemStack bottle = new ItemStack(Items.POTION);
      bottle.set(DataComponents.POTION_CONTENTS, PotionContents.EMPTY.withPotion(Potions.WATER));
      return bottle;
   }

   public void resetToEmpty() {
      this.clearIngredientsAndCount();
      this.stirsDone = 0;
      this.cookProgress = 0;
      this.lockedRecipeId = null;
      this.servings = 0;
      this.mixtureColor = 0;
      this.mixtureAgeTicks = 0;
      this.overcooked = false;
      this.mixtureResult = ItemStack.EMPTY;
      this.kind = SmallCauldronContents.ContentsKind.EMPTY;
      this.waterMb = 0;
   }

   private void clearIngredientsAndCount() {
      for (int index = 0; index < 4; index++) {
         this.ingredients.set(index, ItemStack.EMPTY);
      }

      this.ingredientCount = 0;
   }

   private void markDirty() {
      this.dirty = true;
   }

   private static int darkenRgb(int rgb, float factor) {
      int r = clamp((int)((rgb >> 16 & 0xFF) * factor), 0, 255);
      int g = clamp((int)((rgb >> 8 & 0xFF) * factor), 0, 255);
      int b = clamp((int)((rgb & 0xFF) * factor), 0, 255);
      return r << 16 | g << 8 | b;
   }

   private static int clamp(int value, int min, int max) {
      return Math.max(min, Math.min(max, value));
   }

   public static enum ContentsKind {
      EMPTY,
      WATER,
      COOKING,
      MIXTURE,
      SPOILED;
   }

   public static enum StirResult {
      STIRRED,
      STARTED_COOKING;
   }
}
