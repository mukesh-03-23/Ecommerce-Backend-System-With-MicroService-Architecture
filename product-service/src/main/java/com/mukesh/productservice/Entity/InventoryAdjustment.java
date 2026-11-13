package com.mukesh.productservice.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "inventory_adjustments")
public class InventoryAdjustment {
    @Id
    private String id;
    protected InventoryAdjustment() {}
    public InventoryAdjustment(String id) { this.id = id; }
    public String getId() { return id; }
}
