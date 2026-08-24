package com.seibel.distanthorizons.api.methods.events.sharedParameterObjects;

import com.seibel.distanthorizons.api.enums.rendering.EDhApiFogFalloff;
import com.seibel.distanthorizons.api.enums.rendering.EDhApiHeightFogDirection;
import com.seibel.distanthorizons.api.enums.rendering.EDhApiHeightFogMixMode;
import com.seibel.distanthorizons.api.methods.events.interfaces.IDhApiEventParam;
import java.awt.Color;

public class DhApiFogRenderParam implements IDhApiEventParam {
   protected Color fogColor;
   protected EDhApiFogFalloff farFogFalloff;
   protected float farFogStartPercent;
   protected float farFogEndPercent;
   protected float farFogMinThickness;
   protected float farFogMaxThickness;
   protected float farFogDensity;
   protected EDhApiFogFalloff heightFogFalloff;
   protected EDhApiHeightFogMixMode heightFogMixingMode;
   protected EDhApiHeightFogDirection heightFogDirection;
   protected float heightFogBaseHeight;
   protected float heightFogStartPercent;
   protected float heightFogEndPercent;
   protected float heightFogMinThickness;
   protected float heightFogMaxThickness;
   protected float heightFogDensity;

   public Color getFogColor() {
      return this.fogColor;
   }

   public EDhApiFogFalloff getFarFogFalloff() {
      return this.farFogFalloff;
   }

   public float getFarFogStartPercent() {
      return this.farFogStartPercent;
   }

   public float getFarFogEndPercent() {
      return this.farFogEndPercent;
   }

   public float getFarFogMinThickness() {
      return this.farFogMinThickness;
   }

   public float getFarFogMaxThickness() {
      return this.farFogMaxThickness;
   }

   public float getFarFogDensity() {
      return this.farFogDensity;
   }

   public EDhApiFogFalloff getHeightFogFalloff() {
      return this.heightFogFalloff;
   }

   public EDhApiHeightFogMixMode getHeightFogMixingMode() {
      return this.heightFogMixingMode;
   }

   public EDhApiHeightFogDirection getHeightFogDirection() {
      return this.heightFogDirection;
   }

   public float getHeightFogBaseHeight() {
      return this.heightFogBaseHeight;
   }

   public float getHeightFogStartPercent() {
      return this.heightFogStartPercent;
   }

   public float getHeightFogEndPercent() {
      return this.heightFogEndPercent;
   }

   public float getHeightFogMinThickness() {
      return this.heightFogMinThickness;
   }

   public float getHeightFogMaxThickness() {
      return this.heightFogMaxThickness;
   }

   public float getHeightFogDensity() {
      return this.heightFogDensity;
   }

   public DhApiFogRenderParam(DhApiFogRenderParam parent) {
      this(
         parent.fogColor,
         parent.farFogFalloff,
         parent.farFogStartPercent,
         parent.farFogEndPercent,
         parent.farFogMinThickness,
         parent.farFogEndPercent,
         parent.farFogDensity,
         parent.heightFogFalloff,
         parent.heightFogMixingMode,
         parent.heightFogDirection,
         parent.heightFogBaseHeight,
         parent.heightFogStartPercent,
         parent.heightFogEndPercent,
         parent.heightFogMinThickness,
         parent.heightFogMaxThickness,
         parent.heightFogDensity
      );
   }

   public DhApiFogRenderParam(
      Color fogColor,
      EDhApiFogFalloff farFogFalloff,
      float farFogStartPercent,
      float farFogEndPercent,
      float farFogMinThickness,
      float farFogMaxThickness,
      float farFogDensity,
      EDhApiFogFalloff heightFogFalloff,
      EDhApiHeightFogMixMode heightFogMixingMode,
      EDhApiHeightFogDirection heightFogDirection,
      float heightFogBaseHeight,
      float heightFogStartPercent,
      float heightFogEndPercent,
      float heightFogMinThickness,
      float heightFogMaxThickness,
      float heightFogDensity
   ) {
      this.fogColor = fogColor;
      this.farFogFalloff = farFogFalloff;
      this.farFogStartPercent = farFogStartPercent;
      this.farFogEndPercent = farFogEndPercent;
      this.farFogMinThickness = farFogMinThickness;
      this.farFogMaxThickness = farFogMaxThickness;
      this.farFogDensity = farFogDensity;
      this.heightFogFalloff = heightFogFalloff;
      this.heightFogMixingMode = heightFogMixingMode;
      this.heightFogDirection = heightFogDirection;
      this.heightFogBaseHeight = heightFogBaseHeight;
      this.heightFogStartPercent = heightFogStartPercent;
      this.heightFogEndPercent = heightFogEndPercent;
      this.heightFogMinThickness = heightFogMinThickness;
      this.heightFogMaxThickness = heightFogMaxThickness;
      this.heightFogDensity = heightFogDensity;
   }

   public DhApiFogRenderParam copy() {
      return new DhApiFogRenderParam(this);
   }
}
