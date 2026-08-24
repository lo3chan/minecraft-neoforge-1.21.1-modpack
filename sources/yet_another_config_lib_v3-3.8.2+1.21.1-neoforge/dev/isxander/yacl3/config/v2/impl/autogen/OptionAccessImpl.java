package dev.isxander.yacl3.config.v2.impl.autogen;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.config.v2.api.autogen.OptionAccess;
import dev.isxander.yacl3.impl.utils.YACLConstants;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import org.jetbrains.annotations.Nullable;

public class OptionAccessImpl implements OptionAccess {
   private final Map<String, Option<?>> storage = new HashMap<>();
   private final Map<String, Consumer<Option<?>>> scheduledOperations = new HashMap<>();

   @Nullable
   @Override
   public Option<?> getOption(String fieldName) {
      return this.storage.get(fieldName);
   }

   @Override
   public void scheduleOptionOperation(String fieldName, Consumer<Option<?>> optionConsumer) {
      if (this.storage.containsKey(fieldName)) {
         optionConsumer.accept(this.storage.get(fieldName));
      } else {
         this.scheduledOperations.merge(fieldName, optionConsumer, Consumer::andThen);
      }
   }

   public void putOption(String fieldName, Option<?> option) {
      this.storage.put(fieldName, option);
      Consumer<Option<?>> consumer = this.scheduledOperations.remove(fieldName);
      if (consumer != null) {
         consumer.accept(option);
      }
   }

   public void checkBadOperations() {
      if (!this.scheduledOperations.isEmpty()) {
         YACLConstants.LOGGER
            .warn(
               "There are scheduled operations on the `OptionAccess` that tried to reference fields that do not exist. The following have been referenced that do not exist: "
                  + String.join(", ", this.scheduledOperations.keySet())
            );
      }
   }
}
