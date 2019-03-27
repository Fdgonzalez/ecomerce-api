package com.cloud.ecomerce.user.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Student {
    @Id
    private long id;

    private User user;
}
