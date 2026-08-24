package io.wispforest.owo.ui.component;

import io.wispforest.owo.ui.base.BaseComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Color;
import io.wispforest.owo.ui.core.Component;
import io.wispforest.owo.ui.core.CursorStyle;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.core.ParentComponent;
import io.wispforest.owo.ui.core.PositionedRectangle;
import io.wispforest.owo.ui.core.Positioning;
import io.wispforest.owo.ui.core.Size;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.Surface;
import io.wispforest.owo.ui.parsing.UIModel;
import io.wispforest.owo.ui.parsing.UIParsing;
import io.wispforest.owo.ui.util.UISounds;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class DropdownComponent extends FlowLayout {
   protected static final ResourceLocation ICONS_TEXTURE = ResourceLocation.fromNamespaceAndPath("owo", "textures/gui/dropdown_icons.png");
   protected final FlowLayout entries;
   protected boolean closeWhenNotHovered = false;
   private static final Map<Screen, List<BiConsumer<Double, Double>>> componentHook = new HashMap<>();

   protected DropdownComponent(Sizing horizontalSizing) {
      super(Sizing.content(), Sizing.content(), FlowLayout.Algorithm.HORIZONTAL);
      this.entries = Containers.verticalFlow(horizontalSizing, Sizing.content());
      this.entries.padding(Insets.of(1));
      this.entries.allowOverflow(true);
      this.entries.surface(Surface.flat(-956301312).and(Surface.blur(3.0F, 5.0F)).and(Surface.outline(-15592942)));
      this.child(this.entries);
   }

   public static <R extends ParentComponent> DropdownComponent openContextMenu(
      Screen screen, R rootComponent, BiConsumer<R, DropdownComponent> mountFunction, double mouseX, double mouseY, Consumer<DropdownComponent> builder
   ) {
      DropdownComponent dropdown = new DropdownComponent(Sizing.content());
      builder.accept(dropdown);
      mountFunction.accept(rootComponent, dropdown);
      int xLocation = (int)mouseX - rootComponent.x();
      int yLocation = (int)mouseY - rootComponent.y();
      if (xLocation + dropdown.width() > screen.width) {
         xLocation -= xLocation + dropdown.width() - screen.width;
      }

      if (yLocation + dropdown.height() > screen.height) {
         yLocation -= yLocation + dropdown.height() - screen.height;
      }

      dropdown.positioning(Positioning.absolute(xLocation, yLocation));
      MutableBoolean dismounted = new MutableBoolean(false);
      componentHook.computeIfAbsent(screen, screen1 -> new ArrayList<>()).add((mouseX_, mouseY_) -> {
         if (!dismounted.isTrue() && !dropdown.isInBoundingBox(mouseX_, mouseY_)) {
            rootComponent.removeChild(dropdown);
            dismounted.setTrue();
         }
      });
      return dropdown;
   }

   @Override
   public ParentComponent surface(Surface surface) {
      return this.entries.surface(surface);
   }

   @Override
   public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
      super.draw(context, mouseX, mouseY, partialTicks, delta);
      if (this.closeWhenNotHovered && !this.isInBoundingBox(mouseX, mouseY)) {
         this.queue(() -> {
            this.closeWhenNotHovered(false);
            this.parent.removeChild(this);
         });
      }
   }

   @Override
   public void layout(Size space) {
      super.layout(space);
      List<Component> entries = this.entries.children();

      for (int i = 0; i < entries.size(); i++) {
         Component entry = entries.get(i);
         if (entry instanceof DropdownComponent.ResizeableComponent sizeable) {
            sizeable.setWidth(this.entries.width() - this.entries.padding().get().horizontal() - entry.margins().get().horizontal());
         }
      }
   }

   public DropdownComponent divider() {
      this.entries.child(new DropdownComponent.Divider());
      return this;
   }

   public DropdownComponent text(net.minecraft.network.chat.Component text) {
      this.entries.child(Components.label(text).color(Color.ofFormatting(ChatFormatting.GRAY)).margins(Insets.of(2)));
      return this;
   }

   public DropdownComponent button(net.minecraft.network.chat.Component text, Consumer<DropdownComponent> onClick) {
      this.entries.child(new DropdownComponent.Button(this, text, onClick).margins(Insets.of(2)));
      return this;
   }

   public DropdownComponent checkbox(net.minecraft.network.chat.Component text, boolean state, Consumer<Boolean> onClick) {
      this.entries.child(new DropdownComponent.Checkbox(this, text, state, onClick).margins(Insets.of(2)));
      return this;
   }

   public DropdownComponent nested(net.minecraft.network.chat.Component text, Sizing horizontalSizing, Consumer<DropdownComponent> builder) {
      DropdownComponent nested = new DropdownComponent(horizontalSizing);
      builder.accept(nested);
      this.entries.child(new DropdownComponent.NestEntry(this, text, nested).margins(Insets.of(2)));
      return this;
   }

   @Override
   public FlowLayout removeChild(Component child) {
      if (child == this.entries) {
         this.queue(() -> {
            this.closeWhenNotHovered(false);
            this.parent.removeChild(this);
         });
      }

      return super.removeChild(child);
   }

   public DropdownComponent closeWhenNotHovered(boolean closeWhenNotHovered) {
      this.closeWhenNotHovered = closeWhenNotHovered;
      return this;
   }

   public boolean closeWhenNotHovered() {
      return this.closeWhenNotHovered;
   }

   @Override
   public void parseProperties(UIModel model, Element element, Map<String, Element> children) {
      super.parseProperties(model, element, children);
      UIParsing.apply(children, "entries", Function.identity(), this::parseAndApplyEntries);
      UIParsing.apply(children, "close-when-not-hovered", UIParsing::parseBool, this::closeWhenNotHovered);
   }

   protected void parseAndApplyEntries(Element container) {
      for (Node node : UIParsing.allChildrenOfType(container, (short)1)) {
         Element entry = (Element)node;
         String var5 = entry.getNodeName();
         switch (var5) {
            case "divider":
               this.divider();
               break;
            case "text":
               this.text(UIParsing.parseText(entry));
               break;
            case "button": {
               Map<String, Element> children = UIParsing.childElements(entry);
               UIParsing.expectChildren(entry, children, "text");
               net.minecraft.network.chat.Component text = UIParsing.parseText(children.get("text"));
               this.button(text, dropdownComponent -> {});
               break;
            }
            case "checkbox": {
               Map<String, Element> children = UIParsing.childElements(entry);
               UIParsing.expectChildren(entry, children, "text", "checked");
               net.minecraft.network.chat.Component text = UIParsing.parseText(children.get("text"));
               boolean checked = UIParsing.parseBool(children.get("checked"));
               this.checkbox(text, checked, aBoolean -> {});
               break;
            }
            case "nested": {
               MutableComponent text = entry.getAttribute("translate").equals("true")
                  ? net.minecraft.network.chat.Component.translatable(entry.getAttribute("name"))
                  : net.minecraft.network.chat.Component.literal(entry.getAttribute("name"));
               this.nested(text, Sizing.content(), dropdownComponent -> dropdownComponent.parseAndApplyEntries(entry));
            }
         }
      }
   }

   protected static void drawIconFromTexture(OwoUIDrawContext context, ParentComponent dropdown, int y, int u, int v) {
      context.blit(ICONS_TEXTURE, dropdown.x() + dropdown.width() - dropdown.padding().get().right() - 10, y, u, v, 9, 9, 32, 32);
   }

   static {
      NeoForge.EVENT_BUS.addListener(event -> componentHook.remove(event.getScreen()));
      NeoForge.EVENT_BUS
         .addListener(
            event -> componentHook.getOrDefault(event.getScreen(), List.of()).forEach(consumer -> consumer.accept(event.getMouseX(), event.getMouseY()))
         );
   }

   protected static class Button extends LabelComponent implements DropdownComponent.ResizeableComponent {
      protected final DropdownComponent parentDropdown;
      protected Consumer<DropdownComponent> onClick;

      protected Button(DropdownComponent parentDropdown, net.minecraft.network.chat.Component text, Consumer<DropdownComponent> onClick) {
         super(text);
         this.onClick = onClick;
         this.parentDropdown = parentDropdown;
         this.margins(Insets.vertical(1));
         this.cursorStyle(CursorStyle.HAND);
      }

      @Override
      public void setWidth(int width) {
         this.width = width;
      }

      @Override
      public boolean onMouseDown(double mouseX, double mouseY, int button) {
         super.onMouseDown(mouseX, mouseY, button);
         this.onClick.accept(this.parentDropdown);
         this.playInteractionSound();
         return true;
      }

      @Override
      public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
         if (this.isInBoundingBox(mouseX, mouseY)) {
            Insets margins = this.margins.get();
            context.fill(
               this.x - margins.left(), this.y - margins.top(), this.x + this.width + margins.right(), this.y + this.height + margins.bottom(), 1157627903
            );
         }

         super.draw(context, mouseX, mouseY, partialTicks, delta);
      }

      protected void playInteractionSound() {
         UISounds.playButtonSound();
      }
   }

   protected static class Checkbox extends DropdownComponent.Button {
      protected boolean state;

      public Checkbox(DropdownComponent parentDropdown, net.minecraft.network.chat.Component text, boolean state, Consumer<Boolean> onClick) {
         super(parentDropdown, text, dropdownComponent -> {});
         this.state = state;
         this.onClick = dropdownComponent -> {
            this.state = !this.state;
            onClick.accept(this.state);
         };
      }

      @Override
      public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
         super.draw(context, mouseX, mouseY, partialTicks, delta);
         DropdownComponent.drawIconFromTexture(context, this.parent, this.y, this.state ? 16 : 0, 0);
      }

      @Override
      protected int determineHorizontalContentSize(Sizing sizing) {
         return super.determineHorizontalContentSize(sizing) + 17;
      }

      @Override
      protected void playInteractionSound() {
         UISounds.playInteractionSound();
      }
   }

   protected static class Divider extends BaseComponent implements DropdownComponent.ResizeableComponent {
      public Divider() {
         this.sizing(Sizing.fixed(1));
      }

      @Override
      public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
         Insets margins = this.margins.get();
         context.fill(
            this.x - margins.left(), this.y - margins.top(), this.x + this.width + margins.right(), this.y + this.height + margins.bottom(), -15592942
         );
      }

      @Override
      public void setWidth(int width) {
         this.width = width;
      }
   }

   protected static class NestEntry extends LabelComponent {
      private final DropdownComponent child;

      protected NestEntry(DropdownComponent parentDropdown, net.minecraft.network.chat.Component text, DropdownComponent child) {
         super(text);
         this.child = child;
         this.mouseEnter().subscribe(() -> {
            child.margins(Insets.top(this.y - parentDropdown.y));
            parentDropdown.queue(() -> {
               parentDropdown.removeChild(child);
               parentDropdown.child(child);
            });
         });
      }

      @Override
      public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
         super.draw(context, mouseX, mouseY, partialTicks, delta);
         DropdownComponent.drawIconFromTexture(context, this.parent, this.y, 0, 16);
         this.child.closeWhenNotHovered(!PositionedRectangle.of(this.x, this.y, this.parent.width(), this.height).isInBoundingBox(mouseX, mouseY));
      }

      @Override
      protected int determineHorizontalContentSize(Sizing sizing) {
         return super.determineHorizontalContentSize(sizing) + 17;
      }
   }

   protected interface ResizeableComponent {
      void setWidth(int var1);
   }
}
