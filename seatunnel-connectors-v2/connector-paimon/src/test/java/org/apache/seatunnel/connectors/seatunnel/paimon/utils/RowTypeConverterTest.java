/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.seatunnel.connectors.seatunnel.paimon.utils;

import org.apache.seatunnel.api.table.catalog.Column;
import org.apache.seatunnel.api.table.catalog.PhysicalColumn;
import org.apache.seatunnel.api.table.converter.BasicTypeDefine;
import org.apache.seatunnel.api.table.type.ArrayType;
import org.apache.seatunnel.api.table.type.BasicType;
import org.apache.seatunnel.api.table.type.DecimalType;
import org.apache.seatunnel.api.table.type.LocalTimeType;
import org.apache.seatunnel.api.table.type.MapType;
import org.apache.seatunnel.api.table.type.PrimitiveByteArrayType;
import org.apache.seatunnel.api.table.type.SeaTunnelDataType;
import org.apache.seatunnel.api.table.type.SeaTunnelRowType;

import org.apache.paimon.schema.TableSchema;
import org.apache.paimon.types.DataField;
import org.apache.paimon.types.DataType;
import org.apache.paimon.types.DataTypes;
import org.apache.paimon.types.RowType;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RowTypeConverterTest {

    private SeaTunnelRowType seaTunnelRowType;

    private SeaTunnelRowType seaTunnelProjectionRowType;
    private RowType rowType;

    private BasicTypeDefine<DataType> typeDefine;

    private Column column;

    private TableSchema tableSchema;

    public static final RowType DEFAULT_ROW_TYPE =
            RowType.of(
                    new DataType[] {
                        DataTypes.TINYINT(),
                        DataTypes.SMALLINT(),
                        DataTypes.INT(),
                        DataTypes.BIGINT(),
                        DataTypes.FLOAT(),
                        DataTypes.DOUBLE(),
                        DataTypes.DECIMAL(10, 10),
                        DataTypes.STRING(),
                        DataTypes.BYTES(),
                        DataTypes.BOOLEAN(),
                        DataTypes.DATE(),
                        DataTypes.TIMESTAMP(),
                        DataTypes.MAP(DataTypes.STRING(), DataTypes.STRING()),
                        DataTypes.ARRAY(DataTypes.STRING())
                    },
                    new String[] {
                        "c_tinyint",
                        "c_smallint",
                        "c_int",
                        "c_bigint",
                        "c_float",
                        "c_double",
                        "c_decimal",
                        "c_string",
                        "c_bytes",
                        "c_boolean",
                        "c_date",
                        "c_timestamp",
                        "c_map",
                        "c_array"
                    });

    public static final List<String> KEY_NAME_LIST = Arrays.asList("c_tinyint");

    @BeforeEach
    public void before() {
        seaTunnelRowType =
                new SeaTunnelRowType(
                        new String[] {
                            "c_tinyint",
                            "c_smallint",
                            "c_int",
                            "c_bigint",
                            "c_float",
                            "c_double",
                            "c_decimal",
                            "c_string",
                            "c_bytes",
                            "c_boolean",
                            "c_date",
                            "c_timestamp",
                            "c_map",
                            "c_array"
                        },
                        new SeaTunnelDataType<?>[] {
                            BasicType.BYTE_TYPE,
                            BasicType.SHORT_TYPE,
                            BasicType.INT_TYPE,
                            BasicType.LONG_TYPE,
                            BasicType.FLOAT_TYPE,
                            BasicType.DOUBLE_TYPE,
                            new DecimalType(30, 8),
                            BasicType.STRING_TYPE,
                            PrimitiveByteArrayType.INSTANCE,
                            BasicType.BOOLEAN_TYPE,
                            LocalTimeType.LOCAL_DATE_TYPE,
                            LocalTimeType.LOCAL_DATE_TIME_TYPE,
                            new MapType<>(BasicType.STRING_TYPE, BasicType.STRING_TYPE),
                            ArrayType.STRING_ARRAY_TYPE
                        });

        seaTunnelProjectionRowType =
                new SeaTunnelRowType(
                        new String[] {"c_string", "c_int"},
                        new SeaTunnelDataType<?>[] {BasicType.STRING_TYPE, BasicType.INT_TYPE});

        rowType =
                DataTypes.ROW(
                        new DataField(0, "c_tinyint", DataTypes.TINYINT()),
                        new DataField(1, "c_smallint", DataTypes.SMALLINT()),
                        new DataField(2, "c_int", DataTypes.INT()),
                        new DataField(3, "c_bigint", DataTypes.BIGINT()),
                        new DataField(4, "c_float", DataTypes.FLOAT()),
                        new DataField(5, "c_double", DataTypes.DOUBLE()),
                        new DataField(6, "c_decimal", DataTypes.DECIMAL(30, 8)),
                        new DataField(7, "c_string", DataTypes.STRING()),
                        new DataField(8, "c_bytes", DataTypes.BYTES()),
                        new DataField(9, "c_boolean", DataTypes.BOOLEAN()),
                        new DataField(10, "c_date", DataTypes.DATE()),
                        new DataField(11, "c_timestamp", DataTypes.TIMESTAMP(6)),
                        new DataField(
                                12, "c_map", DataTypes.MAP(DataTypes.STRING(), DataTypes.STRING())),
                        new DataField(13, "c_array", DataTypes.ARRAY(DataTypes.STRING())));

        tableSchema =
                new TableSchema(
                        0,
                        TableSchema.newFields(DEFAULT_ROW_TYPE),
                        DEFAULT_ROW_TYPE.getFieldCount(),
                        Collections.EMPTY_LIST,
                        KEY_NAME_LIST,
                        Collections.EMPTY_MAP,
                        "");

        typeDefine =
                BasicTypeDefine.<DataType>builder()
                        .name("c_decimal")
                        .comment("c_decimal_type_define")
                        .columnType("DECIMAL(30, 8)")
                        .nativeType(DataTypes.DECIMAL(30, 8))
                        .dataType(DataTypes.DECIMAL(30, 8).toString())
                        .length(30L)
                        .precision(30L)
                        .scale(8)
                        .defaultValue(3.0)
                        .nullable(false)
                        .build();

        org.apache.seatunnel.api.table.type.DecimalType dataType =
                new org.apache.seatunnel.api.table.type.DecimalType(30, 8);

        column =
                PhysicalColumn.builder()
                        .name("c_decimal")
                        .sourceType(DataTypes.DECIMAL(30, 8).toString())
                        .nullable(false)
                        .dataType(dataType)
                        .columnLength(30L)
                        .defaultValue(3.0)
                        .scale(8)
                        .comment("c_decimal_type_define")
                        .build();
    }

    @Test
    public void paimonRowTypeToSeaTunnel() {
        SeaTunnelRowType convert = RowTypeConverter.convert(rowType, null);
        Assertions.assertEquals(convert, seaTunnelRowType);
    }

    @Test
    public void paimonToSeaTunnelWithProjection() {
        int[] projection = {7, 2};
        SeaTunnelRowType convert = RowTypeConverter.convert(rowType, projection);
        Assertions.assertEquals(convert, seaTunnelProjectionRowType);
    }

    @Test
    public void seaTunnelToPaimon() {
        RowType convert = RowTypeConverter.reconvert(seaTunnelRowType, tableSchema);
        Assertions.assertEquals(convert, rowType);
    }

    @Test
    public void paimonDataTypeToSeaTunnelColumn() {
        Column column = RowTypeConverter.convert(typeDefine);
        isEquals(column, typeDefine);
    }

    @Test
    public void seaTunnelColumnToPaimonDataType() {
        BasicTypeDefine<DataType> dataTypeDefine = RowTypeConverter.reconvert(column);
        isEquals(column, dataTypeDefine);
    }

    private void isEquals(Column column, BasicTypeDefine<DataType> dataTypeDefine) {
        Assertions.assertEquals(column.getComment(), dataTypeDefine.getComment());
        Assertions.assertEquals(column.getColumnLength(), dataTypeDefine.getLength());
        Assertions.assertEquals(column.getName(), dataTypeDefine.getName());
        Assertions.assertEquals(column.isNullable(), dataTypeDefine.isNullable());
        Assertions.assertEquals(column.getDefaultValue(), dataTypeDefine.getDefaultValue());
        Assertions.assertEquals(column.getScale(), dataTypeDefine.getScale());
        Assertions.assertTrue(
                column.getDataType().toString().equalsIgnoreCase(dataTypeDefine.getColumnType()));
    }
}
