package com.iafenvoy.origins.data.power.builtin.prevent;

import com.iafenvoy.origins.attachment.PowerHelper;
import com.iafenvoy.origins.config.OriginsConfig;
import com.iafenvoy.origins.data.condition.ItemCondition;
import com.iafenvoy.origins.data.power.Power;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.ChatFormatting;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber
public class PreventItemUsePower extends Power {
   public static final MapCodec<PreventItemUsePower> CODEC = RecordCodecBuilder.mapCodec(
      i -> i.group(
            Power.BaseSettings.CODEC.forGetter(Power::getSettings),
            ItemCondition.optionalCodec("item_condition").forGetter(PreventItemUsePower::getItemCondition)
         )
         .apply(i, PreventItemUsePower::new)
   );
   private final ItemCondition itemCondition;

   public PreventItemUsePower(Power.BaseSettings settings, ItemCondition itemCondition) {
      super(settings);
      this.itemCondition = itemCondition;
   }

   public ItemCondition getItemCondition() {
      return this.itemCondition;
   }

   @NotNull
   @Override
   public MapCodec<? extends Power> codec() {
      return CODEC;
   }

   public static boolean isUsagePrevented(Entity entity, ItemStack stack) {
      return PowerHelper.get(entity).anyActive(PreventItemUsePower.class, x -> x.itemCondition.test(entity.level(), stack));
   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public static void preventBlockInteraction(RightClickBlock event) {
      if (!(event.getItemStack().getItem() instanceof BlockItem) && isUsagePrevented(event.getEntity(), event.getItemStack())) {
         event.setUseItem(TriState.FALSE);
      }
   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public static void preventItemUsage(RightClickItem event) {
      if (isUsagePrevented(event.getEntity(), event.getItemStack())) {
         event.setCanceled(true);
      }
   }

   @SubscribeEvent
   public static void appendTooltips(ItemTooltipEvent event) {
      Player player = event.getEntity();
      if (player != null) {
         List<PreventItemUsePower> powers = PowerHelper.get(player)
            .listActive(PreventItemUsePower.class, p -> p.itemCondition.test(player.level(), event.getItemStack()));
         List<PreventItemUsePower> var10 = new ArrayList<>(powers);
         if (!var10.isEmpty()) {
            RegistryAccess access = player.registryAccess();
            var10.removeIf(Power::isHidden);
            int size = var10.size();
            String key = String.format(Locale.ROOT, "tooltip.%s.unusable.%s", "origins", event.getItemStack().getUseAnimation().name().toLowerCase(Locale.ROOT));
            ChatFormatting textColor = ChatFormatting.GRAY;
            ChatFormatting powerColor = ChatFormatting.RED;
            if (!(Boolean)OriginsConfig.INSTANCE.general.compactUsabilityHints.getValue() && !var10.isEmpty()) {
               MutableComponent component = ((PreventItemUsePower)var10.getFirst()).getName(access).withStyle(powerColor);

               for (int i = 1; i < var10.size(); i++) {
                  component = component.append(Component.literal(", ").withStyle(textColor));
                  component = component.append(((PreventItemUsePower)var10.get(i)).getName(access).withStyle(powerColor));
               }

               MutableComponent preventText = Component.translatable(key + ".single", new Object[]{component}).withStyle(textColor);
               event.getToolTip().add(preventText);
            } else if (var10.size() == 1) {
               PreventItemUsePower power = (PreventItemUsePower)var10.getFirst();
               event.getToolTip().add(Component.translatable(key + ".single", new Object[]{power.getName(access).withStyle(powerColor)}).withStyle(textColor));
            } else {
               event.getToolTip()
                  .add(
                     Component.translatable(
                           key + ".multiple", new Object[]{Component.literal((var10.isEmpty() ? size : var10.size()) + "").withStyle(powerColor)}
                        )
                        .withStyle(textColor)
                  );
            }
         }
      }
   }
}
