package dev.worldgen.lithostitched.worldgen.feature;

import dev.worldgen.lithostitched.worldgen.feature.config.StructureTemplateConfig;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

public class StructureTemplateFeature extends Feature<StructureTemplateConfig> {
   public static final StructureTemplateFeature FEATURE = new StructureTemplateFeature();

   public StructureTemplateFeature() {
      super(StructureTemplateConfig.CODEC);
   }

   public boolean place(FeaturePlaceContext<StructureTemplateConfig> context) {
      StructureTemplateConfig config = (StructureTemplateConfig)context.config();
      BlockPos origin = context.origin();
      WorldGenLevel level = context.level();
      RandomSource random = context.random();
      StructureTemplateManager templateManager = level.getLevel().getServer().getStructureManager();
      StructureTemplate template = templateManager.getOrCreate(config.template());
      Rotation rotation = config.rotation().orElse(Rotation.getRandom(random));
      StructurePlaceSettings settings = new StructurePlaceSettings().setRotation(rotation).setLiquidSettings(config.liquidSettings()).setRandom(random);

      for (StructureProcessor processor : ((StructureProcessorList)config.processors().value()).list()) {
         settings.addProcessor(processor);
      }

      BlockPos jigsawPos = origin;
      if (config.startJigsawName().isPresent()) {
         ResourceLocation startName = config.startJigsawName().get();
         ObjectArrayList<StructureBlockInfo> jigsawBlocks = template.filterBlocks(
            origin, new StructurePlaceSettings().setRotation(rotation.getRotated(Rotation.CLOCKWISE_180)), Blocks.JIGSAW, true
         );
         ObjectListIterator var13 = jigsawBlocks.iterator();

         while (var13.hasNext()) {
            StructureBlockInfo jigsaw = (StructureBlockInfo)var13.next();
            ResourceLocation jigsawName = ResourceLocation.tryParse(jigsaw.nbt().getString("name"));
            if (jigsawName != null && jigsawName.equals(startName)) {
               jigsawPos = jigsaw.pos();
               break;
            }
         }
      }

      Vec3i offset = jigsawPos.subtract(origin).multiply(-1);
      BlockPos placePos = origin.subtract(offset).offset(0, offset.getY() * 2, 0);
      template.placeInWorld(level, placePos, placePos, settings, random, 3);
      return true;
   }
}
