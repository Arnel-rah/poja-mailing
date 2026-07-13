package com.hei.school.model;

import jakarta.persistence.*;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "uploaded_file")
@Getter
@Setter
@NoArgsConstructor
public class JUploadedFile {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @Column(nullable = false)
  private String fileName;

  @Column(nullable = false)
  private String email;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private UploadStatus status = UploadStatus.PENDING;

  private String bwFileUrl;

  @CreationTimestamp
  @Column(updatable = false)
  private Instant createdDate;
}
