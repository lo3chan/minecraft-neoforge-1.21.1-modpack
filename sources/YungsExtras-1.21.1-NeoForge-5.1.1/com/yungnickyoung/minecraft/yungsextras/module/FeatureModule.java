package com.yungnickyoung.minecraft.yungsextras.module;

import com.yungnickyoung.minecraft.yungsapi.api.autoregister.AutoRegister;
import com.yungnickyoung.minecraft.yungsextras.world.config.DesertWellFeatureConfiguration;
import com.yungnickyoung.minecraft.yungsextras.world.config.ResourceLocationFeatureConfiguration;
import com.yungnickyoung.minecraft.yungsextras.world.feature.desert.ChillzoneDesertFeature;
import com.yungnickyoung.minecraft.yungsextras.world.feature.desert.DesertGiantTorchFeature;
import com.yungnickyoung.minecraft.yungsextras.world.feature.desert.DesertObeliskFeature;
import com.yungnickyoung.minecraft.yungsextras.world.feature.desert.DesertSmallRuinsFeature;
import com.yungnickyoung.minecraft.yungsextras.world.feature.desert.DesertWellFeature;
import com.yungnickyoung.minecraft.yungsextras.world.feature.swamp.SwampArchFeature;
import com.yungnickyoung.minecraft.yungsextras.world.feature.swamp.SwampChurchFeature;
import com.yungnickyoung.minecraft.yungsextras.world.feature.swamp.SwampCubbyFeature;
import com.yungnickyoung.minecraft.yungsextras.world.feature.swamp.SwampDoubleArchFeature;
import com.yungnickyoung.minecraft.yungsextras.world.feature.swamp.SwampOgreFeature;
import com.yungnickyoung.minecraft.yungsextras.world.feature.swamp.SwampPillarFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

@AutoRegister("yungsextras")
public class FeatureModule {
   @AutoRegister("desert_well")
   public static Feature<DesertWellFeatureConfiguration> DESERT_WELL = new DesertWellFeature();
   @AutoRegister("desert_obelisk")
   public static Feature<ResourceLocationFeatureConfiguration> DESERT_OBELISK = new DesertObeliskFeature();
   @AutoRegister("desert_giant_torch")
   public static Feature<NoneFeatureConfiguration> DESERT_GIANT_TORCH = new DesertGiantTorchFeature();
   @AutoRegister("desert_ruins_0")
   public static Feature<NoneFeatureConfiguration> DESERT_RUINS_0 = new DesertSmallRuinsFeature();
   @AutoRegister("desert_chillzone")
   public static Feature<NoneFeatureConfiguration> DESERT_CHILLZONE = new ChillzoneDesertFeature();
   @AutoRegister("swamp_pillar")
   public static Feature<ResourceLocationFeatureConfiguration> SWAMP_PILLAR = new SwampPillarFeature();
   @AutoRegister("swamp_ogre")
   public static Feature<ResourceLocationFeatureConfiguration> SWAMP_OGRE = new SwampOgreFeature();
   @AutoRegister("swamp_cubby")
   public static Feature<ResourceLocationFeatureConfiguration> SWAMP_CUBBY = new SwampCubbyFeature();
   @AutoRegister("swamp_arch")
   public static Feature<ResourceLocationFeatureConfiguration> SWAMP_ARCH = new SwampArchFeature();
   @AutoRegister("swamp_double_arch")
   public static Feature<ResourceLocationFeatureConfiguration> SWAMP_DOUBLE_ARCH = new SwampDoubleArchFeature();
   @AutoRegister("swamp_church")
   public static Feature<ResourceLocationFeatureConfiguration> SWAMP_CHURCH = new SwampChurchFeature();
}
