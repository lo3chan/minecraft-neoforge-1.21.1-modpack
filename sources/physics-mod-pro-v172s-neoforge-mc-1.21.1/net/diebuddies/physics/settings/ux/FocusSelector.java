package net.diebuddies.physics.settings.ux;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Set;
import net.diebuddies.mixins.guiphysics.MixinAbstractWidgetAccessor;
import net.minecraft.client.gui.GuiGraphics;

public class FocusSelector extends Animator {
   private Set<Animatable> list = new ObjectOpenHashSet();
   private Animatable lastFocus = null;

   @Override
   public boolean render(Animatable animatable, GuiGraphics guiGraphics, int mouseX, int mouseY, float renderPercent, float delta) {
      boolean hovered = false;
      if (animatable instanceof MixinAbstractWidgetAccessor accessor) {
         hovered = accessor.getIsHovered();
      }

      if (hovered) {
         if (this.lastFocus != null && this.lastFocus != animatable && this.lastFocus instanceof MixinAbstractWidgetAccessor accessor) {
            accessor.setFocused(false);
         }

         if (animatable instanceof MixinAbstractWidgetAccessor accessor) {
            accessor.setFocused(true);
            this.lastFocus = animatable;
         }
      }

      return super.render(animatable, guiGraphics, mouseX, mouseY, renderPercent, delta);
   }

   @Override
   public void init(Animatable animatable) {
      super.init(animatable);
      this.list.add(animatable);
   }

   public void deselectAll() {
      for (Animatable animatable : this.list) {
         if (animatable instanceof MixinAbstractWidgetAccessor accessor) {
            accessor.setFocused(false);
         }
      }
   }

   public Animatable getFocusedElement() {
      return this.lastFocus;
   }
}
