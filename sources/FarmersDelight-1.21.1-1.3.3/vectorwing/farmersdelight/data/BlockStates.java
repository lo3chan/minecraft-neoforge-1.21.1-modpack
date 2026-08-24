package vectorwing.farmersdelight.data;

import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.VariantBlockStateBuilder;
import net.neoforged.neoforge.client.model.generators.ModelFile.ExistingModelFile;
import net.neoforged.neoforge.client.model.generators.MultiPartBlockStateBuilder.PartBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import vectorwing.farmersdelight.common.block.BasketBlock;
import vectorwing.farmersdelight.common.block.BuddingTomatoBlock;
import vectorwing.farmersdelight.common.block.CabbageBlock;
import vectorwing.farmersdelight.common.block.CabinetBlock;
import vectorwing.farmersdelight.common.block.CookingPotBlock;
import vectorwing.farmersdelight.common.block.FeastBlock;
import vectorwing.farmersdelight.common.block.MushroomColonyBlock;
import vectorwing.farmersdelight.common.block.OnionBlock;
import vectorwing.farmersdelight.common.block.OrganicCompostBlock;
import vectorwing.farmersdelight.common.block.PieBlock;
import vectorwing.farmersdelight.common.block.RiceBlock;
import vectorwing.farmersdelight.common.block.RicePaniclesBlock;
import vectorwing.farmersdelight.common.block.RichSoilFarmlandBlock;
import vectorwing.farmersdelight.common.block.RopeBlock;
import vectorwing.farmersdelight.common.block.SkilletBlock;
import vectorwing.farmersdelight.common.block.StoveBlock;
import vectorwing.farmersdelight.common.block.TatamiBlock;
import vectorwing.farmersdelight.common.block.TatamiMatBlock;
import vectorwing.farmersdelight.common.block.TomatoBlock;
import vectorwing.farmersdelight.common.block.state.CookingPotSupport;
import vectorwing.farmersdelight.common.registry.ModBlocks;

public class BlockStates extends BlockStateProvider {
   private static final int DEFAULT_ANGLE_OFFSET = 180;

   public BlockStates(PackOutput output, ExistingFileHelper existingFileHelper) {
      super(output, "farmersdelight", existingFileHelper);
   }

   private String blockName(Block block) {
      return BuiltInRegistries.BLOCK.getKey(block).getPath();
   }

   public ResourceLocation resourceMCBlock(String path) {
      return ResourceLocation.withDefaultNamespace("block/" + path);
   }

   public ResourceLocation resourceFDBlock(String path) {
      return ResourceLocation.fromNamespaceAndPath("farmersdelight", "block/" + path);
   }

   public ModelFile existingModel(Block block) {
      return new ExistingModelFile(this.resourceFDBlock(this.blockName(block)), this.models().existingFileHelper);
   }

   public ModelFile existingModel(String path) {
      return new ExistingModelFile(this.resourceFDBlock(path), this.models().existingFileHelper);
   }

   protected void registerStatesAndModels() {
      this.simpleBlock(ModBlocks.SAFETY_NET.get(), this.existingModel(ModBlocks.SAFETY_NET.get()));
      this.simpleBlock(ModBlocks.CANVAS_RUG.get(), this.existingModel(ModBlocks.CANVAS_RUG.get()));
      String riceBag = this.blockName(ModBlocks.RICE_BAG.get());
      this.simpleBlock(
         ModBlocks.RICE_BAG.get(),
         ((BlockModelBuilder)((BlockModelBuilder)((BlockModelBuilder)((BlockModelBuilder)((BlockModelBuilder)((BlockModelBuilder)((BlockModelBuilder)this.models()
                                 .withExistingParent(riceBag, "cube"))
                              .texture("particle", this.resourceFDBlock(riceBag + "_top")))
                           .texture("down", this.resourceFDBlock(riceBag + "_bottom")))
                        .texture("up", this.resourceFDBlock(riceBag + "_top")))
                     .texture("north", this.resourceFDBlock(riceBag + "_side_tied")))
                  .texture("south", this.resourceFDBlock(riceBag + "_side_tied")))
               .texture("east", this.resourceFDBlock(riceBag + "_side")))
            .texture("west", this.resourceFDBlock(riceBag + "_side"))
      );
      this.customDirectionalBlock(
         ModBlocks.WOODEN_BASKET.get(), $ -> this.modelBasket(this.blockName(ModBlocks.WOODEN_BASKET.get())), BasketBlock.ENABLED, BasketBlock.WATERLOGGED
      );
      this.customDirectionalBlock(
         ModBlocks.BAMBOO_BASKET.get(), $ -> this.modelBasket(this.blockName(ModBlocks.BAMBOO_BASKET.get())), BasketBlock.ENABLED, BasketBlock.WATERLOGGED
      );
      this.customDirectionalBlock(ModBlocks.RICE_BALE.get(), $ -> this.modelCubeBottomTop(this.blockName(ModBlocks.RICE_BALE.get())));
      this.customHorizontalBlock(ModBlocks.CUTTING_BOARD.get(), $ -> this.existingModel(ModBlocks.CUTTING_BOARD.get()), BasketBlock.WATERLOGGED);
      this.horizontalBlock(ModBlocks.HALF_TATAMI_MAT.get(), this.existingModel("tatami_mat_half"));
      this.stageBlock(ModBlocks.BROWN_MUSHROOM_COLONY.get(), MushroomColonyBlock.COLONY_AGE);
      this.stageBlock(ModBlocks.RED_MUSHROOM_COLONY.get(), MushroomColonyBlock.COLONY_AGE);
      this.customStageBlock(ModBlocks.CABBAGE_CROP.get(), this.resourceFDBlock("template_crop_cross"), "cross", CabbageBlock.AGE, new ArrayList<>());
      this.customStageBlock(ModBlocks.ONION_CROP.get(), this.mcLoc("crop"), "crop", OnionBlock.AGE, Arrays.asList(0, 0, 1, 1, 2, 2, 2, 3));
      this.customStageBlock(
         ModBlocks.BUDDING_TOMATO_CROP.get(), this.resourceFDBlock("template_crop_cross"), "cross", BuddingTomatoBlock.AGE, Arrays.asList(0, 1, 2, 3, 3)
      );
      this.tomatoBlock((Block)ModBlocks.TOMATO_CROP.get(), TomatoBlock.VINE_AGE, TomatoBlock.ROPELOGGED);
      this.ropedTomatoBlock((Block)ModBlocks.TOMATO_CROP_ON_ROPE.get(), TomatoBlock.VINE_AGE);
      this.riceRootBlock(ModBlocks.RICE_CROP.get());
      this.stageBlock(ModBlocks.RICE_CROP_PANICLES.get(), RicePaniclesBlock.RICE_AGE);
      this.crateBlock(ModBlocks.CARROT_CRATE.get(), "carrot");
      this.crateBlock(ModBlocks.POTATO_CRATE.get(), "potato");
      this.crateBlock(ModBlocks.BEETROOT_CRATE.get(), "beetroot");
      this.crateBlock(ModBlocks.CABBAGE_CRATE.get(), "cabbage");
      this.crateBlock(ModBlocks.TOMATO_CRATE.get(), "tomato");
      this.crateBlock(ModBlocks.ONION_CRATE.get(), "onion");
      this.axisBlock((RotatedPillarBlock)ModBlocks.STRAW_BALE.get());
      this.organicCompostBlock(ModBlocks.ORGANIC_COMPOST.get());
      this.simpleBlock(ModBlocks.RICH_SOIL.get(), this.cubeRandomRotation(ModBlocks.RICH_SOIL.get(), ""));
      this.farmlandBlock(ModBlocks.RICH_SOIL_FARMLAND.get(), ModBlocks.RICH_SOIL.get());
      ((PartBuilder)((PartBuilder)((PartBuilder)((PartBuilder)((PartBuilder)((PartBuilder)this.getMultipartBuilder(ModBlocks.ROPE.get())
                           .part()
                           .modelFile(this.existingModel("rope_post"))
                           .addModel())
                        .end()
                        .part()
                        .modelFile(this.existingModel("rope_bell_tie"))
                        .addModel())
                     .condition(RopeBlock.TIED_TO_BELL, new Boolean[]{true})
                     .end()
                     .part()
                     .modelFile(this.existingModel("rope_side"))
                     .addModel())
                  .condition(RopeBlock.NORTH, new Boolean[]{true})
                  .end()
                  .part()
                  .modelFile(this.existingModel("rope_side"))
                  .rotationY(90)
                  .addModel())
               .condition(RopeBlock.EAST, new Boolean[]{true})
               .end()
               .part()
               .modelFile(this.existingModel("rope_side_alt"))
               .addModel())
            .condition(RopeBlock.SOUTH, new Boolean[]{true})
            .end()
            .part()
            .modelFile(this.existingModel("rope_side_alt"))
            .rotationY(90)
            .addModel())
         .condition(RopeBlock.WEST, new Boolean[]{true})
         .end();
      ((PartBuilder)((PartBuilder)((PartBuilder)((PartBuilder)((PartBuilder)this.getMultipartBuilder(ModBlocks.ROPE_FENCE.get())
                        .part()
                        .modelFile(this.existingModel("rope_fence_post"))
                        .addModel())
                     .end()
                     .part()
                     .modelFile(this.existingModel("rope_fence_side"))
                     .addModel())
                  .condition(FenceBlock.NORTH, new Boolean[]{true})
                  .end()
                  .part()
                  .modelFile(this.existingModel("rope_fence_side"))
                  .rotationY(90)
                  .addModel())
               .condition(FenceBlock.EAST, new Boolean[]{true})
               .end()
               .part()
               .modelFile(this.existingModel("rope_fence_side_alt"))
               .addModel())
            .condition(FenceBlock.SOUTH, new Boolean[]{true})
            .end()
            .part()
            .modelFile(this.existingModel("rope_fence_side_alt"))
            .rotationY(90)
            .addModel())
         .condition(FenceBlock.WEST, new Boolean[]{true})
         .end();
      this.ropeFenceGateBlock(ModBlocks.ROPE_FENCE_GATE.get());
      ModelFile head = this.existingModel("tatami_mat_head");
      ModelFile foot = this.existingModel("tatami_mat_foot");
      this.getVariantBuilder(ModBlocks.FULL_TATAMI_MAT.get())
         .forAllStates(
            state -> ConfiguredModel.builder()
               .modelFile(state.getValue(TatamiMatBlock.PART) == BedPart.HEAD ? head : foot)
               .rotationY((int)((Direction)state.getValue(TatamiMatBlock.FACING)).toYRot())
               .build()
         );
      ModelFile odd = this.existingModel("tatami_odd");
      ModelFile even = this.existingModel("tatami_even");
      ModelFile notPaired = this.models()
         .cubeAll(this.blockName(ModBlocks.TATAMI.get()) + "_half", ResourceLocation.fromNamespaceAndPath("farmersdelight", "block/tatami_mat_half"));
      this.getVariantBuilder(ModBlocks.TATAMI.get())
         .forAllStates(
            state -> {
               Direction dir = (Direction)state.getValue(TatamiBlock.FACING);
               return ConfiguredModel.builder()
                  .modelFile(state.getValue(TatamiBlock.PAIRED) ? (dir.get3DDataValue() % 2 == 0 ? even : odd) : notPaired)
                  .rotationX(dir == Direction.DOWN ? 180 : (dir.getAxis().isHorizontal() ? 90 : 0))
                  .rotationY(dir.getAxis().isVertical() ? 0 : ((int)dir.toYRot() + 180) % 360)
                  .build();
            }
         );
      this.cabinetBlock(ModBlocks.OAK_CABINET.get(), "oak");
      this.cabinetBlock(ModBlocks.BIRCH_CABINET.get(), "birch");
      this.cabinetBlock(ModBlocks.SPRUCE_CABINET.get(), "spruce");
      this.cabinetBlock(ModBlocks.JUNGLE_CABINET.get(), "jungle");
      this.cabinetBlock(ModBlocks.ACACIA_CABINET.get(), "acacia");
      this.cabinetBlock(ModBlocks.DARK_OAK_CABINET.get(), "dark_oak");
      this.cabinetBlock(ModBlocks.MANGROVE_CABINET.get(), "mangrove");
      this.cabinetBlock(ModBlocks.CHERRY_CABINET.get(), "cherry");
      this.cabinetBlock(ModBlocks.BAMBOO_CABINET.get(), "bamboo");
      this.cabinetBlock(ModBlocks.CRIMSON_CABINET.get(), "crimson");
      this.cabinetBlock(ModBlocks.WARPED_CABINET.get(), "warped");
      this.pieBlock(ModBlocks.APPLE_PIE.get());
      this.customPieBlock(ModBlocks.CHOCOLATE_PIE.get());
      this.pieBlock(ModBlocks.SWEET_BERRY_CHEESECAKE.get());
      this.pieBlock(ModBlocks.PUMPKIN_PIE.get());
      this.feastBlock((FeastBlock)ModBlocks.STUFFED_PUMPKIN_BLOCK.get());
      this.feastBlock((FeastBlock)ModBlocks.ROAST_CHICKEN_BLOCK.get());
      this.feastBlock((FeastBlock)ModBlocks.HONEY_GLAZED_HAM_BLOCK.get());
      this.feastBlock((FeastBlock)ModBlocks.SHEPHERDS_PIE_BLOCK.get());
      this.feastBlock((FeastBlock)ModBlocks.GLEAMING_SALAD_BLOCK.get());
      this.feastBlock((FeastBlock)ModBlocks.RICE_ROLL_MEDLEY_BLOCK.get());
      this.wildCropBlock(ModBlocks.SANDY_SHRUB.get());
      this.wildCropBlock(ModBlocks.WILD_BEETROOTS.get());
      this.wildCropBlock(ModBlocks.WILD_CABBAGES.get());
      this.wildCropBlock(ModBlocks.WILD_POTATOES.get());
      this.wildCropBlock(ModBlocks.WILD_TOMATOES.get());
      this.wildCropBlock(ModBlocks.WILD_CARROTS.get());
      this.wildCropBlock(ModBlocks.WILD_ONIONS.get());
      this.doublePlantBlock(ModBlocks.WILD_RICE.get());
      this.cookingPotBlock(ModBlocks.COOKING_POT.get());
      this.skilletBlock(ModBlocks.SKILLET.get());
      this.horizontalBlock(
         ModBlocks.STOVE.get(),
         state -> {
            String name = this.blockName(ModBlocks.STOVE.get());
            String suffix = state.getValue(StoveBlock.LIT) ? "_on" : "";
            return this.models()
               .orientableWithBottom(
                  name + suffix,
                  this.resourceFDBlock(name + "_side"),
                  this.resourceFDBlock(name + "_front" + suffix),
                  this.resourceFDBlock(name + "_bottom"),
                  this.resourceFDBlock(name + "_top" + suffix)
               );
         }
      );

      for (Block sign : Sets.newHashSet(
         new Block[]{
            ModBlocks.CANVAS_SIGN.get(),
            ModBlocks.HANGING_CANVAS_SIGN.get(),
            ModBlocks.WHITE_CANVAS_SIGN.get(),
            ModBlocks.WHITE_HANGING_CANVAS_SIGN.get(),
            ModBlocks.ORANGE_CANVAS_SIGN.get(),
            ModBlocks.ORANGE_HANGING_CANVAS_SIGN.get(),
            ModBlocks.MAGENTA_CANVAS_SIGN.get(),
            ModBlocks.MAGENTA_HANGING_CANVAS_SIGN.get(),
            ModBlocks.LIGHT_BLUE_CANVAS_SIGN.get(),
            ModBlocks.LIGHT_BLUE_HANGING_CANVAS_SIGN.get(),
            ModBlocks.YELLOW_CANVAS_SIGN.get(),
            ModBlocks.YELLOW_HANGING_CANVAS_SIGN.get(),
            ModBlocks.LIME_CANVAS_SIGN.get(),
            ModBlocks.LIME_HANGING_CANVAS_SIGN.get(),
            ModBlocks.PINK_CANVAS_SIGN.get(),
            ModBlocks.PINK_HANGING_CANVAS_SIGN.get(),
            ModBlocks.GRAY_CANVAS_SIGN.get(),
            ModBlocks.GRAY_HANGING_CANVAS_SIGN.get(),
            ModBlocks.LIGHT_GRAY_CANVAS_SIGN.get(),
            ModBlocks.LIGHT_GRAY_HANGING_CANVAS_SIGN.get(),
            ModBlocks.CYAN_CANVAS_SIGN.get(),
            ModBlocks.CYAN_HANGING_CANVAS_SIGN.get(),
            ModBlocks.PURPLE_CANVAS_SIGN.get(),
            ModBlocks.PURPLE_HANGING_CANVAS_SIGN.get(),
            ModBlocks.BLUE_CANVAS_SIGN.get(),
            ModBlocks.BLUE_HANGING_CANVAS_SIGN.get(),
            ModBlocks.BROWN_CANVAS_SIGN.get(),
            ModBlocks.BROWN_HANGING_CANVAS_SIGN.get(),
            ModBlocks.GREEN_CANVAS_SIGN.get(),
            ModBlocks.GREEN_HANGING_CANVAS_SIGN.get(),
            ModBlocks.RED_CANVAS_SIGN.get(),
            ModBlocks.RED_HANGING_CANVAS_SIGN.get(),
            ModBlocks.BLACK_CANVAS_SIGN.get(),
            ModBlocks.BLACK_HANGING_CANVAS_SIGN.get(),
            ModBlocks.CANVAS_WALL_SIGN.get(),
            ModBlocks.HANGING_CANVAS_WALL_SIGN.get(),
            ModBlocks.WHITE_CANVAS_WALL_SIGN.get(),
            ModBlocks.WHITE_HANGING_CANVAS_WALL_SIGN.get(),
            ModBlocks.ORANGE_CANVAS_WALL_SIGN.get(),
            ModBlocks.ORANGE_HANGING_CANVAS_WALL_SIGN.get(),
            ModBlocks.MAGENTA_CANVAS_WALL_SIGN.get(),
            ModBlocks.MAGENTA_HANGING_CANVAS_WALL_SIGN.get(),
            ModBlocks.LIGHT_BLUE_CANVAS_WALL_SIGN.get(),
            ModBlocks.LIGHT_BLUE_HANGING_CANVAS_WALL_SIGN.get(),
            ModBlocks.YELLOW_CANVAS_WALL_SIGN.get(),
            ModBlocks.YELLOW_HANGING_CANVAS_WALL_SIGN.get(),
            ModBlocks.LIME_CANVAS_WALL_SIGN.get(),
            ModBlocks.LIME_HANGING_CANVAS_WALL_SIGN.get(),
            ModBlocks.PINK_CANVAS_WALL_SIGN.get(),
            ModBlocks.PINK_HANGING_CANVAS_WALL_SIGN.get(),
            ModBlocks.GRAY_CANVAS_WALL_SIGN.get(),
            ModBlocks.GRAY_HANGING_CANVAS_WALL_SIGN.get(),
            ModBlocks.LIGHT_GRAY_CANVAS_WALL_SIGN.get(),
            ModBlocks.LIGHT_GRAY_HANGING_CANVAS_WALL_SIGN.get(),
            ModBlocks.CYAN_CANVAS_WALL_SIGN.get(),
            ModBlocks.CYAN_HANGING_CANVAS_WALL_SIGN.get(),
            ModBlocks.PURPLE_CANVAS_WALL_SIGN.get(),
            ModBlocks.PURPLE_HANGING_CANVAS_WALL_SIGN.get(),
            ModBlocks.BLUE_CANVAS_WALL_SIGN.get(),
            ModBlocks.BLUE_HANGING_CANVAS_WALL_SIGN.get(),
            ModBlocks.BROWN_CANVAS_WALL_SIGN.get(),
            ModBlocks.BROWN_HANGING_CANVAS_WALL_SIGN.get(),
            ModBlocks.GREEN_CANVAS_WALL_SIGN.get(),
            ModBlocks.GREEN_HANGING_CANVAS_WALL_SIGN.get(),
            ModBlocks.RED_CANVAS_WALL_SIGN.get(),
            ModBlocks.RED_HANGING_CANVAS_WALL_SIGN.get(),
            ModBlocks.BLACK_CANVAS_WALL_SIGN.get(),
            ModBlocks.BLACK_HANGING_CANVAS_WALL_SIGN.get()
         }
      )) {
         this.simpleBlock(sign, this.existingModel(ModBlocks.CANVAS_SIGN.get()));
      }
   }

   public void cookingPotBlock(Block block) {
      this.getVariantBuilder(block)
         .forAllStatesExcept(
            state -> {
               String supportSuffix = switch ((CookingPotSupport)state.getValue(CookingPotBlock.SUPPORT)) {
                  case NONE -> "";
                  case TRAY -> "_tray";
                  case HANDLE -> "_handle";
               };
               return ConfiguredModel.builder()
                  .modelFile(this.existingModel(this.blockName(block) + supportSuffix))
                  .rotationY(((int)((Direction)state.getValue(BlockStateProperties.HORIZONTAL_FACING)).toYRot() + 180) % 360)
                  .build();
            },
            new Property[]{CookingPotBlock.WATERLOGGED}
         );
   }

   public void skilletBlock(Block block) {
      this.getVariantBuilder(block)
         .forAllStatesExcept(
            state -> {
               String supportSuffix = state.getValue(SkilletBlock.SUPPORT) ? "_tray" : "";
               return ConfiguredModel.builder()
                  .modelFile(this.existingModel(this.blockName(block) + supportSuffix))
                  .rotationY(((int)((Direction)state.getValue(BlockStateProperties.HORIZONTAL_FACING)).toYRot() + 180) % 360)
                  .build();
            },
            new Property[]{SkilletBlock.WATERLOGGED}
         );
   }

   public void ropeFenceGateBlock(Block block) {
      this.getVariantBuilder(block)
         .forAllStatesExcept(
            state -> {
               String wallInfix = state.getValue(FenceGateBlock.IN_WALL) ? "_wall" : "";
               ModelFile modelClosed = this.existingModel(this.blockName(block) + wallInfix);
               ModelFile modelOpen = this.existingModel(this.blockName(block) + wallInfix + "_open");
               return ConfiguredModel.builder()
                  .modelFile(state.getValue(FenceGateBlock.OPEN) ? modelOpen : modelClosed)
                  .rotationY((int)((Direction)state.getValue(FenceGateBlock.FACING)).toYRot())
                  .build();
            },
            new Property[]{FenceGateBlock.POWERED}
         );
   }

   public void organicCompostBlock(Block block) {
      this.getVariantBuilder(block).forAllStates(state -> {
         int composting = (Integer)state.getValue(OrganicCompostBlock.COMPOSTING);
         String textureName = this.blockName(block) + "_stage" + composting / 2;
         return ConfiguredModel.allYRotations(this.models().cubeAll(textureName, this.resourceFDBlock(textureName)), 0, false);
      });
   }

   public void farmlandBlock(Block farmlandBlock, Block dirtBlock) {
      this.getVariantBuilder(farmlandBlock).forAllStates(state -> {
         int moisture = (Integer)state.getValue(RichSoilFarmlandBlock.MOISTURE);
         return ConfiguredModel.builder().modelFile(this.modelFarmland(this.blockName(farmlandBlock), this.blockName(dirtBlock), moisture == 7)).build();
      });
   }

   public void customDirectionalBlock(Block block, Function<BlockState, ModelFile> modelFunc, Property<?>... ignored) {
      this.getVariantBuilder(block)
         .forAllStatesExcept(
            state -> {
               Direction dir = (Direction)state.getValue(BlockStateProperties.FACING);
               return ConfiguredModel.builder()
                  .modelFile(modelFunc.apply(state))
                  .rotationX(dir == Direction.DOWN ? 180 : (dir.getAxis().isHorizontal() ? 90 : 0))
                  .rotationY(dir.getAxis().isVertical() ? 0 : ((int)dir.toYRot() + 180) % 360)
                  .build();
            },
            ignored
         );
   }

   public void customHorizontalBlock(Block block, Function<BlockState, ModelFile> modelFunc, Property<?>... ignored) {
      this.getVariantBuilder(block)
         .forAllStatesExcept(
            state -> ConfiguredModel.builder()
               .modelFile(modelFunc.apply(state))
               .rotationY(((int)((Direction)state.getValue(BlockStateProperties.HORIZONTAL_FACING)).toYRot() + 180) % 360)
               .build(),
            ignored
         );
   }

   public void stageBlock(Block block, IntegerProperty ageProperty, Property<?>... ignored) {
      this.getVariantBuilder(block)
         .forAllStatesExcept(
            state -> {
               int age = (Integer)state.getValue(ageProperty);
               String stageName = this.blockName(block) + "_stage" + age;
               return ConfiguredModel.builder()
                  .modelFile(((BlockModelBuilder)this.models().cross(stageName, this.resourceFDBlock(stageName))).renderType("cutout"))
                  .build();
            },
            ignored
         );
   }

   public void customStageBlock(
      Block block, @Nullable ResourceLocation parent, String textureKey, IntegerProperty ageProperty, List<Integer> suffixes, Property<?>... ignored
   ) {
      this.getVariantBuilder(block)
         .forAllStatesExcept(
            state -> {
               int age = (Integer)state.getValue(ageProperty);
               String stageName = this.blockName(block) + "_stage";
               stageName = stageName + (suffixes.isEmpty() ? age : suffixes.get(Math.min(suffixes.size(), age)));
               return parent == null
                  ? ConfiguredModel.builder()
                     .modelFile(((BlockModelBuilder)this.models().cross(stageName, this.resourceFDBlock(stageName))).renderType("cutout"))
                     .build()
                  : ConfiguredModel.builder()
                     .modelFile(
                        ((BlockModelBuilder)this.models().singleTexture(stageName, parent, textureKey, this.resourceFDBlock(stageName))).renderType("cutout")
                     )
                     .build();
            },
            ignored
         );
   }

   public void riceRootBlock(Block block) {
      this.getVariantBuilder(block)
         .forAllStatesExcept(
            state -> {
               int age = (Integer)state.getValue(RiceBlock.AGE);
               boolean isSupporting = (Boolean)state.getValue(RiceBlock.SUPPORTING) && age == 3;
               String stageName = isSupporting ? this.blockName(block) + "_supporting" : this.blockName(block) + "_stage" + age;
               return ConfiguredModel.builder()
                  .modelFile(((BlockModelBuilder)this.models().cross(stageName, this.resourceFDBlock(stageName))).renderType("cutout"))
                  .build();
            },
            new Property[0]
         );
   }

   public void tomatoBlock(Block block, IntegerProperty ageProperty, BooleanProperty ropeloggedProperty, Property<?>... ignored) {
      this.getVariantBuilder(block)
         .forAllStatesExcept(
            state -> {
               int age = (Integer)state.getValue(ageProperty);
               boolean ropelogged = (Boolean)state.getValue(ropeloggedProperty);
               String stageName = this.blockName(block) + "_stage" + age;
               String ropeloggedStageName = this.blockName(block) + "_old_stage" + age;
               return ConfiguredModel.builder()
                  .modelFile(
                     (ModelFile)(ropelogged
                        ? this.modelCropWithRope(ropeloggedStageName, "tomatoes_coiled_rope")
                        : ((BlockModelBuilder)this.models()
                              .singleTexture(stageName, this.resourceFDBlock("template_crop_cross"), "cross", this.resourceFDBlock(stageName)))
                           .renderType("cutout"))
                  )
                  .build();
            },
            ignored
         );
   }

   public void ropedTomatoBlock(Block block, IntegerProperty ageProperty, Property<?>... ignored) {
      this.getVariantBuilder(block).forAllStatesExcept(state -> {
         int age = (Integer)state.getValue(ageProperty);
         String stageName = this.blockName(block) + "_stage" + age;
         return ConfiguredModel.builder().modelFile(this.modelCropWithRope(stageName, "tomatoes_coiled_rope")).build();
      }, ignored);
   }

   public void wildCropBlock(Block block) {
      this.wildCropBlock(block, false);
   }

   public void wildCropBlock(Block block, boolean isBushCrop) {
      if (isBushCrop) {
         this.simpleBlock(
            block,
            ((BlockModelBuilder)this.models()
                  .singleTexture(this.blockName(block), this.resourceFDBlock("template_bush_crop"), "crop", this.resourceFDBlock(this.blockName(block))))
               .renderType("cutout")
         );
      } else {
         this.simpleBlock(
            block, ((BlockModelBuilder)this.models().cross(this.blockName(block), this.resourceFDBlock(this.blockName(block)))).renderType("cutout")
         );
      }
   }

   public void crateBlock(Block block, String cropName) {
      this.simpleBlock(
         block,
         this.models()
            .cubeBottomTop(
               this.blockName(block),
               this.resourceFDBlock(cropName + "_crate_side"),
               this.resourceFDBlock("crate_bottom"),
               this.resourceFDBlock(cropName + "_crate_top")
            )
      );
   }

   public void cabinetBlock(Block block, String woodType) {
      this.horizontalBlock(
         block,
         state -> {
            String suffix = state.getValue(CabinetBlock.OPEN) ? "_open" : "";
            return this.models()
               .orientable(
                  this.blockName(block) + suffix,
                  this.resourceFDBlock(woodType + "_cabinet_side"),
                  this.resourceFDBlock(woodType + "_cabinet_front" + suffix),
                  this.resourceFDBlock(woodType + "_cabinet_top")
               );
         }
      );
   }

   public void feastBlock(FeastBlock block) {
      this.getVariantBuilder(block)
         .forAllStates(
            state -> {
               IntegerProperty servingsProperty = block.getServingsProperty();
               int servings = (Integer)state.getValue(servingsProperty);
               String suffix = "_stage" + (block.getMaxServings() - servings);
               if (servings == 0) {
                  suffix = block.hasLeftovers ? "_leftovers" : "_stage" + (servingsProperty.getPossibleValues().toArray().length - 2);
               }

               return ConfiguredModel.builder()
                  .modelFile(this.existingModel(this.blockName(block) + suffix))
                  .rotationY(((int)((Direction)state.getValue(FeastBlock.FACING)).toYRot() + 180) % 360)
                  .build();
            }
         );
   }

   public void doublePlantBlock(Block block) {
      ((VariantBlockStateBuilder)this.getVariantBuilder(block)
            .partialState()
            .with(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER)
            .modelForState()
            .modelFile(
               ((BlockModelBuilder)this.models().cross(this.blockName(block) + "_bottom", this.resourceFDBlock(this.blockName(block) + "_bottom")))
                  .renderType("cutout")
            )
            .addModel())
         .partialState()
         .with(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER)
         .modelForState()
         .modelFile(
            ((BlockModelBuilder)this.models().cross(this.blockName(block) + "_top", this.resourceFDBlock(this.blockName(block) + "_top"))).renderType("cutout")
         )
         .addModel();
   }

   public void pieBlock(Block block) {
      this.getVariantBuilder(block)
         .forAllStates(
            state -> {
               int bites = (Integer)state.getValue(PieBlock.BITES);
               return ConfiguredModel.builder()
                  .modelFile(bites > 0 ? this.modelPieSlice(this.blockName(block), bites) : this.modelPie(this.blockName(block)))
                  .rotationY(((int)((Direction)state.getValue(PieBlock.FACING)).toYRot() + 180) % 360)
                  .build();
            }
         );
   }

   public void customPieBlock(Block block) {
      this.getVariantBuilder(block)
         .forAllStates(
            state -> {
               int bites = (Integer)state.getValue(PieBlock.BITES);
               String suffix = bites > 0 ? "_slice" + bites : "";
               return ConfiguredModel.builder()
                  .modelFile(this.existingModel(this.blockName(block) + suffix))
                  .rotationY(((int)((Direction)state.getValue(PieBlock.FACING)).toYRot() + 180) % 360)
                  .build();
            }
         );
   }

   public ConfiguredModel[] cubeRandomRotation(Block block, String suffix) {
      String formattedName = this.blockName(block) + (suffix.isEmpty() ? "" : "_" + suffix);
      return ConfiguredModel.allYRotations(this.models().cubeAll(formattedName, this.resourceFDBlock(formattedName)), 0, false);
   }

   private ModelFile modelCubeBottomTop(String baseName) {
      return ((BlockModelBuilder)((BlockModelBuilder)((BlockModelBuilder)this.models().withExistingParent(baseName, this.resourceMCBlock("cube_bottom_top")))
               .texture("bottom", this.resourceFDBlock(baseName + "_bottom")))
            .texture("side", this.resourceFDBlock(baseName + "_side")))
         .texture("top", this.resourceFDBlock(baseName + "_top"));
   }

   private ModelFile modelBasket(String baseName) {
      return ((BlockModelBuilder)((BlockModelBuilder)((BlockModelBuilder)((BlockModelBuilder)this.models()
                     .withExistingParent(baseName, this.resourceFDBlock("template_basket")))
                  .texture("bottom", this.resourceFDBlock(baseName + "_bottom")))
               .texture("side", this.resourceFDBlock(baseName + "_side")))
            .texture("top", this.resourceFDBlock(baseName + "_top")))
         .texture("handle", this.resourceFDBlock(baseName + "_handle"));
   }

   private ModelFile modelCropCross(String baseName) {
      return ((BlockModelBuilder)((BlockModelBuilder)this.models().withExistingParent(baseName, this.resourceFDBlock("template_crop_cross")))
            .texture("cross", this.resourceFDBlock(baseName)))
         .renderType("cutout");
   }

   private ModelFile modelCropWithRope(String baseName, String ropeSideTextureName) {
      return ((BlockModelBuilder)((BlockModelBuilder)((BlockModelBuilder)((BlockModelBuilder)this.models()
                     .withExistingParent(baseName, this.resourceFDBlock("template_crop_with_rope")))
                  .texture("crop", this.resourceFDBlock(baseName)))
               .texture("rope_side", this.resourceFDBlock(ropeSideTextureName)))
            .texture("rope_top", this.resourceFDBlock("rope_top")))
         .renderType("cutout");
   }

   private ModelFile modelPie(String baseName) {
      return ((BlockModelBuilder)((BlockModelBuilder)((BlockModelBuilder)this.models().withExistingParent(baseName, this.resourceFDBlock("template_pie")))
               .texture("bottom", this.resourceFDBlock("pie_bottom")))
            .texture("side", this.resourceFDBlock("pie_side")))
         .texture("top", this.resourceFDBlock(baseName + "_top"));
   }

   private ModelFile modelPieSlice(String baseName, int bites) {
      return ((BlockModelBuilder)((BlockModelBuilder)((BlockModelBuilder)((BlockModelBuilder)this.models()
                     .withExistingParent(baseName + "_slice" + bites, this.resourceFDBlock("template_pie_slice" + bites)))
                  .texture("bottom", this.resourceFDBlock("pie_bottom")))
               .texture("side", this.resourceFDBlock("pie_side")))
            .texture("inner", this.resourceFDBlock(baseName + "_inner")))
         .texture("top", this.resourceFDBlock(baseName + "_top"));
   }

   private ModelFile modelFarmland(String farmlandName, String dirtName, boolean moist) {
      String moistSuffix = moist ? "_moist" : "";
      return ((BlockModelBuilder)((BlockModelBuilder)((BlockModelBuilder)this.models()
                  .withExistingParent(farmlandName + moistSuffix, this.resourceFDBlock("template_farmland_custom")))
               .texture("bottom", this.resourceFDBlock(dirtName)))
            .texture("side", this.resourceFDBlock(moist ? farmlandName + moistSuffix + "_side" : dirtName)))
         .texture("top", this.resourceFDBlock(farmlandName + moistSuffix));
   }
}
