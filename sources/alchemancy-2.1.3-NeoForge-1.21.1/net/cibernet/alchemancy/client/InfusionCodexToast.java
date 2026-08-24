package net.cibernet.alchemancy.client;

import com.google.common.collect.Lists;
import java.util.List;
import net.cibernet.alchemancy.client.data.CodexEntryReloadListenener;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.registries.AlchemancyItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.gui.components.toasts.Toast.Visibility;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class InfusionCodexToast implements Toast {
   private static final Component TITLE_TEXT = Component.translatable("infusion_codex.toast.title");
   private static final Component DESCRIPTION_TEXT = Component.translatable("infusion_codex.toast.description");
   private static final ResourceLocation BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace("toast/recipe");
   private static final double DISPLAY_TIME = 5000.0;
   private long lastChanged;
   private boolean changed;
   private final List<Holder<Property>> properties = Lists.newArrayList();
   private static final ItemStack CODEX_ICON = AlchemancyItems.INFUSION_CODEX.toStack();

   public InfusionCodexToast(Holder<Property> property) {
      this.properties.add(property);
   }

   public int width() {
      return Math.max(Minecraft.getInstance().font.width(TITLE_TEXT), Minecraft.getInstance().font.width(DESCRIPTION_TEXT)) + 38;
   }

   public Visibility render(GuiGraphics guiGraphics, ToastComponent toastComponent, long timeSinceLastVisible) {
      if (this.changed) {
         this.lastChanged = timeSinceLastVisible;
         this.changed = false;
      }

      guiGraphics.blitSprite(BACKGROUND_SPRITE, 0, 0, this.width(), this.height());
      guiGraphics.drawString(toastComponent.getMinecraft().font, TITLE_TEXT, 30, 7, -11534256, false);
      guiGraphics.drawString(toastComponent.getMinecraft().font, DESCRIPTION_TEXT, 30, 18, -16777216, false);
      Holder<Property> propertyholder = this.properties
         .get(
            (int)(
               timeSinceLastVisible
                  / Math.max(1.0, 5000.0 * toastComponent.getNotificationDisplayTimeMultiplier() / this.properties.size())
                  % this.properties.size()
            )
         );
      guiGraphics.pose().pushPose();
      guiGraphics.pose().scale(0.6F, 0.6F, 1.0F);
      guiGraphics.renderFakeItem(CODEX_ICON, 3, 3);
      guiGraphics.pose().popPose();
      guiGraphics.renderFakeItem(InfusedPropertiesHelper.createPropertyCapsule(propertyholder), 8, 8);
      return timeSinceLastVisible - this.lastChanged >= 5000.0 * toastComponent.getNotificationDisplayTimeMultiplier() ? Visibility.HIDE : Visibility.SHOW;
   }

   private void addItem(Holder<Property> property) {
      this.properties.add(property);
      this.changed = true;
   }

   public static void addOrUpdate(ToastComponent toastComponent, Holder<Property> property) {
      if (CodexEntryReloadListenener.getEntries().containsKey(property)) {
         InfusionCodexToast propertytoast = (InfusionCodexToast)toastComponent.getToast(InfusionCodexToast.class, NO_TOKEN);
         if (propertytoast == null) {
            toastComponent.addToast(new InfusionCodexToast(property));
         } else {
            propertytoast.addItem(property);
         }
      }
   }
}
