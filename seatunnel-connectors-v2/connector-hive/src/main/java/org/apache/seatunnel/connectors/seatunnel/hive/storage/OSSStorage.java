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

package org.apache.seatunnel.connectors.seatunnel.hive.storage;

import org.apache.seatunnel.shade.com.typesafe.config.Config;

import org.apache.seatunnel.api.configuration.ReadonlyConfig;
import org.apache.seatunnel.connectors.seatunnel.file.config.HadoopConf;
import org.apache.seatunnel.connectors.seatunnel.file.oss.config.OssHadoopConf;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;

import java.util.Map;

public class OSSStorage extends AbstractStorage {

    @Override
    public HadoopConf buildHadoopConfWithReadOnlyConfig(ReadonlyConfig readonlyConfig) {
        Configuration configuration = loadHiveBaseHadoopConfig(readonlyConfig);
        Config config = fillBucket(readonlyConfig, configuration);
        HadoopConf hadoopConf = OssHadoopConf.buildWithConfig(ReadonlyConfig.fromConfig(config));
        Map<String, String> propsInConfiguration =
                configuration.getPropsWithPrefix(StringUtils.EMPTY);
        hadoopConf.setExtraOptions(propsInConfiguration);
        return hadoopConf;
    }
}
