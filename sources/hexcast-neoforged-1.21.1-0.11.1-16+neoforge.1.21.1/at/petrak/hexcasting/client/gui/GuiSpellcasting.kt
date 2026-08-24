package at.petrak.hexcasting.client.gui

import at.petrak.hexcasting.api.casting.eval.ExecutionClientView
import at.petrak.hexcasting.api.casting.eval.ResolvedPattern
import at.petrak.hexcasting.api.casting.eval.ResolvedPatternType
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.casting.math.HexAngle
import at.petrak.hexcasting.api.casting.math.HexCoord
import at.petrak.hexcasting.api.casting.math.HexDir
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.api.mod.HexConfig
import at.petrak.hexcasting.api.mod.HexTags
import at.petrak.hexcasting.api.utils.HexUtils
import at.petrak.hexcasting.client.ClientTickCounter
import at.petrak.hexcasting.client.ShiftScrollListener
import at.petrak.hexcasting.client.ktxt.ClientAccessorWrappers
import at.petrak.hexcasting.client.render.RenderLib
import at.petrak.hexcasting.client.sound.GridSoundInstance
import at.petrak.hexcasting.common.lib.HexAttributes
import at.petrak.hexcasting.common.lib.HexSounds
import at.petrak.hexcasting.common.msgs.MsgNewSpellPatternC2S
import at.petrak.hexcasting.xplat.IClientXplatAbstractions
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import java.util.ArrayList
import java.util.HashSet
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.math.MathKt
import net.minecraft.client.Minecraft
import net.minecraft.client.MouseHandler
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.player.LocalPlayer
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.client.renderer.ShaderInstance
import net.minecraft.client.resources.sounds.SimpleSoundInstance
import net.minecraft.client.resources.sounds.SoundInstance
import net.minecraft.client.sounds.SoundManager
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundSource
import net.minecraft.util.FormattedCharSequence
import net.minecraft.util.Mth
import net.minecraft.util.RandomSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.phys.Vec2
import org.joml.Matrix4f

@SourceDebugExtension(["SMAP\nGuiSpellcasting.kt\nKotlin\n*S Kotlin\n*F\n+ 1 GuiSpellcasting.kt\nat/petrak/hexcasting/client/gui/GuiSpellcasting\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,514:1\n295#2,2:515\n1563#2:518\n1634#2,3:519\n1#3:517\n*S KotlinDebug\n*F\n+ 1 GuiSpellcasting.kt\nat/petrak/hexcasting/client/gui/GuiSpellcasting\n*L\n74#1:515,2\n89#1:518\n89#1:519,3\n*E\n"])
public class GuiSpellcasting(handOpenedWith: InteractionHand,
   patterns: MutableList<ResolvedPattern>,
   cachedStack: List<CompoundTag>,
   cachedRavenmind: CompoundTag?,
   parenCount: Int
) : Screen(HexUtils.getAsTranslatedComponent("gui.hexcasting.spellcasting") as Component) {
   private final val handOpenedWith: InteractionHand
   private final var patterns: MutableList<ResolvedPattern>
   private final var cachedStack: List<CompoundTag>
   private final var cachedRavenmind: CompoundTag?
   private final var parenCount: Int
   private final var stackDescs: List<FormattedCharSequence>
   private final var parenDescs: List<FormattedCharSequence>
   private final var ravenmind: FormattedCharSequence?
   private final var drawState: at.petrak.hexcasting.client.gui.GuiSpellcasting.PatternDrawState
   private final val usedSpots: MutableSet<HexCoord>
   private final var ambianceSoundInstance: GridSoundInstance?
   private final val randSrc: RandomSource

   init {
      this.handOpenedWith = handOpenedWith;
      this.patterns = patterns;
      this.cachedStack = cachedStack;
      this.cachedRavenmind = cachedRavenmind;
      this.parenCount = parenCount;
      this.stackDescs = CollectionsKt.emptyList();
      this.parenDescs = CollectionsKt.emptyList();
      this.drawState = GuiSpellcasting.PatternDrawState.BetweenPatterns.INSTANCE;
      this.usedSpots = new HashSet<>();
      this.randSrc = SoundInstance.createUnseededRandom();

      for (ResolvedPattern var7 : this.patterns) {
         this.usedSpots.addAll(var7.component1().positions(var7.component2()));
      }

      this.calculateIotaDisplays();
   }

   public fun recvServerUpdate(info: ExecutionClientView, index: Int) {
      if (info.isStackClear()) {
         if (this.minecraft != null) {
            this.minecraft.setScreen(null);
         }
      } else {
         if (info.getResolutionType() === ResolvedPatternType.UNDONE) {
            var var10000: Any;
            label43: {
               val `$this$firstOrNull$iv`: java.lang.Iterable;
               for (Object element$iv : $this$firstOrNull$iv) {
                  if ((`element$iv` as ResolvedPattern).getType() === ResolvedPatternType.ESCAPED) {
                     var10000 = (ResolvedPattern)`element$iv`;
                     break label43;
                  }
               }

               var10000 = null;
            }

            var10000 = var10000;
            if (var10000 != null) {
               var10000.setType(ResolvedPatternType.UNDONE);
            }

            var10000 = CollectionsKt.getOrNull(this.patterns, index) as ResolvedPattern;
            if (var10000 != null) {
               var10000.setType(ResolvedPatternType.EVALUATED);
            }
         } else {
            val var17: ResolvedPattern = CollectionsKt.getOrNull(this.patterns, index) as ResolvedPattern;
            if (var17 != null) {
               var17.setType(info.getResolutionType());
            }
         }

         this.cachedStack = info.getStackDescs();
         this.cachedRavenmind = info.getRavenmind();
         this.calculateIotaDisplays();
      }
   }

   public fun calculateIotaDisplays() {
      val mc: Minecraft = Minecraft.getInstance();
      val width: Int = (int)(this.width * 0.7);
      val `$this$map$iv`: java.lang.Iterable = this.cachedStack;
      val var6: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(this.cachedStack, 10));

      for (Object item$iv$iv : $this$map$iv) {
         var6.add(IotaType.getDisplayWithMaxWidth(`item$iv$iv` as CompoundTag, width, mc.font));
      }

      this.stackDescs = CollectionsKt.asReversed(var6 as java.util.List);
      this.parenDescs = CollectionsKt.emptyList();
      var var10000: GuiSpellcasting = this;
      val var10001: FormattedCharSequence;
      if (this.cachedRavenmind != null) {
         var10001 = IotaType.getDisplayWithMaxWidth(this.cachedRavenmind, (int)((double)this.width * 0.15), mc.font);
         var10000 = this;
      } else {
         var10001 = null;
      }

      var10000.ravenmind = var10001;
   }

   protected open fun init() {
      val minecraft: Minecraft = Minecraft.getInstance();
      val soundManager: SoundManager = minecraft.getSoundManager();
      soundManager.stop(HexSounds.CASTING_AMBIANCE.getLocation(), null);
      if (minecraft.player != null) {
         this.ambianceSoundInstance = new GridSoundInstance(minecraft.player as Player);
         val var10001: GridSoundInstance = this.ambianceSoundInstance;
         soundManager.play(var10001 as SoundInstance);
      }

      this.calculateIotaDisplays();
   }

   public open fun tick() {
      val player: LocalPlayer = Minecraft.getInstance().player;
      if (player != null) {
         val heldItem: ItemStack = player.getItemInHand(this.handOpenedWith);
         if (heldItem.isEmpty() || !heldItem.is(HexTags.Items.STAVES)) {
            this.closeForReal();
         }
      }
   }

   public open fun mouseClicked(mxOut: Double, myOut: Double, pButton: Int): Boolean {
      if (super.mouseClicked(mxOut, myOut, pButton)) {
         return true;
      } else {
         val mx: Double = Mth.clamp(mxOut, 0.0, (double)this.width);
         val my: Double = Mth.clamp(myOut, 0.0, (double)this.height);
         if (this.drawState is GuiSpellcasting.PatternDrawState.BetweenPatterns) {
            val coord: HexCoord = this.pxToCoord(new Vec2((float)mx, (float)my));
            if (!this.usedSpots.contains(coord)) {
               this.drawState = new GuiSpellcasting.PatternDrawState.JustStarted(coord);
               val var10000: SoundManager = Minecraft.getInstance().getSoundManager();
               val var10003: SoundEvent = HexSounds.START_PATTERN;
               val var10004: SoundSource = SoundSource.PLAYERS;
               val var10007: RandomSource = this.randSrc;
               val var10008: GridSoundInstance = this.ambianceSoundInstance;
               val var11: Double = var10008.getX();
               val var10009: GridSoundInstance = this.ambianceSoundInstance;
               val var12: Double = var10009.getY();
               val var10010: GridSoundInstance = this.ambianceSoundInstance;
               var10000.play((new SimpleSoundInstance(var10003, var10004, 0.25F, 1.0F, var10007, var11, var12, var10010.getZ())) as SoundInstance);
               return true;
            }
         }

         return false;
      }
   }

   public open fun mouseDragged(mxOut: Double, myOut: Double, pButton: Int, pDragX: Double, pDragY: Double): Boolean {
      if (super.mouseDragged(mxOut, myOut, pButton, pDragX, pDragY)) {
         return true;
      } else {
         val mx: Double = Mth.clamp(mxOut, 0.0, (double)this.width);
         val my: Double = Mth.clamp(myOut, 0.0, (double)this.height);
         val anchor: GuiSpellcasting.PatternDrawState = this.drawState;
         val var10000: HexCoord;
         if (this.drawState == GuiSpellcasting.PatternDrawState.BetweenPatterns.INSTANCE) {
            var10000 = null;
         } else if (anchor is GuiSpellcasting.PatternDrawState.JustStarted) {
            val var36: GuiSpellcasting.PatternDrawState = this.drawState;
            var10000 = (var36 as GuiSpellcasting.PatternDrawState.JustStarted).getStart();
         } else {
            if (anchor !is GuiSpellcasting.PatternDrawState.Drawing) {
               throw new NoWhenBranchMatchedException();
            }

            val var37: GuiSpellcasting.PatternDrawState = this.drawState;
            var10000 = (var37 as GuiSpellcasting.PatternDrawState.Drawing).getCurrent();
         }

         if (var10000 == null) {
            return false;
         } else {
            val var28: Vec2 = this.coordToPx(var10000);
            val mouse: Vec2 = new Vec2((float)mx, (float)my);
            if (var28.distanceToSqr(mouse) >= this.hexSize() * this.hexSize() * 2.0 * Mth.clamp(HexConfig.client().gridSnapThreshold(), 0.5, 1.0)) {
               val delta: Vec2 = mouse.add(var28.negated());
               val newdir: Float = (float)Math.atan2((double)delta.y, (double)delta.x) / 6.2831855F;
               val playSound: Float = newdir % 6.0F;
               val snappedAngle: Float = if (newdir % 6.0F != 0.0F && Math.signum(newdir % 6.0F) != Math.signum(6.0F)) newdir % 6.0F + 6.0F else newdir % 6.0F;
               val var38: Array<HexDir> = HexDir.values();
               val var30: Int = MathKt.roundToInt(snappedAngle * (float)6) + 1;
               val var29: HexDir = var38[var30 % 6 + (6 and ((var30 % 6 xor 6) and (var30 % 6 or -(var30 % 6))) shr 31)];
               val var31: HexCoord = var10000.plus(var38[var30 % 6 + (6 and ((var30 % 6 xor 6) and (var30 % 6 or -(var30 % 6))) shr 31)]);
               var var33: Boolean = false;
               if (!this.usedSpots.contains(var31)) {
                  if (this.drawState is GuiSpellcasting.PatternDrawState.JustStarted) {
                     this.drawState = new GuiSpellcasting.PatternDrawState.Drawing(var10000, var31, new HexPattern(var29, null, 2, null));
                     var33 = true;
                  } else if (this.drawState is GuiSpellcasting.PatternDrawState.Drawing) {
                     val var39: GuiSpellcasting.PatternDrawState = this.drawState;
                     val var35: GuiSpellcasting.PatternDrawState.Drawing = var39 as GuiSpellcasting.PatternDrawState.Drawing;
                     if (var29 === (var39 as GuiSpellcasting.PatternDrawState.Drawing).getWipPattern().finalDir().rotatedBy(HexAngle.BACK)) {
                        if (var35.getWipPattern().getAngles().isEmpty()) {
                           this.drawState = new GuiSpellcasting.PatternDrawState.JustStarted(var35.getCurrent().plus(var29));
                        } else {
                           var35.setCurrent(var35.getCurrent().plus(var29));
                           var35.getWipPattern().getAngles().removeLast();
                        }

                        var33 = true;
                     } else {
                        val success: Boolean = var35.getWipPattern().tryAppendDir(var29);
                        if (success) {
                           var35.setCurrent(var31);
                        }

                        var33 = success;
                     }
                  }
               }

               if (var33) {
                  val var40: SoundManager = Minecraft.getInstance().getSoundManager();
                  val var10003: SoundEvent = HexSounds.ADD_TO_PATTERN;
                  val var10004: SoundSource = SoundSource.PLAYERS;
                  val var10006: Float = 1.0F + ((float)Math.random() - 0.5F) * 0.1F;
                  val var10007: RandomSource = this.randSrc;
                  val var10008: GridSoundInstance = this.ambianceSoundInstance;
                  val var41: Double = var10008.getX();
                  val var10009: GridSoundInstance = this.ambianceSoundInstance;
                  val var42: Double = var10009.getY();
                  val var10010: GridSoundInstance = this.ambianceSoundInstance;
                  var40.play((new SimpleSoundInstance(var10003, var10004, 0.25F, var10006, var10007, var41, var42, var10010.getZ())) as SoundInstance);
               }
            }

            return true;
         }
      }
   }

   public open fun mouseReleased(mx: Double, my: Double, pButton: Int): Boolean {
      if (super.mouseReleased(mx, my, pButton)) {
         return true;
      } else {
         val var6: GuiSpellcasting.PatternDrawState = this.drawState;
         if (!(this.drawState == GuiSpellcasting.PatternDrawState.BetweenPatterns.INSTANCE)) {
            if (var6 is GuiSpellcasting.PatternDrawState.JustStarted) {
               this.drawState = GuiSpellcasting.PatternDrawState.BetweenPatterns.INSTANCE;
               return true;
            } else if (var6 is GuiSpellcasting.PatternDrawState.Drawing) {
               val var10000: GuiSpellcasting.PatternDrawState = this.drawState;
               val start: HexCoord = (var10000 as GuiSpellcasting.PatternDrawState.Drawing).component1();
               val pat: HexPattern = (var10000 as GuiSpellcasting.PatternDrawState.Drawing).component3();
               this.drawState = GuiSpellcasting.PatternDrawState.BetweenPatterns.INSTANCE;
               this.patterns.add(new ResolvedPattern(pat, start, ResolvedPatternType.UNRESOLVED));
               this.usedSpots.addAll(pat.positions(start));
               IClientXplatAbstractions.INSTANCE.sendPacketToServer(new MsgNewSpellPatternC2S(this.handOpenedWith, pat, this.patterns));
               return true;
            } else {
               throw new NoWhenBranchMatchedException();
            }
         } else {
            return false;
         }
      }
   }

   public open fun mouseScrolled(pMouseX: Double, pMouseY: Double, pDeltaX: Double, pDeltaY: Double): Boolean {
      super.mouseScrolled(pMouseX, pMouseY, pDeltaX, pDeltaY);
      val mouseHandler: MouseHandler = Minecraft.getInstance().mouseHandler;
      if (ClientAccessorWrappers.getAccumulatedScroll(mouseHandler) != 0.0
         && Math.signum(pDeltaY) != Math.signum(ClientAccessorWrappers.getAccumulatedScroll(mouseHandler))) {
         ClientAccessorWrappers.setAccumulatedScroll(mouseHandler, 0.0);
      }

      ClientAccessorWrappers.setAccumulatedScroll(mouseHandler, ClientAccessorWrappers.getAccumulatedScroll(mouseHandler) + pDeltaY);
      val accumulation: Int = (int)ClientAccessorWrappers.getAccumulatedScroll(mouseHandler);
      if (accumulation == 0) {
         return true;
      } else {
         ClientAccessorWrappers.setAccumulatedScroll(mouseHandler, ClientAccessorWrappers.getAccumulatedScroll(mouseHandler) - (double)accumulation);
         ShiftScrollListener.onScroll(pDeltaY, false);
         return true;
      }
   }

   public open fun onClose() {
      if (this.drawState == GuiSpellcasting.PatternDrawState.BetweenPatterns.INSTANCE) {
         this.closeForReal();
      } else {
         this.drawState = GuiSpellcasting.PatternDrawState.BetweenPatterns.INSTANCE;
      }
   }

   public fun closeForReal() {
      Minecraft.getInstance().getSoundManager().stop(HexSounds.CASTING_AMBIANCE.getLocation(), null);
      super.onClose();
   }

   public open fun render(graphics: GuiGraphics, pMouseX: Int, pMouseY: Int, pPartialTick: Float) {
      super.render(graphics, pMouseX, pMouseY, pPartialTick);
      if (this.ambianceSoundInstance != null) {
         this.ambianceSoundInstance.setMousePosX((double)pMouseX / (double)this.width);
      }

      if (this.ambianceSoundInstance != null) {
         this.ambianceSoundInstance.setMousePosY((double)pMouseX / (double)this.width);
      }

      val ps: PoseStack = graphics.pose();
      val mat: Matrix4f = ps.last().pose();
      val prevShader: ShaderInstance = RenderSystem.getShader();
      RenderSystem.setShader(GameRenderer::getPositionColorShader);
      RenderSystem.disableDepthTest();
      RenderSystem.disableCull();
      val mousePos: Vec2 = new Vec2(pMouseX, pMouseY);
      val mouseCoord: HexCoord = this.pxToCoord(mousePos);
      val radius: Int = 3;
      var mc: java.util.Iterator = mouseCoord.rangeAround(3);

      while (mc.hasNext()) {
         val font: HexCoord = mc.next() as HexCoord;
         if (!this.usedSpots.contains(font)) {
            val kotlinBad: Vec2 = this.coordToPx(font);
            val addlScale: Float = Mth.clamp(
               1.0F - (kotlinBad.add(mousePos.negated()).length() - this.hexSize()) / ((float)radius * this.hexSize()), 0.0F, 1.0F
            );
            RenderLib.drawSpot(
               mat, kotlinBad, addlScale * 2.0F, Mth.lerp(addlScale, 0.4F, 0.5F), Mth.lerp(addlScale, 0.8F, 1.0F), Mth.lerp(addlScale, 0.7F, 0.9F), addlScale
            );
         }
      }

      RenderSystem.defaultBlendFunc();
      mc = this.patterns.iterator();
      var var22: Int = 0;

      while (mc.hasNext()) {
         val var25: Int = var22++;
         val var30: ResolvedPattern = mc.next() as ResolvedPattern;
         val var34: HexPattern = var30.component1();
         val time: HexCoord = var30.component2();
         val opacity: ResolvedPatternType = var30.component3();
         RenderLib.drawPatternFromPoints(
            mat,
            var34.toLines(this.hexSize(), this.coordToPx(time)),
            RenderLib.findDupIndices(HexPattern.positions$default(var34, null, 1, null)),
            true,
            opacity.getColor() or -939524096,
            opacity.getFadeColor() or -939524096,
            if (opacity.getSuccess()) 0.2F else 0.9F,
            0.2F,
            1.0F,
            (double)var25
         );
      }

      if (this.drawState !is GuiSpellcasting.PatternDrawState.BetweenPatterns) {
         val var20: java.util.List = new ArrayList();
         var var23: java.util.Set = null;
         if (this.drawState is GuiSpellcasting.PatternDrawState.JustStarted) {
            val var41: GuiSpellcasting.PatternDrawState = this.drawState;
            var20.add(this.coordToPx((var41 as GuiSpellcasting.PatternDrawState.JustStarted).getStart()));
         } else if (this.drawState is GuiSpellcasting.PatternDrawState.Drawing) {
            val var10000: GuiSpellcasting.PatternDrawState = this.drawState;
            val var26: GuiSpellcasting.PatternDrawState.Drawing = var10000 as GuiSpellcasting.PatternDrawState.Drawing;
            var23 = RenderLib.findDupIndices(
               HexPattern.positions$default((var10000 as GuiSpellcasting.PatternDrawState.Drawing).getWipPattern(), null, 1, null)
            );

            for (HexCoord pos : HexPattern.positions$default(ds.getWipPattern(), null, 1, null)) {
               var20.add(this.coordToPx(var35.plus(var26.getStart())));
            }
         }

         var20.add(mousePos);
         RenderLib.drawPatternFromPoints(mat, var20, var23, false, -10172161, -78874, 0.1F, 0.2F, 1.0F, (double)this.patterns.size());
      }

      RenderSystem.enableDepthTest();
      val var24: Font = Minecraft.getInstance().font;
      ps.pushPose();
      ps.translate(10.0, 10.0, 0.0);
      if (!this.stackDescs.isEmpty()) {
         val var28: Float = (this.stackDescs.size() + 1.0F) * 10.0F;
         RenderSystem.setShader(GameRenderer::getPositionColorShader);
         RenderSystem.enableBlend();
         val var42: GuiSpellcasting.Companion = Companion;
         GuiSpellcasting.Companion.drawBox$default(var42, ps, 0.0F, 0.0F, (float)((double)this.width * 0.7 + (double)5), var28, 0.0F, 32, null);
         ps.translate(0.0, 0.0, 1.0);
         RenderSystem.setShader(GuiSpellcasting::render$lambda$6);

         for (FormattedCharSequence desc : this.stackDescs) {
            graphics.drawString(var24, var36, 5, 7, -1);
            ps.translate(0.0, 10.0, 0.0);
         }
      }

      ps.popPose();
      if (this.ravenmind != null) {
         val var43: FormattedCharSequence = this.ravenmind;
         ps.pushPose();
         ps.translate((double)this.width * (1.0 - 0.15 * (double)1.5F) - (double)10, 10.0, 0.0);
         RenderSystem.setShader(GameRenderer::getPositionColorShader);
         RenderSystem.enableBlend();
         val var44: GuiSpellcasting.Companion = Companion;
         GuiSpellcasting.Companion.drawBox$default(var44, ps, 0.0F, 0.0F, (float)((double)this.width * 0.15 * (double)1.5F), 15.0F * 1.5F, 0.0F, 32, null);
         ps.translate(5.0, 5.0, 1.0);
         ps.scale(1.5F, 1.5F, 1.0F);
         val color: Int = 16777215 or (int)Mth.map((float)Math.sin((double)(ClientTickCounter.getTotal() * 0.2F)), -1.0F, 1.0F, 150.0F, 255.0F) shl 24;
         RenderSystem.setShader(GuiSpellcasting::render$lambda$7);
         graphics.drawString(var24, var43, 0, 0, color);
         ps.popPose();
      }

      RenderSystem.setShader(GuiSpellcasting::render$lambda$8);
   }

   public open fun isPauseScreen(): Boolean {
      return false;
   }

   public fun hexSize(): Float {
      val var10000: LocalPlayer = Minecraft.getInstance().player;
      return (float)(
         Math.sqrt((double)this.width * (double)this.height / 512.0)
            / var10000.getAttributeValue(BuiltInRegistries.ATTRIBUTE.wrapAsHolder(HexAttributes.GRID_ZOOM))
      );
   }

   public fun coordsOffset(): Vec2 {
      return new Vec2(this.width * 0.5F, this.height * 0.5F);
   }

   public fun coordToPx(coord: HexCoord): Vec2 {
      return HexUtils.coordToPx(coord, this.hexSize(), this.coordsOffset());
   }

   public fun pxToCoord(px: Vec2): HexCoord {
      return HexUtils.pxToCoord(px, this.hexSize(), this.coordsOffset());
   }

   @JvmStatic
   fun `render$lambda$6`(`$prevShader`: ShaderInstance): ShaderInstance {
      return `$prevShader`;
   }

   @JvmStatic
   fun `render$lambda$7`(`$prevShader`: ShaderInstance): ShaderInstance {
      return `$prevShader`;
   }

   @JvmStatic
   fun `render$lambda$8`(`$prevShader`: ShaderInstance): ShaderInstance {
      return `$prevShader`;
   }

   public companion object {
      public const val LHS_IOTAS_ALLOCATION: Double
      public const val RHS_IOTAS_ALLOCATION: Double

      public fun drawBox(ps: PoseStack, x: Float, y: Float, w: Float, h: Float, leftMargin: Float = 2.5F) {
         RenderSystem.setShader(GameRenderer::getPositionColorShader);
         RenderSystem.enableBlend();
         RenderLib.renderQuad(ps, x, y, w, h, 1345335344);
         RenderLib.renderQuad(ps, x + leftMargin, y + 2.5F, w - leftMargin - 2.5F, h - 5.0F, 1345335344);
      }
   }

   private sealed class PatternDrawState protected constructor() {
      public object BetweenPatterns : GuiSpellcasting.PatternDrawState()

      public data class Drawing(start: HexCoord, current: HexCoord, wipPattern: HexPattern) : GuiSpellcasting.PatternDrawState() {
         public final val start: HexCoord

         public final var current: HexCoord
            internal set

         public final val wipPattern: HexPattern

         init {
            this.start = start;
            this.current = current;
            this.wipPattern = wipPattern;
         }

         public operator fun component1(): HexCoord {
            return this.start;
         }

         public operator fun component2(): HexCoord {
            return this.current;
         }

         public operator fun component3(): HexPattern {
            return this.wipPattern;
         }

         public fun copy(start: HexCoord = this.start, current: HexCoord = this.current, wipPattern: HexPattern = this.wipPattern): at.petrak.hexcasting.client.gui.GuiSpellcasting.PatternDrawState.Drawing {
            return new GuiSpellcasting.PatternDrawState.Drawing(start, current, wipPattern);
         }

         public override fun toString(): String {
            return "Drawing(start=${this.start}, current=${this.current}, wipPattern=${this.wipPattern})";
         }

         public override fun hashCode(): Int {
            return (this.start.hashCode() * 31 + this.current.hashCode()) * 31 + this.wipPattern.hashCode();
         }

         public override operator fun equals(other: Any?): Boolean {
            if (this === other) {
               return true;
            } else if (other !is GuiSpellcasting.PatternDrawState.Drawing) {
               return false;
            } else {
               val var2: GuiSpellcasting.PatternDrawState.Drawing = other as GuiSpellcasting.PatternDrawState.Drawing;
               if (!(this.start == (other as GuiSpellcasting.PatternDrawState.Drawing).start)) {
                  return false;
               } else if (!(this.current == var2.current)) {
                  return false;
               } else {
                  return this.wipPattern == var2.wipPattern;
               }
            }
         }
      }

      public data class JustStarted(start: HexCoord) : GuiSpellcasting.PatternDrawState() {
         public final val start: HexCoord

         init {
            this.start = start;
         }

         public operator fun component1(): HexCoord {
            return this.start;
         }

         public fun copy(start: HexCoord = this.start): at.petrak.hexcasting.client.gui.GuiSpellcasting.PatternDrawState.JustStarted {
            return new GuiSpellcasting.PatternDrawState.JustStarted(start);
         }

         public override fun toString(): String {
            return "JustStarted(start=${this.start})";
         }

         public override fun hashCode(): Int {
            return this.start.hashCode();
         }

         public override operator fun equals(other: Any?): Boolean {
            if (this === other) {
               return true;
            } else if (other !is GuiSpellcasting.PatternDrawState.JustStarted) {
               return false;
            } else {
               return this.start == (other as GuiSpellcasting.PatternDrawState.JustStarted).start;
            }
         }
      }
   }
}
