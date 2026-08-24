package io.wispforest.owo.ui.parsing;

import io.wispforest.owo.Owo;
import io.wispforest.owo.ui.core.Component;
import io.wispforest.owo.ui.core.OwoUIAdapter;
import io.wispforest.owo.ui.core.ParentComponent;
import io.wispforest.owo.ui.core.Sizing;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

public class UIModel {
   private static final Pattern PARAMETER_PATTERN = Pattern.compile("\\{\\{[-_a-zA-Z]+}}");
   private static final DocumentBuilder DOCUMENT_BUILDER;
   @Nullable
   private final Element componentsElement;
   private final Map<String, Element> templates;
   private final Deque<UIModel.ExpansionFrame> expansionStack = new ArrayDeque<>();

   protected UIModel(@Nullable Element componentsElement, Map<String, Element> templates) {
      this.componentsElement = componentsElement;
      this.templates = templates;
   }

   protected UIModel(Element docElement) {
      docElement.normalize();
      if (!docElement.getNodeName().equals("owo-ui")) {
         throw new UIModelParsingException("Missing 'owo-ui' root element");
      } else {
         Map<String, Element> children = UIParsing.childElements(docElement);
         if (children.containsKey("components")) {
            List<Element> componentsList = UIParsing.allChildrenOfType(children.get("components"), (short)1);
            if (componentsList.size() != 1) {
               throw new UIModelParsingException("Invalid number of children in 'components' element - a single child must be declared");
            }

            this.componentsElement = componentsList.get(0);
         } else {
            this.componentsElement = null;
         }

         this.templates = UIParsing.<Map<String, Element>, Element>get(children, "templates", element -> {
            NodeList templateChildren = element.getChildNodes();
            HashMap<String, Element> templates = new HashMap<>();

            for (int i = 0; i < templateChildren.getLength(); i++) {
               Node child = templateChildren.item(i);
               if (child.getNodeType() == 1) {
                  Element childElement = (Element)child;
                  if (childElement.getNodeName().equals("template")) {
                     UIParsing.expectAttributes(childElement, "name");
                     templates.put(childElement.getAttribute("name"), childElement);
                  } else {
                     templates.put(childElement.getNodeName(), childElement);
                  }
               }
            }

            return templates;
         }).orElseGet(HashMap::new);
      }
   }

   @Nullable
   public static UIModel load(Path path) {
      try {
         UIModel var2;
         try (InputStream in = Files.newInputStream(path)) {
            var2 = load(in);
         }

         return var2;
      } catch (Exception var6) {
         Owo.LOGGER.warn("Could not load UI model from file {}", path, var6);
         return null;
      }
   }

   public static UIModel load(InputStream stream) throws ParserConfigurationException, IOException, SAXException, UIModelParsingException {
      return new UIModel(DOCUMENT_BUILDER.parse(stream).getDocumentElement());
   }

   public <T extends ParentComponent> OwoUIAdapter<T> createAdapter(Class<T> expectedRootComponentClass, Screen screen) {
      return OwoUIAdapter.create(screen, (horizontalSizing, verticalSizing) -> this.parseComponentTree(expectedRootComponentClass));
   }

   public <T extends ParentComponent> OwoUIAdapter<T> createAdapterWithoutScreen(int x, int y, int width, int height, Class<T> expectedRootComponentClass) {
      return OwoUIAdapter.createWithoutScreen(x, y, width, height, (horizontalSizing, verticalSizing) -> this.parseComponentTree(expectedRootComponentClass));
   }

   public <T extends Component> T parseComponent(Class<T> expectedClass, Element componentElement) {
      if (componentElement.getNodeName().equals("template")) {
         String templateName = componentElement.getAttribute("name").strip();
         if (templateName.isEmpty()) {
            throw new UIModelParsingException("Template element is missing 'name' attribute");
         } else {
            HashMap<String, String> templateParams = new HashMap<>();
            HashMap<String, Element> childParams = new HashMap<>();

            for (Element element : UIParsing.allChildrenOfType(componentElement, (short)1)) {
               if (element.getNodeName().equals("child")) {
                  childParams.put(element.getAttribute("id"), UIParsing.<Element>allChildrenOfType(element, (short)1).get(0));
               } else {
                  templateParams.put(element.getNodeName(), element.getTextContent());
               }
            }

            return this.expandTemplate(expectedClass, templateName, templateParams::get, childParams::get);
         }
      } else {
         Component component = UIParsing.getFactory(componentElement).apply(componentElement);
         component.parseProperties(this, componentElement, UIParsing.childElements(componentElement));
         if (!expectedClass.isAssignableFrom(component.getClass())) {
            String idString = componentElement.hasAttribute("id") ? " with id '" + componentElement.getAttribute("id") + "'" : "";
            throw new IncompatibleUIModelException(
               "Expected component '"
                  + componentElement.getNodeName()
                  + "'"
                  + idString
                  + " to be a "
                  + expectedClass.getSimpleName()
                  + ", but it is a "
                  + component.getClass().getSimpleName()
            );
         } else {
            return (T)component;
         }
      }
   }

   public <T extends Component> T expandTemplate(
      Class<T> expectedClass, String name, Function<String, String> parameterSupplier, Function<String, Element> childSupplier
   ) {
      if (this.expansionStack.isEmpty()) {
         this.expansionStack.push(new UIModel.ExpansionFrame(parameterSupplier, childSupplier));
      } else {
         UIModel.ExpansionFrame currentFrame = this.expansionStack.peek();
         this.expansionStack
            .push(
               new UIModel.ExpansionFrame(
                  this.cascadeIfNull(currentFrame.parameterSupplier, parameterSupplier), this.cascadeIfNull(currentFrame.childSupplier, childSupplier)
               )
            );
      }

      String[] splitTemplateName = name.split("@");
      Element template;
      if (splitTemplateName.length == 2) {
         UIModel modelReference = UIModelLoader.get(ResourceLocation.parse(splitTemplateName[1]));
         if (modelReference == null) {
            throw new UIModelParsingException("Unknown UI model " + splitTemplateName[1] + ", referenced by template " + splitTemplateName[0]);
         }

         template = modelReference.templates.get(splitTemplateName[0]);
      } else {
         template = this.templates.get(name);
      }

      if (template == null) {
         throw new UIModelParsingException("Unknown template '" + name + "'");
      } else {
         template = (Element)template.cloneNode(true);
         this.expandChildren(template);
         this.applySubstitutions(template);
         Component component = this.parseComponent(Component.class, UIParsing.<Element>allChildrenOfType(template, (short)1).get(0));
         if (!expectedClass.isAssignableFrom(component.getClass())) {
            throw new IncompatibleUIModelException(
               "Expected template '"
                  + name
                  + "' to expand into a "
                  + expectedClass.getSimpleName()
                  + ", but it expanded into a "
                  + component.getClass().getSimpleName()
            );
         } else {
            this.expansionStack.pop();
            return (T)component;
         }
      }
   }

   public <T extends Component> T expandTemplate(Class<T> expectedClass, String name, Map<String, String> parameters) {
      return this.expandTemplate(expectedClass, name, parameters::get, s -> null);
   }

   protected <T extends ParentComponent> T parseComponentTree(Class<T> expectedRootComponentClass) {
      if (this.componentsElement == null) {
         throw new IncompatibleUIModelException("This UI model does not declare a component tree and can thus only provide templates");
      } else {
         T documentComponent = (T)this.parseComponent(expectedRootComponentClass, this.componentsElement);
         documentComponent.sizing(Sizing.fill(100), Sizing.fill(100));
         return documentComponent;
      }
   }

   protected void applySubstitutions(Element template) {
      Function<String, String> parameterSupplier = this.expansionStack.peek().parameterSupplier;
      Function<MatchResult, String> replacer = matchResult -> {
         String paramName = matchResult.group().substring(2, matchResult.group().length() - 2);
         String substitution = parameterSupplier.apply(paramName);
         if (substitution == null) {
            throw new IncompatibleUIModelException("No substitution provided for template parameter '" + paramName + "'");
         } else {
            return Matcher.quoteReplacement(substitution);
         }
      };

      for (Element child : UIParsing.allChildrenOfType(template, (short)1)) {
         for (Text node : UIParsing.allChildrenOfType(child, (short)3)) {
            String textContent = node.getTextContent();
            node.setTextContent(PARAMETER_PATTERN.matcher(textContent).replaceAll(replacer));
         }

         for (int i = 0; i < child.getAttributes().getLength(); i++) {
            Attr attr = (Attr)child.getAttributes().item(i);
            attr.setValue(PARAMETER_PATTERN.matcher(attr.getValue()).replaceAll(replacer));
         }

         this.applySubstitutions(child);
      }
   }

   protected void expandChildren(Element template) {
      Function<String, Element> childSupplier = this.expansionStack.peek().childSupplier;

      for (Element child : UIParsing.allChildrenOfType(template, (short)1)) {
         if (child.getNodeName().equals("template-child")) {
            String childId = child.getAttribute("id");
            Element expanded = childSupplier.apply(childId);
            if (expanded == null) {
               throw new IncompatibleUIModelException("No expansion provided for template child '" + childId + "'");
            }

            expanded = (Element)expanded.cloneNode(true);
            Map<String, Element> expandedChildren = UIParsing.childElements(expanded);

            for (Element element : UIParsing.allChildrenOfType(child, (short)1)) {
               if (!expandedChildren.containsKey(element.getTagName())) {
                  expanded.appendChild(element);
               }
            }

            template.replaceChild(expanded, child);
         }

         this.expandChildren(child);
      }
   }

   protected <T, S> Function<T, S> cascadeIfNull(Function<T, S> first, Function<T, S> second) {
      return t -> {
         S firstValue = first.apply(t);
         return firstValue == null ? second.apply(t) : firstValue;
      };
   }

   static {
      try {
         DOCUMENT_BUILDER = DocumentBuilderFactory.newDefaultInstance().newDocumentBuilder();
      } catch (ParserConfigurationException var1) {
         throw new RuntimeException("we love checked exceptions, we love checked exceptions, we love checked exceptions", var1);
      }
   }

   private record ExpansionFrame(Function<String, String> parameterSupplier, Function<String, Element> childSupplier) {
   }
}
