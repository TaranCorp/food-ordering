package com.food.ordering.system.order.service.domain.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.food.ordering.system.order.service.domain.exception.OrderDomainException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ObjectMapperHelper {
    private static final String UNEXPECTED_ERROR_WHILE_MAPPING_TO_JSON = "Unexpected error thrown while mapping to json";
    private static final String NULL_PAYLOAD_ERROR_MSG = "Payload cannot be null";

    private final ObjectMapper objectMapper;

    public ObjectMapperHelper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public <T extends Object> String serializePayload(T payload) {
        return Optional.ofNullable(payload)
                .map(orderPayload -> {
                    if (objectMapper.canSerialize(payload.getClass())) {
                        return getSerializedPayload(payload);
                    }
                    throw new OrderDomainException(UNEXPECTED_ERROR_WHILE_MAPPING_TO_JSON);
                })
                .orElseThrow(() -> new OrderDomainException(NULL_PAYLOAD_ERROR_MSG));
    }

    private <T extends Object> String getSerializedPayload(T payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new OrderDomainException(UNEXPECTED_ERROR_WHILE_MAPPING_TO_JSON);
        }
    }
}
