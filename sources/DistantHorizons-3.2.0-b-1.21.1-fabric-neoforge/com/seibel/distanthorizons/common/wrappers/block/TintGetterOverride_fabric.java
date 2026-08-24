package com.seibel.distanthorizons.common.wrappers.block;

import com.seibel.distanthorizons.core.dataObjects.fullData.sources.FullDataSourceV2;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.IClientLevelWrapper;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.class_1944;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_2586;
import net.minecraft.class_2591;
import net.minecraft.class_265;
import net.minecraft.class_2680;
import net.minecraft.class_3568;
import net.minecraft.class_3610;
import net.minecraft.class_3959;
import net.minecraft.class_3965;
import net.minecraft.class_4538;
import net.minecraft.class_5702;
import org.jetbrains.annotations.Nullable;

public class TintGetterOverride_fabric extends AbstractDhTintGetter_fabric {
   private class_4538 parent;

   @Override
   public void update(
      BiomeWrapper_fabric biomeWrapper, BlockStateWrapper_fabric blockStateWrapper, FullDataSourceV2 fullDataSource, IClientLevelWrapper clientLevelWrapper
   ) {
      super.update(biomeWrapper, blockStateWrapper, fullDataSource, clientLevelWrapper);
      this.parent = (class_4538)this.clientLevelWrapper.getWrappedMcObject();
   }

   public float method_24852(class_2350 direction, boolean bl) {
      return this.parent.method_24852(direction, bl);
   }

   public class_3568 method_22336() {
      return this.parent.method_22336();
   }

   public int method_8314(class_1944 lightLayer, class_2338 blockPos) {
      return this.parent.method_8314(lightLayer, blockPos);
   }

   public int method_22335(class_2338 blockPos, int i) {
      return this.parent.method_22335(blockPos, i);
   }

   public boolean method_8311(class_2338 blockPos) {
      return this.parent.method_8311(blockPos);
   }

   @Nullable
   public class_2586 method_8321(class_2338 blockPos) {
      return this.parent.method_8321(blockPos);
   }

   public class_2680 method_8320(class_2338 blockPos) {
      return this.parent.method_8320(blockPos);
   }

   public class_3610 method_8316(class_2338 blockPos) {
      return this.parent.method_8316(blockPos);
   }

   public int method_8317(class_2338 blockPos) {
      return this.parent.method_8317(blockPos);
   }

   public int method_8315() {
      return this.parent.method_8315();
   }

   public Stream<class_2680> method_29546(class_238 aABB) {
      return this.parent.method_29546(aABB);
   }

   public class_3965 method_17742(class_3959 clipContext) {
      return this.parent.method_17742(clipContext);
   }

   @Nullable
   public class_3965 method_17745(class_243 vec3, class_243 vec32, class_2338 blockPos, class_265 voxelShape, class_2680 blockState) {
      return this.parent.method_17745(vec3, vec32, blockPos, voxelShape, blockState);
   }

   public double method_30346(class_265 voxelShape, Supplier<class_265> supplier) {
      return this.parent.method_30346(voxelShape, supplier);
   }

   public double method_30347(class_2338 blockPos) {
      return this.parent.method_30347(blockPos);
   }

   public int method_31600() {
      return this.parent.method_31600();
   }

   public <T extends class_2586> Optional<T> method_35230(class_2338 blockPos, class_2591<T> blockEntityType) {
      return this.parent.method_35230(blockPos, blockEntityType);
   }

   public class_3965 method_32880(class_5702 clipBlockStateContext) {
      return this.parent.method_32880(clipBlockStateContext);
   }

   public int method_31605() {
      return this.parent.method_31605();
   }

   public int method_31607() {
      return this.parent.method_31607();
   }

   public int method_32890() {
      return this.parent.method_32890();
   }

   public int method_32891() {
      return this.parent.method_32891();
   }

   public int method_31597() {
      return this.parent.method_31597();
   }

   public boolean method_31606(class_2338 blockPos) {
      return this.parent.method_31606(blockPos);
   }

   public boolean method_31601(int i) {
      return this.parent.method_31601(i);
   }

   public int method_31602(int i) {
      return this.parent.method_31602(i);
   }

   public int method_31603(int i) {
      return this.parent.method_31603(i);
   }

   public int method_31604(int i) {
      return this.parent.method_31604(i);
   }
}
