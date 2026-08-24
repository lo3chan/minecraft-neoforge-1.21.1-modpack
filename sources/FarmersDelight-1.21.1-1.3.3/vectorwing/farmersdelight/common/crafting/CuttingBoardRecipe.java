package vectorwing.farmersdelight.common.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;
import vectorwing.farmersdelight.common.crafting.ingredient.ChanceResult;
import vectorwing.farmersdelight.common.registry.ModRecipeSerializers;
import vectorwing.farmersdelight.common.registry.ModRecipeTypes;

public class CuttingBoardRecipe implements Recipe<CuttingBoardRecipeInput> {
   public static final int MAX_RESULTS = 4;
   private final String group;
   private final Ingredient input;
   private final Ingredient tool;
   private final NonNullList<ChanceResult> results;
   private final Optional<SoundEvent> soundEvent;

   public CuttingBoardRecipe(String group, Ingredient input, Ingredient tool, NonNullList<ChanceResult> results, Optional<SoundEvent> soundEvent) {
      this.group = group;
      this.input = input;
      this.tool = tool;
      this.results = results;
      this.soundEvent = soundEvent;
   }

   public boolean matches(CuttingBoardRecipeInput input, Level level) {
      return this.input.test(input.item()) && this.tool.test(input.tool());
   }

   public ItemStack assemble(CuttingBoardRecipeInput inv, Provider provider) {
      return ((ChanceResult)this.results.getFirst()).stack().copy();
   }

   public boolean isSpecial() {
      return true;
   }

   public String getGroup() {
      return this.group;
   }

   public NonNullList<Ingredient> getIngredients() {
      NonNullList<Ingredient> nonnulllist = NonNullList.create();
      nonnulllist.add(this.input);
      return nonnulllist;
   }

   public Ingredient getTool() {
      return this.tool;
   }

   public ItemStack getResultItem(Provider provider) {
      return ((ChanceResult)this.results.getFirst()).stack();
   }

   public List<ItemStack> getResults() {
      return this.getRollableResults().stream().map(ChanceResult::stack).collect(Collectors.toList());
   }

   public NonNullList<ChanceResult> getRollableResults() {
      return this.results;
   }

   public List<ItemStack> rollResults(RandomSource random, int fortuneLevel, RecipeWrapper inventory) {
      List<ItemStack> results = new ArrayList<>();

      for (ChanceResult output : this.getRollableResults()) {
         ItemStack stack = output.rollOutput(random, fortuneLevel);
         if (!stack.isEmpty()) {
            results.add(stack);
         }
      }

      return results;
   }

   public Optional<SoundEvent> getSoundEvent() {
      return this.soundEvent;
   }

   protected int getMaxInputCount() {
      return 1;
   }

   public boolean canCraftInDimensions(int width, int height) {
      return width * height >= this.getMaxInputCount();
   }

   public RecipeSerializer<?> getSerializer() {
      return ModRecipeSerializers.CUTTING.get();
   }

   public RecipeType<?> getType() {
      return ModRecipeTypes.CUTTING.get();
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         CuttingBoardRecipe that = (CuttingBoardRecipe)o;
         if (!this.getGroup().equals(that.getGroup())) {
            return false;
         } else if (!this.input.equals(that.input)) {
            return false;
         } else if (!this.getTool().equals(that.getTool())) {
            return false;
         } else {
            return !this.getResults().equals(that.getResults()) ? false : Objects.equals(this.soundEvent, that.soundEvent);
         }
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int result = this.getGroup() != null ? this.getGroup().hashCode() : 0;
      result = 31 * result + this.input.hashCode();
      result = 31 * result + this.getTool().hashCode();
      result = 31 * result + this.getResults().hashCode();
      return 31 * result + this.soundEvent.map(Object::hashCode).orElse(0);
   }

   public static class Serializer implements RecipeSerializer<CuttingBoardRecipe> {
      public static final StreamCodec<RegistryFriendlyByteBuf, CuttingBoardRecipe> STREAM_CODEC = StreamCodec.of(
         CuttingBoardRecipe.Serializer::toNetwork, CuttingBoardRecipe.Serializer::fromNetwork
      );
      private static final MapCodec<CuttingBoardRecipe> CODEC = RecordCodecBuilder.mapCodec(
         inst -> inst.group(
               Codec.STRING.optionalFieldOf("group", "").forGetter(CuttingBoardRecipe::getGroup),
               Ingredient.LIST_CODEC_NONEMPTY.fieldOf("ingredients").flatXmap(ingredients -> {
                  if (ingredients.isEmpty()) {
                     return DataResult.error(() -> "No ingredients for cutting recipe");
                  } else if (ingredients.size() > 1) {
                     return DataResult.error(() -> "Too many ingredients for cutting recipe! Please define only one ingredient");
                  } else {
                     NonNullList<Ingredient> nonNullList = NonNullList.create();
                     nonNullList.add((Ingredient)ingredients.get(0));
                     return DataResult.success((Ingredient)ingredients.get(0));
                  }
               }, ingredient -> {
                  NonNullList<Ingredient> nonNullList = NonNullList.create();
                  nonNullList.add(ingredient);
                  return DataResult.success(nonNullList);
               }).forGetter(cuttingBoardRecipe -> cuttingBoardRecipe.input),
               Ingredient.CODEC.fieldOf("tool").forGetter(CuttingBoardRecipe::getTool),
               Codec.list(ChanceResult.CODEC).fieldOf("result").flatXmap(chanceResults -> {
                  if (chanceResults.size() > 4) {
                     return DataResult.error(() -> "Too many results for cutting recipe! The maximum quantity of unique results is 4");
                  } else {
                     NonNullList<ChanceResult> nonNullList = NonNullList.create();
                     nonNullList.addAll(chanceResults);
                     return DataResult.success(nonNullList);
                  }
               }, DataResult::success).forGetter(CuttingBoardRecipe::getRollableResults),
               SoundEvent.DIRECT_CODEC.optionalFieldOf("sound").forGetter(CuttingBoardRecipe::getSoundEvent)
            )
            .apply(inst, CuttingBoardRecipe::new)
      );

      public static CuttingBoardRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
         String group = buffer.readUtf(32767);
         Ingredient inputItem = (Ingredient)Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
         Ingredient tool = (Ingredient)Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
         int i = buffer.readVarInt();
         NonNullList<ChanceResult> results = NonNullList.withSize(i, ChanceResult.EMPTY);
         results.replaceAll(ignored -> ChanceResult.read(buffer));
         Optional<SoundEvent> soundEvent = Optional.empty();
         if (buffer.readBoolean()) {
            Optional<Reference<SoundEvent>> holder = BuiltInRegistries.SOUND_EVENT.getHolder(buffer.readResourceKey(Registries.SOUND_EVENT));
            if (holder.isPresent() && holder.get().isBound()) {
               soundEvent = Optional.of((SoundEvent)holder.get().value());
            }
         }

         return new CuttingBoardRecipe(group, inputItem, tool, results, soundEvent);
      }

      public static void toNetwork(RegistryFriendlyByteBuf buffer, CuttingBoardRecipe recipe) {
         buffer.writeUtf(recipe.group);
         Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.input);
         Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.tool);
         buffer.writeVarInt(recipe.results.size());

         for (ChanceResult result : recipe.results) {
            result.write(buffer);
         }

         if (recipe.getSoundEvent().isPresent()) {
            Optional<ResourceKey<SoundEvent>> resourceKey = BuiltInRegistries.SOUND_EVENT.getResourceKey(recipe.getSoundEvent().get());
            resourceKey.ifPresentOrElse(rk -> {
               buffer.writeBoolean(true);
               buffer.writeResourceKey(rk);
            }, () -> buffer.writeBoolean(false));
         } else {
            buffer.writeBoolean(false);
         }
      }

      public MapCodec<CuttingBoardRecipe> codec() {
         return CODEC;
      }

      public StreamCodec<RegistryFriendlyByteBuf, CuttingBoardRecipe> streamCodec() {
         return STREAM_CODEC;
      }
   }
}
