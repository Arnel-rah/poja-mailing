package com.hei.school.model;

import java.time.Instant;

public record UploadedFile(
    String id,
    String fileName,
    String email,
    UploadStatus status,
    String bwFileUrl,
    Instant createdDate) {

  public static UploadedFile fromEntity(JUploadedFile e) {
    return new UploadedFile(
        e.getId(),
        e.getFileName(),
        e.getEmail(),
        e.getStatus(),
        e.getBwFileUrl(),
        e.getCreatedDate());
  }

  public record Submission(String uploadedFileId, String bucketKey, UploadedFile uploadedFile) {}
}
