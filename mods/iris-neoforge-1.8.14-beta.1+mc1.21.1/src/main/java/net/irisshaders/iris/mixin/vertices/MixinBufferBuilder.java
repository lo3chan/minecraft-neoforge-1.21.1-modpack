/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.BufferBuilder
 *  com.mojang.blaze3d.vertex.ByteBufferBuilder
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.mojang.blaze3d.vertex.VertexFormat
 *  com.mojang.blaze3d.vertex.VertexFormat$Mode
 *  com.mojang.blaze3d.vertex.VertexFormatElement
 *  org.joml.Vector3f
 *  org.lwjgl.system.MemoryUtil
 *  org.spongepowered.asm.mixin.Dynamic
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.ModifyVariable
 *  org.spongepowered.asm.mixin.injection.Redirect
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package net.irisshaders.iris.mixin.vertices;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import java.util.Arrays;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.uniforms.CapturedRenderingState;
import net.irisshaders.iris.vertices.BlockSensitiveBufferBuilder;
import net.irisshaders.iris.vertices.BufferBuilderPolygonView;
import net.irisshaders.iris.vertices.ExtendedDataHelper;
import net.irisshaders.iris.vertices.ImmediateState;
import net.irisshaders.iris.vertices.IrisVertexFormats;
import net.irisshaders.iris.vertices.MojangBufferAccessor;
import net.irisshaders.iris.vertices.NormI8;
import net.irisshaders.iris.vertices.NormalHelper;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={BufferBuilder.class})
public abstract class MixinBufferBuilder
implements VertexConsumer,
BlockSensitiveBufferBuilder {
    @Unique
    private final BufferBuilderPolygonView polygon = new BufferBuilderPolygonView();
    @Unique
    private final Vector3f normal = new Vector3f();
    @Unique
    private final long[] vertexOffsets = new long[4];
    @Shadow
    private int elementsToFill;
    @Unique
    private boolean skipEndVertexOnce;
    @Shadow
    @Final
    private VertexFormat.Mode mode;
    @Shadow
    @Final
    private VertexFormat format;
    @Shadow
    @Final
    private int[] offsetsByElement;
    @Shadow
    @Final
    private boolean fastFormat;
    @Shadow
    private long vertexPointer;
    @Shadow
    private int vertices;
    @Unique
    private boolean extending;
    @Unique
    private boolean injectNormalAndUV1;
    @Unique
    private int iris$vertexCount;
    @Unique
    private int currentBlock = -1;
    @Unique
    private byte currentRenderType = (byte)-1;
    @Unique
    private int currentLocalPosX;
    @Unique
    private int currentLocalPosY;
    @Unique
    private int currentLocalPosZ;
    @Shadow
    @Final
    private ByteBufferBuilder buffer;
    @Unique
    private int lastBlock = -1;

    @Shadow
    public abstract VertexConsumer setNormal(float var1, float var2, float var3);

    @Shadow
    protected abstract long beginElement(VertexFormatElement var1);

    @ModifyVariable(method={"<init>"}, at=@At(value="FIELD", target="Lcom/mojang/blaze3d/vertex/VertexFormatElement;POSITION:Lcom/mojang/blaze3d/vertex/VertexFormatElement;", ordinal=0), argsOnly=true)
    private VertexFormat iris$extendFormat(VertexFormat format) {
        boolean iris$isTerrain = false;
        this.injectNormalAndUV1 = false;
        if (ImmediateState.skipExtension.get().booleanValue() || !Iris.isPackInUseQuick()) {
            return format;
        }
        if (format == DefaultVertexFormat.BLOCK || format == IrisVertexFormats.TERRAIN) {
            this.extending = true;
            iris$isTerrain = true;
            this.injectNormalAndUV1 = false;
            return IrisVertexFormats.TERRAIN;
        }
        if (format == DefaultVertexFormat.NEW_ENTITY || format == IrisVertexFormats.ENTITY) {
            this.extending = true;
            iris$isTerrain = false;
            this.injectNormalAndUV1 = false;
            return IrisVertexFormats.ENTITY;
        }
        if (format == DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP || format == IrisVertexFormats.GLYPH) {
            this.extending = true;
            iris$isTerrain = false;
            this.injectNormalAndUV1 = true;
            return IrisVertexFormats.GLYPH;
        }
        return format;
    }

    @Redirect(method={"addVertex(FFFIFFIIFFF)V"}, at=@At(value="FIELD", target="Lcom/mojang/blaze3d/vertex/BufferBuilder;fastFormat:Z"))
    private boolean fastFormat(BufferBuilder instance) {
        return this.fastFormat && !this.extending;
    }

    @Inject(method={"addVertex(FFF)Lcom/mojang/blaze3d/vertex/VertexConsumer;"}, at={@At(value="RETURN")})
    private void injectMidBlock(float x, float y, float z, CallbackInfoReturnable<VertexConsumer> cir) {
        if ((this.elementsToFill & IrisVertexFormats.MID_BLOCK_ELEMENT.mask()) != 0) {
            long midBlockOffset = this.beginElement(IrisVertexFormats.MID_BLOCK_ELEMENT);
            MemoryUtil.memPutInt((long)midBlockOffset, (int)ExtendedDataHelper.computeMidBlock(x, y, z, this.currentLocalPosX, this.currentLocalPosY, this.currentLocalPosZ));
            byte currentBlockEmission = -1;
            MemoryUtil.memPutByte((long)(midBlockOffset + 3L), (byte)currentBlockEmission);
        }
        if ((this.elementsToFill & IrisVertexFormats.ENTITY_ELEMENT.mask()) != 0) {
            offset = this.beginElement(IrisVertexFormats.ENTITY_ELEMENT);
            MemoryUtil.memPutShort((long)offset, (short)((short)this.currentBlock));
            MemoryUtil.memPutShort((long)(offset + 2L), (short)this.currentRenderType);
        } else if ((this.elementsToFill & IrisVertexFormats.ENTITY_ID_ELEMENT.mask()) != 0) {
            offset = this.beginElement(IrisVertexFormats.ENTITY_ID_ELEMENT);
            MemoryUtil.memPutShort((long)offset, (short)((short)CapturedRenderingState.INSTANCE.getCurrentRenderedEntity()));
            MemoryUtil.memPutShort((long)(offset + 2L), (short)((short)CapturedRenderingState.INSTANCE.getCurrentRenderedBlockEntity()));
            MemoryUtil.memPutShort((long)(offset + 4L), (short)((short)CapturedRenderingState.INSTANCE.getCurrentRenderedItem()));
        }
    }

    @Inject(method={"push"}, at={@At(value="TAIL")}, remap=false, require=0)
    @Dynamic(value="Used to skip endLastVertex if the last push was made by Sodium")
    private void iris$skipSodiumChange(CallbackInfo ci) {
        this.skipEndVertexOnce = true;
    }

    @Inject(method={"endLastVertex"}, at={@At(value="HEAD")})
    private void iris$beforeNext(CallbackInfo ci) {
        if (this.vertices == 0 || !this.extending) {
            return;
        }
        this.elementsToFill &= ~IrisVertexFormats.MID_TEXTURE_ELEMENT.mask();
        this.elementsToFill &= ~IrisVertexFormats.TANGENT_ELEMENT.mask();
        if (this.injectNormalAndUV1 && this.elementsToFill != (this.elementsToFill & ~VertexFormatElement.NORMAL.mask())) {
            this.setNormal(0.0f, 1.0f, 0.0f);
        }
        if (this.skipEndVertexOnce) {
            this.skipEndVertexOnce = false;
            return;
        }
        if (this.mode != VertexFormat.Mode.QUADS && this.mode != VertexFormat.Mode.TRIANGLES) {
            return;
        }
        this.vertexOffsets[this.iris$vertexCount] = this.vertexPointer - ((MojangBufferAccessor)this.buffer).getPointer();
        ++this.iris$vertexCount;
        if (this.mode == VertexFormat.Mode.QUADS && this.iris$vertexCount == 4 || this.mode == VertexFormat.Mode.TRIANGLES && this.iris$vertexCount == 3) {
            this.fillExtendedData(this.iris$vertexCount);
        }
    }

    @Override
    public void beginBlock(int block, byte renderType, byte blockEmission, int localPosX, int localPosY, int localPosZ) {
        this.currentBlock = block;
        this.currentRenderType = renderType;
        this.currentLocalPosX = localPosX;
        this.currentLocalPosY = localPosY;
        this.currentLocalPosZ = localPosZ;
    }

    @Override
    public void overrideBlock(int block) {
        this.lastBlock = this.currentBlock;
        this.currentBlock = block;
    }

    @Override
    public void restoreBlock() {
        if (this.lastBlock != -1) {
            this.currentBlock = this.lastBlock;
            this.lastBlock = -1;
        }
    }

    @Override
    public void endBlock() {
        this.currentBlock = -1;
        this.currentRenderType = (byte)-1;
    }

    @Override
    public void ignoreMidBlock(boolean b) {
    }

    @Unique
    private void fillExtendedData(int vertexAmount) {
        this.iris$vertexCount = 0;
        int stride = this.format.getVertexSize();
        this.polygon.setup(((MojangBufferAccessor)this.buffer).getPointer(), this.vertexOffsets, stride, vertexAmount);
        float midU = 0.0f;
        float midV = 0.0f;
        for (int vertex = 0; vertex < vertexAmount; ++vertex) {
            midU += this.polygon.u(vertex);
            midV += this.polygon.v(vertex);
        }
        midU /= (float)vertexAmount;
        midV /= (float)vertexAmount;
        int midTexOffset = this.offsetsByElement[IrisVertexFormats.MID_TEXTURE_ELEMENT.id()];
        int normalOffset = this.offsetsByElement[VertexFormatElement.NORMAL.id()];
        int tangentOffset = this.offsetsByElement[IrisVertexFormats.TANGENT_ELEMENT.id()];
        if (vertexAmount == 3) {
            for (int vertex = 0; vertex < vertexAmount; ++vertex) {
                long newPointer = ((MojangBufferAccessor)this.buffer).getPointer() + this.vertexOffsets[vertex];
                int vertexNormal = MemoryUtil.memGetInt((long)(newPointer + (long)normalOffset));
                int tangent = NormalHelper.computeTangentSmooth(NormI8.unpackX(vertexNormal), NormI8.unpackY(vertexNormal), NormI8.unpackZ(vertexNormal), this.polygon);
                MemoryUtil.memPutFloat((long)(newPointer + (long)midTexOffset), (float)midU);
                MemoryUtil.memPutFloat((long)(newPointer + (long)midTexOffset + 4L), (float)midV);
                MemoryUtil.memPutInt((long)(newPointer + (long)tangentOffset), (int)tangent);
            }
        } else {
            boolean recalculateNormal = ImmediateState.isRenderingLevel;
            NormalHelper.computeFaceNormal(this.normal, this.polygon);
            int packedNormal = 0;
            if (recalculateNormal) {
                packedNormal = NormI8.pack(this.normal.x, this.normal.y, this.normal.z, 0.0f);
            }
            int tangent = NormalHelper.computeTangent(this.normal.x, this.normal.y, this.normal.z, this.polygon);
            for (int vertex = 0; vertex < vertexAmount; ++vertex) {
                long newPointer = ((MojangBufferAccessor)this.buffer).getPointer() + this.vertexOffsets[vertex];
                MemoryUtil.memPutFloat((long)(newPointer + (long)midTexOffset), (float)midU);
                MemoryUtil.memPutFloat((long)(newPointer + (long)midTexOffset + 4L), (float)midV);
                if (recalculateNormal) {
                    MemoryUtil.memPutInt((long)(newPointer + (long)normalOffset), (int)packedNormal);
                }
                MemoryUtil.memPutInt((long)(newPointer + (long)tangentOffset), (int)tangent);
            }
        }
        Arrays.fill(this.vertexOffsets, 0L);
    }
}

