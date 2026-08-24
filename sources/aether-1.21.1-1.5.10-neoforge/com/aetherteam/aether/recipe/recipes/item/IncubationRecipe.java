package com.aetherteam.aether.recipe.recipes.item;

import com.aetherteam.aether.block.AetherBlocks;
import com.aetherteam.aether.recipe.AetherRecipeSerializers;
import com.aetherteam.aether.recipe.AetherRecipeTypes;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

public class IncubationRecipe implements Recipe<SingleRecipeInput> {
   protected final RecipeType<?> type = (RecipeType<?>)AetherRecipeTypes.INCUBATION.get();
   protected final String group;
   protected final Ingredient ingredient;
   protected final EntityType<?> entity;
   protected final Optional<CompoundTag> tag;
   protected final int incubationTime;

   public IncubationRecipe(String group, Ingredient ingredient, EntityType<?> entity, Optional<CompoundTag> tag, int incubationTime) {
      this.group = group;
      this.ingredient = ingredient;
      this.entity = entity;
      this.tag = tag;
      this.incubationTime = incubationTime;
   }

   public boolean matches(SingleRecipeInput menu, Level level) {
      return this.ingredient.test(menu.getItem(0));
   }

   public ItemStack assemble(SingleRecipeInput menu, Provider provider) {
      return ItemStack.EMPTY;
   }

   public ItemStack getResultItem(Provider provider) {
      return this.ingredient.getItems()[0];
   }

   public boolean canCraftInDimensions(int width, int height) {
      return true;
   }

   public int getIncubationTime() {
      return this.incubationTime;
   }

   public EntityType<?> getEntity() {
      return this.entity;
   }

   public Optional<CompoundTag> getTag() {
      return this.tag;
   }

   public NonNullList<Ingredient> getIngredients() {
      NonNullList<Ingredient> nonNullList = NonNullList.create();
      nonNullList.add(this.ingredient);
      return nonNullList;
   }

   public String getGroup() {
      return this.group;
   }

   public ItemStack getToastSymbol() {
      return new ItemStack((ItemLike)AetherBlocks.INCUBATOR.get());
   }

   public RecipeSerializer<?> getSerializer() {
      return (RecipeSerializer<?>)AetherRecipeSerializers.INCUBATION.get();
   }

   public RecipeType<?> getType() {
      return this.type;
   }

   public static class Serializer implements RecipeSerializer<IncubationRecipe> {
      public MapCodec<IncubationRecipe> codec() {
         return RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                  Codec.STRING.optionalFieldOf("group", "").forGetter(p_300832_ -> p_300832_.group),
                  Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(recipe -> recipe.ingredient),
                  BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("entity").forGetter(recipe -> recipe.entity),
                  CompoundTag.CODEC.optionalFieldOf("tag").forGetter(recipe -> recipe.tag),
                  Codec.INT.fieldOf("incubationtime").orElse(500).forGetter(recipe -> recipe.incubationTime)
               )
               .apply(instance, IncubationRecipe::new)
         );
      }

      public StreamCodec<RegistryFriendlyByteBuf, IncubationRecipe> streamCodec() {
         return StreamCodec.of(this::toNetwork, this::fromNetwork);
      }

      @Nullable
      public IncubationRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
         String group = buffer.readUtf();
         Ingredient ingredient = (Ingredient)Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
         EntityType<?> entityType = (EntityType<?>)EntityType.byString(buffer.readUtf())
            .orElseThrow(() -> new JsonSyntaxException("Entity type cannot be found"));
         Optional<CompoundTag> tag = buffer.readOptional(FriendlyByteBuf::readNbt);
         int incubationTime = buffer.readVarInt();
         return new IncubationRecipe(group, ingredient, entityType, tag, incubationTime);
      }

      public void toNetwork(RegistryFriendlyByteBuf buffer, IncubationRecipe recipe) {
         buffer.writeUtf(recipe.group);
         Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.ingredient);
         buffer.writeUtf(EntityType.getKey(recipe.getEntity()).toString());
         buffer.writeOptional(recipe.tag, FriendlyByteBuf::writeNbt);
         buffer.writeVarInt(recipe.getIncubationTime());
      }
   }
}
