package com.covoiturage.service;

import com.covoiturage.model.Notification;
import com.covoiturage.model.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    public void notifierEmail(User user, String message) {
        user.envoyerNotificationEmail(message);
    }

    public void notifierSMS(User user, String message) {
        user.envoyerNotificationSMS(message);
    }

    public List<Notification> getNotifications(User user) {
        return user.getNotifications();
    }
}
