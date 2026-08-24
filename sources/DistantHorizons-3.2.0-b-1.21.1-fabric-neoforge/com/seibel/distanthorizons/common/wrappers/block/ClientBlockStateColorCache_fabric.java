package com.seibel.distanthorizons.common.wrappers.block;

import com.seibel.distanthorizons.api.methods.events.abstractEvents.DhApiBlockColorOverrideEvent;
import com.seibel.distanthorizons.common.wrappers.McObjectConverter_fabric;
import com.seibel.distanthorizons.core.dataObjects.fullData.sources.FullDataSourceV2;
import com.seibel.distanthorizons.core.enums.EDhDirection;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.pos.blockPos.DhBlockPos;
import com.seibel.distanthorizons.core.pos.blockPos.DhBlockPosMutable;
import com.seibel.distanthorizons.core.wrapperInterfaces.world.IClientLevelWrapper;
import com.seibel.distanthorizons.coreapi.DependencyInjection.ApiEventInjector;
import com.seibel.distanthorizons.coreapi.util.ColorUtil;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import net.minecraft.class_1058;
import net.minecraft.class_2381;
import net.minecraft.class_2465;
import net.minecraft.class_2482;
import net.minecraft.class_2680;
import net.minecraft.class_2771;
import net.minecraft.class_310;
import net.minecraft.class_777;
import org.jetbrains.annotations.Nullable;

public class ClientBlockStateColorCache_fabric {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   private static final class_310 MC = class_310.method_1551();
   private static final HashSet<class_2680> BLOCK_STATES_THAT_NEED_LEVEL = new HashSet<>();
   private static final HashSet<class_2680> BROKEN_BLOCK_STATES = new HashSet<>();
   private static final ReentrantLock RESOLVE_LOCK = new ReentrantLock();
   public static final int INVALID_COLOR = -1;
   @Nullable
   private static final EDhDirection[] COLOR_RESOLUTION_DIRECTION_ORDER = new EDhDirection[]{
      EDhDirection.UP, null, EDhDirection.NORTH, EDhDirection.EAST, EDhDirection.WEST, EDhDirection.SOUTH, EDhDirection.DOWN
   };
   private static final int FLOWER_COLOR_SCALE = 5;
   private final IClientLevelWrapper clientLevelWrapper;
   private final class_2680 blockState;
   private final BlockStateWrapper_fabric blockStateWrapper;
   private boolean isColorResolved = false;
   private int baseColor = 0;
   private boolean needPostTinting = false;
   private int tintIndex = 0;
   private static final int MIN_SRGB_BITS = 956301312;
   private static final int MAX_SRGB_BITS = 1065353215;
   private static final float MIN_SRGB_BOUND = Float.intBitsToFloat(956301312);
   private static final float MAX_SRGB_BOUND = Float.intBitsToFloat(1065353215);
   private static final int[] linearToSrgbTable = new int[]{
      7536653,
      7995405,
      8388621,
      8847373,
      9240589,
      9699341,
      10092557,
      10551309,
      10944538,
      11796506,
      12648474,
      13500442,
      14286874,
      15138842,
      15990810,
      16842778,
      17694771,
      19398707,
      21037107,
      22741043,
      24444979,
      26148915,
      27787315,
      29491251,
      31195239,
      34537575,
      37945447,
      41287783,
      44695655,
      48037991,
      51445863,
      54788199,
      58196174,
      64946382,
      71696590,
      78446798,
      85197006,
      91947205,
      98369724,
      104530101,
      110559576,
      121766210,
      132317488,
      142278944,
      151716114,
      160694534,
      169279740,
      177537266,
      185532875,
      200540590,
      214630805,
      227869056,
      240517486,
      252510558,
      263979344,
      274923843,
      285672036,
      305660478,
      324469277,
      342229505,
      359006697,
      374997459,
      390332864,
      405012911,
      419300145,
      446038782,
      471139026,
      494797485,
      517210765,
      538575472,
      559022678,
      578617920,
      597623875,
      633340926,
      666829764,
      698418066,
      728367975,
      756876097,
      784204575,
      810353408,
      835782064,
      883426645,
      928122119,
      970261701,
      1010238603,
      1048314968,
      1084752938,
      1119683585,
      1153566616,
      1217267486,
      1276905142,
      1333134941,
      1386546704,
      1437337036,
      1485964687,
      1532560729,
      1577847331,
      1662781824,
      1742407926,
      1817512063,
      1888749592,
      1956644797,
      2021459820,
      2083718947
   };
   private static final float[] srgbToLinearTable = new float[]{
      0.0F,
      3.03527E-4F,
      6.07054E-4F,
      9.1058103E-4F,
      0.001214108F,
      0.001517635F,
      0.0018211621F,
      0.002124689F,
      0.002428216F,
      0.002731743F,
      0.00303527F,
      0.0033465356F,
      0.003676507F,
      0.004024717F,
      0.004391442F,
      0.0047769533F,
      0.005181517F,
      0.0056053917F,
      0.0060488326F,
      0.006512091F,
      0.00699541F,
      0.0074990317F,
      0.008023192F,
      0.008568125F,
      0.009134057F,
      0.009721218F,
      0.010329823F,
      0.010960094F,
      0.011612245F,
      0.012286487F,
      0.012983031F,
      0.013702081F,
      0.014443844F,
      0.015208514F,
      0.015996292F,
      0.016807375F,
      0.017641952F,
      0.018500218F,
      0.019382361F,
      0.020288562F,
      0.02121901F,
      0.022173883F,
      0.023153365F,
      0.02415763F,
      0.025186857F,
      0.026241222F,
      0.027320892F,
      0.028426038F,
      0.029556843F,
      0.03071345F,
      0.03189604F,
      0.033104774F,
      0.03433981F,
      0.035601325F,
      0.036889452F,
      0.038204376F,
      0.039546248F,
      0.04091521F,
      0.042311423F,
      0.043735042F,
      0.045186214F,
      0.046665095F,
      0.048171833F,
      0.049706575F,
      0.051269468F,
      0.052860655F,
      0.05448028F,
      0.056128494F,
      0.057805434F,
      0.05951124F,
      0.06124607F,
      0.06301003F,
      0.06480328F,
      0.06662595F,
      0.06847818F,
      0.07036011F,
      0.07227186F,
      0.07421358F,
      0.07618539F,
      0.07818743F,
      0.08021983F,
      0.082282715F,
      0.084376216F,
      0.086500466F,
      0.088655606F,
      0.09084173F,
      0.09305898F,
      0.095307484F,
      0.09758736F,
      0.09989874F,
      0.10224175F,
      0.10461649F,
      0.10702311F,
      0.10946172F,
      0.111932434F,
      0.11443538F,
      0.116970696F,
      0.11953845F,
      0.12213881F,
      0.12477186F,
      0.12743773F,
      0.13013652F,
      0.13286836F,
      0.13563336F,
      0.13843165F,
      0.14126332F,
      0.1441285F,
      0.1470273F,
      0.14995982F,
      0.15292618F,
      0.1559265F,
      0.15896086F,
      0.16202943F,
      0.16513224F,
      0.16826946F,
      0.17144115F,
      0.17464745F,
      0.17788847F,
      0.1811643F,
      0.18447503F,
      0.1878208F,
      0.19120172F,
      0.19461787F,
      0.19806935F,
      0.2015563F,
      0.20507877F,
      0.2086369F,
      0.21223079F,
      0.21586053F,
      0.21952623F,
      0.22322798F,
      0.22696589F,
      0.23074007F,
      0.23455065F,
      0.23839766F,
      0.2422812F,
      0.2462014F,
      0.25015837F,
      0.25415218F,
      0.2581829F,
      0.26225072F,
      0.26635566F,
      0.27049786F,
      0.27467737F,
      0.27889434F,
      0.2831488F,
      0.2874409F,
      0.2917707F,
      0.29613832F,
      0.30054384F,
      0.30498737F,
      0.30946895F,
      0.31398875F,
      0.31854683F,
      0.32314324F,
      0.32777813F,
      0.33245158F,
      0.33716366F,
      0.34191445F,
      0.3467041F,
      0.3515327F,
      0.35640025F,
      0.36130688F,
      0.3662527F,
      0.37123778F,
      0.37626222F,
      0.3813261F,
      0.38642952F,
      0.39157256F,
      0.3967553F,
      0.40197787F,
      0.4072403F,
      0.4125427F,
      0.41788515F,
      0.42326775F,
      0.42869055F,
      0.4341537F,
      0.43965724F,
      0.44520125F,
      0.45078585F,
      0.45641106F,
      0.46207705F,
      0.46778384F,
      0.47353154F,
      0.47932023F,
      0.48514998F,
      0.4910209F,
      0.49693304F,
      0.5028866F,
      0.50888145F,
      0.5149178F,
      0.5209957F,
      0.52711535F,
      0.5332766F,
      0.5394797F,
      0.5457247F,
      0.5520116F,
      0.5583406F,
      0.5647117F,
      0.57112503F,
      0.57758063F,
      0.5840786F,
      0.590619F,
      0.597202F,
      0.60382754F,
      0.61049575F,
      0.61720675F,
      0.62396055F,
      0.63075733F,
      0.637597F,
      0.6444799F,
      0.6514058F,
      0.65837497F,
      0.66538745F,
      0.67244333F,
      0.6795426F,
      0.68668544F,
      0.69387203F,
      0.70110214F,
      0.70837605F,
      0.7156938F,
      0.72305536F,
      0.730461F,
      0.7379107F,
      0.7454045F,
      0.75294244F,
      0.76052475F,
      0.7681514F,
      0.77582246F,
      0.78353804F,
      0.79129815F,
      0.79910296F,
      0.8069525F,
      0.8148468F,
      0.822786F,
      0.8307701F,
      0.83879924F,
      0.84687346F,
      0.8549928F,
      0.8631574F,
      0.87136734F,
      0.8796226F,
      0.8879232F,
      0.89626956F,
      0.90466136F,
      0.913099F,
      0.92158204F,
      0.93011117F,
      0.9386859F,
      0.9473069F,
      0.9559735F,
      0.9646866F,
      0.9734455F,
      0.98225087F,
      0.9911022F,
      1.0F
   };
   private static final ThreadLocal<TintWithoutLevelOverrider_fabric> TintWithoutLevelOverrideGetter = ThreadLocal.withInitial(
      TintWithoutLevelOverrider_fabric::new
   );
   private static final ThreadLocal<TintGetterOverride_fabric> TintOverrideGetter = ThreadLocal.withInitial(TintGetterOverride_fabric::new);
   private static final ThreadLocal<DhApiBlockColorOverrideEvent.EventParam> ColorOverrideEventParamGetter = ThreadLocal.withInitial(
      DhApiBlockColorOverrideEvent.EventParam::new
   );

   public ClientBlockStateColorCache_fabric(class_2680 blockState, IClientLevelWrapper clientLevelWrapper) {
      this.blockState = blockState;
      this.blockStateWrapper = BlockStateWrapper_fabric.fromBlockState(blockState, clientLevelWrapper);
      this.clientLevelWrapper = clientLevelWrapper;
      this.resolveColors();
   }

   private void resolveColors() {
      if (!this.isColorResolved) {
         try {
            RESOLVE_LOCK.lock();
            if (this.blockStateWrapper.isLiquid()) {
               this.needPostTinting = true;
               this.tintIndex = 0;
               this.baseColor = this.getParticleIconColor();
            } else {
               List<class_777> quads = null;

               for (int i = 0; i < COLOR_RESOLUTION_DIRECTION_ORDER.length; i++) {
                  EDhDirection direction = COLOR_RESOLUTION_DIRECTION_ORDER[i];

                  try {
                     quads = this.getQuadsForDirection(direction);
                  } catch (Exception var15) {
                  }

                  if (quads != null && !quads.isEmpty() && (!(this.blockState.method_26204() instanceof class_2465) || direction != EDhDirection.UP)) {
                     break;
                  }
               }

               if (quads == null || quads.isEmpty()) {
                  try {
                     quads = this.getUnculledQuads();
                  } catch (Exception var14) {
                  }
               }

               if (quads != null && !quads.isEmpty() && quads.get(0) != null) {
                  try {
                     class_777 firstQuad = quads.get(0);
                     this.needPostTinting = firstQuad.method_3360();
                     this.tintIndex = firstQuad.method_3359();
                     this.baseColor = calculateColorFromTexture(
                        firstQuad.method_35788(), ClientBlockStateColorCache$EColorMode_fabric.getColorMode(this.blockState.method_26204())
                     );
                  } catch (Exception var13) {
                     LOGGER.warn(
                        "Failed to get texture color for block ["
                           + this.blockStateWrapper.getSerialString()
                           + "] due to: ["
                           + var13.getMessage()
                           + "], falling back to particle color."
                     );
                     this.needPostTinting = false;
                     this.tintIndex = 0;
                     this.baseColor = this.getParticleIconColor();
                  }
               } else {
                  this.needPostTinting = false;
                  this.tintIndex = 0;
                  this.baseColor = this.getParticleIconColor();
               }
            }

            this.isColorResolved = true;
         } catch (Exception var16) {
            LOGGER.warn(
               "Failed to get color for block ["
                  + this.blockStateWrapper.getSerialString()
                  + "], error: ["
                  + var16.getMessage()
                  + "]. Attempting to use particle icon color...",
               var16
            );
            this.needPostTinting = true;
            this.tintIndex = 0;

            try {
               this.baseColor = this.getParticleIconColor();
            } catch (Exception var12) {
               LOGGER.warn(
                  "Failed to get particle icon color for block ["
                     + this.blockStateWrapper.getSerialString()
                     + "], error: ["
                     + var12.getMessage()
                     + "], block will render as hot pink.",
                  var12
               );
               this.baseColor = ColorUtil.HOT_PINK;
            }
         } finally {
            RESOLVE_LOCK.unlock();
         }
      }
   }

   @Nullable
   private List<class_777> getUnculledQuads() throws Exception {
      return this.getQuadsForDirection(null);
   }

   @Nullable
   private List<class_777> getQuadsForDirection(@Nullable EDhDirection direction) throws Exception {
      class_2680 effectiveBlockState = this.blockState;
      if (this.blockState.method_26204() instanceof class_2482) {
         effectiveBlockState = (class_2680)this.blockState.method_11657(class_2482.field_11501, class_2771.field_12682);
      }

      if (this.blockState.method_26204() instanceof class_2381) {
         effectiveBlockState = this.blockState.method_26204().method_9564();
      }

      return QuadWrapper_fabric.getQuadsForDirection(effectiveBlockState, direction);
   }

   private static int calculateColorFromTexture(class_1058 texture, ClientBlockStateColorCache$EColorMode_fabric colorMode) {
      int count = 0;
      int alpha = 0;
      double red = 0.0;
      double green = 0.0;
      double blue = 0.0;
      if (colorMode != ClientBlockStateColorCache$EColorMode_fabric.Chisel) {
         int textureHeight = TextureAtlasSpriteWrapper_fabric.getHeight(texture);
         int textureWidth = TextureAtlasSpriteWrapper_fabric.getWidth(texture);

         for (int v = 0; v < textureHeight; v++) {
            for (int u = 0; u < textureWidth; u++) {
               int tempColor = TextureAtlasSpriteWrapper_fabric.getPixelARGB(texture, 0, u, v);
               int r = ColorUtil.getRed(tempColor);
               int g = ColorUtil.getGreen(tempColor);
               int b = ColorUtil.getBlue(tempColor);
               int a = ColorUtil.getAlpha(tempColor);
               int scale = 1;
               if (colorMode == ClientBlockStateColorCache$EColorMode_fabric.Leaves) {
                  if (a == 0) {
                     continue;
                  }

                  a = 255;
               } else {
                  if (a == 0 && colorMode != ClientBlockStateColorCache$EColorMode_fabric.Glass) {
                     continue;
                  }

                  if (colorMode == ClientBlockStateColorCache$EColorMode_fabric.Flower && (g + 25 < b || g + 25 < r)) {
                     scale = 5;
                  }
               }

               count += scale;
               alpha += a * scale;
               red += srgbToLinearTable[r] * a * scale;
               green += srgbToLinearTable[g] * a * scale;
               blue += srgbToLinearTable[b] * a * scale;
            }
         }
      }

      int tempColor;
      if (count == 0) {
         tempColor = ColorUtil.argbToInt(0, 255, 255, 255);
      } else {
         tempColor = ColorUtil.argbToInt(
            alpha / count, linearToSrgb((float)(red / alpha)), linearToSrgb((float)(green / alpha)), linearToSrgb((float)(blue / alpha))
         );
      }

      if (tempColor == ColorUtil.argbToInt(255, 182, 0, 182)) {
         tempColor = ColorUtil.argbToInt(0, 255, 255, 255);
      }

      return tempColor;
   }

   private static int linearToSrgb(float color) {
      if (!(color > MIN_SRGB_BOUND)) {
         color = MIN_SRGB_BOUND;
      }

      if (color > MAX_SRGB_BOUND) {
         color = MAX_SRGB_BOUND;
      }

      int inputBits = Float.floatToRawIntBits(color);
      int entry = linearToSrgbTable[inputBits - 956301312 >> 20];
      int bias = entry >>> 16 << 9;
      int scale = entry & 65535;
      int t = inputBits >>> 12 & 0xFF;
      return bias + scale * t >>> 16;
   }

   private int getParticleIconColor() {
      return BlockStateWrapper_fabric.isAir(this.blockState)
         ? ColorUtil.INVISIBLE
         : calculateColorFromTexture(
            class_310.method_1551().method_1554().method_4743().method_3339(this.blockState),
            ClientBlockStateColorCache$EColorMode_fabric.getColorMode(this.blockState.method_26204())
         );
   }

   public int getColor(BiomeWrapper_fabric biomeWrapper, FullDataSourceV2 fullDataSource, DhBlockPos blockPos, boolean allowApiOverride) {
      int tintColor = -1;
      if (this.needPostTinting) {
         if (BROKEN_BLOCK_STATES.contains(this.blockState)) {
            return this.baseColor;
         }

         try {
            if (!BLOCK_STATES_THAT_NEED_LEVEL.contains(this.blockState)) {
               try {
                  TintWithoutLevelOverrider_fabric tintOverride = TintWithoutLevelOverrideGetter.get();
                  tintOverride.update(biomeWrapper, this.blockStateWrapper, fullDataSource, this.clientLevelWrapper);
                  tintColor = tintOverride.tryGetBlockTint(new DhBlockPosMutable(blockPos));
                  if (tintColor == -1) {
                     tintColor = class_310.method_1551()
                        .method_1505()
                        .method_1697(this.blockState, tintOverride, McObjectConverter_fabric.convert(blockPos), this.tintIndex);
                  }
               } catch (Exception var8) {
                  LOGGER.debug(
                     "Unable to use ["
                        + TintWithoutLevelOverrider_fabric.class.getSimpleName()
                        + "] to get the block tint for block: ["
                        + this.blockState
                        + "] and biome: ["
                        + biomeWrapper
                        + "] at pos: "
                        + blockPos
                        + ". Error: ["
                        + var8.getMessage()
                        + "]. Attempting to use backup method...",
                     var8
                  );
                  BLOCK_STATES_THAT_NEED_LEVEL.add(this.blockState);
               }
            }

            if (BLOCK_STATES_THAT_NEED_LEVEL.contains(this.blockState)) {
               TintGetterOverride_fabric tintOverride = TintOverrideGetter.get();
               tintOverride.update(biomeWrapper, this.blockStateWrapper, fullDataSource, this.clientLevelWrapper);
               tintColor = tintOverride.tryGetBlockTint(new DhBlockPosMutable(blockPos));
               if (tintColor == -1) {
                  tintColor = class_310.method_1551()
                     .method_1505()
                     .method_1697(this.blockState, tintOverride, McObjectConverter_fabric.convert(blockPos), this.tintIndex);
               }
            }
         } catch (Exception var9) {
            if (!BROKEN_BLOCK_STATES.contains(this.blockState)) {
               LOGGER.warn(
                  "Failed to get block color for block: ["
                     + this.blockState
                     + "] and biome: ["
                     + biomeWrapper
                     + "] at pos: "
                     + blockPos
                     + ". Error: ["
                     + var9.getMessage()
                     + "]. Note: future errors for this block/biome will be ignored.",
                  var9
               );
               BROKEN_BLOCK_STATES.add(this.blockState);
            }
         }
      }

      int returnColor;
      if (tintColor != -1) {
         returnColor = ColorUtil.multiplyARGBwithRGB(this.baseColor, tintColor);
      } else {
         returnColor = this.baseColor;
      }

      if (allowApiOverride && this.blockStateWrapper.allowApiColorOverride()) {
         DhApiBlockColorOverrideEvent.EventParam eventParam = ColorOverrideEventParamGetter.get();
         eventParam.update(
            this.clientLevelWrapper, fullDataSource, this.blockStateWrapper, biomeWrapper, returnColor, blockPos.getX(), blockPos.getY(), blockPos.getZ()
         );
         ApiEventInjector.INSTANCE.fireAllEvents(DhApiBlockColorOverrideEvent.class, eventParam);
         returnColor = eventParam.getColorAsInt();
      }

      return returnColor;
   }

   public static void clearCachedTints() {
      AbstractDhTintGetter_fabric.clear();
   }
}
