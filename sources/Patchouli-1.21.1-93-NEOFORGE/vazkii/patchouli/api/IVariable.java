package vazkii.patchouli.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.BuiltInRegistries;
import org.jetbrains.annotations.Nullable;

public interface IVariable {
   <T> T as(Class<T> var1);

   default <T> T as(Class<T> clazz, T def) {
      return this.unwrap().isJsonNull() ? def : this.as(clazz);
   }

   JsonElement unwrap();

   default String asString() {
      return this.asString("");
   }

   default String asString(String def) {
      return this.unwrap().isJsonNull() ? def : this.unwrap().getAsString();
   }

   default Number asNumber() {
      return this.asNumber(0);
   }

   default Number asNumber(Number def) {
      return this.unwrap().isJsonNull() ? def : this.unwrap().getAsNumber();
   }

   default boolean asBoolean() {
      return this.asBoolean(false);
   }

   default boolean asBoolean(boolean def) {
      return this.unwrap().isJsonNull()
         ? def
         : !this.unwrap().getAsString().equals("false") && !this.unwrap().getAsString().isEmpty() && this.unwrap().getAsBoolean();
   }

   default Stream<IVariable> asStream(Provider registries) {
      return StreamSupport.<JsonElement>stream(this.unwrap().getAsJsonArray().spliterator(), false).map(json -> wrap(json, registries));
   }

   default Stream<IVariable> asStreamOrSingleton(Provider registries) {
      return this.unwrap().isJsonArray() ? this.asStream(registries) : Stream.of(this);
   }

   default List<IVariable> asList(Provider registries) {
      return this.asStream(registries).collect(Collectors.toList());
   }

   default List<IVariable> asListOrSingleton(Provider registries) {
      return this.asStreamOrSingleton(registries).collect(Collectors.toList());
   }

   static <T> IVariable from(@Nullable T object, Provider registries) {
      return object != null ? VariableHelper.instance().createFromObject(object, registries) : empty();
   }

   static IVariable wrap(@Nullable JsonElement elem, Provider registries) {
      return elem != null ? VariableHelper.instance().createFromJson(elem, registries) : empty();
   }

   static IVariable wrapList(Iterable<IVariable> elems, Provider registries) {
      JsonArray arr = new JsonArray();

      for (IVariable v : elems) {
         arr.add(v.unwrap());
      }

      return wrap(arr, registries);
   }

   @Deprecated
   static IVariable wrap(@Nullable Number n) {
      return wrap(n, RegistryAccess.EMPTY);
   }

   static IVariable wrap(@Nullable Number n, Provider registries) {
      return n != null ? wrap(new JsonPrimitive(n), registries) : empty();
   }

   @Deprecated
   static IVariable wrap(@Nullable Boolean b) {
      return wrap(b, RegistryAccess.EMPTY);
   }

   static IVariable wrap(@Nullable Boolean b, Provider registries) {
      return b != null ? wrap(new JsonPrimitive(b), registries) : empty();
   }

   @Deprecated
   static IVariable wrap(@Nullable String s) {
      return wrap(s, RegistryAccess.EMPTY);
   }

   static IVariable wrap(@Nullable String s, Provider registries) {
      return s != null ? wrap(new JsonPrimitive(s), registries) : empty();
   }

   static IVariable empty() {
      return wrap(JsonNull.INSTANCE, RegistryAccess.EMPTY);
   }

   public static class Serializer implements JsonDeserializer<IVariable> {
      private Provider registryCache;

      public IVariable deserialize(JsonElement elem, Type t, JsonDeserializationContext c) {
         if (this.registryCache == null || this.registryCache.listRegistries().findFirst().isEmpty()) {
            this.registryCache = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);
         }

         return IVariable.wrap(elem, this.registryCache);
      }

      public void setRegistries(Provider registries) {
         this.registryCache = registries;
      }
   }
}
