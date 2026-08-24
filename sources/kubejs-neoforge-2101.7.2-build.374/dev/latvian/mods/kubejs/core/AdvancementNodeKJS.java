package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import java.util.Set;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

@RemapPrefixForJS("kjs$")
public interface AdvancementNodeKJS {
   default AdvancementNode kjs$self() {
      return (AdvancementNode)this;
   }

   default ResourceLocation kjs$getId() {
      return this.kjs$self().holder().id();
   }

   @Nullable
   default AdvancementNode kjs$getParent() {
      return this.kjs$self().parent();
   }

   default Set<AdvancementNode> kjs$getChildren() {
      return (Set<AdvancementNode>)this.kjs$self().children();
   }

   default void kjs$addChild(AdvancementNode a) {
      this.kjs$self().addChild(a);
   }

   default Component kjs$getDisplayText() {
      return (Component)this.kjs$self().advancement().name().orElse(Component.empty());
   }

   default boolean kjs$hasDisplay() {
      return this.kjs$self().advancement().display().isPresent();
   }

   default Component kjs$getTitle() {
      return this.kjs$self().advancement().display().<Component>map(DisplayInfo::getTitle).orElse(Component.empty());
   }

   default Component kjs$getDescription() {
      return this.kjs$self().advancement().display().<Component>map(DisplayInfo::getDescription).orElse(Component.empty());
   }

   @Nullable
   default DisplayInfo kjs$getDisplay() {
      return (DisplayInfo)this.kjs$self().advancement().display().orElse(null);
   }
}
