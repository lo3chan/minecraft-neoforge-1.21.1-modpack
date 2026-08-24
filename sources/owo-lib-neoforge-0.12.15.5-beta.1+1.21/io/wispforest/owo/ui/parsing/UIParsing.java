package io.wispforest.owo.ui.parsing;

import io.wispforest.owo.ui.component.BlockComponent;
import io.wispforest.owo.ui.component.ColorPickerComponent;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.DiscreteSliderComponent;
import io.wispforest.owo.ui.component.EntityComponent;
import io.wispforest.owo.ui.component.SlimSliderComponent;
import io.wispforest.owo.ui.component.SmallCheckboxComponent;
import io.wispforest.owo.ui.component.SpacerComponent;
import io.wispforest.owo.ui.component.SpriteComponent;
import io.wispforest.owo.ui.component.TextureComponent;
import io.wispforest.owo.ui.container.CollapsibleContainer;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.GridLayout;
import io.wispforest.owo.ui.container.ScrollContainer;
import io.wispforest.owo.ui.core.Component;
import io.wispforest.owo.ui.core.Sizing;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.ResourceLocationException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class UIParsing {
   private static final Map<String, Function<Element, Component>> COMPONENT_FACTORIES = new HashMap<>();

   /** @deprecated */
   @Internal
   public static void registerFactory(String componentTagName, Function<Element, Component> factory) {
      if (COMPONENT_FACTORIES.containsKey(componentTagName)) {
         throw new IllegalStateException("A component factory with name " + componentTagName + " is already registered");
      } else {
         COMPONENT_FACTORIES.put(componentTagName, factory);
      }
   }

   public static void registerFactory(ResourceLocation componentId, Function<Element, Component> factory) {
      registerFactory(componentId.getNamespace() + "." + componentId.getPath(), factory);
   }

   public static Function<Element, Component> getFactory(Element element) {
      Function<Element, Component> factory = COMPONENT_FACTORIES.get(element.getNodeName());
      if (factory == null) {
         throw new UIModelParsingException("Unknown component type: " + element.getNodeName());
      } else {
         return factory;
      }
   }

   public static <T extends Node> List<T> allChildrenOfType(Element element, short type) {
      ArrayList<T> list = new ArrayList<>();

      for (int i = 0; i < element.getChildNodes().getLength(); i++) {
         Node child = element.getChildNodes().item(i);
         if (child.getNodeType() == type) {
            list.add((T)child);
         }
      }

      return list;
   }

   public static Map<String, Element> childElements(Element element) {
      NodeList children = element.getChildNodes();
      HashMap<String, Element> map = new HashMap<>();

      for (int i = 0; i < children.getLength(); i++) {
         Node child = children.item(i);
         if (child.getNodeType() == 1) {
            if (map.containsKey(child.getNodeName())) {
               throw new UIModelParsingException("Duplicate child " + child.getNodeName() + " in element " + element.getNodeName());
            }

            map.put(child.getNodeName(), (Element)child);
         }
      }

      return map;
   }

   public static int parseSignedInt(Node node) {
      return parseInt(node, true);
   }

   public static int parseUnsignedInt(Node node) {
      return parseInt(node, false);
   }

   public static float parseFloat(Node node) {
      String data = node.getTextContent().strip();
      if (data.matches("-?\\d+(\\.\\d+)?")) {
         return Float.parseFloat(data);
      } else {
         throw new UIModelParsingException("Invalid value '" + data + "', expected a floating point number");
      }
   }

   public static double parseDouble(Node node) {
      String data = node.getTextContent().strip();
      if (data.matches("-?\\d+(\\.\\d+)?")) {
         return Double.parseDouble(data);
      } else {
         throw new UIModelParsingException("Invalid value '" + data + "', expected a double-precision floating point number");
      }
   }

   public static boolean parseBool(Node node) {
      return node.getTextContent().strip().equalsIgnoreCase("true");
   }

   public static ResourceLocation parseIdentifier(Node node) {
      try {
         return ResourceLocation.parse(node.getTextContent().strip());
      } catch (ResourceLocationException var2) {
         throw new UIModelParsingException("Invalid identifier '" + node.getTextContent() + "'", var2);
      }
   }

   public static net.minecraft.network.chat.Component parseText(Element element) {
      return element.getAttribute("translate").equalsIgnoreCase("true")
         ? net.minecraft.network.chat.Component.translatable(element.getTextContent())
         : net.minecraft.network.chat.Component.literal(element.getTextContent());
   }

   public static <E extends Enum<E>> Function<Element, E> parseEnum(Class<E> enumClass) {
      return element -> {
         String name = element.getTextContent().strip().toUpperCase(Locale.ROOT).replace('-', '_');

         for (E value : (Enum[])enumClass.getEnumConstants()) {
            if (Objects.equals(name, value.name())) {
               return value;
            }
         }

         throw new UIModelParsingException("No such constant " + name + " in enum " + enumClass.getSimpleName());
      };
   }

   public static <T, E extends Node> Optional<T> get(Map<String, E> properties, String key, Function<E, T> parser) {
      return !properties.containsKey(key) ? Optional.empty() : Optional.of(parser.apply(properties.get(key)));
   }

   public static <T, E extends Node> void apply(Map<String, E> properties, String key, Function<E, T> parser, Consumer<T> consumer) {
      if (properties.containsKey(key)) {
         consumer.accept(parser.apply(properties.get(key)));
      }
   }

   public static void expectAttributes(Element element, String... attributes) {
      for (String attr : attributes) {
         if (!element.hasAttribute(attr)) {
            throw new UIModelParsingException("Element '" + element.getNodeName() + "' is missing attribute '" + attr + "'");
         }
      }
   }

   public static void expectChildren(Element element, Map<String, Element> children, String... expected) {
      for (String childName : expected) {
         if (!children.containsKey(childName)) {
            throw new UIModelParsingException("Element '" + element.getNodeName() + "' is missing element '" + childName + "'");
         }
      }
   }

   protected static int parseInt(Node node, boolean allowNegative) {
      String data = node.getTextContent().strip();
      if (data.matches((allowNegative ? "-?" : "") + "\\d+")) {
         return Integer.parseInt(data);
      } else {
         throw new UIModelParsingException("Invalid value '" + data + "', expected " + (allowNegative ? "" : "positive") + " integer");
      }
   }

   static {
      registerFactory("flow-layout", FlowLayout::parse);
      registerFactory("grid-layout", GridLayout::parse);
      registerFactory("stack-layout", element -> Containers.stack(Sizing.content(), Sizing.content()));
      registerFactory("scroll", ScrollContainer::parse);
      registerFactory("collapsible", CollapsibleContainer::parse);
      registerFactory("draggable", element -> Containers.draggable(Sizing.content(), Sizing.content(), null));
      registerFactory("sprite", SpriteComponent::parse);
      registerFactory("texture", TextureComponent::parse);
      registerFactory("entity", EntityComponent::parse);
      registerFactory("item", element -> Components.item(ItemStack.EMPTY));
      registerFactory("block", BlockComponent::parse);
      registerFactory("label", element -> Components.label(net.minecraft.network.chat.Component.empty()));
      registerFactory("box", element -> Components.box(Sizing.content(), Sizing.content()));
      registerFactory("button", element -> Components.button(net.minecraft.network.chat.Component.empty(), button -> {}));
      registerFactory("checkbox", element -> Components.checkbox(net.minecraft.network.chat.Component.empty()));
      registerFactory("text-box", element -> Components.textBox(Sizing.content()));
      registerFactory("text-area", element -> Components.textArea(Sizing.content(), Sizing.content()));
      registerFactory("slider", element -> Components.slider(Sizing.content()));
      registerFactory("discrete-slider", DiscreteSliderComponent::parse);
      registerFactory("dropdown", element -> Components.dropdown(Sizing.content()));
      registerFactory("color-picker", element -> new ColorPickerComponent());
      registerFactory("slim-slider", SlimSliderComponent::parse);
      registerFactory("small-checkbox", element -> new SmallCheckboxComponent());
      registerFactory("spacer", SpacerComponent::parse);
   }
}
