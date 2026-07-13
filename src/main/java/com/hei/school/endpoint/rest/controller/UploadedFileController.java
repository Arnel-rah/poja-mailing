package com.hei.school.endpoint.rest.controller;

import com.hei.school.endpoint.event.EventProducer;
import com.hei.school.endpoint.event.model.UploadedFileSubmitted;
import com.hei.school.model.UploadedFile;
import com.hei.school.service.UploadedFileService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@AllArgsConstructor
public class UploadedFileController {

  private final UploadedFileService service;
  private final EventProducer<UploadedFileSubmitted> eventProducer;

  @GetMapping("/uploaded-files")
  public ResponseEntity<List<UploadedFile>> findAll() {
    return ResponseEntity.ok(service.findAll());
  }

  @PostMapping(value = "/uploaded-files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<UploadedFile> submit(
      @RequestParam("file") MultipartFile file, @RequestParam("email") String email) {

    UploadedFile.Submission submission = service.persistAndUpload(file, email);

    var event =
        UploadedFileSubmitted.builder()
            .uploadedFileId(submission.uploadedFileId())
            .bucketKey(submission.bucketKey())
            .email(email)
            .build();
    eventProducer.accept(List.of(event));

    return ResponseEntity.status(HttpStatus.ACCEPTED).body(submission.uploadedFile());
  }
}
