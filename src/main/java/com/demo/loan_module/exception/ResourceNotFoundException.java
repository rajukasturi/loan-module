package com.demo.loan_module.exception;

/**
 * Exception thrown when a requested resource is not found.
 * Used for 404 scenarios.
 */
public class ResourceNotFoundException extends LoanProcessingException {
    
    private final String resourceType;
    private final Long resourceId;
    
    public ResourceNotFoundException(String resourceType, Long resourceId) {
        super("LOAN-404-001", "%s with id %d not found", resourceType, resourceId);
        this.resourceType = resourceType;
        this.resourceId = resourceId;
    }
    
    public String getResourceType() {
        return resourceType;
    }
    
    public Long getResourceId() {
        return resourceId;
    }
}
