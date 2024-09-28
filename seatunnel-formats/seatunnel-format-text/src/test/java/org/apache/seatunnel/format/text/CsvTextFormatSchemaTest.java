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

package org.apache.seatunnel.format.text;

import org.apache.seatunnel.api.table.type.ArrayType;
import org.apache.seatunnel.api.table.type.BasicType;
import org.apache.seatunnel.api.table.type.DecimalType;
import org.apache.seatunnel.api.table.type.LocalTimeType;
import org.apache.seatunnel.api.table.type.MapType;
import org.apache.seatunnel.api.table.type.SeaTunnelDataType;
import org.apache.seatunnel.api.table.type.SeaTunnelRow;
import org.apache.seatunnel.api.table.type.SeaTunnelRowType;
import org.apache.seatunnel.common.utils.DateTimeUtils.Formatter;
import org.apache.seatunnel.format.text.splitor.CsvLineSplitor;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CsvTextFormatSchemaTest {
    public String content =
            "\"mess,age\","
                    + "true,"
                    + "1,"
                    + "2,"
                    + "3,"
                    + "4,"
                    + "6.66,"
                    + "7.77,"
                    + "8.8888888,"
                    + ','
                    + "2022-09-24,"
                    + "22:45:00,"
                    + "2022-09-24 22:45:00,"
                    // row field
                    + String.join("\u0003", Arrays.asList("1", "2", "3", "4", "5", "6"))
                    + '\002'
                    + "tyrantlucifer\00418\003Kris\00421"
                    + ','
                    // array field
                    + String.join("\u0002", Arrays.asList("1", "2", "3", "4", "5", "6"))
                    + ','
                    // map field
                    + "tyrantlucifer"
                    + '\003'
                    + "18"
                    + '\002'
                    + "Kris"
                    + '\003'
                    + "21"
                    + '\002'
                    + "nullValueKey"
                    + '\003'
                    + '\002'
                    + '\003'
                    + "1231";

    public SeaTunnelRowType seaTunnelRowType;

    @BeforeEach
    public void initSeaTunnelRowType() {
        seaTunnelRowType =
                new SeaTunnelRowType(
                        new String[] {
                            "string_field",
                            "boolean_field",
                            "tinyint_field",
                            "smallint_field",
                            "int_field",
                            "bigint_field",
                            "float_field",
                            "double_field",
                            "decimal_field",
                            "null_field",
                            "date_field",
                            "time_field",
                            "timestamp_field",
                            "row_field",
                            "array_field",
                            "map_field"
                        },
                        new SeaTunnelDataType<?>[] {
                            BasicType.STRING_TYPE,
                            BasicType.BOOLEAN_TYPE,
                            BasicType.BYTE_TYPE,
                            BasicType.SHORT_TYPE,
                            BasicType.INT_TYPE,
                            BasicType.LONG_TYPE,
                            BasicType.FLOAT_TYPE,
                            BasicType.DOUBLE_TYPE,
                            new DecimalType(30, 8),
                            BasicType.VOID_TYPE,
                            LocalTimeType.LOCAL_DATE_TYPE,
                            LocalTimeType.LOCAL_TIME_TYPE,
                            LocalTimeType.LOCAL_DATE_TIME_TYPE,
                            new SeaTunnelRowType(
                                    new String[] {
                                        "array_field", "map_field",
                                    },
                                    new SeaTunnelDataType<?>[] {
                                        ArrayType.INT_ARRAY_TYPE,
                                        new MapType<>(BasicType.STRING_TYPE, BasicType.INT_TYPE),
                                    }),
                            ArrayType.INT_ARRAY_TYPE,
                            new MapType<>(BasicType.STRING_TYPE, BasicType.INT_TYPE)
                        });
    }

    @Test
    public void testParse() throws IOException {
        String delimiter = ",";
        TextDeserializationSchema deserializationSchema =
                TextDeserializationSchema.builder()
                        .seaTunnelRowType(seaTunnelRowType)
                        .delimiter(delimiter)
                        .textLineSplitor(new CsvLineSplitor())
                        .build();
        SeaTunnelRow seaTunnelRow = deserializationSchema.deserialize(content.getBytes());
        Assertions.assertEquals("mess,age", seaTunnelRow.getField(0));
        Assertions.assertEquals(Boolean.TRUE, seaTunnelRow.getField(1));
        Assertions.assertEquals(Byte.valueOf("1"), seaTunnelRow.getField(2));
        Assertions.assertEquals(Short.valueOf("2"), seaTunnelRow.getField(3));
        Assertions.assertEquals(Integer.valueOf("3"), seaTunnelRow.getField(4));
        Assertions.assertEquals(Long.valueOf("4"), seaTunnelRow.getField(5));
        Assertions.assertEquals(Float.valueOf("6.66"), seaTunnelRow.getField(6));
        Assertions.assertEquals(Double.valueOf("7.77"), seaTunnelRow.getField(7));
        Assertions.assertEquals(BigDecimal.valueOf(8.8888888D), seaTunnelRow.getField(8));
        Assertions.assertNull((seaTunnelRow.getField(9)));
        Assertions.assertEquals(LocalDate.of(2022, 9, 24), seaTunnelRow.getField(10));
        Assertions.assertEquals(((Map<?, ?>) (seaTunnelRow.getField(15))).get("tyrantlucifer"), 18);
        Assertions.assertEquals(((Map<?, ?>) (seaTunnelRow.getField(15))).get("Kris"), 21);
    }

    @Test
    public void testSerializationWithTimestamp() {
        String delimiter = ",";

        SeaTunnelRowType schema =
                new SeaTunnelRowType(
                        new String[] {"timestamp"},
                        new SeaTunnelDataType[] {LocalTimeType.LOCAL_DATE_TIME_TYPE});
        LocalDateTime timestamp = LocalDateTime.of(2022, 9, 24, 22, 45, 0, 123456000);
        TextSerializationSchema textSerializationSchema =
                TextSerializationSchema.builder()
                        .seaTunnelRowType(schema)
                        .dateTimeFormatter(Formatter.YYYY_MM_DD_HH_MM_SS_SSSSSS)
                        .delimiter(delimiter)
                        .build();
        SeaTunnelRow row = new SeaTunnelRow(new Object[] {timestamp});

        assertEquals(
                "2022-09-24 22:45:00.123456", new String(textSerializationSchema.serialize(row)));

        timestamp = LocalDateTime.of(2022, 9, 24, 22, 45, 0, 0);
        row = new SeaTunnelRow(new Object[] {timestamp});
        assertEquals(
                "2022-09-24 22:45:00.000000", new String(textSerializationSchema.serialize(row)));

        timestamp = LocalDateTime.of(2022, 9, 24, 22, 45, 0, 1000);
        row = new SeaTunnelRow(new Object[] {timestamp});
        assertEquals(
                "2022-09-24 22:45:00.000001", new String(textSerializationSchema.serialize(row)));

        timestamp = LocalDateTime.of(2022, 9, 24, 22, 45, 0, 123456);
        row = new SeaTunnelRow(new Object[] {timestamp});
        assertEquals(
                "2022-09-24 22:45:00.000123", new String(textSerializationSchema.serialize(row)));
    }
}
