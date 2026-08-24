package vazkii.psi.common.item.component;

import java.util.HashMap;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vazkii.psi.api.cad.EnumCADComponent;
import vazkii.psi.api.cad.EnumCADStat;
import vazkii.psi.api.cad.ICADComponent;
import vazkii.psi.api.internal.TooltipHelper;
import vazkii.psi.common.Psi;

public abstract class ItemCADComponent extends Item implements ICADComponent {
   private final HashMap<EnumCADStat, Integer> stats = new HashMap<>();

   public ItemCADComponent(Properties properties) {
      super(properties.stacksTo(1));
   }

   public static void addStatToStack(Item item, EnumCADStat stat, int value) {
      if (item instanceof ItemCADComponent) {
         ((ItemCADComponent)item).addStat(stat, value);
      } else {
         Psi.logger.error("Tried to add stats to non-component Item: {}", item.getDescription());
      }
   }

   public void appendHoverText(@NotNull ItemStack stack, @Nullable TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag advanced) {
      TooltipHelper.tooltipIfShift(tooltip, () -> {
         EnumCADComponent componentType = this.getComponentType(stack);
         Component componentName = Component.translatable(componentType.getName());
         tooltip.add(Component.translatable("psimisc.component_type", new Object[]{componentName}));

         for (EnumCADStat stat : EnumCADStat.class.getEnumConstants()) {
            if (stat.getSourceType() == componentType) {
               int statVal = this.getCADStatValue(stack, stat);
               String statValStr = statVal == -1 ? "∞" : statVal + "";
               Component name = Component.translatable(stat.getName()).withStyle(ChatFormatting.AQUA);
               tooltip.add(Component.literal(" ").append(name).append(": " + statValStr));
            }
         }
      });
   }

   public void addStat(EnumCADStat stat, int value) {
      this.stats.put(stat, value);
   }

   @Override
   public int getCADStatValue(ItemStack stack, EnumCADStat stat) {
      return this.stats.containsKey(stat) ? this.stats.get(stat) : 0;
   }
}
