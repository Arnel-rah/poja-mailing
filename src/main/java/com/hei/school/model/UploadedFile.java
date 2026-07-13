package com.hei.school.model;

import java.time.Instant;

public record UploadedFile(String id, String fileName, String email, Instant createdDate) {

  public static UploadedFile fromEntity(JUploadedFile entity) {
    return new UploadedFile(
        entity.getId(), entity.getFileName(), entity.getEmail(), entity.getCreatedDate());
  }
}
