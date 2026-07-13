package com.hei.school.service.event;

import com.hei.school.endpoint.event.model.UploadedFileSubmitted;
import com.hei.school.file.bucket.BucketComponent;
import com.hei.school.mail.Email;
import com.hei.school.mail.Mailer;
import com.hei.school.model.JUploadedFile;
import com.hei.school.model.UploadStatus;
import com.hei.school.repository.JUploadedFileRepository;
import com.hei.school.service.ImageProcessor;
import jakarta.mail.internet.InternetAddress;
import java.io.File;
import java.io.FileOutputStream;
import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UploadedFileSubmittedService implements Consumer<UploadedFileSubmitted> {

  private final BucketComponent bucketComponent;
  private final JUploadedFileRepository repository;
  private final Mailer mailer;

  @SneakyThrows
  @Override
  public void accept(UploadedFileSubmitted event) {
    JUploadedFile entity = repository.findById(event.getUploadedFileId()).orElseThrow();

    try {
      File original = bucketComponent.download(event.getBucketKey());
      byte[] grayscaleBytes = ImageProcessor.toGrayscale(original);

      String bwKey = "uploads/bw/" + entity.getId() + ".png";
      File bwFile = File.createTempFile("bw-", ".png");
      try (FileOutputStream out = new FileOutputStream(bwFile)) {
        out.write(grayscaleBytes);
      }
      bucketComponent.upload(bwFile, bwKey);

      String presignedUrl = bucketComponent.presign(bwKey, Duration.ofMinutes(30)).toString();

      entity.setBwFileUrl(presignedUrl);
      entity.setStatus(UploadStatus.PROCESSED);
      repository.save(entity);

      InternetAddress recipient = new InternetAddress(entity.getEmail());
      mailer.accept(
          new Email(
              recipient,
              List.of(),
              List.of(),
              "Votre image est prête",
              "Voici le lien vers votre image en noir et blanc : " + presignedUrl,
              List.of()));

    } catch (Exception e) {
      entity.setStatus(UploadStatus.FAILED);
      repository.save(entity);
    }
  }
}
