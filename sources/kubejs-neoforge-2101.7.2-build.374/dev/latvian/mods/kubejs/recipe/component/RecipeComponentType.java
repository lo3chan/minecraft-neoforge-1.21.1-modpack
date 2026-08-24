package dev.latvian.mods.kubejs.recipe.component;

import com.mojang.serialization.MapCodec;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.RecipeTypeRegistryContext;
import dev.latvian.mods.kubejs.util.ID;
import java.util.function.Function;
import net.minecraft.resources.ResourceLocation;

public abstract class RecipeComponentType<T> {
   private final ResourceLocation id;
   private final String idString;

   public static <T> RecipeComponentType.Unit<T> unit(ResourceLocation id, Function<RecipeComponentType<T>, RecipeComponent<T>> instanceGetter) {
      return new RecipeComponentType.Unit<>(id, instanceGetter);
   }

   public static <T> RecipeComponentType.Unit<T> unit(ResourceLocation id, RecipeComponent<T> instance) {
      return new RecipeComponentType.Unit<>(id, new RecipeComponentType.Unit.Simple<>(instance));
   }

   public static <CT extends RecipeComponent<?>> RecipeComponentType<?> dynamic(ResourceLocation id, RecipeComponentCodecFactory<CT> codecFactory) {
      return new RecipeComponentType.Dynamic(id, codecFactory);
   }

   public static <CT extends RecipeComponent<?>> RecipeComponentType<?> dynamic(ResourceLocation id, MapCodec<CT> mapCodec) {
      return new RecipeComponentType.Dynamic(id, new RecipeComponentType.Dynamic.Simple(mapCodec));
   }

   public RecipeComponentType(ResourceLocation id) {
      this.id = id;
      this.idString = ID.reduceKjs(id);
   }

   public final ResourceLocation id() {
      return this.id;
   }

   @Override
   public final int hashCode() {
      return this.id.hashCode();
   }

   @Override
   public final boolean equals(Object obj) {
      return obj == this || obj instanceof RecipeComponentType<?> type && this.id.equals(type.id);
   }

   @Override
   public final String toString() {
      return this.idString;
   }

   public boolean isUnit() {
      return false;
   }

   public RecipeComponent<T> instance() {
      throw new NullPointerException("This recipe component type is not a unit type");
   }

   public abstract MapCodec<RecipeComponent<?>> mapCodec(RecipeTypeRegistryContext ctx);

   public RecipeKey<T> key(String name, ComponentRole role) {
      return this.instance().key(name, role);
   }

   public RecipeKey<T> inputKey(String name) {
      return this.key(name, ComponentRole.INPUT);
   }

   public RecipeKey<T> outputKey(String name) {
      return this.key(name, ComponentRole.OUTPUT);
   }

   public RecipeKey<T> otherKey(String name) {
      return this.key(name, ComponentRole.OTHER);
   }

   public static class Dynamic<T> extends RecipeComponentType<T> {
      private final RecipeComponentCodecFactory factory;

      private Dynamic(ResourceLocation id, RecipeComponentCodecFactory factory) {
         super(id);
         this.factory = factory;
      }

      @Override
      public MapCodec<RecipeComponent<?>> mapCodec(RecipeTypeRegistryContext ctx) {
         return this.factory.create(this, ctx);
      }

      private record Simple(MapCodec mapCodec) implements RecipeComponentCodecFactory<RecipeComponent<?>> {
         @Override
         public MapCodec<RecipeComponent<?>> create(RecipeComponentType<?> type, RecipeTypeRegistryContext ctx) {
            return this.mapCodec;
         }
      }
   }

   public static class Unit<T> extends RecipeComponentType<T> {
      private final RecipeComponent<T> instance;
      private MapCodec<RecipeComponent<?>> mapCodec;

      private Unit(ResourceLocation id, Function<RecipeComponentType<T>, RecipeComponent<T>> instanceGetter) {
         super(id);
         this.instance = instanceGetter.apply(this);
      }

      @Override
      public boolean isUnit() {
         return true;
      }

      @Override
      public final RecipeComponent<T> instance() {
         return this.instance;
      }

      @Override
      public MapCodec<RecipeComponent<?>> mapCodec(RecipeTypeRegistryContext ctx) {
         if (this.mapCodec == null) {
            this.mapCodec = MapCodec.unit(this.instance);
         }

         return this.mapCodec;
      }

      private record Simple<T>(RecipeComponent<T> value) implements Function<RecipeComponentType<T>, RecipeComponent<T>> {
         public RecipeComponent<T> apply(RecipeComponentType<T> type) {
            return this.value;
         }
      }
   }
}
