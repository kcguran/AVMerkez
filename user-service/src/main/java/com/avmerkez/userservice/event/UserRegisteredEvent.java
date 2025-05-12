package com.avmerkez.userservice.event;

import com.avmerkez.userservice.entity.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UserRegisteredEvent extends ApplicationEvent {
    private User user;

    public UserRegisteredEvent(Object source, User user) {
        super(source);
        this.user = user;
    }

}