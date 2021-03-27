package net.erp.eveline.model.product;

public enum Quantification {
    UNITARY, // Elements allocated in the inventory individually, like shirts, tires, helmets, etc.
    PACKAGE, // Items that normally come in boxes, screws, zip ties, gloves. Bought for spare or pricing motives.
    COMPOSE, // Items composed or other items. We need to reinforce a maximum of 2 levels by code.
    INFINITE // Products that are not quantifiable and always are required, fees, manual labor charges, etc.
}
