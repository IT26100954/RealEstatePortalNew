package com.realestate.common;

public class PropertyReference {
    private final String id;
    private final String ownerId;
    private final String status;
    public PropertyReference(String id, String ownerId, String status) {
        this.id=id; this.ownerId=ownerId; this.status=status;
    }
    public String getId() { return id; }
    public String getOwnerId() { return ownerId; }
    public String getStatus() { return status; }
}
