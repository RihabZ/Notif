package com.rihab.interventions.service;


import com.rihab.interventions.entities.Notification;
import com.rihab.interventions.entities.User;
import com.rihab.interventions.repos.NotificationRepository;
import com.rihab.interventions.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class RealTimeNotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void sendNotification(Long userId, String message, Long idImage) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            Notification notification = Notification.builder()
                .message(message)
                .createdAt(new Date())
                .seen(false)
                .user(user)
                .idImage(idImage)
                .build();
            notificationRepository.save(notification);

            messagingTemplate.convertAndSendToUser(
                user.getUsername(),
                "/queue/notifications",
                notification.getMessage()
            );
        }
    }

    public List<Notification> getNotificationsForUser(User user) {
        return notificationRepository.findByUserAndSeenFalse(user);
    }

    public List<Notification> getAllNotificationsForUser(User user) {
        return notificationRepository.findByUser(user); // Nouvelle méthode pour toutes les notifications
    }
    
    public Notification save(Notification notification) {
        return notificationRepository.save(notification);
    }

    public Notification findById(Long id) {
        return notificationRepository.findById(id).orElse(null);
    }

    
    
}
