package com.example.src;

class AuditService {
    private final LoggerService loggerService;

    public AuditService(LoggerService loggerService) {
        this.loggerService = loggerService;
    }

    public void audit(String message) {
        loggerService.log("AUDIT: " + message);
    }
}