package net.joefoxe.hexerei.data.candle;

import java.util.ArrayList;
import java.util.List;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Component.Serializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class CandleData {
   public boolean returnToBlock;
   public int returnToBlockLastTick;
   public boolean hasCandle;
   public float x;
   public float y;
   public float z;
   public float xO;
   public float yO;
   public float zO;
   public float xTarget;
   public float yTarget;
   public float zTarget;
   public int height;
   public boolean lit;
   public float meltTimer;
   public static int meltTimerMAX = 6000;
   public int dyeColor;
   public int cooldown;
   public CandleLayer base;
   public CandleLayer herb;
   public CandleLayer glow;
   public CandleLayer swirl;
   public AbstractCandleEffect effect;
   public ArrayList<AbstractCandleEffect> effects;
   public Component customName;
   public List<String> effectParticle;
   public int baseHeight = 0;

   public CandleData(int dyeColor, boolean returnToBlock, float x, float y, float z, int height, int meltTimer, AbstractCandleEffect effect) {
      this.dyeColor = dyeColor;
      this.returnToBlock = returnToBlock;
      this.x = x;
      this.y = y;
      this.z = z;
      this.xTarget = x;
      this.yTarget = y;
      this.zTarget = z;
      this.height = height;
      this.meltTimer = meltTimer;
      this.effect = effect;
      this.effects = new ArrayList<>();
      if (effect != null) {
         this.effects.add(effect);
      }

      this.effectParticle = null;
      this.cooldown = 0;
      this.base = new CandleLayer();
      this.herb = new CandleLayer();
      this.glow = new CandleLayer();
      this.swirl = new CandleLayer();
   }

   public CandleData() {
      this(13419416, false, 0.0F, 0.0F, 0.0F, 7, meltTimerMAX, null);
   }

   public void setNotReturn(int returnToBlockLastTick) {
      this.returnToBlock = false;
      this.returnToBlockLastTick = returnToBlockLastTick;
   }

   public float getMeltingSpeedMultiplier() {
      return this.base.meltingSpeedMultiplier * this.herb.meltingSpeedMultiplier * this.glow.meltingSpeedMultiplier * this.swirl.meltingSpeedMultiplier;
   }

   public float getRadiusMultiplier() {
      return this.base.radiusMultiplier * this.herb.radiusMultiplier * this.glow.radiusMultiplier * this.swirl.radiusMultiplier;
   }

   public float getEffectAmplifierMultiplier() {
      return this.base.effectAmplifierMultiplier
         * this.herb.effectAmplifierMultiplier
         * this.glow.effectAmplifierMultiplier
         * this.swirl.effectAmplifierMultiplier;
   }

   public float getEffectCooldownMultiplier() {
      return this.base.effectCooldownMultiplier * this.herb.effectCooldownMultiplier * this.glow.effectCooldownMultiplier * this.swirl.effectCooldownMultiplier;
   }

   public float getEffectDurationMultiplier() {
      return this.base.effectDurationMultiplier * this.herb.effectDurationMultiplier * this.glow.effectDurationMultiplier * this.swirl.effectDurationMultiplier;
   }

   public boolean hasBase() {
      return this.base.layer != null;
   }

   public boolean hasHerb() {
      return this.herb.layer != null;
   }

   public boolean hasGlow() {
      return this.glow.layer != null;
   }

   public boolean hasSwirl() {
      return this.swirl.layer != null;
   }

   public void setEffect(AbstractCandleEffect effect) {
      this.effect = effect;
   }

   public void setOldPos() {
      this.xO = this.x;
      this.yO = this.y;
      this.zO = this.z;
   }

   public void move() {
      this.x = HexereiUtil.moveTo(this.x, this.xTarget, Mth.abs(this.xTarget - this.x) * 0.075F + 0.00125F);
      this.y = HexereiUtil.moveTo(this.y, this.yTarget, Mth.abs(this.yTarget - this.y) * 0.115F + 0.00125F);
      this.z = HexereiUtil.moveTo(this.z, this.zTarget, Mth.abs(this.zTarget - this.z) * 0.075F + 0.00125F);
   }

   public void moveInstantlyToTarget() {
      this.x = this.xTarget;
      this.y = this.yTarget;
      this.z = this.zTarget;
      this.setOldPos();
   }

   public AbstractCandleEffect getEffect() {
      return this.effect;
   }

   public ArrayList<AbstractCandleEffect> getEffects() {
      return this.effects;
   }

   public CompoundTag save(Provider registries) {
      return this.save(new CompoundTag(), registries, false, true);
   }

   public CompoundTag save(CompoundTag tag, Provider registries, boolean asItem) {
      return this.save(tag, registries, asItem, true);
   }

   public CompoundTag save(CompoundTag tag, Provider registries, boolean asItem, boolean withColor) {
      if (this.dyeColor != 13419416 && withColor) {
         tag.putInt("dyeColor", this.dyeColor);
      }

      if (this.height < 7) {
         tag.putInt("height", this.height);
      }

      if (!asItem) {
         tag.putFloat("meltTimer", this.meltTimer);
         tag.putBoolean("hasCandle", this.hasCandle);
         tag.putBoolean("lit", this.lit);
         tag.putBoolean("returnToBlock", this.returnToBlock);
         tag.putInt("cooldown", this.cooldown);
      }

      if (this.effect != null && !this.effect.isEmpty()) {
         tag.putString("effect", this.effect.getLocationName());
      }

      if (this.effectParticle != null && !this.effectParticle.isEmpty()) {
         CompoundTag compoundTag = new CompoundTag();

         for (int i = 0; i < this.effectParticle.size(); i++) {
            compoundTag.putString("particle" + i, this.effectParticle.get(i));
            tag.put("effectParticle", compoundTag);
         }
      }

      if (this.base.layer != null) {
         tag.put("base", this.base.save());
      }

      if (this.herb.layer != null) {
         tag.put("herb", this.herb.save());
      }

      if (this.glow.layer != null) {
         tag.put("glow", this.glow.save());
      }

      if (this.swirl.layer != null) {
         tag.put("swirl", this.swirl.save());
      }

      if (this.customName != null) {
         tag.putString("customName", Serializer.toJson(this.customName, registries));
      }

      return tag;
   }

   public void load(CompoundTag tag, Provider registries, boolean fromItem) {
      if (tag.contains("dyeColor")) {
         this.dyeColor = tag.getInt("dyeColor");
      } else {
         this.dyeColor = 13419416;
      }

      if (tag.contains("height")) {
         this.height = tag.getInt("height");
      } else {
         this.height = 7;
      }

      if (tag.contains("meltTimer")) {
         this.meltTimer = tag.getFloat("meltTimer");
      } else {
         this.meltTimer = meltTimerMAX;
      }

      if (tag.contains("hasCandle")) {
         this.hasCandle = tag.getBoolean("hasCandle");
      } else {
         this.hasCandle = fromItem;
      }

      if (tag.contains("lit")) {
         this.lit = tag.getBoolean("lit");
      } else {
         this.lit = false;
      }

      if (tag.contains("returnToBlock")) {
         this.returnToBlock = tag.getBoolean("returnToBlock");
      } else {
         this.returnToBlock = true;
      }

      if (tag.contains("effect")) {
         this.setEffect(CandleEffects.getEffect(tag.getString("effect")));
         this.cooldown = tag.getInt("cooldown");
      } else {
         this.effect = null;
         this.cooldown = 0;
      }

      if (tag.contains("effectParticle")) {
         this.effectParticle = new ArrayList<>();
         CompoundTag compoundTag = tag.getCompound("effectParticle");

         for (int i = 0; i < compoundTag.size(); i++) {
            this.effectParticle.add(compoundTag.getString("particle" + i));
         }
      } else {
         this.effectParticle = new ArrayList<>();
      }

      if (tag.contains("base")) {
         CompoundTag ct = tag.getCompound("base");
         this.base = new CandleLayer(
            ct.getFloat("meltingSpeedMultiplier"),
            ct.getFloat("radiusMultiplier"),
            ct.getFloat("effectAmplifierMultiplier"),
            ct.getFloat("effectCooldownMultiplier"),
            ct.getFloat("effectDurationMultiplier"),
            ResourceLocation.parse(ct.getString("layer")),
            ct.getBoolean("layerFromBlockLocation")
         );
      } else {
         this.base = new CandleLayer();
      }

      if (tag.contains("herb")) {
         CompoundTag ct = tag.getCompound("herb");
         this.herb = new CandleLayer(
            ct.getFloat("meltingSpeedMultiplier"),
            ct.getFloat("radiusMultiplier"),
            ct.getFloat("effectAmplifierMultiplier"),
            ct.getFloat("effectCooldownMultiplier"),
            ct.getFloat("effectDurationMultiplier"),
            ResourceLocation.parse(ct.getString("layer")),
            ct.getBoolean("layerFromBlockLocation")
         );
      } else {
         this.herb = new CandleLayer();
      }

      if (tag.contains("glow")) {
         CompoundTag ct = tag.getCompound("glow");
         this.glow = new CandleLayer(
            ct.getFloat("meltingSpeedMultiplier"),
            ct.getFloat("radiusMultiplier"),
            ct.getFloat("effectAmplifierMultiplier"),
            ct.getFloat("effectCooldownMultiplier"),
            ct.getFloat("effectDurationMultiplier"),
            ResourceLocation.parse(ct.getString("layer")),
            ct.getBoolean("layerFromBlockLocation")
         );
      } else {
         this.glow = new CandleLayer();
      }

      if (tag.contains("swirl")) {
         CompoundTag ct = tag.getCompound("swirl");
         this.swirl = new CandleLayer(
            ct.getFloat("meltingSpeedMultiplier"),
            ct.getFloat("radiusMultiplier"),
            ct.getFloat("effectAmplifierMultiplier"),
            ct.getFloat("effectCooldownMultiplier"),
            ct.getFloat("effectDurationMultiplier"),
            ResourceLocation.parse(ct.getString("layer")),
            ct.getBoolean("layerFromBlockLocation")
         );
      } else {
         this.swirl = new CandleLayer();
      }

      if (tag.contains("customName")) {
         this.customName = Serializer.fromJson(tag.getString("customName"), registries);
      } else {
         this.customName = null;
      }
   }

   public void load(CompoundTag tag, Provider registries) {
      this.load(tag, registries, false);
   }
}
