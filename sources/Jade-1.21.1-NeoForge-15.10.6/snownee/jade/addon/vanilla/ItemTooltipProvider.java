package snownee.jade.addon.vanilla;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.TooltipFlag.Default;
import snownee.jade.JadeClient;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.JadeIds;
import snownee.jade.api.TraceableException;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.impl.ui.TextElement;
import snownee.jade.overlay.DisplayHelper;
import snownee.jade.util.ModIdentification;
import snownee.jade.util.WailaExceptionHandler;

public enum ItemTooltipProvider implements IEntityComponentProvider {
   INSTANCE;

   public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
      ItemStack stack = ((ItemEntity)accessor.getEntity()).getItem();
      TooltipContext tooltipContext = TooltipContext.of(accessor.getLevel());
      JadeClient.hideModNameIn(tooltipContext);
      List<Either<FormattedText, TooltipComponent>> lines = Lists.newArrayList();

      try {
         stack.getTooltipLines(tooltipContext, null, Default.NORMAL).stream().peek(component -> {
            if (component instanceof MutableComponent mutable && mutable.getStyle().getColor() != null) {
               mutable.setStyle(mutable.getStyle().withColor((TextColor)null));
            }
         }).map(Either::left).forEach(lines::add);
      } catch (Throwable var14) {
         String namespace = BuiltInRegistries.ITEM.getKey(stack.getItem()).getNamespace();
         WailaExceptionHandler.handleErr(TraceableException.create(var14, namespace), this, tooltip::add);
      }

      if (!lines.isEmpty()) {
         List<FormattedText> realLines = lines.stream().map($ -> $.left()).filter(Optional::isPresent).map(Optional::get).skip(1L).toList();
         String modName = ModIdentification.getModName(stack);
         Font font = DisplayHelper.font();
         int maxWidth = 250;

         for (FormattedText text : realLines) {
            if (!Objects.equals(ChatFormatting.stripFormatting(text.getString()), modName)) {
               int width = font.width(text);
               if (width > maxWidth) {
                  tooltip.add(Component.literal(font.substrByWidth(text, maxWidth - 5).getString() + ".."));
               } else {
                  tooltip.add(new TextElement(text));
               }
            }
         }
      }
   }

   @Override
   public ResourceLocation getUid() {
      return JadeIds.MC_ITEM_TOOLTIP;
   }
}
