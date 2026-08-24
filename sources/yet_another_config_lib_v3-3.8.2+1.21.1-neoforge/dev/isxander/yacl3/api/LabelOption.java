package dev.isxander.yacl3.api;

import dev.isxander.yacl3.impl.LabelOptionImpl;
import java.util.Collection;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public interface LabelOption extends Option<Component> {
   @NotNull
   Component label();

   static LabelOption create(@NotNull Component label) {
      return new LabelOptionImpl(label);
   }

   static LabelOption.Builder createBuilder() {
      return new LabelOptionImpl.BuilderImpl();
   }

   public interface Builder {
      LabelOption.Builder state(@NotNull StateManager<Component> var1);

      LabelOption.Builder line(@NotNull Component var1);

      LabelOption.Builder lines(@NotNull Collection<? extends Component> var1);

      LabelOption build();
   }
}
