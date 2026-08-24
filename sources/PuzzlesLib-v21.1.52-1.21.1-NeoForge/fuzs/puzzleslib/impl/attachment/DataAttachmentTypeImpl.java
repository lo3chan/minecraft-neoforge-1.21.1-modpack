package fuzs.puzzleslib.impl.attachment;

import com.google.common.collect.ImmutableMap;
import fuzs.puzzleslib.api.attachment.v4.DataAttachmentType;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import net.minecraft.core.RegistryAccess;
import org.jetbrains.annotations.Nullable;

public final class DataAttachmentTypeImpl<T, V> implements DataAttachmentType<T, V> {
   private final AttachmentTypeAdapter<T, V> attachmentType;
   private final Function<T, RegistryAccess> registryAccessExtractor;
   private final Map<Predicate<T>, Function<RegistryAccess, V>> defaultValues;

   public DataAttachmentTypeImpl(
      AttachmentTypeAdapter<T, V> attachmentType,
      Function<T, RegistryAccess> registryAccessExtractor,
      Map<Predicate<T>, Function<RegistryAccess, V>> defaultValues
   ) {
      this.attachmentType = attachmentType;
      this.registryAccessExtractor = registryAccessExtractor;
      this.defaultValues = ImmutableMap.copyOf(defaultValues);
   }

   @Nullable
   private V getDefaultValue(T holder) {
      for (Entry<Predicate<T>, Function<RegistryAccess, V>> entry : this.defaultValues.entrySet()) {
         if (entry.getKey().test(holder)) {
            return entry.getValue().apply(this.registryAccessExtractor.apply(holder));
         }
      }

      return null;
   }

   @Nullable
   @Override
   public V get(T holder) {
      if (!this.attachmentType.hasData(holder)) {
         V defaultValue = this.getDefaultValue(holder);
         if (defaultValue != null) {
            this.attachmentType.setData(holder, defaultValue);
         }
      }

      if (this.attachmentType.hasData(holder)) {
         V value = this.attachmentType.getData(holder);
         Objects.requireNonNull(value, () -> "value for " + this.attachmentType.id() + " is null");
         return value;
      } else {
         return null;
      }
   }

   @Override
   public V getOrDefault(T holder, V defaultValue) {
      V value = this.get(holder);
      return value != null ? value : defaultValue;
   }

   @Override
   public boolean has(T holder) {
      return this.attachmentType.hasData(holder) || this.getDefaultValue(holder) != null;
   }

   @Override
   public void set(T holder, @Nullable V value) {
      this.setWithReturn(holder, value);
   }

   @Nullable
   private V setWithReturn(T holder, @Nullable V value) {
      V oldValue = this.attachmentType.hasData(holder) ? this.attachmentType.getData(holder) : null;
      if (!Objects.equals(oldValue, value)) {
         return value != null ? this.attachmentType.setData(holder, value) : this.attachmentType.removeData(holder);
      } else {
         return oldValue;
      }
   }

   @Nullable
   @Override
   public V remove(T holder) {
      return this.setWithReturn(holder, null);
   }

   @Override
   public void apply(T holder, UnaryOperator<V> valueUpdater) {
      this.set(holder, valueUpdater.apply(this.get(holder)));
   }
}
