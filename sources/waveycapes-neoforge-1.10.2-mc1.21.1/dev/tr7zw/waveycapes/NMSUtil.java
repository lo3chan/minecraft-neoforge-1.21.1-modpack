package dev.tr7zw.waveycapes;

import java.util.function.IntUnaryOperator;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class NMSUtil {
   public static ModelPart[] buildCape(int texWidth, int texHight, IntUnaryOperator uvX, IntUnaryOperator uvY) {
      ModelPart[] customCape = new ModelPart[16];
      MeshDefinition meshDefinition = new MeshDefinition();
      PartDefinition partDefinition = meshDefinition.getRoot();

      for (int i = 0; i < 16; i++) {
         partDefinition.addOrReplaceChild(
            "customCape_" + i,
            CubeListBuilder.create().texOffs(uvX.applyAsInt(i), uvY.applyAsInt(i)).addBox(-5.0F, i, -1.0F, 10.0F, 1.0F, 1.0F, CubeDeformation.NONE, 1.0F, 0.5F),
            PartPose.offset(0.0F, 0.0F, 0.0F)
         );
      }

      ModelPart modelPart = partDefinition.bake(texWidth, texHight);

      for (int i = 0; i < 16; i++) {
         customCape[i] = modelPart.getChild("customCape_" + i);
      }

      return customCape;
   }
}
