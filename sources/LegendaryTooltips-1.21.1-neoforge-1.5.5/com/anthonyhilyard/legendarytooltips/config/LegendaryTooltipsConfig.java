package com.anthonyhilyard.legendarytooltips.config;

import com.anthonyhilyard.iceberg.config.IcebergConfig;
import com.anthonyhilyard.iceberg.services.IIcebergConfigSpecBuilder;
import com.anthonyhilyard.iceberg.services.Services;
import com.anthonyhilyard.iceberg.util.Selectors;
import com.anthonyhilyard.iceberg.util.StringRecomposer;
import com.anthonyhilyard.iceberg.util.Selectors.SelectorDocumentation;
import com.anthonyhilyard.legendarytooltips.LegendaryTooltips;
import com.anthonyhilyard.legendarytooltips.tooltip.TooltipDecor;
import com.anthonyhilyard.prism.item.ItemColors;
import com.anthonyhilyard.prism.text.DynamicColor;
import com.anthonyhilyard.prism.util.ConfigHelper;
import com.anthonyhilyard.prism.util.IColor;
import com.anthonyhilyard.prism.util.ImageAnalysis;
import com.anthonyhilyard.prism.util.ConfigHelper.ColorFormatDocumentation;
import com.google.common.collect.Lists;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.exception.ExceptionUtils;

public class LegendaryTooltipsConfig extends IcebergConfig<LegendaryTooltipsConfig> {
   public static final int DEFAULT_FRAME_WIDTH = 64;
   public static final int DEFAULT_PART_SIZE = 8;
   public static final int DEFAULT_PART_OFFSET = -1;
   public static final int DEFAULT_CORNER_OFFSET = 2;
   public static final Map<LegendaryTooltipsConfig.ColorType, TextColor> defaultColors = Map.of(
      LegendaryTooltipsConfig.ColorType.BORDER_START,
      TextColor.fromRgb(-6723294),
      LegendaryTooltipsConfig.ColorType.BORDER_END,
      TextColor.fromRgb(-10864099),
      LegendaryTooltipsConfig.ColorType.BG_START,
      TextColor.fromRgb(-266991104),
      LegendaryTooltipsConfig.ColorType.BG_END,
      TextColor.fromRgb(-401208832)
   );
   public static final LegendaryTooltipsConfig.FrameDefinition STANDARD_BORDER = new LegendaryTooltipsConfig.FrameDefinition(
      null, -1, null, null, null, null, LegendaryTooltipsConfig.FrameSource.NONE, 0, 64, 8, -1, 2
   );
   public static final LegendaryTooltipsConfig.FrameDefinition NO_BORDER = new LegendaryTooltipsConfig.FrameDefinition(
      null, -2, null, null, null, null, LegendaryTooltipsConfig.FrameSource.NONE, 0, 0, 0, 0, 0
   );
   public final Supplier<Boolean> nameSeparator;
   public final Supplier<Boolean> showSeparatorForEmpty;
   public final Supplier<Boolean> bordersMatchRarity;
   public final Supplier<Boolean> tooltipShadow;
   public final Supplier<Boolean> shineEffect;
   public final Supplier<Boolean> centeredTitle;
   public final Supplier<Boolean> enforceMinimumWidth;
   public final Supplier<Boolean> compactTooltips;
   private final Supplier<LegendaryTooltipsConfig.ModelRenderType> renderItemModel;
   public final Supplier<Double> modelRotationSpeed;
   public final Supplier<Boolean> fixMC271840;
   private final Supplier<Integer> maxTooltipWidth;
   private final Supplier<Integer> maxTooltipHeight;
   private final Supplier<LegendaryTooltipsConfig.TooltipsScrollActive> enableTooltipScrolling;
   public final Supplier<Double> scrollSpeed;
   final TextColor[] startColors = new TextColor[16];
   final TextColor[] endColors = new TextColor[16];
   private final TextColor[] startBGColors = new TextColor[16];
   private final TextColor[] endBGColors = new TextColor[16];
   final Supplier<List<? extends Integer>> framePriorities;
   static final List<Supplier<List<? extends String>>> itemSelectors = new ArrayList<>(16);
   private final Supplier<List<? extends String>> blacklist;
   private static final Map<LegendaryTooltipsConfig.FrameDefinition, Set<String>> customFrameDefinitions = new LinkedHashMap<>();
   private static final Map<ItemStack, LegendaryTooltipsConfig.FrameDefinition> frameDefinitionCache = new HashMap<>();
   private static final Map<FormattedText, FormattedText> formattedTitleCache = new HashMap<>();
   private static final List<Supplier<Supplier<?>>> colorSuppliers = new ArrayList<>();
   private static TagKey<Item> armorTag = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "armors"));
   private static TagKey<Item> toolsTag = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "tools"));

   public static LegendaryTooltipsConfig getInstance() {
      return (LegendaryTooltipsConfig)configInstances.get("legendarytooltips");
   }

   public LegendaryTooltipsConfig(IIcebergConfigSpecBuilder build) {
      build.comment(
            " Legendary Tooltips Configuration Instructions\n\n *** READ THIS FIRST ***\n\n By default, this mod does not apply special borders to most items.  It was designed to work well with mod packs where\n the available selection of items can vary widely, so it is up to the user or mod pack designer to customize as needed.\n There are many options available for setting up which custom borders (also called frames) apply to which items.  Follow these steps:\n   1. Decide which items you want to have custom borders, and which borders.  Note that each custom border has a number associated with it (starting at 0).\n   2. For each custom border you want to use, fill out the associated list in the \"definitions\" section.  This will be filled out with a list of \"selectors\",\n      each of which tell the mod what items have that border.  Please read the information above the definitions section for specifics.\n   3. Selectors for borders are checked in the order provided in the \"priorities\" section.  Once a match is found, that border is displayed.\n      For example, if border 0 had the selector \"%Diamond\" and border 1 had the selector \"diamond_sword\", they would both match for diamond swords.\n      In this case, whichever border number came first in the priority list would be the border that would get drawn in-game.\n   4. Optionally, border colors associated with custom borders can be set in the \"colors\" section.  The start color is the color at the top of the tooltip,\n      and the end color is the bottom, with a smooth transition between.  Please read the information above the color section for specifics."
         )
         .push("client")
         .push("visual_options");
      this.nameSeparator = build.comment(" Whether item names in tooltips should have a line under them separating them from the rest of the tooltip.")
         .add("name_separator", true);
      this.showSeparatorForEmpty = build.comment(
            " If enabled, the name separator will be shown for all tooltips.  If disabled, it will only be shown for item tooltips."
         )
         .add("show_separator_for_empty", true);
      this.bordersMatchRarity = build.comment(" If enabled, tooltip border colors will match item rarity colors (except for custom borders).")
         .add("borders_match_rarity", true);
      this.tooltipShadow = build.comment(" If enabled, tooltips will display a drop shadow.").add("tooltip_shadow", true);
      this.shineEffect = build.comment(" If enabled, items showing a custom border will have a special shine effect when hovered over.")
         .add("shine_effect", true);
      this.centeredTitle = build.comment(" If enabled, tooltip titles will be drawn centered.").add("centered_title", true);
      this.enforceMinimumWidth = build.comment(
            " If enabled, tooltips with custom borders will always be at least wide enough to display all border decorations."
         )
         .add("enforce_minimum_width", false);
      this.compactTooltips = build.comment(" If enabled, some unnecessary text and spacing will be removed from equipment tooltips.")
         .add("compact_tooltips", true);
      this.renderItemModel = build.comment(
            " Which items should have a 3D model rendered in the tooltip.  If set to \"equipment\", the model will only be rendered for armor, tools, and items with durability."
         )
         .addEnum("render_item_model", LegendaryTooltipsConfig.ModelRenderType.EQUIPMENT);
      this.modelRotationSpeed = build.comment(
            " The speed at which 3D models in tooltips will rotate.  Lower values rotate faster, set to 0 to disable rotation."
         )
         .addInRange("model_rotation_speed", 12.0, 0.0, 50.0);
      this.fixMC271840 = build.comment(
            " If enabled, fixes a vanilla bug where displayed tooltip damage values are incorrect for weapons with the Sharpness enchantment."
         )
         .add("fix_mc271840", true);
      this.maxTooltipWidth = build.comment(" (EXPERIMENTAL) The maximum width of tooltips.  Set to 0 for no limit.")
         .addInRange("max_tooltip_width", 0, 0, 2147483647);
      this.maxTooltipHeight = build.comment(" (EXPERIMENTAL) The maximum height of tooltips.  Set to 0 for no limit.")
         .addInRange("max_tooltip_height", 0, 0, 2147483647);
      this.enableTooltipScrolling = build.comment(
            " (EXPERIMENTAL) If enabled, tooltips that are larger than the maximum height specified (or the screen if not specified) will be scrollable with the mouse wheel."
         )
         .addEnum("enable_tooltip_scrolling", LegendaryTooltipsConfig.TooltipsScrollActive.NEVER);
      this.scrollSpeed = build.comment(" (EXPERIMENTAL) The speed at which tooltips will scroll when scrolling is enabled.")
         .addInRange("scroll_speed", 10.0, 1.0, 50.0);
      build.pop()
         .comment(
            String.format(
               " Custom borders are broken into %d \"levels\", with level 0 being intended for the \"best\" or \"rarest\" items. Only level 0 has a custom border built-in, but others can be added with resource packs.",
               16
            )
         )
         .push("custom_borders");
      StringBuilder selectorsComment = new StringBuilder(" Entry types:\n");

      for (SelectorDocumentation doc : Selectors.selectorDocumentation()) {
         selectorsComment.append("    ").append(doc.name()).append(" - ").append(doc.description());
         if (!doc.examples().isEmpty()) {
            selectorsComment.append("  Examples: ");

            for (int i = 0; i < doc.examples().size(); i++) {
               if (i > 0) {
                  selectorsComment.append(", ");
               }

               selectorsComment.append("\"").append((String)doc.examples().get(i)).append("\"");
            }
         }

         selectorsComment.append("\n");
      }

      selectorsComment.setLength(selectorsComment.length() - 1);
      build.pop().comment(selectorsComment.toString());
      build.push("definitions");
      itemSelectors.clear();
      itemSelectors.add(build.addListAllowEmpty("level0_entries", Arrays.asList("!epic", "!rare"), e -> Selectors.validateSelector((String)e)));

      for (int i = 1; i < 16; i++) {
         itemSelectors.add(build.addListAllowEmpty(String.format("level%d_entries", i), Lists.newArrayList(), e -> Selectors.validateSelector((String)e)));
      }

      this.blacklist = build.comment(
            " Enter blacklist selectors here using the same format as above. Any items that match these selectors will NOT show a border."
         )
         .addListAllowEmpty("blacklist", List.of(), e -> Selectors.validateSelector((String)e));
      build.pop()
         .comment(
            " Set border priorities here.  This should be a list of numbers that correspond to border levels, with numbers coming first being higher priority.\n Optionally, -1 can be inserted to indicate relative priority of data and api-defined borders.  If you don't know what that means, you don't need to worry about it."
         )
         .push("priorities");
      this.framePriorities = build.addList(
         "priorities", IntStream.rangeClosed(0, 15).boxed().collect(Collectors.toList()), e -> (Integer)e >= -1 && (Integer)e < 16
      );
      StringBuilder colorFormatsComment = new StringBuilder(" VALID COLOR FORMATS\n");

      for (ColorFormatDocumentation doc : ConfigHelper.colorFormatDocumentation()) {
         colorFormatsComment.append("   ").append(doc.name()).append(" - ").append(doc.description().replace("\n", "\n         "));
         if (!doc.examples().isEmpty()) {
            colorFormatsComment.append("\n     Examples: ");

            for (int i = 0; i < doc.examples().size(); i++) {
               if (i > 0) {
                  colorFormatsComment.append(", ");
               }

               colorFormatsComment.append((String)doc.examples().get(i));
            }
         }

         colorFormatsComment.append("\n\n");
      }

      colorFormatsComment.setLength(colorFormatsComment.length() - 2);
      build.pop()
         .comment(
            " The colors used for each tooltip, in this order: top border color, bottom border color, top background color, bottom background color.\n None of these colors are required, though any colors not specified will be replaced with the default tooltip colors.\n\n"
               + colorFormatsComment.toString()
         )
         .push("colors");
      colorSuppliers.clear();

      for (int i = 0; i < 16; i++) {
         Supplier<?> colorsValue = build.addList(
            String.format("level%d_colors", i),
            i == 0
               ? List.of(
                  defaultColors.get(LegendaryTooltipsConfig.ColorType.BORDER_START).getValue(),
                  defaultColors.get(LegendaryTooltipsConfig.ColorType.BORDER_END).getValue(),
                  defaultColors.get(LegendaryTooltipsConfig.ColorType.BG_START).getValue(),
                  defaultColors.get(LegendaryTooltipsConfig.ColorType.BG_END).getValue()
               )
               : List.of("auto", "auto", "auto", "auto"),
            v -> validateColor(v)
         );
         colorSuppliers.add(() -> colorsValue);
      }

      build.pop().pop();
   }

   public static int getMaxTooltipWidth() {
      int maxWidth = getInstance().maxTooltipWidth.get();
      if (maxWidth == 0) {
         Minecraft minecraft = Minecraft.getInstance();
         maxWidth = minecraft.getWindow().getGuiScaledWidth();
      }

      if (getInstance().enforceMinimumWidth.get() && maxWidth < 48) {
         maxWidth = 48;
      }

      return maxWidth;
   }

   public static int getMaxTooltipHeight() {
      int maxHeight = getInstance().maxTooltipHeight.get();
      if (maxHeight == 0) {
         Minecraft minecraft = Minecraft.getInstance();
         maxHeight = minecraft.getWindow().getGuiScaledHeight();
      }

      return maxHeight;
   }

   public static boolean shouldScrollTooltip() {
      switch ((LegendaryTooltipsConfig.TooltipsScrollActive)getInstance().enableTooltipScrolling.get()) {
         case ALWAYS:
            return true;
         case WITH_KEYBIND:
            return LegendaryTooltips.scrollTooltipsKeyDown;
         case NEVER:
         default:
            return false;
      }
   }

   public static boolean showModelForItem(ItemStack itemStack) {
      switch ((LegendaryTooltipsConfig.ModelRenderType)getInstance().renderItemModel.get()) {
         case NONE:
         default:
            return false;
         case EQUIPMENT:
            return !itemStack.isEmpty() && (itemStack.has(DataComponents.MAX_DAMAGE) || itemStack.is(armorTag) || itemStack.is(toolsTag));
         case ALL:
            return !itemStack.isEmpty();
      }
   }

   private static FormattedCharSequence getTitlePadding(Font font, int maxWidth) {
      String pad = " ";

      while (font.width(pad) < maxWidth) {
         pad = pad + " ";
      }

      return FormattedCharSequence.forward(pad, Style.EMPTY);
   }

   private static Font getFontForTitle(FormattedText title) {
      Font result = null;
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft.screen != null && minecraft.screen.font != null && minecraft.screen.font != minecraft.font) {
         result = minecraft.screen.font;
      } else {
         result = minecraft.font;
      }

      return result;
   }

   public static FormattedText getFormattedTitle(FormattedText title) {
      if (!formattedTitleCache.containsKey(title)) {
         Font font = getFontForTitle(title);
         FormattedCharSequence paddedTitle = FormattedCharSequence.fromList(
            List.of(getTitlePadding(font, 24), Language.getInstance().getVisualOrder(title), getTitlePadding(font, 4))
         );
         List<FormattedText> recomposedTitle = StringRecomposer.recompose(List.of(ClientTooltipComponent.create(paddedTitle)));
         if (!recomposedTitle.isEmpty()) {
            formattedTitleCache.put(title, recomposedTitle.get(0));
         }
      }

      return formattedTitleCache.get(title);
   }

   public static TextColor getColor(Object value) {
      return getColor(value, null, null, 0, null);
   }

   public static TextColor getColor(Object value, TextColor defaultColor, ResourceLocation borderImage, int index, LegendaryTooltipsConfig.ColorType colorType) {
      TextColor color = (TextColor)ConfigHelper.parseColor(value);
      if (color == null) {
         if (value instanceof String string && string.contentEquals("auto") && borderImage != null) {
            Rect2i region = new Rect2i(index / 8 * 64, index * 16 % 128, 64, 16);
            switch (colorType) {
               case BORDER_START:
                  region.setHeight(8);
                  break;
               case BORDER_END:
                  region.setHeight(8);
                  region.setY(region.getY() + 8);
            }

            color = ImageAnalysis.getDominantColor(borderImage, region);
            if (color == null) {
               return defaultColor;
            }

            switch (colorType) {
               case BORDER_START:
                  if (DynamicColor.fromColor((IColor)color).value() > 80) {
                     color = ConfigHelper.applyModifiers(List.of("-v10", "+s10"), color);
                     if (DynamicColor.fromColor((IColor)color).value() > 200) {
                        color = ConfigHelper.applyModifiers(List.of("-v10"), color);
                     }
                  }

                  if (DynamicColor.fromColor((IColor)color).saturation() > 40) {
                     color = ConfigHelper.applyModifiers(List.of("+s10"), color);
                  }
                  break;
               case BORDER_END:
                  if (DynamicColor.fromColor((IColor)color).value() > 80) {
                     color = ConfigHelper.applyModifiers(List.of("-v30"), color);
                     if (DynamicColor.fromColor((IColor)color).value() > 170 && DynamicColor.fromColor((IColor)color).value() < 220) {
                        color = ConfigHelper.applyModifiers(List.of("-v30"), color);
                     }
                  }

                  if (DynamicColor.fromColor((IColor)color).saturation() > 40) {
                     color = ConfigHelper.applyModifiers(List.of("+s50"), color);
                  }
                  break;
               case BG_START:
                  color = ConfigHelper.applyModifiers(List.of("=v8", "+s50", "=a245"), color);
                  break;
               case BG_END:
                  color = ConfigHelper.applyModifiers(List.of("=v20", "+s75", "=a230"), color);
            }

            if (color != null) {
               return color;
            }
         }

         return defaultColor;
      } else {
         return color;
      }
   }

   private static boolean validateColor(Object value) {
      return getColor(value) != null || value instanceof String string && string.contentEquals("auto");
   }

   private static void resolveColors() {
      for (int i = 0; i < 16; i++) {
         if (colorSuppliers.get(i).get().get() instanceof List<?> colorsList) {
            getInstance().startColors[i] = getColor(
               colorsList.size() > 0 ? colorsList.get(0) : null,
               defaultColors.get(LegendaryTooltipsConfig.ColorType.BORDER_START),
               TooltipDecor.DEFAULT_BORDERS,
               i,
               LegendaryTooltipsConfig.ColorType.BORDER_START
            );
            getInstance().endColors[i] = getColor(
               colorsList.size() > 1 ? colorsList.get(1) : null,
               defaultColors.get(LegendaryTooltipsConfig.ColorType.BORDER_END),
               TooltipDecor.DEFAULT_BORDERS,
               i,
               LegendaryTooltipsConfig.ColorType.BORDER_END
            );
            getInstance().startBGColors[i] = getColor(
               colorsList.size() > 2 ? colorsList.get(2) : null,
               defaultColors.get(LegendaryTooltipsConfig.ColorType.BG_START),
               TooltipDecor.DEFAULT_BORDERS,
               i,
               LegendaryTooltipsConfig.ColorType.BG_START
            );
            getInstance().endBGColors[i] = getColor(
               colorsList.size() > 3 ? colorsList.get(3) : null,
               defaultColors.get(LegendaryTooltipsConfig.ColorType.BG_END),
               TooltipDecor.DEFAULT_BORDERS,
               i,
               LegendaryTooltipsConfig.ColorType.BG_END
            );
         } else {
            getInstance().startColors[i] = defaultColors.get(LegendaryTooltipsConfig.ColorType.BORDER_START);
            getInstance().endColors[i] = defaultColors.get(LegendaryTooltipsConfig.ColorType.BORDER_END);
            getInstance().startBGColors[i] = defaultColors.get(LegendaryTooltipsConfig.ColorType.BG_START);
            getInstance().endBGColors[i] = defaultColors.get(LegendaryTooltipsConfig.ColorType.BG_END);
         }
      }
   }

   public static LegendaryTooltipsConfig.FrameDefinition getDefinitionColors(
      ItemStack item, int defaultStartBorder, int defaultEndBorder, int defaultStartBackground, int defaultEndBackground, Provider provider
   ) {
      LegendaryTooltipsConfig.FrameDefinition result = getInstance().getFrameDefinition(item, provider);
      if (result == NO_BORDER) {
         result = new LegendaryTooltipsConfig.FrameDefinition(
            result.resource(),
            result.index(),
            () -> defaultStartBorder,
            () -> defaultEndBorder,
            () -> defaultStartBackground,
            () -> defaultEndBackground,
            LegendaryTooltipsConfig.FrameSource.NONE,
            0,
            result.frameWidth(),
            result.partSize(),
            result.partOffset(),
            result.cornerOffset()
         );
      } else if (result == STANDARD_BORDER && getInstance().bordersMatchRarity.get()) {
         TextColor color = ItemColors.getColorForItem(item, TextColor.fromLegacyFormat(ChatFormatting.WHITE));
         DynamicColor rarityColor = DynamicColor.fromRgb(color.getValue());
         int hue = rarityColor.hue();
         boolean addHue = false;
         if (hue >= 62 && hue <= 240) {
            addHue = true;
         }

         int startHue = addHue ? hue - 4 : hue + 4;
         int endHue = addHue ? hue + 18 : hue - 18;
         int startBGHue = addHue ? hue - 3 : hue + 3;
         int endBGHue = addHue ? hue + 13 : hue - 13;
         startHue = (startHue + 360) % 360;
         endHue = (endHue + 360) % 360;
         startBGHue = (startBGHue + 360) % 360;
         endBGHue = (endBGHue + 360) % 360;
         DynamicColor startColor = DynamicColor.fromAHSV(255, startHue, rarityColor.saturation(), rarityColor.value());
         DynamicColor endColor = DynamicColor.fromAHSV(255, endHue, rarityColor.saturation(), (int)(rarityColor.value() * 0.95F));
         DynamicColor startBGColor = DynamicColor.fromAHSV(228, startBGHue, (int)(rarityColor.saturation() * 0.9F), 14);
         DynamicColor endBGColor = DynamicColor.fromAHSV(253, endBGHue, (int)(rarityColor.saturation() * 0.8F), 18);
         result = new LegendaryTooltipsConfig.FrameDefinition(
            result.resource(),
            result.index(),
            () -> startColor.getIntValue(),
            () -> endColor.getIntValue(),
            () -> startBGColor.getIntValue(),
            () -> endBGColor.getIntValue(),
            LegendaryTooltipsConfig.FrameSource.NONE,
            0,
            result.frameWidth(),
            result.partSize(),
            result.partOffset(),
            result.cornerOffset()
         );
      }

      if (result.startBorder() == null) {
         result = new LegendaryTooltipsConfig.FrameDefinition(
            result.resource(),
            result.index(),
            () -> defaultStartBorder,
            result.endBorder(),
            result.startBackground(),
            result.endBackground(),
            LegendaryTooltipsConfig.FrameSource.NONE,
            0,
            result.frameWidth(),
            result.partSize(),
            result.partOffset(),
            result.cornerOffset()
         );
      }

      if (result.endBorder() == null) {
         result = new LegendaryTooltipsConfig.FrameDefinition(
            result.resource(),
            result.index(),
            result.startBorder(),
            () -> defaultEndBorder,
            result.startBackground(),
            result.endBackground(),
            LegendaryTooltipsConfig.FrameSource.NONE,
            0,
            result.frameWidth(),
            result.partSize(),
            result.partOffset(),
            result.cornerOffset()
         );
      }

      if (result.startBackground() == null) {
         result = new LegendaryTooltipsConfig.FrameDefinition(
            result.resource(),
            result.index(),
            result.startBorder(),
            result.endBorder(),
            () -> defaultStartBackground,
            result.endBackground(),
            LegendaryTooltipsConfig.FrameSource.NONE,
            0,
            result.frameWidth(),
            result.partSize(),
            result.partOffset(),
            result.cornerOffset()
         );
      }

      if (result.endBackground() == null) {
         result = new LegendaryTooltipsConfig.FrameDefinition(
            result.resource(),
            result.index(),
            result.startBorder(),
            result.endBorder(),
            result.startBackground(),
            () -> defaultEndBackground,
            LegendaryTooltipsConfig.FrameSource.NONE,
            0,
            result.frameWidth(),
            result.partSize(),
            result.partOffset(),
            result.cornerOffset()
         );
      }

      return result;
   }

   public void addFrameDefinition(
      ResourceLocation resource,
      int index,
      Supplier<Integer> startBorder,
      Supplier<Integer> endBorder,
      Supplier<Integer> background,
      int priority,
      List<String> selectors
   ) {
      this.addFrameDefinition(resource, index, startBorder, endBorder, background, background, priority, selectors, 64, 8, -1, 2);
   }

   public void addFrameDefinition(
      ResourceLocation resource,
      int index,
      Supplier<Integer> startBorder,
      Supplier<Integer> endBorder,
      Supplier<Integer> startBackground,
      Supplier<Integer> endBackground,
      int priority,
      List<String> selectors
   ) {
      this.addFrameDefinition(resource, index, startBorder, endBorder, startBackground, endBackground, priority, selectors, 64, 8, -1, 2);
   }

   public void addFrameDefinition(
      ResourceLocation resource,
      int index,
      Supplier<Integer> startBorder,
      Supplier<Integer> endBorder,
      Supplier<Integer> background,
      int priority,
      List<String> selectors,
      int frameWidth,
      int partSize,
      int partOffset,
      int cornerOffset
   ) {
      this.addFrameDefinition(
         resource, index, startBorder, endBorder, background, background, priority, selectors, frameWidth, partSize, partOffset, cornerOffset
      );
   }

   public void addFrameDefinition(
      ResourceLocation resource,
      int index,
      Supplier<Integer> startBorder,
      Supplier<Integer> endBorder,
      Supplier<Integer> startBackground,
      Supplier<Integer> endBackground,
      int priority,
      List<String> selectors,
      int frameWidth,
      int partSize,
      int partOffset,
      int cornerOffset
   ) {
      LegendaryTooltipsConfig.FrameDefinition definition = new LegendaryTooltipsConfig.FrameDefinition(
         resource,
         index,
         startBorder,
         endBorder,
         startBackground,
         endBackground,
         LegendaryTooltipsConfig.FrameSource.API,
         priority,
         frameWidth,
         partSize,
         partOffset,
         cornerOffset
      );
      this.addFrameDefinition(definition, selectors);
   }

   void addFrameDefinition(LegendaryTooltipsConfig.FrameDefinition definition, List<String> selectors) {
      if (definition.source == LegendaryTooltipsConfig.FrameSource.API || definition.source == LegendaryTooltipsConfig.FrameSource.DATA) {
         Set<String> selectorSet = new LinkedHashSet<>();
         if (customFrameDefinitions.containsKey(definition)) {
            selectorSet.addAll(customFrameDefinitions.get(definition));
         }

         selectorSet.addAll(selectors);
         customFrameDefinitions.put(definition, selectorSet);
      }
   }

   void clearDataFrames() {
      customFrameDefinitions.entrySet().removeIf(entry -> entry.getKey().source == LegendaryTooltipsConfig.FrameSource.DATA);
   }

   public LegendaryTooltipsConfig.FrameDefinition getFrameDefinition(ItemStack item, Provider provider) {
      if (frameDefinitionCache.containsKey(item)) {
         return frameDefinitionCache.get(item);
      } else if (item == null) {
         frameDefinitionCache.put(item, STANDARD_BORDER);
         return STANDARD_BORDER;
      } else {
         if (this.startColors[0] == null) {
            resolveColors();
         }

         for (String entry : this.blacklist.get()) {
            if (Selectors.itemMatches(item, entry, provider)) {
               frameDefinitionCache.put(item, NO_BORDER);
               return NO_BORDER;
            }
         }

         if (Services.getPlatformHelper().isModLoaded("relics")) {
            try {
               Minecraft minecraft = Minecraft.getInstance();
               Method hasTooltipDecorMethod = Class.forName("com.anthonyhilyard.legendarytooltips.compat.RelicsHandler")
                  .getDeclaredMethod("hasTooltipDecor", ItemStack.class, LocalPlayer.class);
               if ((Boolean)hasTooltipDecorMethod.invoke(null, item, minecraft.player)) {
                  frameDefinitionCache.put(item, NO_BORDER);
                  return NO_BORDER;
               }
            } catch (Exception var13) {
               LegendaryTooltips.LOGGER.debug(ExceptionUtils.getStackTrace(var13));
            }
         }

         List<Integer> priorities = this.framePriorities.get().stream().map(ix -> ix).collect(Collectors.toCollection(ArrayList::new));
         if (!priorities.contains(-1)) {
            priorities.add(0, -1);
         }

         for (int i = 0; i < priorities.size(); i++) {
            int frameIndex = priorities.get(i);
            if (frameIndex != -1 && frameIndex < 16) {
               TextColor startColor = this.startColors[frameIndex] == null
                  ? defaultColors.get(LegendaryTooltipsConfig.ColorType.BORDER_START)
                  : this.startColors[frameIndex];
               TextColor endColor = this.endColors[frameIndex] == null
                  ? defaultColors.get(LegendaryTooltipsConfig.ColorType.BORDER_END)
                  : this.endColors[frameIndex];
               TextColor startBGColor = this.startBGColors[frameIndex] == null
                  ? defaultColors.get(LegendaryTooltipsConfig.ColorType.BG_START)
                  : this.startBGColors[frameIndex];
               TextColor endBGColor = this.endBGColors[frameIndex] == null
                  ? defaultColors.get(LegendaryTooltipsConfig.ColorType.BG_END)
                  : this.endBGColors[frameIndex];

               for (String entryx : itemSelectors.get(frameIndex).get()) {
                  if (Selectors.itemMatches(item, entryx, provider)) {
                     LegendaryTooltipsConfig.FrameDefinition frameDefinition = new LegendaryTooltipsConfig.FrameDefinition(
                        TooltipDecor.DEFAULT_BORDERS,
                        frameIndex,
                        () -> startColor.getValue(),
                        () -> endColor.getValue(),
                        () -> startBGColor.getValue(),
                        () -> endBGColor.getValue(),
                        LegendaryTooltipsConfig.FrameSource.CONFIG,
                        i,
                        64,
                        8,
                        -1,
                        2
                     );
                     frameDefinitionCache.put(item, frameDefinition);
                     return frameDefinition;
                  }
               }
            } else {
               for (LegendaryTooltipsConfig.FrameDefinition frameDefinition : customFrameDefinitions.keySet()
                  .stream()
                  .sorted((a, b) -> Integer.compare(a.priority, b.priority))
                  .toList()) {
                  for (String entryxx : customFrameDefinitions.get(frameDefinition)) {
                     if (Selectors.itemMatches(item, entryxx, provider)) {
                        frameDefinitionCache.put(item, frameDefinition);
                        return frameDefinition;
                     }
                  }
               }
            }
         }

         frameDefinitionCache.put(item, STANDARD_BORDER);
         return STANDARD_BORDER;
      }
   }

   public static void reset() {
      frameDefinitionCache.clear();
      formattedTitleCache.clear();
      resolveColors();
   }

   protected void onReload() {
      reset();
   }

   public static enum ColorType {
      BORDER_START,
      BORDER_END,
      BG_START,
      BG_END;
   }

   public record FrameDefinition(
      ResourceLocation resource,
      int index,
      Supplier<Integer> startBorder,
      Supplier<Integer> endBorder,
      Supplier<Integer> startBackground,
      Supplier<Integer> endBackground,
      LegendaryTooltipsConfig.FrameSource source,
      int priority,
      int frameWidth,
      int partSize,
      int partOffset,
      int cornerOffset
   ) {
   }

   public static enum FrameSource {
      NONE,
      CONFIG,
      API,
      DATA;
   }

   private static enum ModelRenderType {
      NONE,
      EQUIPMENT,
      ALL;
   }

   private static enum TooltipsScrollActive {
      ALWAYS,
      WITH_KEYBIND,
      NEVER;
   }
}
