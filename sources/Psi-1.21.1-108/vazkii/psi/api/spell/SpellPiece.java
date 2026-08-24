package vazkii.psi.api.spell;

import com.google.common.base.CaseFormat;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderStateShard.CullStateShard;
import net.minecraft.client.renderer.RenderStateShard.LightmapStateShard;
import net.minecraft.client.renderer.RenderStateShard.ShaderStateShard;
import net.minecraft.client.renderer.RenderStateShard.TextureStateShard;
import net.minecraft.client.renderer.RenderStateShard.TransparencyStateShard;
import net.minecraft.client.renderer.RenderType.CompositeState;
import net.minecraft.client.resources.model.Material;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import org.joml.Matrix4f;
import vazkii.psi.api.ClientPsiAPI;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.internal.PsiRenderHelper;
import vazkii.psi.api.internal.TooltipHelper;

public abstract class SpellPiece {
   public static final Spell dummySpell = new Spell();
   private static final String TAG_KEY_LEGACY = "spellKey";
   private static final String TAG_KEY = "key";
   private static final String TAG_PARAMS = "params";
   private static final String TAG_COMMENT = "comment";
   private static final String PSI_PREFIX = "psi.spellparam.";
   public static final Codec<SpellPiece> CODEC = CompoundTag.CODEC.xmap(t -> createFromNBT(dummySpell, t), p -> {
      CompoundTag tag = new CompoundTag();
      p.writeToNBT(tag);
      return tag;
   });
   public static final StreamCodec<ByteBuf, SpellPiece> STREAM_CODEC = ByteBufCodecs.COMPOUND_TAG.map(t -> createFromNBT(dummySpell, t), p -> {
      CompoundTag tag = new CompoundTag();
      p.writeToNBT(tag);
      return tag;
   });
   @OnlyIn(Dist.CLIENT)
   private static RenderType layer;
   public final ResourceLocation registryKey;
   public final Spell spell;
   public final Map<String, SpellParam<?>> params = new LinkedHashMap<>();
   public final Map<SpellParam<?>, SpellParam.Side> paramSides = new LinkedHashMap<>();
   private final Map<EnumSpellStat, StatLabel> statLabels = new HashMap<>();
   public boolean isInGrid = false;
   public int x;
   public int y;
   public String comment;

   public SpellPiece(Spell spell) {
      this.spell = spell;
      this.registryKey = PsiAPI.SPELL_PIECE_REGISTRY.getKey(this.getClass());
      this.initParams();
   }

   @OnlyIn(Dist.CLIENT)
   public static RenderType getLayer() {
      if (layer == null) {
         CompositeState glState = CompositeState.builder()
            .setShaderState(new ShaderStateShard(GameRenderer::getPositionColorTexLightmapShader))
            .setTextureState(new TextureStateShard(InventoryMenu.BLOCK_ATLAS, false, false))
            .setLightmapState(new LightmapStateShard(true))
            .setTransparencyState(new TransparencyStateShard("translucent_transparency", () -> {
               RenderSystem.enableBlend();
               RenderSystem.blendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ONE_MINUS_SRC_ALPHA);
            }, () -> {
               RenderSystem.disableBlend();
               RenderSystem.defaultBlendFunc();
            }))
            .setCullState(new CullStateShard(false))
            .createCompositeState(false);
         layer = RenderType.create(InventoryMenu.BLOCK_ATLAS.toString(), DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, Mode.QUADS, 64, glState);
      }

      return layer;
   }

   public static SpellPiece createFromNBT(Spell spell, CompoundTag cmp) {
      String key;
      if (cmp.contains("spellKey")) {
         key = cmp.getString("spellKey");
      } else {
         key = cmp.getString("key");
      }

      if (key.startsWith("_")) {
         key = "psi.spellparam." + key.substring(1);
      }

      try {
         key = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, key);
      } catch (Exception var8) {
      }

      boolean exists = false;
      ResourceLocation rl = ResourceLocation.parse(key);
      if (PsiAPI.SPELL_PIECE_REGISTRY.containsKey(rl)) {
         exists = true;
      } else {
         for (String namespace : (Set)PsiAPI.SPELL_PIECE_REGISTRY.keySet().stream().map(ResourceLocation::getNamespace).collect(Collectors.toSet())) {
            rl = ResourceLocation.fromNamespaceAndPath(namespace, key);
            if (PsiAPI.SPELL_PIECE_REGISTRY.containsKey(rl)) {
               exists = true;
               break;
            }
         }
      }

      if (exists) {
         Class<? extends SpellPiece> clazz = (Class<? extends SpellPiece>)PsiAPI.SPELL_PIECE_REGISTRY.get(rl);
         SpellPiece p = create(clazz, spell);
         p.readFromNBT(cmp);
         return p;
      } else {
         return null;
      }
   }

   public static SpellPiece create(Class<? extends SpellPiece> clazz, Spell spell) {
      try {
         return clazz.getConstructor(Spell.class).newInstance(spell);
      } catch (Exception var3) {
         throw new RuntimeException(var3);
      }
   }

   public static SpellPiece create(ResourceLocation location) {
      return PsiAPI.SPELL_PIECE_REGISTRY.getOptional(location).map(clazz -> create((Class<? extends SpellPiece>)clazz, dummySpell)).orElse(null);
   }

   public void initParams() {
   }

   public abstract EnumPieceType getPieceType();

   public abstract Class<?> getEvaluationType();

   public abstract Object evaluate() throws SpellCompilationException;

   public abstract Object execute(SpellContext var1) throws SpellRuntimeException;

   public Component getEvaluationTypeString() {
      Class<?> evalType = this.getEvaluationType();
      String evalStr = evalType == null ? "null" : CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, evalType.getSimpleName());
      MutableComponent s = Component.translatable("psi.datatype." + evalStr);
      if (this.getPieceType() == EnumPieceType.CONSTANT) {
         s.append(" ").append(Component.translatable("psimisc.constant"));
      }

      return s;
   }

   public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
   }

   public void addParam(SpellParam<?> param) {
      this.params.put(param.name, param);
      this.paramSides.put(param, SpellParam.Side.OFF);
   }

   public boolean isInputSide(SpellParam.Side side) {
      return this.paramSides.containsValue(side);
   }

   public <T> T getParamValueOrDefault(SpellContext context, SpellParam<T> param, T def) {
      try {
         T v = this.getParamValue(context, param);
         return v == null ? def : v;
      } catch (SpellRuntimeException var5) {
         return def;
      }
   }

   public <T> T getNotNullParamValue(SpellContext context, SpellParam<T> param) throws SpellRuntimeException {
      T v = this.getParamValue(context, param);
      if (v == null) {
         throw new SpellRuntimeException("psi.spellerror.nulltarget");
      } else {
         return v;
      }
   }

   public <T> T getParamValue(SpellContext context, SpellParam<T> param) throws SpellRuntimeException {
      T returnValue = (T)this.getRawParamValue(context, param);
      if (!(returnValue instanceof Number number && (Double.isNaN(number.doubleValue()) || Double.isInfinite(number.doubleValue())))) {
         return returnValue;
      } else {
         throw new SpellRuntimeException("psi.spellerror.nan", Component.translatable(param.name));
      }
   }

   public Object getRawParamValue(SpellContext context, SpellParam<?> param) {
      SpellParam.Side side = this.paramSides.get(param);
      if (!side.isEnabled()) {
         return null;
      } else {
         try {
            SpellPiece piece = this.spell.grid.getPieceAtSideWithRedirections(this.x, this.y, side);
            return piece != null && param.canAccept(piece) ? context.evaluatedObjects[piece.x][piece.y] : null;
         } catch (SpellCompilationException var5) {
            return null;
         }
      }
   }

   public <T> T getParamEvaluationeOrDefault(SpellParam<T> param, T def) throws SpellCompilationException {
      T v = this.getParamEvaluation(param);
      return v == null ? def : v;
   }

   public <T> T getNotNullParamEvaluation(SpellParam<T> param) throws SpellCompilationException {
      T v = this.getParamEvaluation(param);
      if (v == null) {
         throw new SpellCompilationException("psi.spellerror.nullparam", this.x, this.y);
      } else {
         return v;
      }
   }

   public <T> T getParamEvaluation(SpellParam<?> param) throws SpellCompilationException {
      SpellParam.Side side = this.paramSides.get(param);
      if (!side.isEnabled()) {
         return null;
      } else {
         SpellPiece piece = this.spell.grid.getPieceAtSideWithRedirections(this.x, this.y, side);
         return (T)(piece != null && param.canAccept(piece) ? piece.evaluate() : null);
      }
   }

   public String getUnlocalizedName() {
      return this.registryKey.getNamespace() + ".spellpiece." + this.registryKey.getPath();
   }

   public String getSortingName() {
      return Component.translatable(this.getUnlocalizedName()).getString();
   }

   public String getUnlocalizedDesc() {
      return this.registryKey.getNamespace() + ".spellpiece." + this.registryKey.getPath() + ".desc";
   }

   public void setStatLabel(EnumSpellStat type, StatLabel descriptor) {
      this.statLabels.put(type, descriptor);
   }

   @OnlyIn(Dist.CLIENT)
   public void draw(PoseStack pPoseStack, MultiBufferSource buffers, int light) {
      pPoseStack.pushPose();
      this.drawBackground(pPoseStack, buffers, light);
      pPoseStack.translate(0.0F, 0.0F, 0.1F);
      this.drawAdditional(pPoseStack, buffers, light);
      if (this.isInGrid) {
         pPoseStack.translate(0.0F, 0.0F, 0.1F);
         this.drawParams(pPoseStack, buffers, light);
         pPoseStack.translate(0.0F, 0.0F, 0.1F);
         this.drawComment(pPoseStack, buffers, light);
      }

      pPoseStack.popPose();
   }

   @OnlyIn(Dist.CLIENT)
   public void drawBackground(PoseStack pPoseStack, MultiBufferSource buffers, int light) {
      Material material = (Material)ClientPsiAPI.SPELL_PIECE_MATERIAL_REGISTRY.get(this.registryKey);
      if (material != null) {
         VertexConsumer buffer = material.buffer(buffers, ignored -> getLayer());
         Matrix4f mat = pPoseStack.last().pose();
         buffer.addVertex(mat, 0.0F, 16.0F, 0.0F).setColor(1.0F, 1.0F, 1.0F, 1.0F);
         buffer.setUv(0.0F, 1.0F).setLight(light);
         buffer.addVertex(mat, 16.0F, 16.0F, 0.0F).setColor(1.0F, 1.0F, 1.0F, 1.0F);
         buffer.setUv(1.0F, 1.0F).setLight(light);
         buffer.addVertex(mat, 16.0F, 0.0F, 0.0F).setColor(1.0F, 1.0F, 1.0F, 1.0F);
         buffer.setUv(1.0F, 0.0F).setLight(light);
         buffer.addVertex(mat, 0.0F, 0.0F, 0.0F).setColor(1.0F, 1.0F, 1.0F, 1.0F);
         buffer.setUv(0.0F, 0.0F).setLight(light);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void drawAdditional(PoseStack pPoseStack, MultiBufferSource buffers, int light) {
   }

   @OnlyIn(Dist.CLIENT)
   public void drawComment(PoseStack pPoseStack, MultiBufferSource buffers, int light) {
      if (this.comment != null && !this.comment.isEmpty()) {
         VertexConsumer buffer = buffers.getBuffer(PsiAPI.internalHandler.getProgrammerLayer());
         float wh = 6.0F;
         float minU = 0.5859375F;
         float minV = 0.71875F;
         float maxU = (150.0F + wh) / 256.0F;
         float maxV = (184.0F + wh) / 256.0F;
         Matrix4f mat = pPoseStack.last().pose();
         buffer.addVertex(mat, -2.0F, 4.0F, 0.0F).setColor(1.0F, 1.0F, 1.0F, 1.0F).setUv(minU, maxV).setLight(light);
         buffer.addVertex(mat, 4.0F, 4.0F, 0.0F).setColor(1.0F, 1.0F, 1.0F, 1.0F).setUv(maxU, maxV).setLight(light);
         buffer.addVertex(mat, 4.0F, -2.0F, 0.0F).setColor(1.0F, 1.0F, 1.0F, 1.0F).setUv(maxU, minV).setLight(light);
         buffer.addVertex(mat, -2.0F, -2.0F, 0.0F).setColor(1.0F, 1.0F, 1.0F, 1.0F).setUv(minU, minV).setLight(light);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void drawParams(PoseStack pPoseStack, MultiBufferSource buffers, int light) {
      VertexConsumer buffer = buffers.getBuffer(PsiAPI.internalHandler.getProgrammerLayer());

      for (SpellParam<?> param : this.paramSides.keySet()) {
         this.drawParam(pPoseStack, buffer, light, param);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void drawParam(PoseStack pPoseStack, VertexConsumer buffer, int light, SpellParam<?> param) {
      SpellParam.Side side = this.paramSides.get(param);
      if (side.isEnabled() && param.getArrowType() != SpellParam.ArrowType.NONE) {
         int index = this.getParamArrowIndex(param);
         int count = this.getParamArrowCount(side);
         SpellPiece neighbour = this.spell.grid.getPieceAtSideSafely(this.x, this.y, side);
         if (neighbour != null) {
            int nbcount = neighbour.getParamArrowCount(side.getOpposite());
            if (side.asInt() > side.getOpposite().asInt()) {
               index += nbcount;
            }

            count += nbcount;
         }

         float percent = 0.5F;
         if (count > 1) {
            percent = (float)index / (count - 1);
         }

         this.drawParam(pPoseStack, buffer, light, side, param.color, param.getArrowType(), percent);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void drawParam(PoseStack pPoseStack, VertexConsumer buffer, int light, SpellParam.Side side, int color, SpellParam.ArrowType arrowType, float percent) {
      if (arrowType != SpellParam.ArrowType.NONE) {
         float minX = 4.0F + side.minx * percent + side.maxx * (1.0F - percent);
         float minY = 4.0F + side.miny * percent + side.maxy * (1.0F - percent);
         float maxX = minX + 8.0F;
         float maxY = minY + 8.0F;
         if (arrowType == SpellParam.ArrowType.OUT) {
            side = side.getOpposite();
         }

         float wh = 8.0F;
         float minU = side.u / 256.0F;
         float minV = side.v / 256.0F;
         float maxU = (side.u + wh) / 256.0F;
         float maxV = (side.v + wh) / 256.0F;
         int r = PsiRenderHelper.r(color);
         int g = PsiRenderHelper.g(color);
         int b = PsiRenderHelper.b(color);
         int a = 255;
         Matrix4f mat = pPoseStack.last().pose();
         buffer.addVertex(mat, minX, maxY, 0.0F).setColor(r, g, b, a).setUv(minU, maxV).setLight(light);
         buffer.addVertex(mat, maxX, maxY, 0.0F).setColor(r, g, b, a).setUv(maxU, maxV).setLight(light);
         buffer.addVertex(mat, maxX, minY, 0.0F).setColor(r, g, b, a).setUv(maxU, minV).setLight(light);
         buffer.addVertex(mat, minX, minY, 0.0F).setColor(r, g, b, a).setUv(minU, minV).setLight(light);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public int getParamArrowCount(SpellParam.Side side) {
      int count = 0;

      for (SpellParam<?> p : this.paramSides.keySet()) {
         if (p.getArrowType() != SpellParam.ArrowType.NONE && this.paramSides.get(p) == side) {
            count++;
         }
      }

      return count;
   }

   @OnlyIn(Dist.CLIENT)
   public int getParamArrowIndex(SpellParam<?> param) {
      SpellParam.Side side = this.paramSides.get(param);
      int count = 0;

      for (SpellParam<?> p : this.paramSides.keySet()) {
         if (p == param) {
            return count;
         }

         if (p.getArrowType() != SpellParam.ArrowType.NONE && this.paramSides.get(p) == side) {
            count++;
         }
      }

      return 0;
   }

   @OnlyIn(Dist.CLIENT)
   public void drawTooltip(GuiGraphics graphics, int tooltipX, int tooltipY, List<Component> tooltip, Screen screen) {
      PsiAPI.internalHandler.renderTooltip(graphics, tooltipX, tooltipY, tooltip, 1347420415, -267386864, screen.width, screen.height);
   }

   @OnlyIn(Dist.CLIENT)
   public void drawCommentText(GuiGraphics graphics, int tooltipX, int tooltipY, List<Component> commentText, Screen screen) {
      PsiAPI.internalHandler
         .renderTooltip(graphics, tooltipX, tooltipY - 9 - commentText.size() * 10, commentText, 1342218240, -268427776, screen.width, screen.height);
   }

   @OnlyIn(Dist.CLIENT)
   public void getTooltip(List<Component> tooltip) {
      tooltip.add(Component.translatable(this.getUnlocalizedName()));
      tooltip.add(Component.translatable(this.getUnlocalizedDesc()).withStyle(ChatFormatting.GRAY));
      TooltipHelper.tooltipIfShift(tooltip, () -> this.addToTooltipAfterShift(tooltip));
      if (!this.statLabels.isEmpty()) {
         TooltipHelper.tooltipIfCtrl(tooltip, () -> this.addToTooltipAfterCtrl(tooltip));
      }

      String addon = this.registryKey.getNamespace();
      if (!addon.equals("psi") && ModList.get().getModContainerById(addon).isPresent()) {
         tooltip.add(
            Component.translatable("psimisc.provider_mod", new Object[]{((ModContainer)ModList.get().getModContainerById(addon).get()).getNamespace()})
         );
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void addToTooltipAfterShift(List<Component> tooltip) {
      tooltip.add(Component.literal(""));
      MutableComponent eval = this.getEvaluationTypeString().plainCopy().withStyle(ChatFormatting.GOLD);
      tooltip.add(Component.literal("Output ").append(eval));

      for (SpellParam<?> param : this.paramSides.keySet()) {
         Component pName = Component.translatable(param.name).withStyle(ChatFormatting.YELLOW);
         Component pEval = Component.literal(" [").append(param.getRequiredTypeString()).append("]").withStyle(ChatFormatting.YELLOW);
         tooltip.add(Component.literal(param.canDisable ? "[Input] " : " Input  ").append(pName).append(pEval));
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void addToTooltipAfterCtrl(List<Component> tooltip) {
      tooltip.add(Component.literal(""));
      this.statLabels.forEach((type, stat) -> {
         tooltip.add(Component.translatable(type.getName()).append(":"));
         tooltip.add(Component.literal(" " + stat.toString()).withStyle(ChatFormatting.YELLOW));
      });
   }

   @OnlyIn(Dist.CLIENT)
   public boolean interceptKeystrokes() {
      return false;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean onCharTyped(char character, int keyCode, boolean doit) {
      return false;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean onKeyPressed(int keyCode, int scanCode, boolean doit) {
      return false;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean hasConfig() {
      return !this.params.isEmpty();
   }

   @OnlyIn(Dist.CLIENT)
   public void getShownPieces(List<SpellPiece> pieces) {
      pieces.add(this);
   }

   public SpellPiece copy() {
      CompoundTag cmp = new CompoundTag();
      this.writeToNBT(cmp);
      return createFromNBT(this.spell, cmp);
   }

   public SpellPiece copyFromSpell(Spell spell) {
      CompoundTag cmp = new CompoundTag();
      this.writeToNBT(cmp);
      return createFromNBT(spell, cmp);
   }

   public void readFromNBT(CompoundTag cmp) {
      CompoundTag paramCmp = cmp.getCompound("params");

      for (String s : this.params.keySet()) {
         SpellParam<?> param = this.params.get(s);
         String key = s;
         if (paramCmp.contains(s)) {
            this.paramSides.put(param, SpellParam.Side.fromInt(paramCmp.getInt(s)));
         } else {
            if (s.startsWith("psi.spellparam.")) {
               key = "_" + s.substring("psi.spellparam.".length());
            }

            this.paramSides.put(param, SpellParam.Side.fromInt(paramCmp.getInt(key)));
         }
      }

      this.comment = cmp.getString("comment");
   }

   public void writeToNBT(CompoundTag cmp) {
      if (this.comment == null) {
         this.comment = "";
      }

      cmp.putString("key", this.registryKey.toString().replaceAll("^psi.spellparam.", "_"));
      int paramCount = 0;
      CompoundTag paramCmp = new CompoundTag();

      for (String s : this.params.keySet()) {
         SpellParam<?> param = this.params.get(s);
         SpellParam.Side side = this.paramSides.get(param);
         paramCmp.putInt(s.replaceAll("^psi.spellparam.", "_"), side.asInt());
         paramCount++;
      }

      if (paramCount > 0) {
         cmp.put("params", paramCmp);
      }

      if (!this.comment.isEmpty()) {
         cmp.putString("comment", this.comment);
      }
   }
}
