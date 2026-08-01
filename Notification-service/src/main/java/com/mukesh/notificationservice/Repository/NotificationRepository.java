package com.mukesh.notificationservice.Repository;

import com.mukesh.notificationservice.Entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
