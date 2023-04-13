package ua.foxminded.university.customexceptions;

public class MailSenderServiceException extends Exception{
    public MailSenderServiceException(String message,Throwable cause){
        super(message, cause);
    }
}
