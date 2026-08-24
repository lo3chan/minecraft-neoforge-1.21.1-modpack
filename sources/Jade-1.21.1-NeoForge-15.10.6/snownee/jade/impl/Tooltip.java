package snownee.jade.impl;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.UnaryOperator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.Nullable;
import snownee.jade.Jade;
import snownee.jade.api.ITooltip;
import snownee.jade.api.JadeIds;
import snownee.jade.api.ui.IElement;
import snownee.jade.api.ui.IElementHelper;
import snownee.jade.api.ui.ScreenDirection;
import snownee.jade.impl.ui.ElementHelper;
import snownee.jade.overlay.DisplayHelper;

public class Tooltip implements ITooltip {
   public final List<Tooltip.Line> lines = new ArrayList<>();
   public boolean sneakyDetails;

   public static void drawDebugBorder(GuiGraphics guiGraphics, float x, float y, IElement element) {
      if (Jade.CONFIG.get().getGeneral().isDebug() && Screen.hasControlDown()) {
         Vec2 translate = element.getTranslation();
         Vec2 size = element.getCachedSize();
         DisplayHelper.INSTANCE.drawBorder(guiGraphics, x, y, x + size.x, y + size.y, 1.0F, -1996554240, true);
         if (!Vec2.ZERO.equals(translate)) {
            DisplayHelper.INSTANCE
               .drawBorder(guiGraphics, x + translate.x, y + translate.y, x + translate.x + size.x, y + translate.y + size.y, 1.0F, -2013265665, true);
         }
      }
   }

   @Override
   public void clear() {
      this.lines.clear();
   }

   @Override
   public void append(int index, IElement element) {
      if (element.getTag() == null) {
         element.tag(ElementHelper.INSTANCE.currentUid());
      }

      if (!this.isEmpty() && index != this.size()) {
         Tooltip.Line line = this.lines.get(index);
         line.elements.add(element);
         line.markDirty();
      } else {
         this.add(element);
      }
   }

   @Override
   public int size() {
      return this.lines.size();
   }

   @Override
   public void add(int index, IElement element) {
      if (element.getTag() == null) {
         element.tag(ElementHelper.INSTANCE.currentUid());
      }

      Tooltip.Line line = new Tooltip.Line();
      line.elements.add(element);
      this.lines.add(index, line);
   }

   @Override
   public List<IElement> get(ResourceLocation tag) {
      List<IElement> elements = Lists.newArrayList();

      for (Tooltip.Line line : this.lines) {
         line.sortedElements().stream().filter(e -> Objects.equal(tag, e.getTag())).forEach(elements::add);
      }

      return elements;
   }

   @Override
   public List<IElement> get(int index, IElement.Align align) {
      Tooltip.Line line = this.lines.get(index);
      return line.alignedElements(align);
   }

   @Override
   public boolean remove(ResourceLocation tag) {
      return this.removeInternal(tag, true, null);
   }

   private boolean removeInternal(ResourceLocation tag, boolean removeFirstLineIfEmpty, @Nullable List<List<IElement>> collector) {
      boolean removed = false;
      List<IElement> collected = collector == null ? null : Lists.newArrayList();
      Iterator<Tooltip.Line> iterator = this.lines.iterator();

      while (iterator.hasNext()) {
         Tooltip.Line line = iterator.next();
         if (line.elements.removeIf(e -> {
            if (Objects.equal(tag, e.getTag())) {
               if (collector != null) {
                  collected.add(e);
               }

               return true;
            } else {
               return false;
            }
         })) {
            line.markDirty();
            if (line.elements.isEmpty() && (removed || removeFirstLineIfEmpty)) {
               iterator.remove();
            }

            removed = true;
            if (collector != null && !collected.isEmpty()) {
               collector.add(Lists.newArrayList(collected));
               collected.clear();
            }
         }
      }

      return removed;
   }

   @Override
   public boolean replace(ResourceLocation tag, Component component) {
      return this.replace(tag, (UnaryOperator<List<List<IElement>>>)($ -> List.of(List.of(IElementHelper.get().text(component)))));
   }

   @Override
   public boolean replace(ResourceLocation tag, UnaryOperator<List<List<IElement>>> operator) {
      int firstX = -1;
      int firstY = -1;

      for (int y = 0; y < this.lines.size(); y++) {
         Tooltip.Line line = this.lines.get(y);

         for (int x = 0; x < line.sortedElements().size(); x++) {
            IElement element = line.sortedElements().get(x);
            if (Objects.equal(tag, element.getTag()) && firstX == -1) {
               firstX = x;
               firstY = y;
            }
         }
      }

      if (firstX != -1) {
         List<List<IElement>> elements = Lists.newArrayList();
         this.removeInternal(tag, false, elements);
         elements = operator.apply(elements);

         for (List<IElement> elementList : elements) {
            for (IElement element : elementList) {
               if (element.getTag() == null) {
                  element.tag(tag);
               }
            }
         }

         for (int i = 0; i < elements.size(); i++) {
            List<IElement> list = elements.get(i);
            if (i == 0) {
               Tooltip.Line line = this.lines.get(firstY);
               line.sortedElements().addAll(firstX, list);
               line.markDirty();
            } else {
               this.add(firstY + i, list);
            }
         }
      }

      return firstX != -1;
   }

   @Override
   public String getMessage() {
      List<String> msgs = Lists.newArrayList();

      for (Tooltip.Line line : this.lines) {
         msgs.add(
            String.join(
               " ",
               line.sortedElements()
                  .stream()
                  .filter(e -> !JadeIds.CORE_MOD_NAME.equals(e.getTag()))
                  .map(IElement::getCachedMessage)
                  .filter(java.util.Objects::nonNull)
                  .toList()
            )
         );
      }

      return String.join("\n", msgs);
   }

   @Override
   public String getMessage(ResourceLocation tag) {
      return String.join(" ", this.get(tag).stream().map(IElement::getCachedMessage).filter(java.util.Objects::nonNull).toList());
   }

   @Override
   public void setLineMargin(int index, ScreenDirection side, int margin) {
      if (index < 0) {
         index += this.lines.size();
      }

      Tooltip.Line line = this.lines.get(index);
      switch (side) {
         case UP:
            line.marginTop = margin;
            break;
         case DOWN:
            line.marginBottom = margin;
            break;
         default:
            throw new IllegalArgumentException("Only TOP and BOTTOM are allowed.");
      }
   }

   public static class Line {
      private final List<IElement> elements = Lists.newArrayList();
      private final int[] starts = new int[2];
      private final float[] widths = new float[3];
      public int marginTop = 0;
      public int marginBottom = 2;
      private Vec2 size;
      private boolean sorted;

      public void sort() {
         if (!this.sorted) {
            this.sorted = true;
            Arrays.fill(this.starts, 0);
            Arrays.fill(this.widths, 0.0F);
            List<IElement> tempList = Lists.newArrayListWithExpectedSize(this.elements.size());
            float width = 0.0F;
            float height = 0.0F;

            for (IElement element : this.elements) {
               int index = element.getAlignment().ordinal();
               int start = index == 2 ? tempList.size() : this.starts[index];
               tempList.add(start, element);

               for (int i = index; i < this.starts.length; i++) {
                  this.starts[i]++;
               }

               Vec2 elementSize = element.getCachedSize();
               this.widths[index] = this.widths[index] + elementSize.x;
               width += elementSize.x;
               height = Math.max(height, elementSize.y);
            }

            this.elements.clear();
            this.elements.addAll(tempList);
            this.size = new Vec2(width, height);
         }
      }

      public void markDirty() {
         this.sorted = false;
         this.size = null;
      }

      public List<IElement> sortedElements() {
         this.sort();
         return this.elements;
      }

      public List<IElement> alignedElements(IElement.Align align) {
         this.sort();
         int index = align.ordinal();
         int start = index == 0 ? 0 : this.starts[index - 1];
         int end = index == 2 ? this.elements.size() : this.starts[index];
         return this.elements.subList(start, end);
      }

      public Vec2 size() {
         this.sort();
         return this.size;
      }

      public void render(GuiGraphics guiGraphics, float x, float y, float maxX, float maxY) {
         this.sort();

         for (IElement.Align align : IElement.Align.VALUES) {
            this.renderAligned(guiGraphics, x, y, maxX, maxY, align);
         }
      }

      private void renderAligned(GuiGraphics guiGraphics, float x, float y, float maxX, float maxY, IElement.Align align) {
         List<IElement> alignedElements = this.alignedElements(align);

         float ox = switch (align) {
            case LEFT -> x;
            case RIGHT -> maxX - this.widths[1];
            case CENTER -> {
               float left = x + this.widths[0];
               float right = maxX - this.widths[1];
               yield left + (right - left - this.widths[2]) / 2.0F;
            }
         };
         boolean extendable = align == IElement.Align.LEFT && alignedElements.size() == this.elements.size();
         IElement lastElement = alignedElements.isEmpty() ? null : (IElement)alignedElements.getLast();

         for (IElement element : alignedElements) {
            Vec2 translate = element.getTranslation();
            Vec2 size = element.getCachedSize();
            Tooltip.drawDebugBorder(guiGraphics, ox, y, element);
            element.render(
               guiGraphics, ox + translate.x, y + translate.y, (extendable && element == lastElement ? maxX : ox + size.x) + translate.x, maxY + translate.y
            );
            ox += size.x;
         }
      }
   }
}
