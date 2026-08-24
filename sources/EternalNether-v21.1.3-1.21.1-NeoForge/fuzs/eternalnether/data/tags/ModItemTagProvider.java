package fuzs.eternalnether.data.tags;

import fuzs.eternalnether.init.ModItems;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import fuzs.puzzleslib.api.data.v2.tags.AbstractTagProvider;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;

public class ModItemTagProvider extends AbstractTagProvider<Item> {
   public ModItemTagProvider(DataProviderContext context) {
      super(Registries.ITEM, context);
   }

   public void addTags(Provider provider) {
      this.add(ItemTags.STONE_CRAFTING_MATERIALS).add((Item)ModItems.COBBLED_BLACKSTONE.value());
      this.add(ItemTags.STONE_TOOL_MATERIALS).add((Item)ModItems.COBBLED_BLACKSTONE.value());
      this.add("c:music_discs").add((Item)ModItems.WITHER_WALTZ_MUSIC_DISC.value());
      this.add("c:bones").add((Item)ModItems.WITHERED_BONE.value());
      this.add("c:fertilizers").add((Item)ModItems.WITHERED_BONE_MEAL.value());
      this.add(ItemTags.DURABILITY_ENCHANTABLE).add((Item)ModItems.GILDED_NETHERITE_SHIELD.value());
      this.add("c:tools/shield").add((Item)ModItems.GILDED_NETHERITE_SHIELD.value());
      this.add(ItemTags.SWORDS).add((Item)ModItems.CUTLASS.value());
      this.add("c:tools/melee_weapon").add((Item)ModItems.CUTLASS.value());
   }
}
