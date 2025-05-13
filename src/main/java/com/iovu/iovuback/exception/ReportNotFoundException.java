package com.iovu.iovuback.exception;

/**
 * 404: Report not found
 */
public class ReportNotFoundException extends RuntimeException {
    public ReportNotFoundException(Object id) {
        super("Report not found: " + id);
    }
}
