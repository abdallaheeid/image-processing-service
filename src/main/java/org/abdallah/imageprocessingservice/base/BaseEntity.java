package org.abdallah.imageprocessingservice.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import jakarta.xml.bind.annotation.*;
import lombok.*;
import org.abdallah.imageprocessingservice.utils.Views;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;
import java.util.UUID;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@XmlRootElement
@Data
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Setter(AccessLevel.NONE)
    @XmlTransient
    @JsonIgnore
    private Long id;

    @NonNull
    @Setter(AccessLevel.NONE)
    @Column(unique = true)
    @XmlID
    @JsonView(Views.Public.class)
    private String uuid = UUID.randomUUID().toString();

    @NonNull
    @Setter(AccessLevel.NONE)
    @CreatedDate
    private Date createdDate;

    @NonNull
    @Setter(AccessLevel.NONE)
    @LastModifiedDate
    private Date lastModifiedAt;

    @CreatedBy
    @Setter(AccessLevel.NONE)
    private Long createdBy;

    @LastModifiedBy
    @Setter(AccessLevel.NONE)
    private Long lastModifiedBy;

}
