package com.mapohl.gtfsprocessor.taxiride.configuration;

import com.mapohl.gtfsprocessor.genericproducer.domain.EntityMapper;
import com.mapohl.gtfsprocessor.genericproducer.services.DownstreamEntityEmissionService;
import com.mapohl.gtfsprocessor.genericproducer.services.sources.BasicEntityQueue;
import com.mapohl.gtfsprocessor.taxiride.domain.taxiride.intermediateprice.IntermediatePrice;
import com.mapohl.gtfsprocessor.taxiride.domain.taxiride.intermediateprice.IntermediatePriceMapper;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class IntermediatePriceConfiguration {

    @Value("${kafka.intermediateprice.topic}")
    private String kafkaTopic;

    @Bean
    public NewTopic intermediatePriceTopic(
            @Value("${kafka.intermediateprice.partition-count}") int partitionCount,
            @Value("${kafka.intermediateprice.replication-factor}") short replicationFactor) {
        return new NewTopic(this.kafkaTopic, partitionCount, replicationFactor);
    }

    @Bean
    public KafkaTemplate<Integer, IntermediatePrice> intermediatePriceKafkaTemplate(ProducerFactory<Integer, IntermediatePrice> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public EntityMapper<String, IntermediatePrice> intermediatePriceEntityMapper() {
        return new IntermediatePriceMapper(5);
    }

    @Bean
    public DownstreamEntityEmissionService intermediatePriceEntityEmissionService(EntityMapper<String, IntermediatePrice> intermediatePriceEntityMapper,
                                                                                  KafkaTemplate<Integer, IntermediatePrice> intermediatePriceKafkaTemplate) {
        return new DownstreamEntityEmissionService(new BasicEntityQueue<>(intermediatePriceEntityMapper), this.kafkaTopic, intermediatePriceKafkaTemplate);
    }
}
