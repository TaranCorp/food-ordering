package com.food.ordering.system.order.service.domain.outbox.model.approval;

public class OrderApprovalEventProduct {
    private String id;
    private Integer quantity;

    public OrderApprovalEventProduct(String id, Integer quantity) {
        this.id = id;
        this.quantity = quantity;
    }

    public String getId() {
        return id;
    }

    public Integer getQuantity() {
        return quantity;
    }
}
