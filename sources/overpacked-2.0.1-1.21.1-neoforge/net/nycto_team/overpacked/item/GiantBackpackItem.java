package net.nycto_team.overpacked.item;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import java.util.ArrayList;
import java.util.Collection;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.nycto_team.overpacked.registry.ModDDItems;
import net.nycto_team.overpacked.registry.ModItems;
import net.nycto_team.overpacked.util.Utils;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class GiantBackpackItem extends Item implements ICurioItem {
   public final int color;

   public GiantBackpackItem(int color) {
      super(new Properties().stacksTo(1));
      this.color = color;
   }

   public boolean canFitInsideContainerItems() {
      return false;
   }

   public boolean canEquip(SlotContext ctx, ItemStack stack) {
      return super.canEquip(ctx, stack) && !ctx.cosmetic();
   }

   public boolean canEquipFromUse(SlotContext ctx, ItemStack stack) {
      return ctx.entity() instanceof Player player && player.isSecondaryUseActive();
   }

   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
      ItemStack stack = player.getItemInHand(hand);
      return Utils.PlaceBackpack(level, stack, player, false);
   }

   public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext, ResourceLocation id, ItemStack stack) {
      Multimap<Holder<Attribute>, AttributeModifier> map = LinkedHashMultimap.create();
      CustomData data = (CustomData)stack.get(DataComponents.CUSTOM_DATA);
      int count = data != null && data.copyTag().contains("Count") ? data.copyTag().getInt("Count") : 0;
      if (count >= 27) {
         map.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(id, -(count < 54 ? 0.1 : (count < 81 ? 0.2 : 0.3)), Operation.ADD_MULTIPLIED_TOTAL));
      }

      return map;
   }

   public void onEquip(SlotContext ctx, ItemStack prev_stack, ItemStack stack) {
      LivingEntity entity = ctx.entity();
      if (entity.getVehicle() instanceof Boat boat && boat.getPassengers().size() > 1) {
         ((Entity)boat.getPassengers().get(1)).stopRiding();
      }
   }

   public static ItemStack get_colored_stack(int color) {
      Collection<DeferredHolder<Item, ? extends Item>> collection = new ArrayList<>(ModItems.reg.getEntries());
      if (Utils.dye_depot) {
         collection.addAll(ModDDItems.reg.getEntries());
      }

      for (DeferredHolder<Item, ? extends Item> item : collection) {
         if (item.get() instanceof GiantBackpackItem giant_backpack && color == giant_backpack.color) {
            return new ItemStack(giant_backpack);
         }
      }

      return new ItemStack((ItemLike)ModItems.giant_backpack.get());
   }
}
