package com.food.ordering.system.ordering.outbox;

public interface OutboxScheduler {
    void processOutboxMessage();
}
