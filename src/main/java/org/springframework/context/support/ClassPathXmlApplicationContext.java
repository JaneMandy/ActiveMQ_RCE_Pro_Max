package org.springframework.context.support;

public class ClassPathXmlApplicationContext extends Throwable{
    private String message;

    public ClassPathXmlApplicationContext(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}