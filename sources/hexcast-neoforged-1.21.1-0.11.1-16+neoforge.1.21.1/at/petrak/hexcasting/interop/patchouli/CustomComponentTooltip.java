package at.petrak.hexcasting.interop.patchouli;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.network.chat.Component;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.ICustomComponent;
import vazkii.patchouli.api.IVariable;

public class CustomComponentTooltip implements ICustomComponent {
   int width;
   int height;
   @SerializedName("tooltip")
   IVariable tooltipReference;
   transient IVariable tooltipVar;
   transient List<Component> tooltip;
   transient Provider registries;
   transient int x;
   transient int y;

   public void build(int componentX, int componentY, int pageNum) {
      this.x = componentX;
      this.y = componentY;
      this.tooltip = new ArrayList<>();

      for (IVariable s : this.tooltipVar.asListOrSingleton(this.registries)) {
         this.tooltip.add((Component)s.as(Component.class));
      }
   }

   public void render(GuiGraphics graphics, IComponentRenderContext context, float pticks, int mouseX, int mouseY) {
      if (context.isAreaHovered(mouseX, mouseY, this.x, this.y, this.width, this.height)) {
         context.setHoverTooltipComponents(this.tooltip);
      }
   }

   public void onVariablesAvailable(UnaryOperator<IVariable> lookup, Provider registries) {
      this.registries = registries;
      this.tooltipVar = lookup.apply(this.tooltipReference);
   }
}
