package at.petrak.hexcasting.common.recipe;

import at.petrak.hexcasting.common.recipe.ingredient.StateIngredient;
import at.petrak.hexcasting.common.recipe.ingredient.StateIngredientHelper;
import at.petrak.hexcasting.common.recipe.ingredient.brainsweep.BrainsweepeeIngredient;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;

public record BrainsweepRecipe(ResourceLocation id, StateIngredient blockIn, BrainsweepeeIngredient entityIn, long mediaCost, BlockState result)
   implements Recipe<RecipeInput> {
   public boolean matches(BlockState blockIn, Entity victim, ServerLevel level) {
      return this.blockIn.test(blockIn) && this.entityIn.test(victim, level);
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public RecipeType<?> getType() {
      return HexRecipeStuffRegistry.BRAINSWEEP_TYPE;
   }

   public RecipeSerializer<?> getSerializer() {
      return HexRecipeStuffRegistry.BRAINSWEEP;
   }

   public boolean matches(RecipeInput pContainer, Level pLevel) {
      return false;
   }

   public ItemStack assemble(RecipeInput pContainer, Provider access) {
      return ItemStack.EMPTY;
   }

   public boolean canCraftInDimensions(int pWidth, int pHeight) {
      return false;
   }

   public ItemStack getResultItem(Provider registryAccess) {
      return ItemStack.EMPTY.copy();
   }

   public static BlockState copyProperties(BlockState original, BlockState copyTo) {
      for (Property prop : original.getProperties()) {
         if (copyTo.hasProperty(prop)) {
            copyTo = (BlockState)copyTo.setValue(prop, original.getValue(prop));
         }
      }

      return copyTo;
   }

   public static class Serializer extends RecipeSerializerBase<BrainsweepRecipe> {
      private static final ResourceLocation CODEC_ID = ResourceLocation.fromNamespaceAndPath("hexcasting", "codec");
      private static final Codec<StateIngredient> STATE_INGREDIENT_CODEC = Codec.PASSTHROUGH
         .xmap(
            dynamic -> StateIngredientHelper.deserialize(((JsonElement)dynamic.convert(JsonOps.INSTANCE).getValue()).getAsJsonObject()),
            ingredient -> new Dynamic(JsonOps.INSTANCE, ingredient.serialize())
         );
      private static final Codec<BrainsweepeeIngredient> ENTITY_INGREDIENT_CODEC = Codec.PASSTHROUGH
         .xmap(
            dynamic -> BrainsweepeeIngredient.deserialize(((JsonElement)dynamic.convert(JsonOps.INSTANCE).getValue()).getAsJsonObject()),
            ingredient -> new Dynamic(JsonOps.INSTANCE, ingredient.serialize())
         );
      private static final Codec<BlockState> BLOCK_STATE_CODEC = Codec.PASSTHROUGH
         .xmap(
            dynamic -> StateIngredientHelper.readBlockState(((JsonElement)dynamic.convert(JsonOps.INSTANCE).getValue()).getAsJsonObject()),
            state -> new Dynamic(JsonOps.INSTANCE, StateIngredientHelper.serializeBlockState(state))
         );
      private static final MapCodec<BrainsweepRecipe> CODEC = RecordCodecBuilder.mapCodec(
         instance -> instance.group(
               STATE_INGREDIENT_CODEC.fieldOf("blockIn").forGetter(BrainsweepRecipe::blockIn),
               ENTITY_INGREDIENT_CODEC.fieldOf("entityIn").forGetter(BrainsweepRecipe::entityIn),
               Codec.LONG.fieldOf("cost").forGetter(BrainsweepRecipe::mediaCost),
               BLOCK_STATE_CODEC.fieldOf("result").forGetter(BrainsweepRecipe::result)
            )
            .apply(instance, (blockIn, entityIn, cost, result) -> new BrainsweepRecipe(CODEC_ID, blockIn, entityIn, cost, result))
      );

      public MapCodec<BrainsweepRecipe> codec() {
         return CODEC;
      }

      public StreamCodec<RegistryFriendlyByteBuf, BrainsweepRecipe> streamCodec() {
         return new StreamCodec<RegistryFriendlyByteBuf, BrainsweepRecipe>() {
            public BrainsweepRecipe decode(RegistryFriendlyByteBuf buf) {
               return Serializer.this.fromNetwork(BrainsweepRecipe.Serializer.CODEC_ID, buf);
            }

            public void encode(RegistryFriendlyByteBuf buf, BrainsweepRecipe recipe) {
               Serializer.this.toNetwork(buf, recipe);
            }
         };
      }

      @NotNull
      public BrainsweepRecipe fromJson(ResourceLocation recipeID, JsonObject json) {
         StateIngredient blockIn = StateIngredientHelper.deserialize(GsonHelper.getAsJsonObject(json, "blockIn"));
         BrainsweepeeIngredient villagerIn = BrainsweepeeIngredient.deserialize(GsonHelper.getAsJsonObject(json, "entityIn"));
         int cost = GsonHelper.getAsInt(json, "cost");
         BlockState result = StateIngredientHelper.readBlockState(GsonHelper.getAsJsonObject(json, "result"));
         return new BrainsweepRecipe(recipeID, blockIn, villagerIn, cost, result);
      }

      public void toNetwork(FriendlyByteBuf buf, BrainsweepRecipe recipe) {
         recipe.blockIn.write(buf);
         recipe.entityIn.wrapWrite(buf);
         buf.writeVarLong(recipe.mediaCost);
         buf.writeVarInt(Block.getId(recipe.result));
      }

      @NotNull
      public BrainsweepRecipe fromNetwork(ResourceLocation recipeID, FriendlyByteBuf buf) {
         StateIngredient blockIn = StateIngredientHelper.read(buf);
         BrainsweepeeIngredient brainsweepeeIn = BrainsweepeeIngredient.read(buf);
         long cost = buf.readVarLong();
         BlockState result = Block.stateById(buf.readVarInt());
         return new BrainsweepRecipe(recipeID, blockIn, brainsweepeeIn, cost, result);
      }
   }
}
