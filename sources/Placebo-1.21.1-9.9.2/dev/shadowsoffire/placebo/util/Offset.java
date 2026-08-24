package dev.shadowsoffire.placebo.util;

import com.google.gson.JsonPrimitive;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.shadowsoffire.placebo.Placebo;
import dev.shadowsoffire.placebo.config.Configuration;
import dev.shadowsoffire.placebo.config.Property;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import java.util.Arrays;
import java.util.function.IntFunction;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.ByIdMap.OutOfBoundsStrategy;

public record Offset(Offset.AnchorPoint anchor, int x, int y) {
   public static final Codec<Offset> CODEC = RecordCodecBuilder.create(
      inst -> inst.group(
            Offset.AnchorPoint.CODEC.fieldOf("anchor").forGetter(Offset::anchor),
            Codec.INT.fieldOf("x").forGetter(Offset::x),
            Codec.INT.fieldOf("y").forGetter(Offset::y)
         )
         .apply(inst, Offset::new)
   );

   public int getX(Offset.Box window, Offset.Box element) {
      return this.anchor.getX(window.width) - this.anchor.getX(element.width) + this.x;
   }

   public int getY(Offset.Box window, Offset.Box element) {
      return this.anchor.getY(window.height) - this.anchor.getY(element.height) + this.y;
   }

   public void apply(PoseStack pose, Offset.Box window, Offset.Box element) {
      pose.translate(this.getX(window, element), this.getY(window, element), 0.0F);
   }

   public static Offset load(String key, String group, Offset def, Configuration cfg) {
      Offset.AnchorPoint anchor = Offset.AnchorPoint.parse(
         cfg.getString(key + " Anchor Point", group, def.anchor.getSerializedName(), "The anchor point for this element.")
      );
      int x = cfg.getInt(key + " X Offset", group, def.x, -1000, 1000, "The X offset for this element.");
      int y = cfg.getInt(key + " Y Offset", group, def.y, -1000, 1000, "The Y Offset for this element.");
      return new Offset(anchor, x, y);
   }

   public static void save(String key, String group, Offset offset, Configuration cfg) {
      Property anchorProp = cfg.get(group, key + " Anchor Point", "");
      anchorProp.setValue(offset.anchor.getSerializedName());
      Property xProp = cfg.get(group, key + " X Offset", 0);
      xProp.setValue(offset.x);
      Property yProp = cfg.get(group, key + " Y Offset", 0);
      yProp.setValue(offset.y);
      cfg.save();
   }

   public static enum AnchorPoint implements StringRepresentable {
      TOP_LEFT("top_left", width -> 0, height -> 0),
      TOP_CENTER("top_center", width -> width / 2, height -> 0),
      TOP_RIGHT("top_right", width -> width, height -> 0),
      MIDDLE_LEFT("middle_left", width -> 0, height -> height / 2),
      MIDDLE_CENTER("middle_center", width -> width / 2, height -> height / 2),
      MIDDLE_RIGHT("middle_right", width -> width, height -> height / 2),
      BOTTOM_LEFT("bottom_left", width -> 0, height -> height),
      BOTTOM_CENTER("bottom_center", width -> width / 2, height -> height),
      BOTTOM_RIGHT("bottom_right", width -> width, height -> height);

      public static final IntFunction<Offset.AnchorPoint> BY_ID = ByIdMap.continuous(Enum::ordinal, values(), OutOfBoundsStrategy.ZERO);
      public static final Codec<Offset.AnchorPoint> CODEC = StringRepresentable.fromValues(Offset.AnchorPoint::values);
      public static final StreamCodec<ByteBuf, Offset.AnchorPoint> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, Enum::ordinal);
      public static final SuggestionProvider<CommandSourceStack> SUGGEST_ANCHOR_POINT = (ctx, builder) -> SharedSuggestionProvider.suggest(
         Arrays.stream(values()).map(StringRepresentable::getSerializedName), builder
      );
      private final String name;
      private final Int2IntFunction xPos;
      private final Int2IntFunction yPos;

      private AnchorPoint(String name, Int2IntFunction xPos, Int2IntFunction yPos) {
         this.name = name;
         this.xPos = xPos;
         this.yPos = yPos;
      }

      public String getSerializedName() {
         return (R)this.name;
      }

      public int getX(int width) {
         return (Integer)this.xPos.apply(width);
      }

      public int getY(int height) {
         return (Integer)this.yPos.apply(height);
      }

      public static Offset.AnchorPoint parse(String s) {
         try {
            return (Offset.AnchorPoint)((Pair)CODEC.decode(JsonOps.INSTANCE, new JsonPrimitive(s)).getOrThrow()).getFirst();
         } catch (Exception var2) {
            Placebo.LOGGER.error("Failed to parse invalid Anchor Point {}", s);
            var2.printStackTrace();
            return TOP_LEFT;
         }
      }
   }

   public record Box(int width, int height) {
   }
}
