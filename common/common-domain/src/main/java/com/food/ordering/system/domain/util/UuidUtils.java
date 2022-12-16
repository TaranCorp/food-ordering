package com.food.ordering.system.domain.util;

import com.food.ordering.system.domain.exception.DomainException;
import com.food.ordering.system.domain.valueobject.BaseId;

import java.util.Optional;
import java.util.UUID;

public final class UuidUtils {
    private UuidUtils() {
    }

    public static <T extends UUID> String extractId(BaseId<T> id) {
        return Optional.ofNullable(id)
                .map(baseId -> {
                    if (baseId.getValue() == null) {
                        throw new DomainException("Cannot stringify null id");
                    }
                    return baseId.getValue().toString();
                })
                .orElseThrow(() -> new DomainException("Cannot extract null id"));
    }

}
