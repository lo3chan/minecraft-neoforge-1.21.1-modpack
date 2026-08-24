package vazkii.psi.data;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider.TagLookup;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags.Blocks;
import net.neoforged.neoforge.common.Tags.Items;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import vazkii.psi.common.item.base.ModItems;
import vazkii.psi.common.lib.ModTags;

public class PsiItemTagProvider extends ItemTagsProvider {
   public PsiItemTagProvider(
      PackOutput output, CompletableFuture<Provider> pLookupProvider, CompletableFuture<TagLookup<Block>> pBlockTags, ExistingFileHelper existingFileHelper
   ) {
      super(output, pLookupProvider, pBlockTags, "psi", existingFileHelper);
   }

   protected void addTags(Provider pProvider) {
      this.tag(Items.DUSTS).add((Item)ModItems.psidust.get());
      this.tag(Items.INGOTS).add((Item)ModItems.psimetal.get());
      this.tag(Items.INGOTS).add((Item)ModItems.ebonyPsimetal.get());
      this.tag(Items.INGOTS).add((Item)ModItems.ivoryPsimetal.get());
      this.tag(Items.GEMS).add((Item)ModItems.psigem.get());
      this.tag(ModTags.PSIDUST).add((Item)ModItems.psidust.get());
      this.tag(ModTags.EBONY_SUBSTANCE).add((Item)ModItems.ebonySubstance.get());
      this.tag(ModTags.IVORY_SUBSTANCE).add((Item)ModItems.ivorySubstance.get());
      this.tag(ModTags.INGOT_PSIMETAL).add((Item)ModItems.psimetal.get());
      this.copy(ModTags.Blocks.BLOCK_PSIMETAL, ModTags.BLOCK_PSIMETAL);
      this.tag(Items.ENCHANTABLES)
         .add(
            new Item[]{
               (Item)ModItems.psimetalSword.get(),
               (Item)ModItems.psimetalAxe.get(),
               (Item)ModItems.psimetalPickaxe.get(),
               (Item)ModItems.psimetalShovel.get(),
               (Item)ModItems.psimetalExosuitHelmet.get(),
               (Item)ModItems.psimetalExosuitChestplate.get(),
               (Item)ModItems.psimetalExosuitLeggings.get(),
               (Item)ModItems.psimetalExosuitBoots.get()
            }
         );
      this.tag(ItemTags.SWORDS).add((Item)ModItems.psimetalSword.get());
      this.tag(ItemTags.SWORD_ENCHANTABLE).add((Item)ModItems.psimetalSword.get());
      this.tag(ItemTags.AXES).add((Item)ModItems.psimetalAxe.get());
      this.tag(ItemTags.PICKAXES).add((Item)ModItems.psimetalPickaxe.get());
      this.tag(ItemTags.SHOVELS).add((Item)ModItems.psimetalShovel.get());
      this.tag(ItemTags.MINING_ENCHANTABLE)
         .add(new Item[]{(Item)ModItems.psimetalAxe.get(), (Item)ModItems.psimetalPickaxe.get(), (Item)ModItems.psimetalShovel.get()});
      this.tag(ItemTags.HEAD_ARMOR).add((Item)ModItems.psimetalExosuitHelmet.get());
      this.tag(ItemTags.HEAD_ARMOR_ENCHANTABLE).add((Item)ModItems.psimetalExosuitHelmet.get());
      this.tag(ItemTags.CHEST_ARMOR).add((Item)ModItems.psimetalExosuitChestplate.get());
      this.tag(ItemTags.CHEST_ARMOR_ENCHANTABLE).add((Item)ModItems.psimetalExosuitChestplate.get());
      this.tag(ItemTags.LEG_ARMOR).add((Item)ModItems.psimetalExosuitLeggings.get());
      this.tag(ItemTags.LEG_ARMOR_ENCHANTABLE).add((Item)ModItems.psimetalExosuitLeggings.get());
      this.tag(ItemTags.FOOT_ARMOR).add((Item)ModItems.psimetalExosuitBoots.get());
      this.tag(ItemTags.FOOT_ARMOR_ENCHANTABLE).add((Item)ModItems.psimetalExosuitBoots.get());
      this.tag(ModTags.GEM_PSIGEM).add((Item)ModItems.psigem.get());
      this.copy(ModTags.Blocks.BLOCK_PSIGEM, ModTags.BLOCK_PSIGEM);
      this.tag(ModTags.INGOT_EBONY_PSIMETAL).add((Item)ModItems.ebonyPsimetal.get());
      this.copy(ModTags.Blocks.BLOCK_EBONY_PSIMETAL, ModTags.BLOCK_EBONY_PSIMETAL);
      this.tag(ModTags.INGOT_IVORY_PSIMETAL).add((Item)ModItems.ivoryPsimetal.get());
      this.copy(ModTags.Blocks.BLOCK_IVORY_PSIMETAL, ModTags.BLOCK_IVORY_PSIMETAL);
      this.copy(Blocks.STORAGE_BLOCKS, Items.STORAGE_BLOCKS);
   }

   @NotNull
   public String getName() {
      return "Psi item tags";
   }
}
