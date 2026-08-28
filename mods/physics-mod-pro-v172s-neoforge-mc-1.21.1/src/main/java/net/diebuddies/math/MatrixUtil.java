/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Math
 *  org.joml.Matrix4d
 *  org.joml.Matrix4dc
 *  org.joml.Matrix4f
 *  org.joml.Quaternionf
 */
package net.diebuddies.math;

import net.diebuddies.physics.PhysicsEntity;
import org.joml.Math;
import org.joml.Matrix4d;
import org.joml.Matrix4dc;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public class MatrixUtil {
    public static Matrix4d lerp(Matrix4d first, Matrix4d second, double target, Matrix4d dst) {
        if (dst == null) {
            dst = new Matrix4d();
        }
        return first.lerp((Matrix4dc)second, target, dst);
    }

    public static Matrix4d slerp(Matrix4d first, Matrix4d second, double target, Matrix4d dst) {
        double scale1;
        double scale0;
        double rotation2Z;
        double rotation2Y;
        double rotation2X;
        double rotation2W;
        double rotation1Z;
        double rotation1Y;
        double rotation1X;
        double rotation1W;
        double s;
        if (dst == null) {
            dst = new Matrix4d();
        }
        double translationX = Math.lerp((double)first.m30(), (double)second.m30(), (double)target);
        double translationY = Math.lerp((double)first.m31(), (double)second.m31(), (double)target);
        double translationZ = Math.lerp((double)first.m32(), (double)second.m32(), (double)target);
        double xs = java.lang.Math.signum(first.m00() * first.m01() * first.m02() * first.m03()) < 0.0 ? -1.0 : 1.0;
        double ys = java.lang.Math.signum(first.m10() * first.m11() * first.m12() * first.m13()) < 0.0 ? -1.0 : 1.0;
        double zs = java.lang.Math.signum(first.m20() * first.m21() * first.m22() * first.m23()) < 0.0 ? -1.0 : 1.0;
        double scaleX1 = xs * MatrixUtil.length(first.m00(), first.m01(), first.m02());
        double scaleY1 = ys * MatrixUtil.length(first.m10(), first.m11(), first.m12());
        double scaleZ1 = zs * MatrixUtil.length(first.m20(), first.m21(), first.m22());
        xs = java.lang.Math.signum(second.m00() * second.m01() * second.m02() * second.m03()) < 0.0 ? -1.0 : 1.0;
        ys = java.lang.Math.signum(second.m10() * second.m11() * second.m12() * second.m13()) < 0.0 ? -1.0 : 1.0;
        zs = java.lang.Math.signum(second.m20() * second.m21() * second.m22() * second.m23()) < 0.0 ? -1.0 : 1.0;
        double scaleX2 = xs * MatrixUtil.length(second.m00(), second.m01(), second.m02());
        double scaleY2 = ys * MatrixUtil.length(second.m10(), second.m11(), second.m12());
        double scaleZ2 = zs * MatrixUtil.length(second.m20(), second.m21(), second.m22());
        double scaleX = Math.lerp((double)scaleX1, (double)scaleX2, (double)target);
        double scaleY = Math.lerp((double)scaleY1, (double)scaleY2, (double)target);
        double scaleZ = Math.lerp((double)scaleZ1, (double)scaleZ2, (double)target);
        double m00 = first.m00();
        double m10 = first.m10();
        double m20 = first.m20();
        double m01 = first.m01();
        double m11 = first.m11();
        double m21 = first.m21();
        double m02 = first.m02();
        double m12 = first.m12();
        double m22 = first.m22();
        double invLengthRow1 = 1.0 / scaleX1;
        double invLengthRow2 = 1.0 / scaleY1;
        double invLengthRow3 = 1.0 / scaleZ1;
        m00 *= invLengthRow1;
        m01 *= invLengthRow1;
        m02 *= invLengthRow1;
        m10 *= invLengthRow2;
        m12 *= invLengthRow2;
        m20 *= invLengthRow3;
        m21 *= invLengthRow3;
        double t = m00 + (m11 *= invLengthRow2) + (m22 *= invLengthRow3);
        if (t >= 0.0) {
            s = java.lang.Math.sqrt(t + 1.0);
            rotation1W = 0.5 * s;
            s = 0.5 / s;
            rotation1X = (m12 - m21) * s;
            rotation1Y = (m20 - m02) * s;
            rotation1Z = (m01 - m10) * s;
        } else if (m00 > m11 && m00 > m22) {
            s = java.lang.Math.sqrt(1.0 + m00 - m11 - m22);
            rotation1X = 0.5 * s;
            s = 0.5 / s;
            rotation1Y = (m01 + m10) * s;
            rotation1Z = (m02 + m20) * s;
            rotation1W = (m12 - m21) * s;
        } else if (m11 > m22) {
            s = java.lang.Math.sqrt(1.0 + m11 - m00 - m22);
            rotation1Y = 0.5 * s;
            s = 0.5 / s;
            rotation1X = (m01 + m10) * s;
            rotation1Z = (m12 + m21) * s;
            rotation1W = (m20 - m02) * s;
        } else {
            s = java.lang.Math.sqrt(1.0 + m22 - m00 - m11);
            rotation1Z = 0.5 * s;
            s = 0.5 / s;
            rotation1X = (m02 + m20) * s;
            rotation1Y = (m12 + m21) * s;
            rotation1W = (m01 - m10) * s;
        }
        m00 = second.m00();
        m10 = second.m10();
        m20 = second.m20();
        m01 = second.m01();
        m11 = second.m11();
        m21 = second.m21();
        m02 = second.m02();
        m12 = second.m12();
        m22 = second.m22();
        invLengthRow1 = 1.0 / scaleX2;
        invLengthRow2 = 1.0 / scaleY2;
        invLengthRow3 = 1.0 / scaleZ2;
        m00 *= invLengthRow1;
        m01 *= invLengthRow1;
        m02 *= invLengthRow1;
        m10 *= invLengthRow2;
        m12 *= invLengthRow2;
        m20 *= invLengthRow3;
        m21 *= invLengthRow3;
        t = m00 + (m11 *= invLengthRow2) + (m22 *= invLengthRow3);
        s = 0.0;
        if (t >= 0.0) {
            s = java.lang.Math.sqrt(t + 1.0);
            rotation2W = 0.5 * s;
            s = 0.5 / s;
            rotation2X = (m12 - m21) * s;
            rotation2Y = (m20 - m02) * s;
            rotation2Z = (m01 - m10) * s;
        } else if (m00 > m11 && m00 > m22) {
            s = java.lang.Math.sqrt(1.0 + m00 - m11 - m22);
            rotation2X = 0.5 * s;
            s = 0.5 / s;
            rotation2Y = (m01 + m10) * s;
            rotation2Z = (m02 + m20) * s;
            rotation2W = (m12 - m21) * s;
        } else if (m11 > m22) {
            s = java.lang.Math.sqrt(1.0 + m11 - m00 - m22);
            rotation2Y = 0.5 * s;
            s = 0.5 / s;
            rotation2X = (m01 + m10) * s;
            rotation2Z = (m12 + m21) * s;
            rotation2W = (m20 - m02) * s;
        } else {
            s = java.lang.Math.sqrt(1.0 + m22 - m00 - m11);
            rotation2Z = 0.5 * s;
            s = 0.5 / s;
            rotation2X = (m02 + m20) * s;
            rotation2Y = (m12 + m21) * s;
            rotation2W = (m01 - m10) * s;
        }
        double cosom = rotation1X * rotation2X + rotation1Y * rotation2Y + rotation1Z * rotation2Z + rotation1W * rotation2W;
        double absCosom = Math.abs((double)cosom);
        if (1.0 - absCosom > 1.0E-6) {
            double sinSqr = 1.0 - absCosom * absCosom;
            double sinom = 1.0 / Math.sqrt((double)sinSqr);
            double omega = Math.atan2((double)(sinSqr * sinom), (double)absCosom);
            scale0 = Math.sin((double)((1.0 - target) * omega)) * sinom;
            scale1 = Math.sin((double)(target * omega)) * sinom;
        } else {
            scale0 = 1.0 - target;
            scale1 = target;
        }
        scale1 = cosom >= 0.0 ? scale1 : -scale1;
        double rotation3X = scale0 * rotation1X + scale1 * rotation2X;
        double rotation3Y = scale0 * rotation1Y + scale1 * rotation2Y;
        double rotation3Z = scale0 * rotation1Z + scale1 * rotation2Z;
        double rotation3W = scale0 * rotation1W + scale1 * rotation2W;
        dst.translationRotateScale(translationX, translationY, translationZ, rotation3X, rotation3Y, rotation3Z, rotation3W, scaleX, scaleY, scaleZ);
        return dst;
    }

    public static Matrix4f slerpNoScale(PhysicsEntity entity, double target, Matrix4f dst) {
        Matrix4d first = entity.getOldTransformation();
        Matrix4d second = entity.getTransformation();
        if (dst == null) {
            dst = new Matrix4f();
        }
        Quaternionf rotation1 = entity.getOldRotation();
        Quaternionf rotation2 = entity.getRotation();
        if (rotation1 == null) {
            rotation1 = first.getNormalizedRotation(new Quaternionf());
            entity.setOldRotation(rotation1);
            rotation2 = second.getNormalizedRotation(new Quaternionf());
            entity.setRotation(rotation2);
        }
        double translationX = Math.lerp((double)first.m30(), (double)second.m30(), (double)target);
        double translationY = Math.lerp((double)first.m31(), (double)second.m31(), (double)target);
        double translationZ = Math.lerp((double)first.m32(), (double)second.m32(), (double)target);
        double cosom = Math.fma((float)rotation1.x, (float)rotation2.x, (float)Math.fma((float)rotation1.y, (float)rotation2.y, (float)Math.fma((float)rotation1.z, (float)rotation2.z, (float)(rotation1.w * rotation2.w))));
        double scale0 = 1.0 - target;
        double scale1 = cosom >= 0.0 ? target : -target;
        double rotation3X = Math.fma((double)scale0, (double)rotation1.x, (double)(scale1 * (double)rotation2.x));
        double rotation3Y = Math.fma((double)scale0, (double)rotation1.y, (double)(scale1 * (double)rotation2.y));
        double rotation3Z = Math.fma((double)scale0, (double)rotation1.z, (double)(scale1 * (double)rotation2.z));
        double rotation3W = Math.fma((double)scale0, (double)rotation1.w, (double)(scale1 * (double)rotation2.w));
        double s = Math.invsqrt((double)Math.fma((double)rotation3X, (double)rotation3X, (double)Math.fma((double)rotation3Y, (double)rotation3Y, (double)Math.fma((double)rotation3Z, (double)rotation3Z, (double)(rotation3W * rotation3W)))));
        dst.translationRotate((float)translationX, (float)translationY, (float)translationZ, (float)(rotation3X *= s), (float)(rotation3Y *= s), (float)(rotation3Z *= s), (float)(rotation3W *= s));
        return dst;
    }

    private static double length(double x, double y, double z) {
        return java.lang.Math.sqrt(x * x + y * y + z * z);
    }
}

