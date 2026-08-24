package io.wispforest.owo.ui.container;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.core.Component;
import io.wispforest.owo.ui.core.CursorStyle;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.Surface;
import io.wispforest.owo.ui.parsing.UIParsing;
import io.wispforest.owo.ui.util.Delta;
import io.wispforest.owo.ui.util.UISounds;
import io.wispforest.owo.util.EventSource;
import io.wispforest.owo.util.EventStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import net.minecraft.ChatFormatting;
import org.w3c.dom.Element;

public class CollapsibleContainer extends FlowLayout {
   public static final Surface SURFACE = (context, component) -> context.fill(
      component.x() + 5, component.y(), component.x() + 6, component.y() + component.height(), 2013265919
   );
   protected final EventStream<CollapsibleContainer.OnToggled> toggledEvents = CollapsibleContainer.OnToggled.newStream();
   protected final List<Component> collapsibleChildren = new ArrayList<>();
   protected final List<Component> collapsibleChildrenView = Collections.unmodifiableList(this.collapsibleChildren);
   protected boolean expanded;
   protected final CollapsibleContainer.SpinnyBoiComponent spinnyBoi;
   protected final FlowLayout titleLayout = Containers.horizontalFlow(Sizing.content(), Sizing.content());
   protected final FlowLayout contentLayout;

   protected CollapsibleContainer(Sizing horizontalSizing, Sizing verticalSizing, net.minecraft.network.chat.Component title, boolean expanded) {
      super(horizontalSizing, verticalSizing, FlowLayout.Algorithm.VERTICAL);
      this.titleLayout.padding(Insets.of(5, 5, 5, 0));
      this.allowOverflow(true);
      net.minecraft.network.chat.Component var5 = title.copy().withStyle(ChatFormatting.UNDERLINE);
      this.titleLayout.child(Components.label(var5).cursorStyle(CursorStyle.HAND));
      this.spinnyBoi = new CollapsibleContainer.SpinnyBoiComponent();
      this.titleLayout.child(this.spinnyBoi);
      this.expanded = expanded;
      this.spinnyBoi.targetRotation = expanded ? 90.0F : 0.0F;
      this.spinnyBoi.rotation = this.spinnyBoi.targetRotation;
      super.child(this.titleLayout);
      this.contentLayout = Containers.verticalFlow(Sizing.content(), Sizing.content());
      this.contentLayout.padding(Insets.left(15));
      this.contentLayout.surface(SURFACE);
      super.child(this.contentLayout);
   }

   public FlowLayout titleLayout() {
      return this.titleLayout;
   }

   public List<Component> collapsibleChildren() {
      return this.collapsibleChildrenView;
   }

   public boolean expanded() {
      return this.expanded;
   }

   public EventSource<CollapsibleContainer.OnToggled> onToggled() {
      return this.toggledEvents.source();
   }

   public void toggleExpansion() {
      if (this.expanded) {
         this.contentLayout.clearChildren();
         this.spinnyBoi.targetRotation = 0.0F;
      } else {
         this.contentLayout.children(this.collapsibleChildren);
         this.spinnyBoi.targetRotation = 90.0F;
      }

      this.expanded = !this.expanded;
      this.toggledEvents.sink().onToggle(this.expanded);
   }

   @Override
   public boolean canFocus(Component.FocusSource source) {
      return source == Component.FocusSource.KEYBOARD_CYCLE;
   }

   @Override
   public boolean onKeyPress(int keyCode, int scanCode, int modifiers) {
      if (keyCode != 32 && keyCode != 257 && keyCode != 335) {
         return super.onKeyPress(keyCode, scanCode, modifiers);
      } else {
         this.toggleExpansion();
         super.onKeyPress(keyCode, scanCode, modifiers);
         return true;
      }
   }

   @Override
   public boolean onMouseDown(double mouseX, double mouseY, int button) {
      boolean superResult = super.onMouseDown(mouseX, mouseY, button);
      if (mouseY <= this.titleLayout.fullSize().height() && !superResult) {
         this.toggleExpansion();
         UISounds.playInteractionSound();
         return true;
      } else {
         return superResult;
      }
   }

   @Override
   public FlowLayout child(Component child) {
      this.collapsibleChildren.add(child);
      if (this.expanded) {
         this.contentLayout.child(child);
      }

      return this;
   }

   @Override
   public FlowLayout children(Collection<? extends Component> children) {
      this.collapsibleChildren.addAll(children);
      if (this.expanded) {
         this.contentLayout.children(children);
      }

      return this;
   }

   @Override
   public FlowLayout child(int index, Component child) {
      this.collapsibleChildren.add(index, child);
      if (this.expanded) {
         this.contentLayout.child(index, child);
      }

      return this;
   }

   @Override
   public FlowLayout children(int index, Collection<? extends Component> children) {
      this.collapsibleChildren.addAll(index, children);
      if (this.expanded) {
         this.contentLayout.children(index, children);
      }

      return this;
   }

   @Override
   public FlowLayout removeChild(Component child) {
      this.collapsibleChildren.remove(child);
      return this.contentLayout.removeChild(child);
   }

   public static CollapsibleContainer parse(Element element) {
      Element textElement = UIParsing.childElements(element).get("text");
      net.minecraft.network.chat.Component title = (net.minecraft.network.chat.Component)(textElement == null
         ? net.minecraft.network.chat.Component.empty()
         : UIParsing.parseText(textElement));
      return element.getAttribute("expanded").equals("true")
         ? Containers.collapsible(Sizing.content(), Sizing.content(), title, true)
         : Containers.collapsible(Sizing.content(), Sizing.content(), title, false);
   }

   public interface OnToggled {
      void onToggle(boolean var1);

      static EventStream<CollapsibleContainer.OnToggled> newStream() {
         return new EventStream<>(subscribers -> nowExpanded -> {
            for (CollapsibleContainer.OnToggled subscriber : subscribers) {
               subscriber.onToggle(nowExpanded);
            }
         });
      }
   }

   protected static class SpinnyBoiComponent extends LabelComponent {
      protected float rotation = 90.0F;
      protected float targetRotation = 90.0F;

      public SpinnyBoiComponent() {
         super(net.minecraft.network.chat.Component.literal(">"));
         this.margins(Insets.of(0, 0, 5, 10));
         this.cursorStyle(CursorStyle.HAND);
      }

      @Override
      public void update(float delta, int mouseX, int mouseY) {
         super.update(delta, mouseX, mouseY);
         this.rotation = (float)(this.rotation + Delta.compute((double)this.rotation, (double)this.targetRotation, delta * 0.65));
      }

      @Override
      public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
         PoseStack matrices = context.pose();
         matrices.pushPose();
         matrices.translate(this.x + this.width / 2.0F - 1.0F, this.y + this.height / 2.0F - 1.0F, 0.0F);
         matrices.mulPose(Axis.ZP.rotationDegrees(this.rotation));
         matrices.translate(-(this.x + this.width / 2.0F - 1.0F), -(this.y + this.height / 2.0F - 1.0F), 0.0F);
         super.draw(context, mouseX, mouseY, partialTicks, delta);
         matrices.popPose();
      }
   }
}
