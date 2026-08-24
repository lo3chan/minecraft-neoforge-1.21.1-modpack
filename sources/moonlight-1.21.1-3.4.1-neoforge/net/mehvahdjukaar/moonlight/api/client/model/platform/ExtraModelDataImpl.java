package net.mehvahdjukaar.moonlight.api.client.model.platform;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.util.Map;
import java.util.Objects;
import net.mehvahdjukaar.moonlight.api.client.model.ExtraModelData;
import net.mehvahdjukaar.moonlight.api.client.model.ModelDataKey;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import org.jetbrains.annotations.Nullable;

public record ExtraModelDataImpl(ModelData data) implements ExtraModelData {
   private static final Object2ObjectArrayMap<ModelDataKey<?>, ModelProperty<?>> KEYS_TO_PROP = new Object2ObjectArrayMap();

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         ExtraModelDataImpl that = (ExtraModelDataImpl)o;
         if (!Objects.equals(this.data.getProperties(), that.data.getProperties())) {
            return false;
         } else {
            for (ModelProperty<?> p : this.data.getProperties()) {
               if (!Objects.equals(this.data.get(p), that.data.get(p))) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   @Nullable
   @Override
   public <T> T get(ModelDataKey<T> key) {
      ModelProperty<T> prop = (ModelProperty<T>)KEYS_TO_PROP.get(key);
      return (T)(prop == null ? null : this.data.get(prop));
   }

   @Override
   public Map<ModelDataKey<?>, Object> values() {
      return ImmutableMap.copyOf(KEYS_TO_PROP);
   }

   public static ExtraModelData.Builder builder() {
      return new ExtraModelDataImpl.Builder();
   }

   private static class Builder implements ExtraModelData.Builder {
      private final net.neoforged.neoforge.client.model.data.ModelData.Builder map = ModelData.builder();

      Builder() {
      }

      @Override
      public <A> ExtraModelData.Builder with(ModelDataKey<A> key, A data) {
         ModelProperty<A> prop = (ModelProperty<A>)ExtraModelDataImpl.KEYS_TO_PROP.computeIfAbsent(key, k -> new ModelProperty());
         this.map.with(prop, data);
         return this;
      }

      public ExtraModelDataImpl build() {
         return new ExtraModelDataImpl(this.map.build());
      }
   }
}
