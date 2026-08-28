/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.model.geom.ModelPart
 *  net.minecraft.client.model.geom.PartPose
 *  net.minecraft.client.model.geom.builders.CubeDeformation
 *  net.minecraft.client.model.geom.builders.CubeListBuilder
 *  net.minecraft.client.model.geom.builders.MeshDefinition
 *  net.minecraft.client.model.geom.builders.PartDefinition
 */
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
        for (int i = 0; i < 16; ++i) {
            partDefinition.addOrReplaceChild("customCape_" + i, CubeListBuilder.create().texOffs(uvX.applyAsInt(i), uvY.applyAsInt(i)).addBox(-5.0f, (float)i, -1.0f, 10.0f, 1.0f, 1.0f, CubeDeformation.NONE, 1.0f, 0.5f), PartPose.offset((float)0.0f, (float)0.0f, (float)0.0f));
        }
        ModelPart modelPart = partDefinition.bake(texWidth, texHight);
        for (int i = 0; i < 16; ++i) {
            customCape[i] = modelPart.getChild("customCape_" + i);
        }
        return customCape;
    }
}

