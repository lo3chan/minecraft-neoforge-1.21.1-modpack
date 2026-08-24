package net.diebuddies.mixins.ocean;

import java.util.function.Function;
import net.diebuddies.compat.Iris;
import net.diebuddies.physics.liquid.ShaderInjectionLiquids;
import net.diebuddies.physics.ocean.ProgramSetOcean;
import net.diebuddies.physics.ocean.ShaderInjectionOcean;
import net.diebuddies.util.ShaderType;
import net.irisshaders.iris.shaderpack.ShaderPack;
import net.irisshaders.iris.shaderpack.include.AbsolutePackPath;
import net.irisshaders.iris.shaderpack.programs.ProgramSet;
import net.irisshaders.iris.shaderpack.programs.ProgramSource;
import net.irisshaders.iris.shaderpack.properties.ShaderProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
   value = {ProgramSet.class},
   remap = false
)
public class MixinProgramSet implements ProgramSetOcean {
   @Unique
   private ProgramSource physicsmod$oceanSource;
   @Unique
   private ProgramSource physicsmod$oceanShadowSource;
   @Unique
   private ProgramSource physicsmod$liquidsSource;
   @Unique
   private ProgramSource physicsmod$liquidsShadowSource;

   @Inject(
      at = {@At("TAIL")},
      method = {"<init>"}
   )
   public void physicsmod$createCustomShaders(
      AbsolutePackPath directory, Function<AbsolutePackPath, String> sourceProvider, ShaderProperties shaderProperties, ShaderPack pack, CallbackInfo info
   ) {
      String customOceanVertex = sourceProvider.apply(directory.resolve("physics_ocean.vsh"));
      String customOceanGeometry = sourceProvider.apply(directory.resolve("physics_ocean.gsh"));
      String customOceanControlTess = sourceProvider.apply(directory.resolve("physics_ocean.tcs"));
      String customOceanEvalTess = sourceProvider.apply(directory.resolve("physics_ocean.tes"));
      String customOceanFragment = sourceProvider.apply(directory.resolve("physics_ocean.fsh"));
      if (customOceanVertex != null) {
         Iris.preprocessOceanStage.set(null);
         this.physicsmod$oceanSource = new ProgramSource(
            "gbuffers_ocean",
            customOceanVertex,
            customOceanGeometry,
            customOceanControlTess,
            customOceanEvalTess,
            customOceanFragment,
            (ProgramSet)this,
            shaderProperties,
            null
         );
      } else {
         Iris.preprocessOceanStage.set(ShaderType.VERTEX);
         String vertexSource = sourceProvider.apply(directory.resolve("gbuffers_water.vsh"));
         Iris.preprocessOceanStage.set(ShaderType.GEOMETRY);
         String geometrySource = sourceProvider.apply(directory.resolve("gbuffers_water.gsh"));
         Iris.preprocessOceanStage.set(ShaderType.FRAGMENT);
         String fragmentSource = sourceProvider.apply(directory.resolve("gbuffers_water.fsh"));
         this.physicsmod$oceanSource = new ProgramSource(
            "gbuffers_ocean", vertexSource, geometrySource, null, null, fragmentSource, (ProgramSet)this, shaderProperties, null
         );
         if (!Iris.vertexShaderSupportsOcean.get()) {
            ((MixinProgramSource)this.physicsmod$oceanSource).setVertexSource(ShaderInjectionOcean.getVertexSource(vertexSource));
         }

         if (!Iris.fragmentShaderSupportsOcean.get()) {
            ((MixinProgramSource)this.physicsmod$oceanSource).setFragmentSource(ShaderInjectionOcean.getFragmentSource(fragmentSource, false));
         }
      }

      String customOceanShadowVertex = sourceProvider.apply(directory.resolve("physics_ocean_shadow.vsh"));
      String customOceanShadowGeometry = sourceProvider.apply(directory.resolve("physics_ocean_shadow.gsh"));
      String customOceanShadowControlTess = sourceProvider.apply(directory.resolve("physics_ocean_shadow.tcs"));
      String customOceanShadowEvalTess = sourceProvider.apply(directory.resolve("physics_ocean_shadow.tes"));
      String customOceanShadowFragment = sourceProvider.apply(directory.resolve("physics_ocean_shadow.fsh"));
      if (customOceanShadowVertex != null) {
         Iris.preprocessOceanStage.set(null);
         this.physicsmod$oceanShadowSource = new ProgramSource(
            "gbuffers_ocean_shadow",
            customOceanShadowVertex,
            customOceanShadowGeometry,
            customOceanShadowControlTess,
            customOceanShadowEvalTess,
            customOceanShadowFragment,
            (ProgramSet)this,
            shaderProperties,
            null
         );
      } else {
         Iris.preprocessOceanStage.set(ShaderType.VERTEX);
         String vertexSourcex = sourceProvider.apply(directory.resolve("shadow.vsh"));
         Iris.preprocessOceanStage.set(ShaderType.GEOMETRY);
         String geometrySourcex = sourceProvider.apply(directory.resolve("shadow.gsh"));
         Iris.preprocessOceanStage.set(ShaderType.FRAGMENT);
         String fragmentSourcex = sourceProvider.apply(directory.resolve("shadow.fsh"));
         this.physicsmod$oceanShadowSource = new ProgramSource(
            "gbuffers_ocean_shadow", vertexSourcex, geometrySourcex, null, null, fragmentSourcex, (ProgramSet)this, shaderProperties, null
         );
         if (!Iris.vertexShaderSupportsOcean.get()) {
            ((MixinProgramSource)this.physicsmod$oceanShadowSource).setVertexSource(ShaderInjectionOcean.getVertexSource(vertexSourcex));
         }

         if (!Iris.fragmentShaderSupportsOcean.get()) {
            ((MixinProgramSource)this.physicsmod$oceanShadowSource).setFragmentSource(ShaderInjectionOcean.getFragmentSource(fragmentSourcex, true));
         }
      }

      Iris.preprocessOceanStage.set(null);
      String customLiquidVertex = sourceProvider.apply(directory.resolve("physics_liquid.vsh"));
      String customLiquidGeometry = sourceProvider.apply(directory.resolve("physics_liquid.gsh"));
      String customLiquidControlTess = sourceProvider.apply(directory.resolve("physics_liquid.tcs"));
      String customLiquidEvalTess = sourceProvider.apply(directory.resolve("physics_liquid.tes"));
      String customLiquidFragment = sourceProvider.apply(directory.resolve("physics_liquid.fsh"));
      if (customLiquidVertex != null) {
         this.physicsmod$liquidsSource = new ProgramSource(
            "gbuffers_liquids",
            customLiquidVertex,
            customLiquidGeometry,
            customLiquidControlTess,
            customLiquidEvalTess,
            customLiquidFragment,
            (ProgramSet)this,
            shaderProperties,
            null
         );
      } else {
         String vertexSourcexx = sourceProvider.apply(directory.resolve("gbuffers_water.vsh"));
         String geometrySourcexx = sourceProvider.apply(directory.resolve("gbuffers_water.gsh"));
         String fragmentSourcexx = sourceProvider.apply(directory.resolve("gbuffers_water.fsh"));
         this.physicsmod$liquidsSource = new ProgramSource(
            "gbuffers_liquids", vertexSourcexx, geometrySourcexx, null, null, fragmentSourcexx, (ProgramSet)this, shaderProperties, null
         );
         ((MixinProgramSource)this.physicsmod$liquidsSource).setVertexSource(ShaderInjectionLiquids.getVertexSource(vertexSourcexx));
         ((MixinProgramSource)this.physicsmod$liquidsSource).setFragmentSource(ShaderInjectionLiquids.getFragmentSource(fragmentSourcexx));
      }

      String customLiquidShadowVertex = sourceProvider.apply(directory.resolve("physics_liquid_shadow.vsh"));
      String customLiquidShadowGeometry = sourceProvider.apply(directory.resolve("physics_liquid_shadow.gsh"));
      String customLiquidShadowControlTess = sourceProvider.apply(directory.resolve("physics_liquid_shadow.tcs"));
      String customLiquidShadowEvalTess = sourceProvider.apply(directory.resolve("physics_liquid_shadow.tes"));
      String customLiquidShadowFragment = sourceProvider.apply(directory.resolve("physics_liquid_shadow.fsh"));
      if (customLiquidShadowVertex != null) {
         this.physicsmod$liquidsShadowSource = new ProgramSource(
            "gbuffers_liquids_shadow",
            customLiquidShadowVertex,
            customLiquidShadowGeometry,
            customLiquidShadowControlTess,
            customLiquidShadowEvalTess,
            customLiquidShadowFragment,
            (ProgramSet)this,
            shaderProperties,
            null
         );
      } else {
         String vertexSourcexx = sourceProvider.apply(directory.resolve("shadow.vsh"));
         String geometrySourcexx = sourceProvider.apply(directory.resolve("shadow.gsh"));
         String fragmentSourcexx = sourceProvider.apply(directory.resolve("shadow.fsh"));
         this.physicsmod$liquidsShadowSource = new ProgramSource(
            "gbuffers_liquids_shadow", vertexSourcexx, geometrySourcexx, null, null, fragmentSourcexx, (ProgramSet)this, shaderProperties, null
         );
         ((MixinProgramSource)this.physicsmod$liquidsShadowSource).setVertexSource(ShaderInjectionLiquids.getVertexShadowSource(vertexSourcexx));
         ((MixinProgramSource)this.physicsmod$liquidsShadowSource).setFragmentSource(ShaderInjectionLiquids.getFragmentShadowSource(fragmentSourcexx));
      }
   }

   @Override
   public ProgramSource getOceanSource() {
      return this.physicsmod$oceanSource;
   }

   @Override
   public ProgramSource getOceanShadowSource() {
      return this.physicsmod$oceanShadowSource;
   }

   @Override
   public ProgramSource getLiquidsSource() {
      return this.physicsmod$liquidsSource;
   }

   @Override
   public ProgramSource getLiquidsShadowSource() {
      return this.physicsmod$liquidsShadowSource;
   }
}
