package com.hei.school.service.event;

import com.hei.school.endpoint.event.model.SendEmailRequested;
import com.hei.school.mail.Email;
import com.hei.school.mail.Mailer;
import jakarta.mail.internet.InternetAddress;
import java.util.List;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class SendEmailRequestedService implements Consumer<SendEmailRequested> {

  private final Mailer mailer;

  @Override
  public void accept(SendEmailRequested event) {
    try {
      log.info("Starting email processing for: {} ", event.getTo());

      InternetAddress recipient = new InternetAddress(event.getTo());

      var email =
          new Email(
              recipient,
              List.of(),
              List.of(),
              "Hello world from Poja",
              "... world! This email was sent asynchronously.",
              List.of());

      mailer.accept(email);

      log.info("email successfully sent to: {}", event.getTo());

    } catch (Exception e) {
      log.error("Failed to send email to: {}", event.getTo(), e);
      throw new RuntimeException("Email sending failed", e);
    }
  }
}
