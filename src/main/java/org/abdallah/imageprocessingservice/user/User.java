package org.abdallah.imageprocessingservice.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;
import org.abdallah.imageprocessingservice.base.BaseEntity;

import java.util.Date;

@Entity
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
public class User extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    @JsonIgnore
    private String password;

}
