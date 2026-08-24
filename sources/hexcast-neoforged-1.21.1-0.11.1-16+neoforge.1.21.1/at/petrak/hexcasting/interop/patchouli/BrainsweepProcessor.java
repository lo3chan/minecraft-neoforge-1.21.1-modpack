package at.petrak.hexcasting.interop.patchouli;

import at.petrak.hexcasting.common.recipe.BrainsweepRecipe;
import at.petrak.hexcasting.common.recipe.HexRecipeStuffRegistry;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

public class BrainsweepProcessor implements IComponentProcessor {
   private BrainsweepRecipe recipe;
   @Nullable
   private String exampleEntityString;

   public void setup(Level level, IVariableProvider vars) {
      ResourceLocation id = ResourceLocation.parse(vars.get("recipe", level.registryAccess()).asString());
      RecipeManager recman = level.getRecipeManager();

      for (RecipeHolder<BrainsweepRecipe> poisonApples : recman.getAllRecipesFor(HexRecipeStuffRegistry.BRAINSWEEP_TYPE)) {
         if (poisonApples.id().equals(id)) {
            this.recipe = (BrainsweepRecipe)poisonApples.value();
            break;
         }
      }
   }

   public IVariable process(Level level, String key) {
      if (this.recipe == null) {
         return null;
      } else {
         switch (key) {
            case "header":
               return IVariable.from(this.recipe.result().getBlock().getName(), level.registryAccess());
            case "input":
               List<ItemStack> inputStacks = this.recipe.blockIn().getDisplayedStacks();
               return IVariable.from(inputStacks.toArray(new ItemStack[0]), level.registryAccess());
            case "result":
               return IVariable.from(new ItemStack(this.recipe.result().getBlock()), level.registryAccess());
            case "entity":
               if (this.exampleEntityString == null) {
                  Entity entity = this.recipe.entityIn().exampleEntity(Minecraft.getInstance().level);
                  if (entity == null) {
                     return null;
                  }

                  StringBuilder bob = new StringBuilder();
                  bob.append(BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()));
                  CompoundTag tag = new CompoundTag();
                  entity.save(tag);
                  bob.append(tag.toString());
                  this.exampleEntityString = bob.toString();
               }

               return IVariable.wrap(this.exampleEntityString);
            case "entityTooltip":
               Minecraft mc = Minecraft.getInstance();
               return IVariable.wrapList(
                  this.recipe
                     .entityIn()
                     .getTooltip(mc.options.advancedItemTooltips)
                     .stream()
                     .map(component -> IVariable.from(component, level.registryAccess()))
                     .toList(),
                  level.registryAccess()
               );
            default:
               return null;
         }
      }
   }
}
