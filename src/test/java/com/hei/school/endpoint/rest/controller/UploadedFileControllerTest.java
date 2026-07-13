package com.hei.school.endpoint.rest.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hei.school.endpoint.event.EventProducer;
import com.hei.school.endpoint.event.model.UploadedFileSubmitted;
import com.hei.school.model.UploadStatus;
import com.hei.school.model.UploadedFile;
import com.hei.school.service.UploadedFileService;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UploadedFileController.class)
class UploadedFileControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private UploadedFileService service;
  @MockBean private EventProducer<UploadedFileSubmitted> eventProducer;

  @Test
  void should_return_all_uploaded_files() throws Exception {
    UploadedFile existing =
        new UploadedFile(
            "abc-123",
            "cat.png",
            "nel@example.com",
            UploadStatus.PROCESSED,
            "https://bucket/bw.png",
            Instant.now());
    when(service.findAll()).thenReturn(List.of(existing));

    mockMvc
        .perform(get("/uploaded-files"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value("abc-123"))
        .andExpect(jsonPath("$[0].email").value("nel@example.com"));
  }

  @Test
  void should_accept_submission_and_return_202() throws Exception {
    MockMultipartFile file =
        new MockMultipartFile("file", "cat.png", "image/png", "fake-bytes".getBytes());

    UploadedFile dto =
        new UploadedFile(
            "abc-123", "cat.png", "nel@example.com", UploadStatus.PENDING, null, Instant.now());
    UploadedFile.Submission submission =
        new UploadedFile.Submission("abc-123", "uploads/original/abc-123-cat.png", dto);

    when(service.persistAndUpload(any(), anyString())).thenReturn(submission);

    mockMvc
        .perform(multipart("/uploaded-files").file(file).param("email", "nel@example.com"))
        .andExpect(status().isAccepted())
        .andExpect(jsonPath("$.id").value("abc-123"))
        .andExpect(jsonPath("$.status").value("PENDING"));

    verify(eventProducer).accept(any(List.class));
  }
}
