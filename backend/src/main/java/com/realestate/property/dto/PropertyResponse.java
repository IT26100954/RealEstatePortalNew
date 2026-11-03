package com.realestate.property.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public class PropertyResponse {
    private String id;
    private String ownerId;
    private String title;
    private String description;
    private String city;
    private String address;
    private String price;
    private String propertyType;
    private String status;

    public PropertyResponse() { }
    public String getId() { return id; }
    public void setId(String id) { this.id=id; }
    public String getOwnerId() { return ownerId; }
    public void setOwnerId(String ownerId) { this.ownerId=ownerId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title=title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description=description; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city=city; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address=address; }
    public String getPrice() { return price; }
    public void setPrice(String price) { this.price=price; }
    public String getPropertyType() { return propertyType; }
    public void setPropertyType(String propertyType) { this.propertyType=propertyType; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status=status; }
}
