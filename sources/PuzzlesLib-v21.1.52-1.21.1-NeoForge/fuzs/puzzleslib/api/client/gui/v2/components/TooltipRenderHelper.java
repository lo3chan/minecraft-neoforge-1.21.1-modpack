package fuzs.puzzleslib.api.client.gui.v2.components;

import com.google.common.collect.ImmutableList;
import fuzs.puzzleslib.api.client.core.v1.ClientAbstractions;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.TooltipFlag.Default;

@Deprecated
public final class TooltipRenderHelper {
   private TooltipRenderHelper() {
   }

   public static List<Component> getTooltipLines(ItemStack itemStack) {
      return getTooltipLines(itemStack, Minecraft.getInstance().options.advancedItemTooltips ? Default.ADVANCED : Default.NORMAL);
   }

   public static List<Component> getTooltipLines(ItemStack itemStack, TooltipFlag tooltipFlag) {
      Objects.requireNonNull(itemStack, "item stack is null");
      Objects.requireNonNull(tooltipFlag, "tooltip flag is null");
      Minecraft minecraft = Minecraft.getInstance();
      return itemStack.getTooltipLines(TooltipContext.of(minecraft.level), minecraft.player, tooltipFlag);
   }

   public static List<ClientTooltipComponent> getTooltip(ItemStack itemStack) {
      return getTooltip(itemStack, Minecraft.getInstance().options.advancedItemTooltips ? Default.ADVANCED : Default.NORMAL);
   }

   public static List<ClientTooltipComponent> getTooltip(ItemStack itemStack, TooltipFlag tooltipFlag) {
      Objects.requireNonNull(itemStack, "item stack is null");
      Objects.requireNonNull(tooltipFlag, "tooltip flag is null");
      List<Component> components = getTooltipLines(itemStack, tooltipFlag);
      List<TooltipComponent> imageComponents = itemStack.getTooltipImage().map(List::of).orElse(List.of());
      return createClientComponents(components, imageComponents);
   }

   public static void renderTooltip(GuiGraphics guiGraphics, int posX, int posY, ItemStack itemStack) {
      Objects.requireNonNull(itemStack, "item stack is null");
      renderTooltipComponents(guiGraphics, posX, posY, getTooltip(itemStack));
   }

   public static void renderTooltip(GuiGraphics guiGraphics, int posX, int posY, Component component, TooltipComponent imageComponent) {
      Objects.requireNonNull(component, "component is null");
      Objects.requireNonNull(imageComponent, "image component is null");
      renderTooltip(guiGraphics, posX, posY, List.of(component), imageComponent);
   }

   public static void renderTooltip(GuiGraphics guiGraphics, int posX, int posY, List<Component> components, TooltipComponent imageComponent) {
      Objects.requireNonNull(imageComponent, "image component is null");
      renderTooltip(guiGraphics, posX, posY, components, List.of(imageComponent));
   }

   public static void renderTooltip(GuiGraphics guiGraphics, int posX, int posY, List<Component> components) {
      renderTooltip(guiGraphics, posX, posY, components, List.of());
   }

   public static void renderTooltip(GuiGraphics guiGraphics, int posX, int posY, List<Component> components, List<TooltipComponent> imageComponents) {
      renderTooltipComponents(guiGraphics, posX, posY, createClientComponents(components, imageComponents));
   }

   public static List<ClientTooltipComponent> createClientComponents(List<Component> components, List<TooltipComponent> imageComponents) {
      return createClientComponents(components, imageComponents, 1);
   }

   public static List<ClientTooltipComponent> createClientComponents(List<Component> components, List<TooltipComponent> imageComponents, int insertAt) {
      List<ClientTooltipComponent> clientComponents = components.stream()
         .map(Component::getVisualOrderText)
         .<ClientTooltipComponent>map(ClientTooltipComponent::create)
         .collect(Collectors.toList());
      List<ClientTooltipComponent> clientImageComponents = imageComponents.stream().map(ClientAbstractions.INSTANCE::createImageComponent).toList();
      if (insertAt == -1) {
         clientComponents.addAll(clientImageComponents);
      } else {
         clientComponents.addAll(Math.min(clientComponents.size(), insertAt), clientImageComponents);
      }

      return ImmutableList.copyOf(clientComponents);
   }

   public static void renderTooltipComponents(GuiGraphics guiGraphics, int posX, int posY, List<? extends ClientTooltipComponent> components) {
      if (!components.isEmpty()) {
         Minecraft minecraft = Minecraft.getInstance();
         boolean result = ClientAbstractions.INSTANCE.onRenderTooltip(guiGraphics, minecraft.font, posX, posY, components, DefaultTooltipPositioner.INSTANCE);
         if (!result) {
            int lineWidth = 0;
            int lineHeight = components.size() == 1 ? -2 : 0;

            for (ClientTooltipComponent component : components) {
               int width = component.getWidth(minecraft.font);
               if (width > lineWidth) {
                  lineWidth = width;
               }

               lineHeight += component.getHeight();
            }

            posX += 12;
            posY -= 12;
            guiGraphics.pose().pushPose();
            renderTooltipBackground(guiGraphics, posX, posY, lineWidth, lineHeight);
            guiGraphics.pose().translate(0.0F, 0.0F, 400.0F);
            int currentPosY = posY;

            for (int i = 0; i < components.size(); i++) {
               ClientTooltipComponent component = components.get(i);
               component.renderText(minecraft.font, posX, currentPosY, guiGraphics.pose().last().pose(), guiGraphics.bufferSource());
               currentPosY += component.getHeight() + (i == 0 ? 2 : 0);
            }

            currentPosY = posY;

            for (int i = 0; i < components.size(); i++) {
               ClientTooltipComponent component = components.get(i);
               component.renderImage(minecraft.font, posX, currentPosY, guiGraphics);
               currentPosY += component.getHeight() + (i == 0 ? 2 : 0);
            }

            guiGraphics.flush();
            guiGraphics.pose().popPose();
         }
      }
   }

   private static void renderTooltipBackground(GuiGraphics guiGraphics, int posX, int posY, int lineWidth, int lineHeight) {
      guiGraphics.drawManaged(() -> TooltipRenderUtil.renderTooltipBackground(guiGraphics, posX, posY, lineWidth, lineHeight, 400));
   }
}
