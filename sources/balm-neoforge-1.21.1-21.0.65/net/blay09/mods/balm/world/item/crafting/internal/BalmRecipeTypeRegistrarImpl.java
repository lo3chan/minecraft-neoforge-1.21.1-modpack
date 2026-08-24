package net.blay09.mods.balm.world.item.crafting.internal;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import net.blay09.mods.balm.core.BalmRegistrar;
import net.blay09.mods.balm.world.item.crafting.BalmRecipeSerializerRegistration;
import net.blay09.mods.balm.world.item.crafting.BalmRecipeTypeRegistrar;
import net.blay09.mods.balm.world.item.crafting.BalmRecipeTypeRegistration;
import net.blay09.mods.balm.world.item.crafting.DeferredRecipeType;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class BalmRecipeTypeRegistrarImpl implements BalmRecipeTypeRegistrar {
   private final BalmRegistrar registrar;
   private final String namespace;

   public BalmRecipeTypeRegistrarImpl(BalmRegistrar registrar, String namespace) {
      this.registrar = registrar;
      this.namespace = namespace;
   }

   @Override
   public <TRecipeInput extends RecipeInput, TRecipe extends Recipe<TRecipeInput>> BalmRecipeTypeRegistration<TRecipeInput, TRecipe> register(
      String name, Function<ResourceLocation, ? extends RecipeType<TRecipe>> constructor
   ) {
      ResourceLocation identifier = ResourceLocation.fromNamespaceAndPath(this.namespace, name);
      ResourceKey<RecipeType<?>> resourceKey = ResourceKey.create(Registries.RECIPE_TYPE, identifier);
      Holder<RecipeType<?>> holder = this.registrar.register(resourceKey, constructor::apply);
      return new BalmRecipeTypeRegistrarImpl.RecipeTypeRegistrationImpl<>(this, holder);
   }

   @Override
   public <TRecipeInput extends RecipeInput, TRecipe extends Recipe<TRecipeInput>> BalmRecipeSerializerRegistration<TRecipe> registerSerializer(
      String name, Function<ResourceLocation, RecipeSerializer<TRecipe>> constructor
   ) {
      ResourceLocation id = ResourceLocation.fromNamespaceAndPath(this.namespace, name);
      ResourceKey<RecipeSerializer<?>> key = ResourceKey.create(Registries.RECIPE_SERIALIZER, id);
      Holder<RecipeSerializer<?>> holder = this.registrar.register(key, constructor::apply);
      return new BalmRecipeTypeRegistrarImpl.RecipeSerializerRegistrationImpl<>(holder);
   }

   private static class DeferredRecipeTypeImpl<TRecipeInput extends RecipeInput, TRecipe extends Recipe<TRecipeInput>>
      implements DeferredRecipeType<TRecipeInput, TRecipe> {
      private final Holder<RecipeType<TRecipe>> type;
      private final Holder<RecipeSerializer<TRecipe>> serializer;

      private DeferredRecipeTypeImpl(Holder<RecipeType<TRecipe>> type, Holder<RecipeSerializer<TRecipe>> serializer) {
         this.type = type;
         this.serializer = serializer;
      }

      @Override
      public RecipeSerializer<TRecipe> serializer() {
         if (this.serializer == null) {
            throw new IllegalStateException("Serializer not registered for recipe type " + ((ResourceKey)this.type.unwrapKey().orElseThrow()).location());
         } else {
            return (RecipeSerializer<TRecipe>)this.serializer.value();
         }
      }

      @Override
      public Optional<RecipeHolder<TRecipe>> getRecipeFor(Level level, TRecipeInput input, ResourceKey<Recipe<?>> lastRecipe) {
         if (level instanceof ServerLevel serverLevel) {
            RecipeManager recipeManager = serverLevel.getServer().getRecipeManager();
            return recipeManager.getRecipeFor((RecipeType)this.type.value(), input, level, lastRecipe.location());
         } else {
            return Optional.empty();
         }
      }

      @Override
      public Optional<RecipeHolder<TRecipe>> getRecipeFor(Level level, TRecipeInput input, RecipeHolder<TRecipe> lastRecipe) {
         if (level instanceof ServerLevel serverLevel) {
            RecipeManager recipeManager = serverLevel.getServer().getRecipeManager();
            return recipeManager.getRecipeFor((RecipeType)this.type.value(), input, level, lastRecipe);
         } else {
            return Optional.empty();
         }
      }

      @Override
      public RecipeType<TRecipe> type() {
         return (RecipeType<TRecipe>)this.type.value();
      }
   }

   private static class RecipeSerializerRegistrationImpl<T extends Recipe<?>> implements BalmRecipeSerializerRegistration<T> {
      private final Holder<RecipeSerializer<T>> holder;

      private RecipeSerializerRegistrationImpl(Holder<?> holder) {
         this.holder = (Holder<RecipeSerializer<T>>)holder;
      }

      @Override
      public Holder<RecipeSerializer<T>> asHolder() {
         return this.holder;
      }
   }

   private static class RecipeTypeRegistrationImpl<TRecipeInput extends RecipeInput, TRecipe extends Recipe<TRecipeInput>>
      implements BalmRecipeTypeRegistration<TRecipeInput, TRecipe> {
      private final BalmRecipeTypeRegistrar recipeTypeRegistrar;
      private final Holder<RecipeType<TRecipe>> holder;
      private BalmRecipeSerializerRegistration<TRecipe> serializerRegistration;

      private RecipeTypeRegistrationImpl(BalmRecipeTypeRegistrar recipeTypeRegistrar, Holder<?> holder) {
         this.recipeTypeRegistrar = recipeTypeRegistrar;
         this.holder = (Holder<RecipeType<TRecipe>>)holder;
      }

      @Override
      public Holder<RecipeType<TRecipe>> asHolder() {
         return this.holder;
      }

      @Override
      public BalmRecipeTypeRegistration<TRecipeInput, TRecipe> withSerializer(Supplier<RecipeSerializer<TRecipe>> constructor) {
         String name = ((ResourceKey)this.holder.unwrapKey().orElseThrow()).location().getPath();
         this.serializerRegistration = this.recipeTypeRegistrar.registerSerializer(name, id -> constructor.get());
         return this;
      }

      @Override
      public DeferredRecipeType<TRecipeInput, TRecipe> asDeferredRecipeType() {
         return new BalmRecipeTypeRegistrarImpl.DeferredRecipeTypeImpl<>(
            this.holder, this.serializerRegistration != null ? this.serializerRegistration.asHolder() : null
         );
      }
   }
}
