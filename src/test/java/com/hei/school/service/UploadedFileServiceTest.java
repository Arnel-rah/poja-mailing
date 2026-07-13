package com.hei.school.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hei.school.file.bucket.BucketComponent;
import com.hei.school.model.JUploadedFile;
import com.hei.school.model.UploadStatus;
import com.hei.school.model.UploadedFile;
import com.hei.school.repository.JUploadedFileRepository;
import java.io.File;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class UploadedFileServiceTest {

  @Mock private JUploadedFileRepository repository;
  @Mock private BucketComponent bucketComponent;

  @InjectMocks private UploadedFileService service;

  private JUploadedFile persistedEntity;

  @BeforeEach
  void setUp() {
    persistedEntity = new JUploadedFile();
    persistedEntity.setId(UUID.randomUUID().toString());
    persistedEntity.setFileName("cat.png");
    persistedEntity.setEmail("nel@gmail.com");
    persistedEntity.setStatus(UploadStatus.PENDING);
    persistedEntity.setCreatedDate(Instant.now());
  }

  @Test
  void should_persist_metadata_and_upload_original_to_bucket() {
    MultipartFile file =
        new MockMultipartFile("file", "cat.png", "image/png", "fake-image-bytes".getBytes());
    when(repository.save(any(JUploadedFile.class))).thenReturn(persistedEntity);

    UploadedFile.Submission submission = service.persistAndUpload(file, "nel@gmail.com");

    assertThat(submission.uploadedFileId()).isEqualTo(persistedEntity.getId());
    assertThat(submission.bucketKey()).contains("cat.png");
    assertThat(submission.uploadedFile().email()).isEqualTo("nel@gmail.com");
    verify(bucketComponent).upload(any(File.class), anyString());
    verify(repository).save(any(JUploadedFile.class));
  }

  @Test
  void should_reject_empty_file() {
    MultipartFile emptyFile = new MockMultipartFile("file", "empty.png", "image/png", new byte[0]);

    assertThrows(
        IllegalArgumentException.class, () -> service.persistAndUpload(emptyFile, "nel@gmail.com"));
  }

  @Test
  void should_reject_non_png_file() {
    MultipartFile jpgFile =
        new MockMultipartFile("file", "cat.jpg", "image/jpeg", "fake-bytes".getBytes());

    assertThrows(
        IllegalArgumentException.class, () -> service.persistAndUpload(jpgFile, "nel@gmail.com"));
  }

  @Test
  void should_return_all_uploaded_files() {
    when(repository.findAll()).thenReturn(List.of(persistedEntity));

    List<UploadedFile> result = service.findAll();

    assertThat(result).hasSize(1);
    assertThat(result.get(0).id()).isEqualTo(persistedEntity.getId());
  }
}
