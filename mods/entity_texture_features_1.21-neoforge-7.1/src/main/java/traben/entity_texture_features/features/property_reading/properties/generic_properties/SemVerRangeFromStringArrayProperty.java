/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package traben.entity_texture_features.features.property_reading.properties.generic_properties;

import java.util.Arrays;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.NumberRangeFromStringArrayProperty;

public abstract class SemVerRangeFromStringArrayProperty
extends NumberRangeFromStringArrayProperty<SemVerNumber> {
    protected SemVerRangeFromStringArrayProperty(String string) throws RandomProperty.RandomPropertyException {
        super(string);
    }

    @Override
    @Nullable
    protected NumberRangeFromStringArrayProperty.RangeTester<SemVerNumber> getRangeTesterFromString(String possibleRange) {
        try {
            SemVerNumber right;
            String[] str = possibleRange.split("(?<!^|-)-");
            SemVerNumber left = new SemVerNumber(str[0]);
            SemVerNumber semVerNumber = right = str.length > 1 ? new SemVerNumber(str[1]) : null;
            if (str.length < 2 || left.sameAs(right)) {
                return value -> value.sameAs(left);
            }
            if (right.largerThan(left)) {
                return value -> value.betweenInclusive(left, right);
            }
            return value -> value.betweenInclusive(right, left);
        }
        catch (Exception exception) {
            return null;
        }
    }

    public static class SemVerNumber
    extends Number {
        private final int[] versions;

        public SemVerNumber(String value) {
            this.versions = Arrays.stream(value.split("\\.")).map(SemVerNumber::parse).mapToInt(i -> i).toArray();
        }

        private static int parse(String string) {
            try {
                return Integer.parseInt(string);
            }
            catch (NumberFormatException e) {
                return 0;
            }
        }

        public boolean sameAs(SemVerNumber other) {
            if (this.versions.length != other.versions.length) {
                return false;
            }
            for (int i = 0; i < this.versions.length; ++i) {
                if (this.versions[i] == other.versions[i]) continue;
                return false;
            }
            return true;
        }

        public boolean betweenInclusive(SemVerNumber smaller, SemVerNumber larger) {
            return this.largerThanOrEqual(smaller) && this.smallerThanOrEqual(larger);
        }

        public boolean largerThanOrEqual(SemVerNumber other) {
            return this.largerThan(other) || this.sameAs(other);
        }

        public boolean smallerThanOrEqual(SemVerNumber other) {
            return this.smallerThan(other) || this.sameAs(other);
        }

        public boolean largerThan(SemVerNumber other) {
            for (int i = 0; i < Math.min(this.versions.length, other.versions.length); ++i) {
                if (this.versions[i] > other.versions[i]) {
                    return true;
                }
                if (this.versions[i] >= other.versions[i]) continue;
                return false;
            }
            return this.versions.length > other.versions.length;
        }

        public boolean smallerThan(SemVerNumber other) {
            for (int i = 0; i < Math.min(this.versions.length, other.versions.length); ++i) {
                if (this.versions[i] < other.versions[i]) {
                    return true;
                }
                if (this.versions[i] <= other.versions[i]) continue;
                return false;
            }
            return this.versions.length < other.versions.length;
        }

        @Override
        public int intValue() {
            return this.versions[0];
        }

        @Override
        public long longValue() {
            return this.versions[0];
        }

        @Override
        public float floatValue() {
            return this.versions[0];
        }

        @Override
        public double doubleValue() {
            return this.versions[0];
        }
    }
}

