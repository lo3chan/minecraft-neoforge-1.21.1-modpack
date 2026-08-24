package com.iafenvoy.jupiter.render;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.iafenvoy.jupiter.util.TextUtil;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public final class TitleStack {
   private static final Component OMIT = TextUtil.literal("...");
   private static final Component SEPARATOR = TextUtil.literal(" -> ").withStyle(ChatFormatting.GRAY);
   private final Font font;
   private final List<Component> titles;
   private MutableComponent cachedTitle;

   private TitleStack(List<Component> titles) {
      this.font = Minecraft.getInstance().font;
      this.cachedTitle = TextUtil.empty();
      this.titles = titles;
   }

   public TitleStack push(Component title) {
      Builder<Component> builder = ImmutableList.builder();
      builder.addAll(this.titles);
      builder.add(title);
      return new TitleStack(builder.build());
   }

   public static TitleStack create(Component title) {
      return new TitleStack(List.of(title));
   }

   public void cacheTitle(int width) {
      if (!this.titles.isEmpty()) {
         MutableComponent part = TextUtil.empty();
         this.cachedTitle = TextUtil.empty();

         for (int i = this.titles.size() - 1; i >= 0; i--) {
            boolean first = i == this.titles.size() - 1;
            if (first) {
               part.append(this.titles.get(i));
               if (i > 0) {
                  this.cachedTitle.append(OMIT).append(SEPARATOR);
               }

               this.cachedTitle.append(part);
            } else {
               part = TextUtil.empty().append(this.titles.get(i)).append(SEPARATOR).append(part);
               MutableComponent component = TextUtil.empty();
               if (i > 0) {
                  component.append(OMIT).append(SEPARATOR);
               }

               component.append(part);
               if (this.font.width(component) > width) {
                  break;
               }

               this.cachedTitle = component;
            }
         }
      }
   }

   public Component getTitle() {
      return this.cachedTitle;
   }
}
