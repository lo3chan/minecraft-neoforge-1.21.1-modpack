package com.anthonyhilyard.legendarytooltips.config;

import com.anthonyhilyard.legendarytooltips.LegendaryTooltips;
import com.anthonyhilyard.legendarytooltips.tooltip.TooltipDecor;
import com.google.common.base.Charsets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.GsonHelper;
import org.apache.commons.lang3.exception.ExceptionUtils;

public final class FrameResourceParser implements ResourceManagerReloadListener {
   public static final FrameResourceParser INSTANCE = new FrameResourceParser();

   private FrameResourceParser() {
   }

   public void onResourceManagerReload(ResourceManager resourceManager) {
      LegendaryTooltipsConfig.reset();
      LegendaryTooltipsConfig.getInstance().clearDataFrames();

      try {
         for (Resource resource : resourceManager.getResourceStack(ResourceLocation.fromNamespaceAndPath("legendarytooltips", "frame_definitions.json"))) {
            try (InputStream inputStream = resource.open()) {
               JsonObject rootObject = GsonHelper.parse(new InputStreamReader(inputStream, Charsets.UTF_8), true);
               if (rootObject.has("definitions")) {
                  JsonArray definitions = GsonHelper.getAsJsonArray(rootObject, "definitions");

                  for (int i = 0; i < definitions.size(); i++) {
                     JsonObject definitionObject = GsonHelper.convertToJsonObject(definitions.get(i), String.format("definitions[%d]", i));
                     ResourceLocation image = TooltipDecor.DEFAULT_BORDERS;
                     int index = 0;
                     int priority = 0;
                     int frameWidth = 64;
                     int partSize = 8;
                     int partOffset = -1;
                     int cornerOffset = 2;
                     Map<String, TextColor> colors = new HashMap<String, TextColor>() {
                        {
                           this.put("startColor", LegendaryTooltipsConfig.defaultColors.get(LegendaryTooltipsConfig.ColorType.BORDER_START));
                           this.put("endColor", LegendaryTooltipsConfig.defaultColors.get(LegendaryTooltipsConfig.ColorType.BORDER_END));
                           this.put("bgColor", LegendaryTooltipsConfig.defaultColors.get(LegendaryTooltipsConfig.ColorType.BG_START));
                           this.put("bgStartColor", null);
                           this.put("bgEndColor", null);
                        }
                     };
                     List<String> selectors = new ArrayList<>();

                     for (JsonElement selectorElement : GsonHelper.getAsJsonArray(definitionObject, "selectors")) {
                        selectors.add(GsonHelper.convertToString(selectorElement, "selector"));
                     }

                     if (definitionObject.has("image")) {
                        String parsedImage = GsonHelper.getAsString(definitionObject, "image");
                        ResourceLocation imageResourceLocation = ResourceLocation.tryParse(parsedImage);
                        if (imageResourceLocation != null) {
                           image = imageResourceLocation;
                        }
                     }

                     if (definitionObject.has("sizes")) {
                        JsonObject sizes = GsonHelper.getAsJsonObject(definitionObject, "sizes");
                        if (sizes.has("frameWidth")) {
                           frameWidth = sizes.get("frameWidth").getAsInt();
                        }

                        if (sizes.has("partSize")) {
                           partSize = sizes.get("partSize").getAsInt();
                        }
                     }

                     if (definitionObject.has("offsets")) {
                        JsonObject offsets = GsonHelper.getAsJsonObject(definitionObject, "offsets");
                        if (offsets.has("partOffset")) {
                           partOffset = offsets.get("partOffset").getAsInt();
                        }

                        if (offsets.has("cornerOffset")) {
                           cornerOffset = offsets.get("cornerOffset").getAsInt();
                        }
                     }

                     if (definitionObject.has("index")) {
                        index = GsonHelper.getAsInt(definitionObject, "index");
                     }

                     if (definitionObject.has("priority")) {
                        priority = GsonHelper.getAsInt(definitionObject, "priority");
                     }

                     for (String colorKey : colors.keySet()) {
                        if (definitionObject.has(colorKey)) {
                           LegendaryTooltipsConfig.ColorType colorType = switch (colorKey) {
                              case "startColor" -> LegendaryTooltipsConfig.ColorType.BORDER_START;
                              case "endColor" -> LegendaryTooltipsConfig.ColorType.BORDER_END;
                              default -> LegendaryTooltipsConfig.ColorType.BG_START;
                              case "bgEndColor" -> LegendaryTooltipsConfig.ColorType.BG_END;
                           };
                           TextColor parsedColor = null;
                           if (GsonHelper.isStringValue(definitionObject, colorKey)) {
                              parsedColor = LegendaryTooltipsConfig.getColor(GsonHelper.getAsString(definitionObject, colorKey), null, image, index, colorType);
                           } else if (GsonHelper.isNumberValue(definitionObject, colorKey)) {
                              parsedColor = LegendaryTooltipsConfig.getColor(GsonHelper.getAsLong(definitionObject, colorKey), null, image, index, colorType);
                           }

                           if (parsedColor != null) {
                              colors.put(colorKey, parsedColor);
                           }
                        }
                     }

                     LegendaryTooltipsConfig.FrameDefinition definition = new LegendaryTooltipsConfig.FrameDefinition(
                        image,
                        index,
                        () -> colors.get("startColor").getValue(),
                        () -> colors.get("endColor").getValue(),
                        () -> colors.get("bgStartColor") != null ? colors.get("bgStartColor").getValue() : colors.get("bgColor").getValue(),
                        () -> colors.get("bgEndColor") != null ? colors.get("bgEndColor").getValue() : colors.get("bgColor").getValue(),
                        LegendaryTooltipsConfig.FrameSource.DATA,
                        priority,
                        frameWidth,
                        partSize,
                        partOffset,
                        cornerOffset
                     );
                     LegendaryTooltipsConfig.getInstance().addFrameDefinition(definition, selectors);
                  }
               }
            } catch (Exception var25) {
               throw var25;
            }
         }
      } catch (Exception var26) {
         LegendaryTooltips.LOGGER.warn("An error occurred while parsing frame definitions data:\n {}", ExceptionUtils.getStackTrace(var26));
      }
   }
}
