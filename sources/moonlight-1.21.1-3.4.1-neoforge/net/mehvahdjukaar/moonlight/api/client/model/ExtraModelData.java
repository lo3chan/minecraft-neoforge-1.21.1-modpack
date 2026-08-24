package net.mehvahdjukaar.moonlight.api.client.model;

import java.util.Map;
import net.mehvahdjukaar.moonlight.api.client.model.platform.ExtraModelDataImpl;
import org.jetbrains.annotations.Nullable;

public interface ExtraModelData {
   ExtraModelData EMPTY = ExtraModelData.ClassLoadingBs.getInstance();

   @Nullable
   <T> T get(ModelDataKey<T> var1);

   Map<ModelDataKey<?>, Object> values();

   default boolean isEmpty() {
      return this == EMPTY;
   }

   static ExtraModelData.Builder builder() {
      return ExtraModelDataImpl.builder();
   }

   public interface Builder {
      <A> ExtraModelData.Builder with(ModelDataKey<A> var1, A var2);

      ExtraModelData build();
   }

   public static class ClassLoadingBs {
      static ExtraModelData getInstance() {
         return ExtraModelData.ClassLoadingBs.Holder.INSTANCE;
      }

      private static class Holder {
         static final ExtraModelData INSTANCE = ExtraModelData.builder().build();
      }
   }
}
