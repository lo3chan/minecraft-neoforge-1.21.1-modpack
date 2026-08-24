package net.cibernet.alchemancy.item;

import java.util.ArrayList;
import net.cibernet.alchemancy.crafting.ForgeRecipeGrid;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.Property;
import net.cibernet.alchemancy.properties.data.IDataHolder;
import net.cibernet.alchemancy.registries.AlchemancyTags;
import net.minecraft.core.Holder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;

public class DreamsteelBowItem extends BowItem {
   public DreamsteelBowItem(Properties properties) {
      super(properties);
   }

   protected Projectile createProjectile(Level level, LivingEntity shooter, ItemStack weapon, ItemStack ammo, boolean isCrit) {
      ammo = infuseItem(level, weapon, ammo);
      return super.createProjectile(level, shooter, weapon, ammo, isCrit);
   }

   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
      return super.use(level, player, hand);
   }

   private static ItemStack infuseItem(Level level, ItemStack bow, ItemStack arrow) {
      if (arrow.is(AlchemancyTags.Items.IMMUNE_TO_INFUSIONS)) {
         return arrow;
      } else {
         ArrayList<Holder<Property>> infusions = new ArrayList<>(InfusedPropertiesHelper.getInfusedProperties(bow));
         boolean perform = false;
         ForgeRecipeGrid grid = new ForgeRecipeGrid(arrow);

         for (Holder<Property> property : infusions) {
            if (InfusedPropertiesHelper.canInfuseWithProperty(arrow, property)
               && ((Property)property.value()).onInfusedByDormantProperty(arrow, bow, grid, infusions, false)) {
               perform = true;
            }
         }

         if (perform) {
            infusions.removeIf(propertyHolder -> !InfusedPropertiesHelper.canInfuseWithProperty(arrow, (Holder<Property>)propertyHolder));
            InfusedPropertiesHelper.addProperties(arrow, infusions);
            InfusedPropertiesHelper.forEachProperty(arrow, propertyHolder -> {
               if (!propertyHolder.is(AlchemancyTags.Properties.CANNOT_CLONE_DATA) && propertyHolder.value() instanceof IDataHolder<?> dataHolder) {
                  dataHolder.combineDataAndSet(arrow, bow);
               }
            }, false);
            arrow = ForgeRecipeGrid.resolveInteractions(arrow, level);
         }

         return arrow;
      }
   }
}
