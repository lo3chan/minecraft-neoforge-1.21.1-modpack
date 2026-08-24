package me.lucko.spark.lib.adventure.text;

import java.util.Deque;
import java.util.List;
import java.util.Set;
import me.lucko.spark.lib.adventure.text.event.HoverEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.NonExtendable;

@FunctionalInterface
@NonExtendable
public interface ComponentIteratorType {
   ComponentIteratorType DEPTH_FIRST = (component, deque, flags) -> {
      if (flags.contains(ComponentIteratorFlag.INCLUDE_TRANSLATABLE_COMPONENT_ARGUMENTS) && component instanceof TranslatableComponent) {
         TranslatableComponent translatable = (TranslatableComponent)component;
         List<? extends ComponentLike> args = translatable.arguments();

         for (int i = args.size() - 1; i >= 0; i--) {
            deque.addFirst(args.get(i).asComponent());
         }
      }

      HoverEvent<?> hoverEvent = component.hoverEvent();
      if (hoverEvent != null) {
         HoverEvent.Action<?> action = hoverEvent.action();
         if (flags.contains(ComponentIteratorFlag.INCLUDE_HOVER_SHOW_ENTITY_NAME) && action == HoverEvent.Action.SHOW_ENTITY) {
            deque.addFirst(((HoverEvent.ShowEntity)hoverEvent.value()).name());
         } else if (flags.contains(ComponentIteratorFlag.INCLUDE_HOVER_SHOW_TEXT_COMPONENT) && action == HoverEvent.Action.SHOW_TEXT) {
            deque.addFirst((Component)hoverEvent.value());
         }
      }

      List<Component> children = component.children();

      for (int i = children.size() - 1; i >= 0; i--) {
         deque.addFirst(children.get(i));
      }
   };
   ComponentIteratorType BREADTH_FIRST = (component, deque, flags) -> {
      if (flags.contains(ComponentIteratorFlag.INCLUDE_TRANSLATABLE_COMPONENT_ARGUMENTS) && component instanceof TranslatableComponent) {
         for (TranslationArgument argument : ((TranslatableComponent)component).arguments()) {
            deque.add(argument.asComponent());
         }
      }

      HoverEvent<?> hoverEvent = component.hoverEvent();
      if (hoverEvent != null) {
         HoverEvent.Action<?> action = hoverEvent.action();
         if (flags.contains(ComponentIteratorFlag.INCLUDE_HOVER_SHOW_ENTITY_NAME) && action == HoverEvent.Action.SHOW_ENTITY) {
            deque.addLast(((HoverEvent.ShowEntity)hoverEvent.value()).name());
         } else if (flags.contains(ComponentIteratorFlag.INCLUDE_HOVER_SHOW_TEXT_COMPONENT) && action == HoverEvent.Action.SHOW_TEXT) {
            deque.addLast((Component)hoverEvent.value());
         }
      }

      deque.addAll(component.children());
   };

   void populate(@NotNull final Component component, @NotNull final Deque<Component> deque, @NotNull final Set<ComponentIteratorFlag> flags);
}
