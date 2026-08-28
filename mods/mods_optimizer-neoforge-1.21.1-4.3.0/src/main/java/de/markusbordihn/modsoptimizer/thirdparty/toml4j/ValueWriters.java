/*
 * Decompiled with CFR 0.152.
 */
package de.markusbordihn.modsoptimizer.thirdparty.toml4j;

import de.markusbordihn.modsoptimizer.thirdparty.toml4j.BooleanValueReaderWriter;
import de.markusbordihn.modsoptimizer.thirdparty.toml4j.DateValueReaderWriter;
import de.markusbordihn.modsoptimizer.thirdparty.toml4j.MapValueWriter;
import de.markusbordihn.modsoptimizer.thirdparty.toml4j.NumberValueReaderWriter;
import de.markusbordihn.modsoptimizer.thirdparty.toml4j.ObjectValueWriter;
import de.markusbordihn.modsoptimizer.thirdparty.toml4j.PrimitiveArrayValueWriter;
import de.markusbordihn.modsoptimizer.thirdparty.toml4j.StringValueReaderWriter;
import de.markusbordihn.modsoptimizer.thirdparty.toml4j.TableArrayValueWriter;
import de.markusbordihn.modsoptimizer.thirdparty.toml4j.ValueWriter;

class ValueWriters {
    static final ValueWriters WRITERS = new ValueWriters();
    private static final ValueWriter[] VALUE_WRITERS = new ValueWriter[]{StringValueReaderWriter.STRING_VALUE_READER_WRITER, NumberValueReaderWriter.NUMBER_VALUE_READER_WRITER, BooleanValueReaderWriter.BOOLEAN_VALUE_READER_WRITER, ValueWriters.getPlatformSpecificDateConverter(), MapValueWriter.MAP_VALUE_WRITER, PrimitiveArrayValueWriter.PRIMITIVE_ARRAY_VALUE_WRITER, TableArrayValueWriter.TABLE_ARRAY_VALUE_WRITER};

    ValueWriter findWriterFor(Object value) {
        for (ValueWriter valueWriter : VALUE_WRITERS) {
            if (!valueWriter.canWrite(value)) continue;
            return valueWriter;
        }
        return ObjectValueWriter.OBJECT_VALUE_WRITER;
    }

    private ValueWriters() {
    }

    private static DateValueReaderWriter getPlatformSpecificDateConverter() {
        String specificationVersion = Runtime.class.getPackage().getSpecificationVersion();
        return specificationVersion != null && specificationVersion.startsWith("1.6") ? DateValueReaderWriter.DATE_PARSER_JDK_6 : DateValueReaderWriter.DATE_VALUE_READER_WRITER;
    }
}

