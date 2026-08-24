package at.petrak.hexcasting.forge.interop.curios;

import at.petrak.hexcasting.api.misc.DiscoveryHandlers;
import at.petrak.hexcasting.common.items.HexBaubleItem;
import at.petrak.hexcasting.common.items.magic.ItemCreativeUnlocker;
import at.petrak.hexcasting.common.lib.HexItems;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.InterModComms;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.SlotTypePreset;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class CuriosApiInterop {
   public static void init() {
      CuriosApi.registerCurio(HexItems.SCRYING_LENS, new CuriosApiInterop.Wrapper(HexItems.SCRYING_LENS));
      DiscoveryHandlers.addDebugItemDiscoverer(
         (player, type) -> {
            Optional<ItemStack> result = CuriosApi.getCuriosInventory(player)
               .flatMap(handler -> handler.findFirstCurio(stack -> ItemCreativeUnlocker.isDebug(stack, type)))
               .map(slot -> slot.stack());
            return result.orElse(ItemStack.EMPTY);
         }
      );
   }

   public static void onInterModEnqueue(InterModEnqueueEvent event) {
      InterModComms.sendTo("curios", "register_type", () -> SlotTypePreset.HEAD.getMessageBuilder().build());
   }

   static class Wrapper implements ICurioItem {
      private final HexBaubleItem bauble;

      Wrapper(HexBaubleItem bauble) {
         this.bauble = bauble;
      }

      public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext, ResourceLocation id, ItemStack stack) {
         HashMultimap<Holder<Attribute>, AttributeModifier> map = HashMultimap.create();
         this.bauble.getHexBaubleAttrs(stack).forEach((attribute, modifier) -> map.put(BuiltInRegistries.ATTRIBUTE.wrapAsHolder(attribute), modifier));
         return map;
      }
   }
}
