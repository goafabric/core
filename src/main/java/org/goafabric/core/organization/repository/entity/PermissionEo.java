package org.goafabric.core.organization.repository.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "permission")
//@EntityListeners(AuditTrailListener.class)
public class PermissionEo {
    @Id
    @org.springframework.data.annotation.Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public String id;

    public String category;

    public String type;

    @Version //optimistic locking
    public Long version;

}