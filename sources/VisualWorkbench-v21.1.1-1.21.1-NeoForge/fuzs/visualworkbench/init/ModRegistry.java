package fuzs.visualworkbench.init;

import fuzs.puzzleslib.api.init.v3.registry.RegistryManager;
import fuzs.puzzleslib.api.init.v3.tags.BoundTagFactory;
import fuzs.visualworkbench.world.inventory.VisualCraftingMenu;
import fuzs.visualworkbench.world.level.block.entity.CraftingTableBlockEntity;
import net.minecraft.core.Holder.Reference;
import net.minecraft.tags.TagKey;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.Builder;

public class ModRegistry {
   static final RegistryManager REGISTRIES = RegistryManager.from("visualworkbench");
   public static final Reference<BlockEntityType<CraftingTableBlockEntity>> CRAFTING_TABLE_BLOCK_ENTITY_TYPE = REGISTRIES.registerBlockEntityType(
      "crafting_table", () -> Builder.of(CraftingTableBlockEntity::new, new Block[0])
   );
   public static final Reference<MenuType<VisualCraftingMenu>> CRAFTING_MENU_TYPE = REGISTRIES.registerMenuType("crafting", () -> VisualCraftingMenu::new);
   static final BoundTagFactory TAGS = BoundTagFactory.make("visualworkbench");
   public static final TagKey<Block> UNALTERED_WORKBENCHES_BLOCK_TAG = TAGS.registerBlockTag("unaltered_workbenches");

   public static void touch() {
   }
}
