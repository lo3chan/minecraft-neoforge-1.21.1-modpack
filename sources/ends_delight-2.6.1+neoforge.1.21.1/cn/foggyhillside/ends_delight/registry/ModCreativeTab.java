package cn.foggyhillside.ends_delight.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCreativeTab {
   public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, "ends_delight");
   public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ENDS_DELIGHT_TAB = CREATIVE_MODE_TABS.register(
      "ends_delight_tab",
      () -> CreativeModeTab.builder()
         .title(Component.translatable("itemGroup.ends_delight"))
         .icon(() -> ((Item)ModItems.BUBBLE_TEA.get()).getDefaultInstance())
         .displayItems((parameters, output) -> {
            output.accept((ItemLike)ModItems.END_STOVE.get());
            output.accept((ItemLike)ModItems.CHORUS_FRUIT_CRATE.get());
            output.accept((ItemLike)ModItems.END_STONE_KNIFE.get());
            output.accept((ItemLike)ModItems.PURPUR_KNIFE.get());
            output.accept((ItemLike)ModItems.DRAGON_EGG_SHELL_KNIFE.get());
            output.accept((ItemLike)ModItems.DRAGON_TOOTH_KNIFE.get());
            output.accept((ItemLike)ModItems.ENDER_PEARL_GRAIN.get());
            output.accept((ItemLike)ModItems.CHORUS_FRUIT_GRAIN.get());
            output.accept((ItemLike)ModItems.CHORUS_SUCCULENT.get());
            output.accept((ItemLike)ModItems.DRIED_CHORUS_FLOWER.get());
            output.accept((ItemLike)ModItems.DRAGON_TOOTH.get());
            output.accept((ItemLike)ModItems.NON_HATCHABLE_DRAGON_EGG.get());
            output.accept((ItemLike)ModItems.HALF_DRAGON_EGG_SHELL.get());
            output.accept((ItemLike)ModItems.LIQUID_DRAGON_EGG.get());
            output.accept((ItemLike)ModItems.FRIED_DRAGON_EGG.get());
            output.accept((ItemLike)ModItems.SHULKER_MEAT.get());
            output.accept((ItemLike)ModItems.SHULKER_MEAT_SLICE.get());
            output.accept((ItemLike)ModItems.ROASTED_SHULKER_MEAT.get());
            output.accept((ItemLike)ModItems.ROASTED_SHULKER_MEAT_SLICE.get());
            output.accept((ItemLike)ModItems.DRAGON_LEG.get());
            output.accept((ItemLike)ModItems.SMOKED_DRAGON_LEG.get());
            output.accept((ItemLike)ModItems.RAW_DRAGON_MEAT.get());
            output.accept((ItemLike)ModItems.ROASTED_DRAGON_MEAT.get());
            output.accept((ItemLike)ModItems.RAW_DRAGON_MEAT_CUTS.get());
            output.accept((ItemLike)ModItems.ROASTED_DRAGON_MEAT_CUTS.get());
            output.accept((ItemLike)ModItems.RAW_ENDERMITE_MEAT.get());
            output.accept((ItemLike)ModItems.DRIED_ENDERMITE_MEAT.get());
            output.accept((ItemLike)ModItems.ENDERMAN_GRISTLE.get());
            output.accept((ItemLike)ModItems.CHORUS_SAUCE.get());
            output.accept((ItemLike)ModItems.SHULKER_OMELETTE_MIXTURE.get());
            output.accept((ItemLike)ModItems.SHULKER_OMELETTE.get());
            output.accept((ItemLike)ModItems.RAW_ENDER_SAUSAGE.get());
            output.accept((ItemLike)ModItems.ENDER_SAUSAGE.get());
            output.accept((ItemLike)ModItems.ENDER_BAMBOO_RICE.get());
            output.accept((ItemLike)ModItems.STUFFED_RICE_CAKE.get());
            output.accept((ItemLike)ModItems.CHORUS_FLOWER_PIE.get());
            output.accept((ItemLike)ModItems.CHORUS_COOKIE.get());
            output.accept((ItemLike)ModItems.CHORUS_FRUIT_POPSICLE.get());
            output.accept((ItemLike)ModItems.CHORUS_FRUIT_MILK_TEA.get());
            output.accept((ItemLike)ModItems.BUBBLE_TEA.get());
            output.accept((ItemLike)ModItems.CHORUS_FRUIT_WINE.get());
            output.accept((ItemLike)ModItems.DRAGON_BREATH_SODA.get());
            output.accept((ItemLike)ModItems.CHORUS_FLOWER_TEA.get());
            output.accept((ItemLike)ModItems.CHORUS_FRUIT_PIE.get());
            output.accept((ItemLike)ModItems.CHORUS_FRUIT_PIE_SLICE.get());
            output.accept((ItemLike)ModItems.ENDER_CONGEE.get());
            output.accept((ItemLike)ModItems.DRAGON_BREATH_AND_CHORUS_SOUP.get());
            output.accept((ItemLike)ModItems.SHULKER_SOUP.get());
            output.accept((ItemLike)ModItems.ENDER_NOODLE.get());
            output.accept((ItemLike)ModItems.ENDERMAN_GRISTLE_STEW.get());
            output.accept((ItemLike)ModItems.STIR_FRIED_SHULKER_MEAT.get());
            output.accept((ItemLike)ModItems.ROASTED_DRAGON_STEAK.get());
            output.accept((ItemLike)ModItems.END_MIXED_SALAD.get());
            output.accept((ItemLike)ModItems.ASSORTED_SALAD.get());
            output.accept((ItemLike)ModItems.END_BARBECUE_STICK.get());
            output.accept((ItemLike)ModItems.DRAGON_LEG_BLOCK.get());
            output.accept((ItemLike)ModItems.DRAGON_LEG_WITH_SAUCE.get());
            output.accept((ItemLike)ModItems.STEAMED_DRAGON_EGG_BLOCK.get());
            output.accept((ItemLike)ModItems.STEAMED_DRAGON_EGG.get());
            output.accept((ItemLike)ModItems.DRAGON_MEAT_STEW_BLOCK.get());
            output.accept((ItemLike)ModItems.DRAGON_MEAT_STEW.get());
            output.accept((ItemLike)ModItems.GRILLED_SHULKER_BLOCK.get());
            output.accept((ItemLike)ModItems.GRILLED_SHULKER.get());
         })
         .build()
   );
}
