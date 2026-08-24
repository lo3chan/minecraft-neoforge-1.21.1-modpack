package vazkii.psi.common.core;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.RegisterEvent;
import vazkii.psi.common.Psi;
import vazkii.psi.common.block.base.ModBlocks;
import vazkii.psi.common.item.ItemCAD;
import vazkii.psi.common.item.base.ModItems;

@EventBusSubscriber(
   modid = "psi"
)
public class PsiCreativeTab {
   public static final ResourceKey<CreativeModeTab> PSI_CREATIVE_TAB = ResourceKey.create(Registries.CREATIVE_MODE_TAB, Psi.location("creative_tab"));

   @SubscribeEvent
   public static void register(RegisterEvent evt) {
      evt.register(
         Registries.CREATIVE_MODE_TAB,
         creativeModeTabRegisterHelper -> {
            CreativeModeTab psiCreativeTab = CreativeModeTab.builder()
               .title(Component.translatable("itemGroup.psi"))
               .icon(() -> new ItemStack((ItemLike)ModItems.cadAssemblyIron.get()))
               .displayItems((parameters, output) -> {
                  output.accept((ItemLike)ModItems.psidust.get());
                  output.accept((ItemLike)ModItems.psimetal.get());
                  output.accept((ItemLike)ModItems.psigem.get());
                  output.accept((ItemLike)ModItems.ebonyPsimetal.get());
                  output.accept((ItemLike)ModItems.ivoryPsimetal.get());
                  output.accept((ItemLike)ModItems.ebonySubstance.get());
                  output.accept((ItemLike)ModItems.ivorySubstance.get());
                  output.accept((ItemLike)ModItems.cadAssemblyIron.get());
                  output.accept((ItemLike)ModItems.cadAssemblyGold.get());
                  output.accept((ItemLike)ModItems.cadAssemblyPsimetal.get());
                  output.accept((ItemLike)ModItems.cadAssemblyIvory.get());
                  output.accept((ItemLike)ModItems.cadAssemblyEbony.get());
                  output.accept((ItemLike)ModItems.cadAssemblyCreative.get());
                  output.accept((ItemLike)ModItems.cadCoreBasic.get());
                  output.accept((ItemLike)ModItems.cadCoreOverclocked.get());
                  output.accept((ItemLike)ModItems.cadCoreConductive.get());
                  output.accept((ItemLike)ModItems.cadCoreHyperClocked.get());
                  output.accept((ItemLike)ModItems.cadCoreRadiative.get());
                  output.accept((ItemLike)ModItems.cadSocketBasic.get());
                  output.accept((ItemLike)ModItems.cadSocketSignaling.get());
                  output.accept((ItemLike)ModItems.cadSocketLarge.get());
                  output.accept((ItemLike)ModItems.cadSocketTransmissive.get());
                  output.accept((ItemLike)ModItems.cadSocketHuge.get());
                  output.accept((ItemLike)ModItems.cadBatteryBasic.get());
                  output.accept((ItemLike)ModItems.cadBatteryExtended.get());
                  output.accept((ItemLike)ModItems.cadBatteryUltradense.get());
                  output.accept((ItemLike)ModItems.cadColorizerWhite.get());
                  output.accept((ItemLike)ModItems.cadColorizerOrange.get());
                  output.accept((ItemLike)ModItems.cadColorizerMagenta.get());
                  output.accept((ItemLike)ModItems.cadColorizerLightBlue.get());
                  output.accept((ItemLike)ModItems.cadColorizerYellow.get());
                  output.accept((ItemLike)ModItems.cadColorizerLime.get());
                  output.accept((ItemLike)ModItems.cadColorizerPink.get());
                  output.accept((ItemLike)ModItems.cadColorizerGray.get());
                  output.accept((ItemLike)ModItems.cadColorizerLightGray.get());
                  output.accept((ItemLike)ModItems.cadColorizerCyan.get());
                  output.accept((ItemLike)ModItems.cadColorizerPurple.get());
                  output.accept((ItemLike)ModItems.cadColorizerBlue.get());
                  output.accept((ItemLike)ModItems.cadColorizerBrown.get());
                  output.accept((ItemLike)ModItems.cadColorizerGreen.get());
                  output.accept((ItemLike)ModItems.cadColorizerRed.get());
                  output.accept((ItemLike)ModItems.cadColorizerBlack.get());
                  output.accept((ItemLike)ModItems.cadColorizerRainbow.get());
                  output.accept((ItemLike)ModItems.cadColorizerPsi.get());
                  output.accept((ItemLike)ModItems.cadColorizerEmpty.get());
                  output.accept((ItemLike)ModItems.flashRing.get());
                  output.accept((ItemLike)ModItems.spellBullet.get());
                  output.accept((ItemLike)ModItems.projectileSpellBullet.get());
                  output.accept((ItemLike)ModItems.loopSpellBullet.get());
                  output.accept((ItemLike)ModItems.circleSpellBullet.get());
                  output.accept((ItemLike)ModItems.grenadeSpellBullet.get());
                  output.accept((ItemLike)ModItems.chargeSpellBullet.get());
                  output.accept((ItemLike)ModItems.mineSpellBullet.get());
                  output.accept((ItemLike)ModItems.spellDrive.get());
                  output.accept((ItemLike)ModItems.detonator.get());
                  output.accept((ItemLike)ModItems.exosuitController.get());
                  output.accept((ItemLike)ModItems.exosuitSensorLight.get());
                  output.accept((ItemLike)ModItems.exosuitSensorHeat.get());
                  output.accept((ItemLike)ModItems.exosuitSensorStress.get());
                  output.accept((ItemLike)ModItems.exosuitSensorWater.get());
                  output.accept((ItemLike)ModItems.exosuitSensorTrigger.get());
                  output.acceptAll(ItemCAD.getCreativeTabItems());
                  output.accept((ItemLike)ModItems.vectorRuler.get());
                  output.accept((ItemLike)ModItems.psimetalShovel.get());
                  output.accept((ItemLike)ModItems.psimetalPickaxe.get());
                  output.accept((ItemLike)ModItems.psimetalAxe.get());
                  output.accept((ItemLike)ModItems.psimetalSword.get());
                  output.accept((ItemLike)ModItems.psimetalExosuitHelmet.get());
                  output.accept((ItemLike)ModItems.psimetalExosuitChestplate.get());
                  output.accept((ItemLike)ModItems.psimetalExosuitLeggings.get());
                  output.accept((ItemLike)ModItems.psimetalExosuitBoots.get());
                  output.accept((ItemLike)ModBlocks.cadAssembler.get());
                  output.accept((ItemLike)ModBlocks.programmer.get());
                  output.accept((ItemLike)ModBlocks.psidustBlock.get());
                  output.accept((ItemLike)ModBlocks.psimetalBlock.get());
                  output.accept((ItemLike)ModBlocks.psigemBlock.get());
                  output.accept((ItemLike)ModBlocks.psimetalPlateBlack.get());
                  output.accept((ItemLike)ModBlocks.psimetalPlateBlackLight.get());
                  output.accept((ItemLike)ModBlocks.psimetalPlateWhite.get());
                  output.accept((ItemLike)ModBlocks.psimetalPlateWhiteLight.get());
                  output.accept((ItemLike)ModBlocks.psimetalEbony.get());
                  output.accept((ItemLike)ModBlocks.psimetalIvory.get());
                  output.accept((ItemLike)ModBlocks.psimetalIvory.get());
               })
               .hideTitle()
               .backgroundTexture(ResourceLocation.withDefaultNamespace("textures/gui/container/creative_inventory/tab_psi.png"))
               .withSearchBar()
               .build();
            creativeModeTabRegisterHelper.register(PSI_CREATIVE_TAB, psiCreativeTab);
         }
      );
   }
}
