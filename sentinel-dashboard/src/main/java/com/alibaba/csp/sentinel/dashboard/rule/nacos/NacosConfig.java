/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.csp.sentinel.dashboard.rule.nacos;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.FlowRuleEntity;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigFactory;
import com.alibaba.nacos.api.config.ConfigService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Properties;

/**
 * @author Eric Zhao
 * @since 1.4.0
 */
@Configuration
public class NacosConfig {

    // 从 application.properties 注入值，并设置默认值防止启动失败
    @Value("${sentinel.nacos.serverAddr:localhost:8848}")
    private String serverAddr;

    @Value("${sentinel.nacos.namespace:}")
    private String namespace;

    @Value("${sentinel.nacos.groupId:SENTINEL_GROUP}")
    private String groupId;

    @Bean
    public Converter<List<FlowRuleEntity>, String> flowRuleEntityEncoder() {
        // 使用 SerializerFeature.PrettyFormat 让JSON格式化输出
        // 使用 WriteMapNullValue 即使值为null也序列化字段
        return entities -> JSON.toJSONString(entities,
                SerializerFeature.PrettyFormat,
                SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteDateUseDateFormat);
    }

    @Bean
    public Converter<String, List<FlowRuleEntity>> flowRuleEntityDecoder() {
        return s -> JSON.parseArray(s, FlowRuleEntity.class);
    }

    @Bean
    public ConfigService nacosConfigService() throws Exception {
        Properties properties = new Properties();

        // 设置Nacos服务器地址（必须）
        properties.put(PropertyKeyConst.SERVER_ADDR, serverAddr);

        // 设置命名空间（如果不是public）
        if (namespace != null && !namespace.trim().isEmpty()) {
            properties.put(PropertyKeyConst.NAMESPACE, namespace);
        }

        // 设置认证信息
//        properties.put(PropertyKeyConst.USERNAME, username);
//        properties.put(PropertyKeyConst.PASSWORD, password);

        // 可以添加更多配置，如超时时间等
//        properties.put(PropertyKeyConst.CONFIG_LONG_POLL_TIMEOUT, "30000");

        return ConfigFactory.createConfigService(properties);
    }
}
