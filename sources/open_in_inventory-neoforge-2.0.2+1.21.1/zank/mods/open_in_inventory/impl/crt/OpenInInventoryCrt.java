package zank.mods.open_in_inventory.impl.crt;

import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import org.openzen.zencode.java.ZenCodeType.Field;
import org.openzen.zencode.java.ZenCodeType.Method;
import org.openzen.zencode.java.ZenCodeType.Name;
import zank.mods.open_in_inventory.api.OpenActionRegistry;

@Name("mods.open_in_inventory.OpenInInventory")
@ZenRegister
public abstract class OpenInInventoryCrt {
   @Field
   public static final OpenInInventoryCrt.LessGenericHandlerRegistry<OpenActionRegistry> ACTION_PROVIDERS = new OpenInInventoryCrt.LessGenericHandlerRegistry<>();
   @Field
   public static final OpenInInventoryCrt.LessGenericHandlerRegistry<Map<String, Collection<String>>> REPLACE_TEMPLATE_PROVIDERS = new OpenInInventoryCrt.LessGenericHandlerRegistry<>();

   @Name("mods.open_in_inventory.util.HandlerRegistry")
   public static class LessGenericHandlerRegistry<T> {
      private final List<Consumer<T>> handlers = new ArrayList<>();

      @Method
      public void register(Consumer<T> handler) {
         this.handlers.add(Objects.requireNonNull(handler));
      }

      @Method
      public void clear() {
         this.handlers.clear();
      }

      @Method
      public List<Consumer<T>> view() {
         return Collections.unmodifiableList(this.handlers);
      }
   }
}
