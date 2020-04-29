package com.mapohl.gtfsprocessor.taxiride.configuration;

import com.google.common.collect.Maps;
import com.mapohl.gtfsprocessor.taxiride.domain.NYCTaxiZone;
import com.mapohl.gtfsprocessor.taxiride.domain.utils.NYCTaxiZoneLoader;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.Map;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;

@Configuration
public class BaseTaxiRideConfiguration {

    @Bean
    public KafkaAdmin kafkaAdmin(@Value("${kafka.bootstrap-servers}") String bootstrapServersStr) {
        Map<String, Object> configs = Maps.newHashMap();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServersStr);

        return new KafkaAdmin(configs);
    }

    @Bean
    @Scope(value = SCOPE_SINGLETON)
    public Map<Integer, NYCTaxiZone> nycTaxiZoneIndex() {
        return NYCTaxiZoneLoader.loadNYCTaxiZoneIndex();
    }

}
