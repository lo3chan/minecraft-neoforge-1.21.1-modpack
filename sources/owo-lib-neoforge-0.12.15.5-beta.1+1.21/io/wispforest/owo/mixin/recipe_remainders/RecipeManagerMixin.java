package io.wispforest.owo.mixin.recipe_remainders;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.serialization.JsonOps;
import io.wispforest.owo.util.RecipeRemainderStorage;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.conditions.WithConditions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin({RecipeManager.class})
public abstract class RecipeManagerMixin {
   public final ThreadLocal<Entry<ResourceLocation, JsonElement>> previousMapEntry = ThreadLocal.withInitial(() -> null);

   @Inject(
      method = {"apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V"},
      at = {@At(
         value = "INVOKE",
         target = "Ljava/util/Optional;ifPresentOrElse(Ljava/util/function/Consumer;Ljava/lang/Runnable;)V"
      )}
   )
   private void deserializeRecipeSpecificRemainders(
      Map<ResourceLocation, JsonElement> map,
      ResourceManager resourceManager,
      ProfilerFiller profiler,
      CallbackInfo ci,
      @Local Entry<ResourceLocation, JsonElement> entry,
      @Local Optional<WithConditions<Recipe<?>>> decoded
   ) {
      if (!decoded.isEmpty()) {
         JsonObject json = entry.getValue().getAsJsonObject();
         if (json.has("owo:remainders")) {
            HashMap<Item, ItemStack> remainders = new HashMap<>();

            for (Entry<String, JsonElement> remainderEntry : json.getAsJsonObject("owo:remainders").entrySet()) {
               Holder<Item> item = GsonHelper.convertToItem(new JsonPrimitive(remainderEntry.getKey()), remainderEntry.getKey());
               if (remainderEntry.getValue().isJsonObject()) {
                  ItemStack remainderStack = (ItemStack)ItemStack.CODEC
                     .parse(JsonOps.INSTANCE, remainderEntry.getValue().getAsJsonObject())
                     .getOrThrow(JsonParseException::new);
                  remainders.put((Item)item.value(), remainderStack);
               } else {
                  Holder<Item> remainderItem = GsonHelper.convertToItem(remainderEntry.getValue(), "item");
                  remainders.put((Item)item.value(), new ItemStack(remainderItem));
               }
            }

            if (!remainders.isEmpty()) {
               RecipeRemainderStorage.store(entry.getKey(), remainders);
            }
         }
      }
   }

   @Inject(
      method = {"getRemainingItemsFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;)Lnet/minecraft/core/NonNullList;"},
      at = {@At(
         value = "RETURN",
         ordinal = 0
      )},
      locals = LocalCapture.CAPTURE_FAILHARD
   )
   private <I extends RecipeInput, R extends Recipe<I>> void addRecipeSpecificRemainders(
      RecipeType<R> type, I inventory, Level world, CallbackInfoReturnable<NonNullList<ItemStack>> cir, Optional<RecipeHolder<R>> optional
   ) {
      if (!optional.isEmpty() && RecipeRemainderStorage.has(optional.get().id())) {
         NonNullList<ItemStack> remainders = (NonNullList<ItemStack>)cir.getReturnValue();
         Map<Item, ItemStack> owoRemainders = RecipeRemainderStorage.get(optional.get().id());

         for (int i = 0; i < remainders.size(); i++) {
            Item item = inventory.getItem(i).getItem();
            if (owoRemainders.containsKey(item)) {
               remainders.set(i, owoRemainders.get(item).copy());
            }
         }
      }
   }
}
