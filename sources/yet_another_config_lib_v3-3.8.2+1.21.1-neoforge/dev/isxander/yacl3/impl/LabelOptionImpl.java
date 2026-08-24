package dev.isxander.yacl3.impl;

import com.google.common.collect.ImmutableSet;
import dev.isxander.yacl3.api.LabelOption;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionEventListener;
import dev.isxander.yacl3.api.StateManager;
import dev.isxander.yacl3.gui.controllers.LabelController;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Internal;

public class LabelOptionImpl extends OptionImpl<Component> implements LabelOption {
   public LabelOptionImpl(@NotNull StateManager<Component> stateManager, @NotNull Collection<OptionEventListener<Component>> optionEventListeners) {
      super(
         Component.literal("Label Option"),
         xva$0 -> OptionDescription.of(xva$0),
         LabelController::new,
         stateManager,
         true,
         ImmutableSet.of(),
         optionEventListeners
      );
   }

   public LabelOptionImpl(Component label) {
      this(StateManager.createImmutable(label), ImmutableSet.of());
   }

   @NotNull
   @Override
   public Component label() {
      return this.stateManager().get();
   }

   @Override
   public void setAvailable(boolean available) {
      throw new UnsupportedOperationException("Cannot change availability of label option");
   }

   @Internal
   public static final class BuilderImpl implements LabelOption.Builder {
      private StateManager<Component> stateManager;
      private final List<Component> lines = new ArrayList<>();

      @Override
      public LabelOption.Builder state(@NotNull StateManager<Component> stateManager) {
         Validate.notNull(stateManager, "`stateManager` must not be null", new Object[0]);
         Validate.isTrue(this.lines.isEmpty(), "Cannot set state manager if lines have already been defined", new Object[0]);
         this.stateManager = stateManager;
         return this;
      }

      @Override
      public LabelOption.Builder line(@NotNull Component line) {
         Validate.isTrue(
            this.stateManager == null,
            ".line() is a helper to create a state manager for you at build. If you have defined a custom state manager, do not use .line()",
            new Object[0]
         );
         Validate.notNull(line, "`line` must not be null", new Object[0]);
         this.lines.add(line);
         return this;
      }

      @Override
      public LabelOption.Builder lines(@NotNull Collection<? extends Component> lines) {
         Validate.isTrue(
            this.stateManager == null,
            ".lines() is a helper to create a state manager for you at build. If you have defined a custom state manager, do not use .lines()",
            new Object[0]
         );
         this.lines.addAll(lines);
         return this;
      }

      @Override
      public LabelOption build() {
         Validate.isTrue(this.stateManager != null || !this.lines.isEmpty(), "Cannot build label option without a state manager or lines", new Object[0]);
         if (!this.lines.isEmpty()) {
            MutableComponent text = Component.empty();
            Iterator<Component> iterator = this.lines.iterator();

            while (iterator.hasNext()) {
               text.append(iterator.next());
               if (iterator.hasNext()) {
                  text.append("\n");
               }
            }

            this.stateManager = StateManager.createSimple(new SelfContainedBinding<>(text));
         }

         return new LabelOptionImpl(this.stateManager, ImmutableSet.of());
      }
   }
}
