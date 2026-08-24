package com.anthonyhilyard.iceberg.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiPredicate;
import net.minecraft.client.Minecraft;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.TooltipFlag.Default;

public class Selectors {
   private static Map<String, Rarity> rarities = new HashMap<String, Rarity>() {
      {
         this.put("common", Rarity.COMMON);
         this.put("uncommon", Rarity.UNCOMMON);
         this.put("rare", Rarity.RARE);
         this.put("epic", Rarity.EPIC);
      }
   };
   private static Map<String, BiPredicate<Tag, String>> nbtComparators = new HashMap<String, BiPredicate<Tag, String>>() {
      {
         this.put("=", (tag, value) -> tag.getAsString().contentEquals(value));
         this.put("!=", (tag, value) -> !tag.getAsString().contentEquals(value));
         this.put(">", (tag, value) -> {
            try {
               double parsedValue = Double.valueOf(value);
               return tag instanceof NumericTag ? ((NumericTag)tag).getAsDouble() > parsedValue : false;
            } catch (Exception var4) {
               return false;
            }
         });
         this.put("<", (tag, value) -> {
            try {
               double parsedValue = Double.valueOf(value);
               return tag instanceof NumericTag ? ((NumericTag)tag).getAsDouble() < parsedValue : false;
            } catch (Exception var4) {
               return false;
            }
         });
      }
   };

   public static List<Selectors.SelectorDocumentation> selectorDocumentation() {
      return Arrays.asList(
         new Selectors.SelectorDocumentation("Match all", "Specifying just an asterisk (*) will match all items.", "*"),
         new Selectors.SelectorDocumentation(
            "Item ID", "Use item ID to match single items.  Must include mod name for modded items.", "minecraft:stick", "iron_ore", "spoiledeggs:spoiled_egg"
         ),
         new Selectors.SelectorDocumentation("Tag", "$ followed by tag name to match all items with that tag.", "$forge:stone", "$planks"),
         new Selectors.SelectorDocumentation("Mod name", "@ followed by mod identifier to match all items from that mod.", "@spoiledeggs"),
         new Selectors.SelectorDocumentation(
            "Rarity", "! followed by item's rarity to match all items with that rarity.  This is ONLY vanilla rarities.", "!uncommon", "!rare", "!epic"
         ),
         new Selectors.SelectorDocumentation("Item name color", "# followed by color hex code, to match all items with that exact color item name.", "#23F632"),
         new Selectors.SelectorDocumentation(
            "Display name",
            "% followed by any text.  Will match any item with this text (case-sensitive) in its tooltip display name.",
            "%Netherite",
            "%Uncommon"
         ),
         new Selectors.SelectorDocumentation(
            "Tooltip text",
            "^ followed by any text.  Will match any item with this text (case-sensitive) anywhere in the tooltip text (besides the name).",
            "^Legendary"
         ),
         new Selectors.SelectorDocumentation(
            "NBT/Item component",
            "& followed by tag or component name and optional comparator (=, >, <, or !=) and value, in the format <name><comparator><value> or just <name>.",
            "&damage>100",
            "&Tier>1",
            "&map_id!=128",
            "&enchantments"
         ),
         new Selectors.SelectorDocumentation(
            "Negation",
            "~ followed by any selector above.  This selector will be negated, matching every item that does NOT match the selector.",
            "~minecraft:stick",
            "~!uncommon",
            "~@minecraft"
         ),
         new Selectors.SelectorDocumentation(
            "Combining selectors",
            "Any number of selectors can be combined by separating them with a plus sign.",
            "minecraft:diamond_sword+&enchantments",
            "minecraft:stick+~!common+&damage>100"
         )
      );
   }

   public static boolean validateSelector(String value) {
      if (value.contains("+")) {
         for (String selector : value.split("\\+")) {
            if (!validateSelector(selector)) {
               return false;
            }
         }

         return true;
      } else if (value.startsWith("~")) {
         return validateSelector(value.substring(1));
      } else if (value.contentEquals("*")) {
         return true;
      } else if (value.startsWith("$")) {
         return ResourceLocation.tryParse(value.substring(1)) != null;
      } else if (value.startsWith("@")) {
         return value.substring(1).matches("^[a-z][a-z0-9_-]{1,63}$");
      } else if (value.startsWith("!")) {
         return rarities.keySet().contains(value.substring(1).toLowerCase());
      } else if (value.startsWith("#")) {
         return TextColor.parseColor(value).result().orElse(null) != null;
      } else if (value.startsWith("%") || value.startsWith("^")) {
         return true;
      } else {
         return value.startsWith("&") ? true : value == null || value == "" || ResourceLocation.tryParse(value) != null;
      }
   }

   public static boolean itemMatches(ItemStack item, String selector, Provider provider) {
      if (item.isEmpty()) {
         return false;
      } else if (selector.contains("+")) {
         for (String subSelector : selector.split("\\+")) {
            if (!itemMatches(item, subSelector, provider)) {
               return false;
            }
         }

         return true;
      } else if (selector.startsWith("~")) {
         return !itemMatches(item, selector.substring(1), provider);
      } else if (selector.contentEquals("*")) {
         return true;
      } else {
         String itemResourceLocation = BuiltInRegistries.ITEM.getKey(item.getItem()).toString();
         if (!selector.equals(itemResourceLocation) && !selector.equals(itemResourceLocation.replace("minecraft:", ""))) {
            if (selector.startsWith("@")) {
               if (itemResourceLocation.startsWith(selector.substring(1) + ":")) {
                  return true;
               }
            } else if (selector.startsWith("#")) {
               TextColor entryColor = (TextColor)TextColor.parseColor(selector).result().orElse(null);
               if (entryColor != null && entryColor.equals(ItemColor.getColorForItem(item, TextColor.fromRgb(16777215)))) {
                  return true;
               }
            } else if (selector.startsWith("!")) {
               if (item.getRarity() == rarities.get(selector.substring(1))) {
                  return true;
               }
            } else if (selector.startsWith("$")) {
               Optional<TagKey<Item>> matchingTag = BuiltInRegistries.ITEM
                  .getTagNames()
                  .filter(tagKey -> tagKey.location().equals(ResourceLocation.parse(selector.substring(1))))
                  .findFirst();
               if (matchingTag.isPresent() && item.is(matchingTag.get())) {
                  return true;
               }
            } else if (selector.startsWith("%")) {
               if (item.getDisplayName().getString().contains(selector.substring(1))) {
                  return true;
               }
            } else if (selector.startsWith("^")) {
               Minecraft mc = Minecraft.getInstance();
               List<Component> lines = item.getTooltipLines(TooltipContext.EMPTY, mc.player, Default.ADVANCED);
               String tooltipText = "";

               for (int n = 1; n < lines.size(); n++) {
                  tooltipText = tooltipText + lines.get(n).getString() + "\n";
               }

               if (tooltipText.contains(selector.substring(1))) {
                  return true;
               }
            } else if (selector.startsWith("&")) {
               String name = selector.substring(1);
               String value = null;
               BiPredicate<Tag, String> valueChecker = null;

               for (String comparator : nbtComparators.keySet()) {
                  if (name.contains(comparator)) {
                     valueChecker = nbtComparators.get(comparator);
                     String[] components = name.split(comparator);
                     name = components[0];
                     if (components.length > 1) {
                        value = components[1];
                     }
                     break;
                  }
               }

               Tag itemTag = item.save(provider);
               boolean result = findMatchingSubtag(itemTag, name, value, valueChecker);
               if (!result) {
                  if (!name.contains(":")) {
                     name = "minecraft:" + name;
                  }

                  if (value != null) {
                     if (!value.contains(":") && value.matches("^[a-z]+$")) {
                        value = "minecraft:" + value;
                     } else if (value.contains("\"")) {
                        String[] components = value.split("\"");

                        for (int i = 0; i < components.length; i++) {
                           if (i % 2 == 1 && !components[i].contains(":")) {
                              components[i] = "minecraft:" + components[i];
                           }
                        }

                        value = String.join("\"", components);
                     }
                  }

                  result = findMatchingSubtag(itemTag, name, value, valueChecker);
               }

               return result;
            }

            return false;
         } else {
            return true;
         }
      }
   }

   private static boolean findMatchingSubtag(Tag tag, String key, String value, BiPredicate<Tag, String> valueChecker) {
      if (tag == null) {
         return false;
      } else {
         if (tag.getId() == 8) {
            try {
               tag = TagParser.parseTag(tag.getAsString());
            } catch (Exception var9) {
            }
         }

         if (tag.getId() == 10) {
            CompoundTag compoundTag = (CompoundTag)tag;
            if (compoundTag.contains(key)) {
               return value == null && valueChecker == null ? true : valueChecker.test(compoundTag.get(key), value);
            } else {
               for (String innerKey : compoundTag.getAllKeys()) {
                  if (compoundTag.getTagType(innerKey) != 9 && compoundTag.getTagType(innerKey) != 10) {
                     if (compoundTag.getTagType(innerKey) == 8) {
                        try {
                           tag = TagParser.parseTag(tag.getAsString());
                           if (findMatchingSubtag(compoundTag.get(innerKey), key, value, valueChecker)) {
                              return true;
                           }
                        } catch (Exception var8) {
                        }
                     }
                  } else if (findMatchingSubtag(compoundTag.get(innerKey), key, value, valueChecker)) {
                     return true;
                  }
               }

               return false;
            }
         } else {
            if (tag.getId() == 9) {
               for (Tag innerTag : (ListTag)tag) {
                  if ((innerTag.getId() == 9 || innerTag.getId() == 10) && findMatchingSubtag(innerTag, key, value, valueChecker)) {
                     return true;
                  }
               }
            }

            return false;
         }
      }
   }

   public record SelectorDocumentation(String name, String description, List<String> examples) {
      public SelectorDocumentation(String name, String description, String... examples) {
         this(name, description, Arrays.asList(examples));
      }
   }
}
