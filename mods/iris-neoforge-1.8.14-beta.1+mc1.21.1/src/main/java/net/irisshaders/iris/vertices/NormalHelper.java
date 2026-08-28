/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 *  org.joml.Vector4f
 */
package net.irisshaders.iris.vertices;

import net.irisshaders.iris.vertices.NormI8;
import net.irisshaders.iris.vertices.views.QuadView;
import net.irisshaders.iris.vertices.views.TriView;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;

public abstract class NormalHelper {
    private static final float EPS = 1.0E-20f;

    private NormalHelper() {
    }

    public static int invertPackedNormal(int packed) {
        int ix = -(packed & 0xFF);
        int iy = -(packed >> 8 & 0xFF);
        int iz = -(packed >> 16 & 0xFF);
        return packed & 0xFF000000 | (iz &= 0xFF) << 16 | (iy &= 0xFF) << 8 | (ix &= 0xFF);
    }

    public static void computeFaceNormal(@NotNull Vector3f saveTo, QuadView q) {
        float x0 = q.x(0);
        float y0 = q.y(0);
        float z0 = q.z(0);
        float x1 = q.x(1);
        float y1 = q.y(1);
        float z1 = q.z(1);
        float x2 = q.x(2);
        float y2 = q.y(2);
        float z2 = q.z(2);
        float x3 = q.x(3);
        float y3 = q.y(3);
        float z3 = q.z(3);
        NormalHelper.computeFaceNormalManual(saveTo, x0, y0, z0, x1, y1, z1, x2, y2, z2, x3, y3, z3);
    }

    public static void computeFaceNormalManual(@NotNull Vector3f saveTo, float x0, float y0, float z0, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3) {
        float dx0 = x2 - x0;
        float dy0 = y2 - y0;
        float dz0 = z2 - z0;
        float dx1 = x3 - x1;
        float dy1 = y3 - y1;
        float dz1 = z3 - z1;
        float normX = dy0 * dz1 - dz0 * dy1;
        float normY = dz0 * dx1 - dx0 * dz1;
        float normZ = dx0 * dy1 - dy0 * dx1;
        saveTo.set(normX, normY, normZ);
        saveTo.normalize();
    }

    public static void computeFaceNormalFlipped(@NotNull Vector3f saveTo, QuadView q) {
        float x0 = q.x(3);
        float y0 = q.y(3);
        float z0 = q.z(3);
        float x1 = q.x(2);
        float y1 = q.y(2);
        float z1 = q.z(2);
        float x2 = q.x(1);
        float y2 = q.y(1);
        float z2 = q.z(1);
        float x3 = q.x(0);
        float y3 = q.y(0);
        float z3 = q.z(0);
        NormalHelper.computeFaceNormalManual(saveTo, x0, y0, z0, x1, y1, z1, x2, y2, z2, x3, y3, z3);
    }

    public static void computeFaceNormalTri(@NotNull Vector3f saveTo, TriView t) {
        float x0 = t.x(0);
        float y0 = t.y(0);
        float z0 = t.z(0);
        float x1 = t.x(1);
        float y1 = t.y(1);
        float z1 = t.z(1);
        float x2 = t.x(2);
        float y2 = t.y(2);
        float z2 = t.z(2);
        NormalHelper.computeFaceNormalManual(saveTo, x0, y0, z0, x1, y1, z1, x2, y2, z2, x0, y0, z0);
    }

    public static int computeTangentSmooth(float normalX, float normalY, float normalZ, TriView t) {
        float deltaV1;
        float deltaU2;
        float x0 = t.x(0);
        float y0 = t.y(0);
        float z0 = t.z(0);
        float x1 = t.x(1);
        float y1 = t.y(1);
        float z1 = t.z(1);
        float x2 = t.x(2);
        float y2 = t.y(2);
        float z2 = t.z(2);
        float d0 = x0 * normalX + y0 * normalY + z0 * normalZ;
        float d1 = x1 * normalX + y1 * normalY + z1 * normalZ;
        float d2 = x2 * normalX + y2 * normalY + z2 * normalZ;
        x0 -= d0 * normalX;
        y0 -= d0 * normalY;
        z0 -= d0 * normalZ;
        x1 -= d1 * normalX;
        y1 -= d1 * normalY;
        z1 -= d1 * normalZ;
        x2 -= d2 * normalX;
        y2 -= d2 * normalY;
        z2 -= d2 * normalZ;
        float edge1x = x1 - x0;
        float edge1y = y1 - y0;
        float edge1z = z1 - z0;
        float edge2x = x2 - x0;
        float edge2y = y2 - y0;
        float edge2z = z2 - z0;
        float u0 = t.u(0);
        float v0 = t.v(0);
        float u1 = t.u(1);
        float v1 = t.v(1);
        float u2 = t.u(2);
        float deltaU1 = u1 - u0;
        float v2 = t.v(2);
        float deltaV2 = v2 - v0;
        float fdenom = deltaU1 * deltaV2 - (deltaU2 = u2 - u0) * (deltaV1 = v1 - v0);
        float f = (double)fdenom == 0.0 ? 1.0f : 1.0f / fdenom;
        float tangentx = f * (deltaV2 * edge1x - deltaV1 * edge2x);
        float tangenty = f * (deltaV2 * edge1y - deltaV1 * edge2y);
        float tangentz = f * (deltaV2 * edge1z - deltaV1 * edge2z);
        float tcoeff = NormalHelper.rsqrt(tangentx * tangentx + tangenty * tangenty + tangentz * tangentz);
        tangentx *= tcoeff;
        tangenty *= tcoeff;
        float bitangentx = f * (-deltaU2 * edge1x + deltaU1 * edge2x);
        float bitangenty = f * (-deltaU2 * edge1y + deltaU1 * edge2y);
        float bitangentz = f * (-deltaU2 * edge1z + deltaU1 * edge2z);
        float bitcoeff = NormalHelper.rsqrt(bitangentx * bitangentx + bitangenty * bitangenty + bitangentz * bitangentz);
        float pbitangentx = tangenty * normalZ - (tangentz *= tcoeff) * normalY;
        float pbitangenty = tangentz * normalX - tangentx * normalZ;
        float pbitangentz = tangentx * normalY - tangenty * normalX;
        float dot = (bitangentx *= bitcoeff) * pbitangentx + (bitangenty *= bitcoeff) * pbitangenty + (bitangentz *= bitcoeff) * pbitangentz;
        float tangentW = dot < 0.0f ? -1.0f : 1.0f;
        return NormI8.pack(tangentx, tangenty, tangentz, tangentW);
    }

    public static int computeTangent(float normalX, float normalY, float normalZ, TriView t) {
        float deltaV1;
        float deltaU2;
        float x0 = t.x(0);
        float y0 = t.y(0);
        float z0 = t.z(0);
        float x1 = t.x(1);
        float y1 = t.y(1);
        float z1 = t.z(1);
        float x2 = t.x(2);
        float y2 = t.y(2);
        float z2 = t.z(2);
        float edge1x = x1 - x0;
        float edge1y = y1 - y0;
        float edge1z = z1 - z0;
        float edge2x = x2 - x0;
        float edge2y = y2 - y0;
        float edge2z = z2 - z0;
        float u0 = t.u(0);
        float v0 = t.v(0);
        float u1 = t.u(1);
        float v1 = t.v(1);
        float u2 = t.u(2);
        float deltaU1 = u1 - u0;
        float v2 = t.v(2);
        float deltaV2 = v2 - v0;
        float fdenom = deltaU1 * deltaV2 - (deltaU2 = u2 - u0) * (deltaV1 = v1 - v0);
        float f = (double)fdenom == 0.0 ? 1.0f : 1.0f / fdenom;
        float tangentx = f * (deltaV2 * edge1x - deltaV1 * edge2x);
        float tangenty = f * (deltaV2 * edge1y - deltaV1 * edge2y);
        float tangentz = f * (deltaV2 * edge1z - deltaV1 * edge2z);
        float tcoeff = NormalHelper.rsqrt(tangentx * tangentx + tangenty * tangenty + tangentz * tangentz);
        tangentx *= tcoeff;
        tangenty *= tcoeff;
        float bitangentx = f * (-deltaU2 * edge1x + deltaU1 * edge2x);
        float bitangenty = f * (-deltaU2 * edge1y + deltaU1 * edge2y);
        float bitangentz = f * (-deltaU2 * edge1z + deltaU1 * edge2z);
        float bitcoeff = NormalHelper.rsqrt(bitangentx * bitangentx + bitangenty * bitangenty + bitangentz * bitangentz);
        float pbitangentx = tangenty * normalZ - (tangentz *= tcoeff) * normalY;
        float pbitangenty = tangentz * normalX - tangentx * normalZ;
        float pbitangentz = tangentx * normalY - tangenty * normalX;
        float dot = (bitangentx *= bitcoeff) * pbitangentx + (bitangenty *= bitcoeff) * pbitangenty + (bitangentz *= bitcoeff) * pbitangentz;
        float tangentW = dot < 0.0f ? -1.0f : 1.0f;
        return NormI8.pack(tangentx, tangenty, tangentz, tangentW);
    }

    public static int computeTangent(float normalX, float normalY, float normalZ, float x0, float y0, float z0, float u0, float v0, float x1, float y1, float z1, float u1, float v1, float x2, float y2, float z2, float u2, float v2) {
        return NormalHelper.computeTangent(null, normalX, normalY, normalZ, x0, y0, z0, u0, v0, x1, y1, z1, u1, v1, x2, y2, z2, u2, v2);
    }

    public static int computeTangent(Vector4f output, float normalX, float normalY, float normalZ, float x0, float y0, float z0, float u0, float v0, float x1, float y1, float z1, float u1, float v1, float x2, float y2, float z2, float u2, float v2) {
        float pbitangentz;
        float pbitangenty;
        float pbitangentx;
        float bitcoeff;
        float dot;
        float tcoeff;
        float edge1x = x1 - x0;
        float edge1y = y1 - y0;
        float edge1z = z1 - z0;
        float edge2x = x2 - x0;
        float edge2y = y2 - y0;
        float edge2z = z2 - z0;
        float deltaU1 = u1 - u0;
        float deltaV2 = v2 - v0;
        float deltaU2 = u2 - u0;
        float deltaV1 = v1 - v0;
        float fdenom = deltaU1 * deltaV2 - deltaU2 * deltaV1;
        float f = (double)fdenom == 0.0 ? 1.0f : 1.0f / fdenom;
        float tangentx = f * (deltaV2 * edge1x - deltaV1 * edge2x);
        float tangenty = f * (deltaV2 * edge1y - deltaV1 * edge2y);
        float tangentz = f * (deltaV2 * edge1z - deltaV1 * edge2z);
        if ((tangentx *= (tcoeff = NormalHelper.rsqrt(tangentx * tangentx + tangenty * tangenty + tangentz * tangentz))) == 0.0f && (tangenty *= tcoeff) == 0.0f && (tangentz *= tcoeff) == 0.0f) {
            return -1;
        }
        float bitangentx = f * (-deltaU2 * edge1x + deltaU1 * edge2x);
        float bitangenty = f * (-deltaU2 * edge1y + deltaU1 * edge2y);
        float bitangentz = f * (-deltaU2 * edge1z + deltaU1 * edge2z);
        float tangentW = (dot = (bitangentx *= (bitcoeff = NormalHelper.rsqrt(bitangentx * bitangentx + bitangenty * bitangenty + bitangentz * bitangentz))) * (pbitangentx = tangenty * normalZ - tangentz * normalY) + (bitangenty *= bitcoeff) * (pbitangenty = tangentz * normalX - tangentx * normalZ) + (bitangentz *= bitcoeff) * (pbitangentz = tangentx * normalY - tangenty * normalX)) < 0.0f ? -1.0f : 1.0f;
        if (output != null) {
            output.set(tangentx, tangenty, tangentz, tangentW);
        }
        return NormI8.pack(tangentx, tangenty, tangentz, tangentW);
    }

    private static float rsqrt(float value) {
        if (value == 0.0f) {
            return 1.0f;
        }
        return (float)(1.0 / Math.sqrt(value));
    }

    private static int snorm12(float v) {
        float c = Math.max(-1.0f, Math.min(1.0f, v));
        int q = Math.round(c * 2047.0f);
        if (q == -2048) {
            q = -2047;
        }
        return q;
    }

    private static float signNotZero(float v) {
        return v >= 0.0f ? 1.0f : -1.0f;
    }

    public static int encodeNormal(float x, float y, float z) {
        float outY;
        float outX;
        float rev = 1.0f / (Math.abs(x) + Math.abs(y) + Math.abs(z));
        float pX = x * rev;
        float pY = y * rev;
        if (z > 0.0f) {
            outX = pX;
            outY = pY;
        } else {
            outX = (1.0f - Math.abs(pY)) * NormalHelper.signNotZero(pX);
            outY = (1.0f - Math.abs(pX)) * NormalHelper.signNotZero(pY);
        }
        int qx = NormalHelper.snorm12(outX);
        int qy = NormalHelper.snorm12(outY);
        return (qx & 0xFFF) << 12 | qy & 0xFFF;
    }

    private static void onbFromUnitNormal(float nx, float ny, float nz, Vector3f t1, Vector3f t2) {
        float s = nz >= 0.0f ? 1.0f : -1.0f;
        float a = -1.0f / (s + nz);
        float b = nx * ny * a;
        t1.set(1.0f + s * nx * nx * a, s * b, -s * nx).normalize();
        t2.set(ny * t1.z - nz * t1.y, nz * t1.x - nx * t1.z, nx * t1.y - ny * t1.x);
    }

    private static float encodeDiamond(float px, float py) {
        float denom = Math.abs(px) + Math.abs(py);
        if (denom <= 1.0E-20f) {
            return 0.5f;
        }
        float x = px / denom;
        float pys = py >= 0.0f ? 1.0f : -1.0f;
        return -pys * 0.25f * x + 0.5f + pys * 0.25f;
    }

    public static int packDiamondByte(Vector3fc normal, Vector3fc tangent, Vector3f t1, Vector3f t2, Vector3f tp) {
        t2.set(normal).normalize();
        float nx = t2.x;
        float ny = t2.y;
        float nz = t2.z;
        NormalHelper.onbFromUnitNormal(nx, ny, nz, t1, t2);
        float NdT = nx * tangent.x() + ny * tangent.y() + nz * tangent.z();
        tp.set(tangent).sub(nx * NdT, ny * NdT, nz * NdT);
        if (tp.lengthSquared() > 1.0E-20f) {
            tp.normalize();
        } else {
            tp.set((Vector3fc)t1);
        }
        float px = tp.dot((Vector3fc)t1);
        float py = tp.dot((Vector3fc)t2);
        float d = NormalHelper.encodeDiamond(px, py);
        int q = Math.min(256, Math.max(0, Math.round(d * 256.0f)));
        return q & 0xFF;
    }

    public static int encodeNormalTangent(Vector3f normal, Vector3f tangent, Vector3f scratch1, Vector3f scratch2, Vector3f scratchOut) {
        int encodedNormal = NormalHelper.encodeNormal(normal.x, normal.y, normal.z);
        int encodedTangent = NormalHelper.packDiamondByte((Vector3fc)normal, (Vector3fc)tangent, scratch1, scratch2, scratchOut);
        return encodedTangent << 24 | encodedNormal;
    }
}

