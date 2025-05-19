package com.avmerkez.searchservice.listener;

import com.avmerkez.searchservice.model.MallDocument;
import com.avmerkez.searchservice.repository.MallSearchRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MallEventListener {
    private static final Logger logger = LoggerFactory.getLogger(MallEventListener.class);
    private final MallSearchRepository mallSearchRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "mall-events", groupId = "search-service")
    public void handleMallCreatedEvent(String eventJson) {
        try {
            var node = objectMapper.readTree(eventJson);
            MallDocument doc = new MallDocument();
            doc.setId(node.get("id").asLong());
            doc.setName(node.get("name").asText());
            doc.setCity(node.get("city").asText());
            doc.setDistrict(node.get("district").asText());
            doc.setDescription(node.get("description").asText());
            double lat = node.get("latitude").asDouble();
            double lon = node.get("longitude").asDouble();
            doc.setLocation(lat + "," + lon);
            mallSearchRepository.save(doc);
            logger.info("MallCreatedEvent alındı ve Elasticsearch'e eklendi: {}", doc);
        } catch (Exception e) {
            logger.error("MallCreatedEvent işlenirken hata: {}", eventJson, e);
        }
    }
} 