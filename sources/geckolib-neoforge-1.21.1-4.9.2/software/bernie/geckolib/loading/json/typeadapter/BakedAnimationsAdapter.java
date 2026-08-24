package software.bernie.geckolib.loading.json.typeadapter;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import it.unimi.dsi.fastutil.doubles.DoubleObjectPair;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;
import net.minecraft.util.GsonHelper;
import org.apache.commons.lang3.math.NumberUtils;
import software.bernie.geckolib.GeckoLibConstants;
import software.bernie.geckolib.animation.Animation;
import software.bernie.geckolib.animation.EasingType;
import software.bernie.geckolib.animation.keyframe.BoneAnimation;
import software.bernie.geckolib.animation.keyframe.Keyframe;
import software.bernie.geckolib.animation.keyframe.KeyframeStack;
import software.bernie.geckolib.loading.math.MathParser;
import software.bernie.geckolib.loading.math.MathValue;
import software.bernie.geckolib.loading.math.value.Constant;
import software.bernie.geckolib.loading.object.BakedAnimations;
import software.bernie.geckolib.util.CompoundException;
import software.bernie.geckolib.util.JsonUtil;

public class BakedAnimationsAdapter implements JsonDeserializer<BakedAnimations> {
   public static ConcurrentMap<Double, Constant> COMPRESSION_CACHE = null;

   public BakedAnimations deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws RuntimeException {
      JsonObject obj = json.getAsJsonObject();
      Map<String, Animation> animations = new Object2ObjectOpenHashMap(obj.size());

      for (Entry<String, JsonElement> entry : obj.entrySet()) {
         try {
            animations.put(entry.getKey(), this.bakeAnimation(entry.getKey(), entry.getValue().getAsJsonObject(), context));
         } catch (Exception var10) {
            if (var10 instanceof CompoundException compoundEx) {
               GeckoLibConstants.LOGGER.error(compoundEx.withMessage("Unable to parse animation: " + entry.getKey()).getLocalizedMessage());
            } else {
               GeckoLibConstants.LOGGER.error("Unable to parse animation: " + entry.getKey());
            }

            var10.printStackTrace();
         }
      }

      return new BakedAnimations(animations);
   }

   private Animation bakeAnimation(String name, JsonObject animationObj, JsonDeserializationContext context) throws CompoundException {
      double length = animationObj.has("animation_length") ? GsonHelper.getAsDouble(animationObj, "animation_length") * 20.0 : -1.0;
      Animation.LoopType loopType = Animation.LoopType.fromJson(animationObj.get("loop"));
      BoneAnimation[] boneAnimations = this.bakeBoneAnimations(GsonHelper.getAsJsonObject(animationObj, "bones", new JsonObject()));
      Animation.Keyframes keyframes = (Animation.Keyframes)context.deserialize(animationObj, Animation.Keyframes.class);
      if (length == -1.0) {
         length = calculateAnimationLength(boneAnimations);
      }

      return new Animation(name, length, loopType, boneAnimations, keyframes);
   }

   private BoneAnimation[] bakeBoneAnimations(JsonObject bonesObj) throws CompoundException {
      BoneAnimation[] animations = new BoneAnimation[bonesObj.size()];
      int index = 0;

      for (Entry<String, JsonElement> entry : bonesObj.entrySet()) {
         JsonObject entryObj = entry.getValue().getAsJsonObject();
         KeyframeStack<Keyframe<MathValue>> scaleFrames = this.buildKeyframeStack(getKeyframes(entryObj.get("scale")), false);
         KeyframeStack<Keyframe<MathValue>> positionFrames = this.buildKeyframeStack(getKeyframes(entryObj.get("position")), false);
         KeyframeStack<Keyframe<MathValue>> rotationFrames = this.buildKeyframeStack(getKeyframes(entryObj.get("rotation")), true);
         animations[index] = new BoneAnimation(entry.getKey(), rotationFrames, positionFrames, scaleFrames);
         index++;
      }

      return animations;
   }

   private static List<DoubleObjectPair<JsonElement>> getKeyframes(JsonElement element) {
      if (element == null) {
         return List.of();
      } else {
         if (element instanceof JsonPrimitive primitive) {
            JsonArray array = new JsonArray(3);
            array.add(primitive);
            array.add(primitive);
            array.add(primitive);
            element = array;
         }

         if (element instanceof JsonArray array) {
            return ObjectArrayList.of(new DoubleObjectPair[]{DoubleObjectPair.of(0.0, array)});
         } else if (!(element instanceof JsonObject obj)) {
            throw new JsonParseException("Invalid object type provided to getTripletObj, got: " + element);
         } else if (obj.has("vector")) {
            return ObjectArrayList.of(new DoubleObjectPair[]{DoubleObjectPair.of(0.0, obj)});
         } else {
            List<DoubleObjectPair<JsonElement>> list = new ObjectArrayList();

            for (Entry<String, JsonElement> entry : obj.entrySet()) {
               double timestamp = readTimestamp(entry.getKey());
               if (timestamp == 0.0 && !list.isEmpty()) {
                  throw new JsonParseException("Invalid keyframe data - multiple starting keyframes?" + entry.getKey());
               }

               if (entry.getValue() instanceof JsonObject entryObj && !entryObj.has("vector")) {
                  addBedrockKeyframes(timestamp, entryObj, list);
               } else {
                  list.add(DoubleObjectPair.of(timestamp, entry.getValue()));
               }
            }

            return list;
         }
      }
   }

   private static JsonArray extractBedrockKeyframe(JsonElement keyframe) {
      if (keyframe.isJsonArray()) {
         return keyframe.getAsJsonArray();
      } else if (!keyframe.isJsonObject()) {
         throw new JsonParseException("Invalid keyframe data - expected array or object, found " + keyframe);
      } else {
         JsonObject keyframeObj = keyframe.getAsJsonObject();
         if (keyframeObj.has("vector")) {
            return keyframeObj.get("vector").getAsJsonArray();
         } else {
            return keyframeObj.has("pre") ? keyframeObj.get("pre").getAsJsonArray() : keyframeObj.get("post").getAsJsonArray();
         }
      }
   }

   private static void addBedrockKeyframes(double timestamp, JsonObject keyframe, List<DoubleObjectPair<JsonElement>> keyframes) {
      boolean addedFrame = false;
      if (keyframe.has("pre")) {
         addedFrame = true;
         keyframes.add(DoubleObjectPair.of(timestamp == 0.0 ? timestamp : timestamp - 0.001, extractBedrockKeyframe(keyframe.get("pre"))));
      }

      if (keyframe.has("post")) {
         JsonArray values = extractBedrockKeyframe(keyframe.get("post"));
         if (keyframe.has("lerp_mode")) {
            JsonObject keyframeObj = new JsonObject();
            keyframeObj.add("vector", values);
            keyframeObj.add("easing", keyframe.get("lerp_mode"));
            keyframes.add(DoubleObjectPair.of(timestamp, keyframeObj));
         } else {
            keyframes.add(DoubleObjectPair.of(timestamp, values));
         }
      } else if (!addedFrame) {
         throw new JsonParseException("Invalid keyframe data - expected array, found " + keyframe);
      }
   }

   private KeyframeStack<Keyframe<MathValue>> buildKeyframeStack(List<DoubleObjectPair<JsonElement>> entries, boolean isForRotation) throws CompoundException {
      if (entries.isEmpty()) {
         return new KeyframeStack<>();
      } else {
         List<Keyframe<MathValue>> xFrames = new ObjectArrayList();
         List<Keyframe<MathValue>> yFrames = new ObjectArrayList();
         List<Keyframe<MathValue>> zFrames = new ObjectArrayList();
         MathValue xPrev = null;
         MathValue yPrev = null;
         MathValue zPrev = null;
         DoubleObjectPair<JsonElement> prevEntry = null;

         for (DoubleObjectPair<JsonElement> entry : entries) {
            JsonElement element = (JsonElement)entry.right();
            double prevTime = prevEntry != null ? prevEntry.leftDouble() : 0.0;
            double curTime = entry.leftDouble();
            double timeDelta = curTime - prevTime;
            JsonArray keyFrameVector = element instanceof JsonArray array ? array : GsonHelper.getAsJsonArray(element.getAsJsonObject(), "vector");
            MathValue rawXValue = MathParser.parseJson(keyFrameVector.get(0));
            MathValue rawYValue = MathParser.parseJson(keyFrameVector.get(1));
            MathValue rawZValue = MathParser.parseJson(keyFrameVector.get(2));
            MathValue xValue = this.compressMathValue(
               (MathValue)(isForRotation && rawXValue instanceof Constant ? new Constant(Math.toRadians(-rawXValue.get())) : rawXValue)
            );
            MathValue yValue = this.compressMathValue(
               (MathValue)(isForRotation && rawYValue instanceof Constant ? new Constant(Math.toRadians(-rawYValue.get())) : rawYValue)
            );
            MathValue zValue = this.compressMathValue(
               (MathValue)(isForRotation && rawZValue instanceof Constant ? new Constant(Math.toRadians(rawZValue.get())) : rawZValue)
            );
            JsonObject entryObj = element instanceof JsonObject obj ? obj : null;
            EasingType easingType = entryObj != null && entryObj.has("easing") ? EasingType.fromJson(entryObj.get("easing")) : EasingType.LINEAR;
            List<MathValue> easingArgs = (List<MathValue>)(entryObj != null && entryObj.has("easingArgs")
               ? JsonUtil.jsonArrayToList(GsonHelper.getAsJsonArray(entryObj, "easingArgs"), ele -> new Constant(ele.getAsDouble()))
               : new ObjectArrayList());
            xFrames.add(new Keyframe<>(timeDelta * 20.0, prevEntry == null ? xValue : xPrev, xValue, easingType, easingArgs));
            yFrames.add(new Keyframe<>(timeDelta * 20.0, prevEntry == null ? yValue : yPrev, yValue, easingType, easingArgs));
            zFrames.add(new Keyframe<>(timeDelta * 20.0, prevEntry == null ? zValue : zPrev, zValue, easingType, easingArgs));
            xPrev = xValue;
            yPrev = yValue;
            zPrev = zValue;
            prevEntry = entry;
         }

         return new KeyframeStack<>(this.addSplineArgs(xFrames), this.addSplineArgs(yFrames), this.addSplineArgs(zFrames));
      }
   }

   private List<Keyframe<MathValue>> addSplineArgs(List<Keyframe<MathValue>> frames) {
      if (frames.size() == 1) {
         Keyframe<MathValue> frame = (Keyframe<MathValue>)frames.getFirst();
         if (frame.easingType() != EasingType.LINEAR) {
            frames.set(0, new Keyframe<>(frame.length(), frame.startValue(), frame.endValue()));
            return frames;
         }
      }

      for (int i = 0; i < frames.size(); i++) {
         Keyframe<MathValue> frame = frames.get(i);
         if (frame.easingType() == EasingType.CATMULLROM) {
            frames.set(
               i,
               new Keyframe<>(
                  frame.length(),
                  frame.startValue(),
                  frame.endValue(),
                  frame.easingType(),
                  ObjectArrayList.of(
                     new MathValue[]{
                        i == 0 ? frame.startValue() : frames.get(i - 1).endValue(), i + 1 >= frames.size() ? frame.endValue() : frames.get(i + 1).endValue()
                     }
                  )
               )
            );
         }
      }

      return frames;
   }

   private MathValue compressMathValue(MathValue input) {
      return COMPRESSION_CACHE != null && !input.isMutable() ? COMPRESSION_CACHE.computeIfAbsent(input.get(), Constant::new) : input;
   }

   private static double calculateAnimationLength(BoneAnimation[] boneAnimations) {
      double length = 0.0;

      for (BoneAnimation animation : boneAnimations) {
         length = Math.max(length, animation.rotationKeyFrames().getLastKeyframeTime());
         length = Math.max(length, animation.positionKeyFrames().getLastKeyframeTime());
         length = Math.max(length, animation.scaleKeyFrames().getLastKeyframeTime());
      }

      return length == 0.0 ? 1.7976931348623157E308 : length;
   }

   private static double readTimestamp(String timestamp) {
      return NumberUtils.isCreatable(timestamp) ? Double.parseDouble(timestamp) : 0.0;
   }
}
