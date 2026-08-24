package com.anthonyhilyard.iceberg.util;

import com.anthonyhilyard.iceberg.Iceberg;
import com.anthonyhilyard.iceberg.component.IExtendedText;
import com.anthonyhilyard.iceberg.component.TitleBreakComponent;
import com.anthonyhilyard.iceberg.events.client.RegisterTooltipComponentFactoryEvent;
import com.anthonyhilyard.iceberg.events.client.RenderTooltipEvents;
import com.anthonyhilyard.iceberg.mixin.GuiGraphicsInvoker;
import com.mojang.datafixers.util.Either;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTextTooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;
import org.joml.Vector2ic;

public class Tooltips {
   public static final Tooltips.TooltipColors DEFAULT_COLORS = new Tooltips.TooltipColors(-267386864, -267386864, 1347420415, 1344798847);
   private static final FormattedCharSequence SPACE = FormattedCharSequence.forward(" ", Style.EMPTY);
   private static boolean tooltipWidthWarningShown = false;
   public static Tooltips.TooltipColors currentColors = DEFAULT_COLORS;
   public static final Tooltips.TooltipRenderContext EMPTY_CONTEXT = new Tooltips.TooltipRenderContext(0, 0, false, 0);
   public static final Tooltips.TooltipRenderContext CALCULATE_RECT_CONTEXT = new Tooltips.TooltipRenderContext(0, 0, false, 0);
   private static Tooltips.TooltipRenderContext currentRenderContext = EMPTY_CONTEXT;
   private static Rect2i currentRect = new Rect2i(0, 0, 0, 0);
   private static boolean tooltipsOnscreen = false;
   private static final Rect2i emptyRect = new Rect2i(0, 0, 0, 0);

   public static Tooltips.TooltipRenderContext getCurrentRenderContext() {
      return currentRenderContext;
   }

   public static void setCurrentRect(int x, int y, int width, int height) {
      currentRect.setX(x);
      currentRect.setY(y);
      currentRect.setWidth(width);
      currentRect.setHeight(height);
   }

   public static Rect2i getCurrentRect() {
      return currentRect;
   }

   public static boolean anyTooltipsVisible() {
      return tooltipsOnscreen;
   }

   public static void setAnyTooltipsVisible(boolean visible) {
      tooltipsOnscreen = visible;
   }

   public static int calculateTitleLines(List<ClientTooltipComponent> components) {
      if (components != null && !components.isEmpty()) {
         int titleLines = 0;
         boolean foundTitleBreak = false;

         for (ClientTooltipComponent component : components) {
            if (component instanceof ClientTextTooltip) {
               titleLines++;
            } else {
               if (component instanceof TitleBreakComponent) {
                  foundTitleBreak = true;
                  break;
               }

               titleLines = 0;
            }
         }

         if (!foundTitleBreak) {
            titleLines = 1;
         }

         return titleLines;
      } else {
         return 0;
      }
   }

   public static int calculateTitleStart(List<ClientTooltipComponent> components) {
      if (components != null && !components.isEmpty()) {
         int firstTextIndex = -1;
         int currentTextRunStart = -1;

         for (int i = 0; i < components.size(); i++) {
            ClientTooltipComponent component = components.get(i);
            if (component instanceof TitleBreakComponent) {
               return currentTextRunStart == -1 ? 0 : currentTextRunStart;
            }

            if (component instanceof ClientTextTooltip) {
               if (currentTextRunStart == -1) {
                  currentTextRunStart = i;
               }

               if (firstTextIndex == -1) {
                  firstTextIndex = i;
               }
            } else {
               currentTextRunStart = -1;
            }
         }

         return firstTextIndex == -1 ? 0 : firstTextIndex;
      } else {
         return 0;
      }
   }

   public static int getTitleOffset(int tooltipWidth, int textWidth, int leftPadding, int rightPadding, IExtendedText.TextAlignment textAlignment) {
      int offset = leftPadding;
      switch (textAlignment) {
         case CENTER:
            offset = leftPadding + (tooltipWidth - textWidth - leftPadding - rightPadding) / 2;
            break;
         case RIGHT:
            offset = leftPadding + (tooltipWidth - textWidth - rightPadding);
      }

      return Math.max(leftPadding, offset);
   }

   public static int getTitleWidth(ClientTextTooltip title, Font font) {
      int textWidth = font.width(title.text);
      if (title instanceof IExtendedText extendedTitle) {
         int leftPadding = extendedTitle.getLeftPadding();
         int rightPadding = extendedTitle.getRightPadding();
         int tooltipWidth = getCurrentRect().getWidth();
         if (tooltipWidth == 0) {
            textWidth += leftPadding + rightPadding;
         } else {
            textWidth += getTitleOffset(tooltipWidth, textWidth, leftPadding, rightPadding, extendedTitle.getAlignment()) + rightPadding;
         }
      }

      return textWidth;
   }

   @Deprecated(
      forRemoval = true,
      since = "1.3.0"
   )
   public static void renderItemTooltip(
      @NotNull ItemStack stack,
      Tooltips.TooltipInfo info,
      Rect2i rect,
      int screenWidth,
      int screenHeight,
      int backgroundColorStart,
      int backgroundColorEnd,
      int borderColorStart,
      int borderColorEnd,
      GuiGraphics graphics,
      ClientTooltipPositioner positioner,
      boolean comparison,
      boolean constrain,
      boolean centeredTitle,
      int index
   ) {
      renderItemTooltip(stack, info.getFont(), info.getComponents(), rect, graphics, positioner, comparison, index);
   }

   @Deprecated(
      forRemoval = true,
      since = "1.3.0"
   )
   public static void renderItemTooltip(
      @NotNull ItemStack stack,
      Tooltips.TooltipInfo info,
      Rect2i rect,
      GuiGraphics graphics,
      ClientTooltipPositioner positioner,
      boolean comparison,
      boolean constrain,
      int index
   ) {
      renderItemTooltip(stack, info.getFont(), info.getComponents(), rect, graphics, positioner, comparison, index);
   }

   public static void renderItemTooltip(
      @NotNull ItemStack stack,
      Font font,
      List<ClientTooltipComponent> components,
      Rect2i rect,
      GuiGraphics graphics,
      ClientTooltipPositioner positioner,
      boolean comparison,
      int index
   ) {
      currentRenderContext = new Tooltips.TooltipRenderContext(rect.getWidth(), rect.getHeight(), comparison, index);
      if (graphics instanceof GuiGraphicsInvoker graphicsInvoker && graphics instanceof ITooltipAccess tooltipAccess) {
         tooltipAccess.setIcebergTooltipStack(stack);
         graphicsInvoker.invokeRenderTooltipInternal(font, components, rect.getX() + 2, rect.getY(), new Tooltips.TooltipRectPositioner(rect));
         tooltipAccess.setIcebergTooltipStack(ItemStack.EMPTY);
      }

      currentRenderContext = EMPTY_CONTEXT;
   }

   private static ClientTooltipComponent getClientComponent(TooltipComponent componentData) {
      ClientTooltipComponent result = null;
      result = RegisterTooltipComponentFactoryEvent.EVENT.invoker().getComponent(componentData);
      if (result == null) {
         try {
            result = ClientTooltipComponent.create(componentData);
         } catch (IllegalArgumentException var4) {
         }
      }

      if (result == null) {
         try {
            result = (ClientTooltipComponent)componentData;
         } catch (ClassCastException var3) {
         }
      }

      if (result == null) {
         throw new IllegalArgumentException("Unknown TooltipComponent");
      } else {
         return result;
      }
   }

   public static List<ClientTooltipComponent> gatherTooltipComponents(
      ItemStack stack,
      List<? extends FormattedText> textElements,
      Optional<TooltipComponent> itemComponent,
      int mouseX,
      int screenWidth,
      int screenHeight,
      Font forcedFont,
      Font fallbackFont,
      int maxWidth
   ) {
      return gatherTooltipComponents(stack, textElements, itemComponent, mouseX, screenWidth, screenHeight, forcedFont, fallbackFont, maxWidth, 0);
   }

   public static List<ClientTooltipComponent> gatherTooltipComponents(
      ItemStack stack,
      List<? extends FormattedText> textElements,
      Optional<TooltipComponent> itemComponent,
      int mouseX,
      int screenWidth,
      int screenHeight,
      Font forcedFont,
      Font fallbackFont,
      int maxWidth,
      int index
   ) {
      Font font = forcedFont == null ? fallbackFont : forcedFont;
      List<Either<FormattedText, TooltipComponent>> elements = textElements.stream()
         .<Either<FormattedText, TooltipComponent>>map(Either::left)
         .collect(Collectors.toCollection(ArrayList::new));
      itemComponent.ifPresent(c -> elements.add(1, Either.right(c)));
      RenderTooltipEvents.GatherResult eventResult = RenderTooltipEvents.GATHER.invoker().onGather(stack, screenWidth, screenHeight, elements, maxWidth, index);
      if (eventResult.result() != InteractionResult.PASS) {
         return List.of();
      } else {
         int tooltipTextWidth = eventResult.tooltipElements().stream().mapToInt(either -> (Integer)either.map(component -> {
            try {
               return font.width(component);
            } catch (Exception var3) {
               if (!tooltipWidthWarningShown) {
                  Iceberg.LOGGER.error("Error rendering tooltip component: \n" + ExceptionUtils.getStackTrace(var3));
                  tooltipWidthWarningShown = true;
               }

               return 0;
            }
         }, component -> 0)).max().orElse(0);
         boolean needsWrap = false;
         int tooltipX = mouseX + 12;
         if (tooltipX + tooltipTextWidth + 4 > screenWidth) {
            tooltipX = mouseX - 16 - tooltipTextWidth;
            if (tooltipX < 4) {
               if (mouseX > screenWidth / 2) {
                  tooltipTextWidth = mouseX - 12 - 8;
               } else {
                  tooltipTextWidth = screenWidth - 16 - mouseX;
               }

               needsWrap = true;
            }
         }

         if (eventResult.maxWidth() > 0 && tooltipTextWidth > eventResult.maxWidth()) {
            tooltipTextWidth = eventResult.maxWidth();
            needsWrap = true;
         }

         if (eventResult.tooltipElements().size() > 0
            && eventResult.tooltipElements().get(0).right().orElse(null) instanceof ClientTooltipComponent clientComponent
            && clientComponent.getWidth(font) < 0) {
            tooltipTextWidth += clientComponent.getWidth(font);
         }

         int tooltipTextWidthFinal = tooltipTextWidth;
         return needsWrap
            ? eventResult.tooltipElements()
               .stream()
               .flatMap(
                  either -> (Stream<? extends ClientTooltipComponent>)either.map(
                     text -> splitLine(text, font, tooltipTextWidthFinal), component -> Stream.of(getClientComponent(component))
                  )
               )
               .toList()
            : eventResult.tooltipElements()
               .stream()
               .map(
                  either -> (ClientTooltipComponent)either.map(
                     text -> ClientTooltipComponent.create(
                        text instanceof Component ? ((Component)text).getVisualOrderText() : Language.getInstance().getVisualOrder(text)
                     ),
                     component -> getClientComponent(component)
                  )
               )
               .toList();
      }
   }

   private static Stream<ClientTooltipComponent> splitLine(FormattedText text, Font font, int maxWidth) {
      return text instanceof Component component && component.getString().isEmpty()
         ? Stream.of(component.getVisualOrderText()).map(ClientTooltipComponent::create)
         : font.split(text, maxWidth).stream().map(ClientTooltipComponent::create);
   }

   @Deprecated(
      forRemoval = true,
      since = "1.3.0"
   )
   public static Rect2i calculateRect(
      ItemStack stack,
      GuiGraphics graphics,
      ClientTooltipPositioner positioner,
      List<ClientTooltipComponent> components,
      int mouseX,
      int mouseY,
      int screenWidth,
      int screenHeight,
      int maxTextWidth,
      Font font,
      int minWidth,
      boolean centeredTitle
   ) {
      return calculateRect(stack, graphics, positioner, components, mouseX, mouseY, font);
   }

   public static Rect2i calculateRect(
      ItemStack stack, GuiGraphics graphics, ClientTooltipPositioner positioner, List<ClientTooltipComponent> components, int mouseX, int mouseY, Font font
   ) {
      if (components != null && !components.isEmpty() && stack != null) {
         Tooltips.TooltipRenderContext prevContext = currentRenderContext;
         currentRenderContext = CALCULATE_RECT_CONTEXT;
         if (graphics instanceof GuiGraphicsInvoker graphicsInvoker && graphics instanceof ITooltipAccess tooltipAccess) {
            ItemStack prevStack = tooltipAccess.getIcebergTooltipStack();
            tooltipAccess.setIcebergTooltipStack(stack);
            graphicsInvoker.invokeRenderTooltipInternal(font, components, mouseX, mouseY, positioner);
            tooltipAccess.setIcebergTooltipStack(prevStack);
         }

         currentRenderContext = prevContext;
         return currentRect;
      } else {
         return emptyRect;
      }
   }

   @Deprecated(
      since = "1.3.0",
      forRemoval = true
   )
   public static List<ClientTooltipComponent> centerTitle(List<ClientTooltipComponent> components, Font font, int width) {
      return centerTitle(components, font, width, calculateTitleLines(components));
   }

   @Deprecated(
      since = "1.3.0",
      forRemoval = true
   )
   public static List<ClientTooltipComponent> centerTitle(List<ClientTooltipComponent> components, Font font, int width, int titleLines) {
      List<ClientTooltipComponent> result = new ArrayList<>(components);
      if (!components.isEmpty() && titleLines > 0 && titleLines < components.size()) {
         int titleStart = calculateTitleStart(components);
         if (titleStart >= components.size()) {
            return result;
         } else {
            for (int i = 0; i < titleLines; i++) {
               ClientTooltipComponent titleComponent = components.get(titleStart + i);
               if (titleComponent != null) {
                  List<FormattedText> recomposedLines = StringRecomposer.recompose(List.of(titleComponent));
                  if (!recomposedLines.isEmpty()) {
                     FormattedCharSequence title = Language.getInstance().getVisualOrder(recomposedLines.get(0));

                     while (ClientTooltipComponent.create(title).getWidth(font) < width) {
                        title = FormattedCharSequence.fromList(List.of(SPACE, title, SPACE));
                        if (title == null) {
                           break;
                        }
                     }

                     result.set(titleStart + i, ClientTooltipComponent.create(title));
                  }
               }
            }

            return result;
         }
      } else {
         return result;
      }
   }

   public record TooltipColors(int backgroundColorStart, int backgroundColorEnd, int borderColorStart, int borderColorEnd) {
   }

   @Deprecated(
      forRemoval = true,
      since = "1.3.0"
   )
   public static class TooltipInfo {
      private int tooltipWidth;
      private int titleLines;
      private int titleStart;
      private Font font;
      private List<ClientTooltipComponent> components;

      public TooltipInfo(List<ClientTooltipComponent> components, Font font) {
         this(components, font, Tooltips.calculateTitleLines(components));
      }

      public TooltipInfo(List<ClientTooltipComponent> components, Font font, int titleLines) {
         this(components, font, titleLines, Tooltips.calculateTitleStart(components));
      }

      public TooltipInfo(List<ClientTooltipComponent> components, Font font, int titleLines, int titleStart) {
         this.components = components;
         this.font = font;
         this.titleLines = titleLines;
         this.titleStart = titleStart;
         this.tooltipWidth = this.getMaxLineWidth();
      }

      public int getTooltipWidth() {
         return this.tooltipWidth;
      }

      public int getTooltipHeight() {
         return this.components.size() > this.titleLines ? this.components.size() * 10 + 2 : 8;
      }

      public int getTitleLines() {
         return this.titleLines;
      }

      public int getTitleStart() {
         return this.titleStart;
      }

      public Font getFont() {
         return this.font;
      }

      public List<ClientTooltipComponent> getComponents() {
         return this.components;
      }

      public void setFont(Font font) {
         this.font = font;
      }

      public int getMaxLineWidth() {
         return this.getMaxLineWidth(0);
      }

      public int getMaxLineWidth(int minWidth) {
         int textWidth = minWidth;

         for (ClientTooltipComponent component : this.components) {
            int componentWidth = component.getWidth(this.font);
            if (componentWidth > textWidth) {
               textWidth = componentWidth;
            }
         }

         return textWidth;
      }
   }

   private static class TooltipRectPositioner implements ClientTooltipPositioner {
      private final Rect2i tooltipRect;

      public TooltipRectPositioner(Rect2i tooltipRect) {
         this.tooltipRect = tooltipRect;
      }

      public Vector2ic positionTooltip(int screenWidth, int screenHeight, int mouseX, int mouseY, int tooltipWidth, int tooltipHeight) {
         return new Vector2i(this.tooltipRect.getX() + 2, this.tooltipRect.getY());
      }
   }

   public record TooltipRenderContext(int maxWidth, int maxHeight, boolean comparison, int index) {
   }
}
