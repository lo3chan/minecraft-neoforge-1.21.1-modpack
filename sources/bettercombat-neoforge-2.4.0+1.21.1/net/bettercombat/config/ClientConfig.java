package net.bettercombat.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry.BoundedDiscrete;
import me.shedaniel.autoconfig.annotation.ConfigEntry.ColorPicker;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.Tooltip;

@Config(
   name = "client"
)
public class ClientConfig implements ConfigData {
   @Tooltip
   public boolean isHoldToAttackEnabled = true;
   @Tooltip
   public boolean isMiningWithWeaponsEnabled = true;
   @Tooltip
   public boolean isSwingThruGrassEnabled = true;
   @Tooltip
   public boolean isSwingThruGrassSmart = true;
   @Tooltip
   public boolean isAttackInsteadOfMineWhenEnemiesCloseEnabled = true;
   @Tooltip
   public boolean isHighlightCrosshairEnabled = true;
   @ColorPicker
   @Tooltip
   public int hudHighlightColor = 16711680;
   @Tooltip
   public boolean isShowingWeaponTrails = true;
   @Tooltip
   public boolean isShowingArmsInFirstPerson = false;
   @Tooltip
   public boolean isShowingOtherHandFirstPerson = true;
   @Tooltip
   public boolean isSweepingParticleEnabled = false;
   @Tooltip
   public boolean isTooltipAttackRangeEnabled = true;
   @Tooltip
   public boolean isTooltipAttackRangeReformat = true;
   @Tooltip
   @BoundedDiscrete(
      min = 0L,
      max = 100L
   )
   public int weaponSwingSoundVolume = 100;
   @Tooltip
   public boolean isDebugOBBEnabled = true;
   @Tooltip
   public String swingThruGrassBlacklist = "farmersdelight";
   @Tooltip
   public String mineWithWeaponBlacklist = "";
   @Tooltip
   public String mineWithWeaponWhitelist = "";
   @Tooltip
   public TriStateAuto firstPersonAnimations = TriStateAuto.AUTO;
   @Tooltip
   public float legAnimationThreshold = 0.0F;
}
