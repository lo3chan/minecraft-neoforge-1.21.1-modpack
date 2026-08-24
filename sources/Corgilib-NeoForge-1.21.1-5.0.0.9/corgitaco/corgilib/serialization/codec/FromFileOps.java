package corgitaco.corgilib.serialization.codec;

import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.ListBuilder;
import com.mojang.serialization.RecordBuilder;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import net.minecraft.resources.DelegatingOps;
import org.jetbrains.annotations.NotNull;

public class FromFileOps<T> extends DelegatingOps<T> {
   private final FromFileOps.Access access;

   public FromFileOps(DynamicOps<T> dynamicOps, FromFileOps.Access access) {
      super(dynamicOps);
      this.access = access;
   }

   @NotNull
   public ListBuilder<T> listBuilder() {
      return this.delegate.listBuilder();
   }

   @NotNull
   public RecordBuilder<T> mapBuilder() {
      return this.delegate.mapBuilder();
   }

   public <E> Map<String, E> getAccess(String s) {
      return this.access.get(s);
   }

   public static final class Access {
      private final Map<String, Map<String, ?>> registry = new HashMap<>();

      public Map<String, Map<String, ?>> registry() {
         return this.registry;
      }

      public <T> Map<String, T> get(String s) {
         return (Map<String, T>)this.registry.computeIfAbsent(s, key -> new HashMap<>());
      }

      @Override
      public boolean equals(Object obj) {
         if (obj == this) {
            return true;
         } else if (obj != null && obj.getClass() == this.getClass()) {
            FromFileOps.Access that = (FromFileOps.Access)obj;
            return Objects.equals(this.registry, that.registry);
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         return Objects.hash(this.registry);
      }

      @Override
      public String toString() {
         return "Access[registry=" + this.registry + "]";
      }
   }
}
