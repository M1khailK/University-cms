package ua.foxminded.university.customexceptions;

public class MailSenderServiceException extends RuntimeException{
    public MailSenderServiceException(String message,Throwable cause){
        super(message, cause);
    }
}
