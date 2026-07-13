package com.hei.school.service;

import static java.io.File.createTempFile;

import com.hei.school.file.bucket.BucketComponent;
import com.hei.school.model.JUploadedFile;
import com.hei.school.model.UploadedFile;
import com.hei.school.repository.JUploadedFileRepository;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@AllArgsConstructor
public class UploadedFileService {

  private final JUploadedFileRepository repository;
  private final BucketComponent bucketComponent;

  @Transactional
  @SneakyThrows
  public UploadedFile.Submission persistAndUpload(MultipartFile file, String email) {
    validate(file);

    String bucketKey = "uploads/original/" + UUID.randomUUID() + "-" + file.getOriginalFilename();
    File tempFile = createTempFile("upload-", ".png");
    try (FileOutputStream out = new FileOutputStream(tempFile)) {
      out.write(file.getBytes());
    }
    bucketComponent.upload(tempFile, bucketKey);

    JUploadedFile entity = new JUploadedFile();
    entity.setFileName(file.getOriginalFilename());
    entity.setEmail(email);
    JUploadedFile saved = repository.save(entity);

    return new UploadedFile.Submission(saved.getId(), bucketKey, UploadedFile.fromEntity(saved));
  }

  private void validate(MultipartFile file) {
    if (file.isEmpty()) {
      throw new IllegalArgumentException("Fichier vide");
    }
    String contentType = file.getContentType();
    if (contentType == null || !contentType.equals("image/png")) {
      throw new IllegalArgumentException("Seuls les fichiers PNG sont acceptés");
    }
  }

  public List<UploadedFile> findAll() {
    return repository.findAll().stream().map(UploadedFile::fromEntity).toList();
  }
}
