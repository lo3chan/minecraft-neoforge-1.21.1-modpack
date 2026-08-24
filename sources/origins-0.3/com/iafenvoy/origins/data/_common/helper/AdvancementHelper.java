package com.iafenvoy.origins.data._common.helper;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.mojang.serialization.Codec;
import java.util.Locale;
import java.util.Set;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public interface AdvancementHelper {
   ResourceLocation advancement();

   AdvancementHelper.Mode selection();

   default Set<AdvancementHolder> getAdvancements(ServerAdvancementManager manager) {
      AdvancementHolder advancement = manager.get(this.advancement());
      if (advancement == null) {
         return Set.of();
      } else {
         AdvancementNode node = manager.tree().get(advancement);
         if (node == null) {
            return Set.of(advancement);
         } else {
            Builder<AdvancementHolder> builder = ImmutableSet.builder();
            if (this.selection().parents) {
               for (AdvancementNode n = node.parent(); n != null; n = n.parent()) {
                  builder.add(n.holder());
               }
            }

            builder.add(advancement);
            if (this.selection().children) {
               addChildren(node, builder);
            }

            return builder.build();
         }
      }
   }

   static void addChildren(AdvancementNode node, Builder<AdvancementHolder> builder) {
      for (AdvancementNode advancementnode : node.children()) {
         builder.add(advancementnode.holder());
         addChildren(advancementnode, builder);
      }
   }

   public static enum Mode implements StringRepresentable {
      ONLY(false, false),
      THROUGH(true, true),
      FROM(false, true),
      UNTIL(true, false),
      EVERYTHING(true, true);

      public static final Codec<AdvancementHelper.Mode> CODEC = StringRepresentable.fromValues(AdvancementHelper.Mode::values);
      private final boolean parents;
      private final boolean children;

      private Mode(boolean parents, boolean children) {
         this.parents = parents;
         this.children = children;
      }

      @NotNull
      public String getSerializedName() {
         return this.name().toLowerCase(Locale.ROOT);
      }
   }
}
