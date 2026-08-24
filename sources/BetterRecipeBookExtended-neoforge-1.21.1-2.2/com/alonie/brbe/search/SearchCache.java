package com.alonie.brbe.search;

import com.alonie.brbe.util.ModNameUtil;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;

public class SearchCache {
   private final Map<String, String> modNameCache = new HashMap<>();
   private final Map<ItemStack, Set<String>> tagsCache = new HashMap<>();
   private final Map<ItemStack, String> tooltipCache = new HashMap<>();
   private final Map<Item, String> namespaceCache = new HashMap<>();
   private TooltipContext tooltipContext;

   private void ensureTooltipContext() {
      if (this.tooltipContext == null) {
         ClientLevel level = Minecraft.getInstance().level;
         if (level != null) {
            this.tooltipContext = TooltipContext.of(level);
         }
      }
   }

   public String getModNamespace(ItemStack stack) {
      return this.namespaceCache.computeIfAbsent(stack.getItem(), item -> BuiltInRegistries.ITEM.getKey(item).getNamespace());
   }

   public String getModName(ItemStack stack) {
      String namespace = this.getModNamespace(stack);
      return this.modNameCache.computeIfAbsent(namespace, ModNameUtil::resolveModName);
   }

   public Set<String> getTags(ItemStack stack) {
      return this.tagsCache.computeIfAbsent(stack.copy(), key -> {
         Set<String> tags = new HashSet<>();
         key.getItem().builtInRegistryHolder().tags().map(tagKey -> tagKey.location().toString()).forEach(tags::add);
         return tags;
      });
   }

   public String getTooltipText(ItemStack stack) {
      return this.tooltipCache.computeIfAbsent(stack.copy(), key -> {
         this.ensureTooltipContext();
         if (this.tooltipContext == null) {
            return "";
         } else {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null) {
               return "";
            } else {
               try {
                  List<Component> lines = key.getTooltipLines(this.tooltipContext, player, TooltipFlag.NORMAL);
                  return lines.stream().<CharSequence>map(Component::getString).collect(Collectors.joining("\n"));
               } catch (Exception var4) {
                  return "";
               }
            }
         }
      });
   }

   public void clear() {
      this.modNameCache.clear();
      this.tagsCache.clear();
      this.tooltipCache.clear();
   }
}
