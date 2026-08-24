package vazkii.psi.common.core.handler.capability;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.capabilities.Capabilities.ItemHandler;
import net.neoforged.neoforge.items.ComponentItemHandler;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.common.core.capability.CapabilityTriggerSensor;
import vazkii.psi.common.entity.ModEntities;
import vazkii.psi.common.item.ItemFlashRing;
import vazkii.psi.common.item.ItemSpellBullet;
import vazkii.psi.common.item.armor.ItemPsimetalArmor;
import vazkii.psi.common.item.base.ModDataComponents;
import vazkii.psi.common.item.base.ModItems;
import vazkii.psi.common.item.tool.ToolSocketable;

@EventBusSubscriber(
   modid = "psi"
)
public class CapabilityHandler {
   @SubscribeEvent
   private static void registerCapabilities(RegisterCapabilitiesEvent event) {
      event.registerEntity(PsiAPI.SPELL_IMMUNE_CAPABILITY, ModEntities.spellCircle, (entity, ctx) -> entity);
      event.registerEntity(PsiAPI.DETONATION_HANDLER_CAPABILITY, EntityType.PLAYER, (player, ctx) -> new CapabilityTriggerSensor(player));
      event.registerEntity(PsiAPI.DETONATION_HANDLER_CAPABILITY, ModEntities.spellCharge, (entity, ctx) -> entity);
      event.registerItem(
         ItemHandler.ITEM,
         (itemStack, context) -> new ComponentItemHandler(itemStack, (DataComponentType)ModDataComponents.BULLETS.get(), 12),
         new ItemLike[]{(ItemLike)ModItems.cad.get()}
      );
      event.registerItem(
         ItemHandler.ITEM,
         (itemStack, context) -> new ComponentItemHandler(itemStack, (DataComponentType)ModDataComponents.BULLETS.get(), 3),
         new ItemLike[]{
            (ItemLike)ModItems.psimetalShovel.get(),
            (ItemLike)ModItems.psimetalPickaxe.get(),
            (ItemLike)ModItems.psimetalAxe.get(),
            (ItemLike)ModItems.psimetalSword.get()
         }
      );
      event.registerItem(
         ItemHandler.ITEM,
         (itemStack, context) -> new ComponentItemHandler(itemStack, (DataComponentType)ModDataComponents.BULLETS.get(), 3),
         new ItemLike[]{
            (ItemLike)ModItems.psimetalExosuitHelmet.get(),
            (ItemLike)ModItems.psimetalExosuitChestplate.get(),
            (ItemLike)ModItems.psimetalExosuitLeggings.get(),
            (ItemLike)ModItems.psimetalExosuitBoots.get()
         }
      );
      event.registerItem(PsiAPI.PSI_BAR_DISPLAY_CAPABILITY, (cad, ctx) -> new CADData(cad), new ItemLike[]{(ItemLike)ModItems.cad.get()});
      event.registerItem(
         PsiAPI.PSI_BAR_DISPLAY_CAPABILITY,
         (tool, ctx) -> new ToolSocketable(tool, 3),
         new ItemLike[]{
            (ItemLike)ModItems.psimetalShovel.get(),
            (ItemLike)ModItems.psimetalPickaxe.get(),
            (ItemLike)ModItems.psimetalAxe.get(),
            (ItemLike)ModItems.psimetalSword.get()
         }
      );
      event.registerItem(
         PsiAPI.PSI_BAR_DISPLAY_CAPABILITY,
         (tool, ctx) -> new ItemPsimetalArmor.ArmorSocketable(tool, 3),
         new ItemLike[]{
            (ItemLike)ModItems.psimetalExosuitHelmet.get(),
            (ItemLike)ModItems.psimetalExosuitChestplate.get(),
            (ItemLike)ModItems.psimetalExosuitLeggings.get(),
            (ItemLike)ModItems.psimetalExosuitBoots.get()
         }
      );
      event.registerItem(PsiAPI.SPELL_ACCEPTOR_CAPABILITY, (cad, ctx) -> new CADData(cad), new ItemLike[]{(ItemLike)ModItems.cad.get()});
      event.registerItem(
         PsiAPI.SPELL_ACCEPTOR_CAPABILITY,
         (tool, ctx) -> new ToolSocketable(tool, 3),
         new ItemLike[]{
            (ItemLike)ModItems.psimetalShovel.get(),
            (ItemLike)ModItems.psimetalPickaxe.get(),
            (ItemLike)ModItems.psimetalAxe.get(),
            (ItemLike)ModItems.psimetalSword.get()
         }
      );
      event.registerItem(
         PsiAPI.SPELL_ACCEPTOR_CAPABILITY,
         (tool, ctx) -> new ItemPsimetalArmor.ArmorSocketable(tool, 3),
         new ItemLike[]{
            (ItemLike)ModItems.psimetalExosuitHelmet.get(),
            (ItemLike)ModItems.psimetalExosuitChestplate.get(),
            (ItemLike)ModItems.psimetalExosuitLeggings.get(),
            (ItemLike)ModItems.psimetalExosuitBoots.get()
         }
      );
      event.registerItem(
         PsiAPI.SPELL_ACCEPTOR_CAPABILITY,
         (stack, ctx) -> new ItemSpellBullet.SpellAcceptor(stack),
         new ItemLike[]{
            (ItemLike)ModItems.spellBullet.get(),
            (ItemLike)ModItems.projectileSpellBullet.get(),
            (ItemLike)ModItems.loopSpellBullet.get(),
            (ItemLike)ModItems.circleSpellBullet.get(),
            (ItemLike)ModItems.grenadeSpellBullet.get(),
            (ItemLike)ModItems.chargeSpellBullet.get(),
            (ItemLike)ModItems.mineSpellBullet.get()
         }
      );
      event.registerItem(
         PsiAPI.SPELL_ACCEPTOR_CAPABILITY, (stack, ctx) -> new ItemFlashRing.SpellAcceptor(stack), new ItemLike[]{(ItemLike)ModItems.flashRing.get()}
      );
      event.registerItem(PsiAPI.CAD_DATA_CAPABILITY, (cad, ctx) -> new CADData(cad), new ItemLike[]{(ItemLike)ModItems.cad.get()});
      event.registerItem(PsiAPI.SOCKETABLE_CAPABILITY, (cad, ctx) -> new CADData(cad), new ItemLike[]{(ItemLike)ModItems.cad.get()});
      event.registerItem(
         PsiAPI.SOCKETABLE_CAPABILITY,
         (tool, ctx) -> new ToolSocketable(tool, 3),
         new ItemLike[]{
            (ItemLike)ModItems.psimetalShovel.get(),
            (ItemLike)ModItems.psimetalPickaxe.get(),
            (ItemLike)ModItems.psimetalAxe.get(),
            (ItemLike)ModItems.psimetalSword.get()
         }
      );
      event.registerItem(
         PsiAPI.SOCKETABLE_CAPABILITY,
         (tool, ctx) -> new ItemPsimetalArmor.ArmorSocketable(tool, 3),
         new ItemLike[]{
            (ItemLike)ModItems.psimetalExosuitHelmet.get(),
            (ItemLike)ModItems.psimetalExosuitChestplate.get(),
            (ItemLike)ModItems.psimetalExosuitLeggings.get(),
            (ItemLike)ModItems.psimetalExosuitBoots.get()
         }
      );
   }
}
