package com.demo.loan_module.exception;

/**
 * Custom exception for loan processing errors.
 * Used when business rule validations fail during loan processing.
 */
public class LoanProcessingException extends RuntimeException {
    
    private final String errorCode;
    private final String errorMessage;
    private final Object[] errorParams;
    
    public LoanProcessingException(String errorCode, String errorMessage) {
        this(errorCode, errorMessage, (Object[]) null);
    }
    
    public LoanProcessingException(String errorCode, String errorMessage, Object... errorParams) {
        super(buildMessage(errorMessage, errorParams));
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.errorParams = errorParams;
    }
    
    public LoanProcessingException(String errorCode, String errorMessage, Throwable cause) {
        super(buildMessage(errorMessage, (Object[]) null), cause);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.errorParams = null;
    }
    
    @SuppressWarnings("varargs")
    private static String buildMessage(String message, Object... params) {
        if (params == null || params.length == 0) {
            return message;
        }
        return String.format(message.replace("{}", "%s"), params);
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public Object[] getErrorParams() {
        return errorParams;
    }
}
