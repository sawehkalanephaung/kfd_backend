package com.kfd.api.kfd_backend.inquiry;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;

@Service
@Slf4j
public class ResendMailServiceImpl implements MailService {

    private final RestClient restClient;
    private final String recipientEmail;

    public ResendMailServiceImpl(
            @Value("${resend.api.key}") String apiKey,
            @Value("${app.mail.recipient}") String recipientEmail) {
        this.recipientEmail = recipientEmail;
        this.restClient = RestClient.builder()
                .baseUrl("https://api.resend.com")
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
    }

    @Override
    public void sendInquiryEmail(InquiryRequestDTO request) {
        ResendEmailRequest payload = new ResendEmailRequest(
                "KFD Website <onboarding@resend.dev>",
                List.of(recipientEmail),
                "[KFD Inquiry - " + request.inquiryType() + "] " + request.subject(),
                buildHtmlBody(request),
                request.senderEmail()
        );

        try {
            restClient.post()
                    .uri("/emails")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .toBodilessEntity();

            log.info("Inquiry email sent via Resend from {} ({})",
                    request.senderName(), request.senderEmail());

        } catch (RestClientResponseException e) {
            log.error("Resend API error [{}]: {}", e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new MailSendFailedException("Failed to send your inquiry. Please try again later.");
        } catch (Exception e) {
            log.error("Unexpected error calling Resend API: {}", e.getMessage(), e);
            throw new MailSendFailedException("Failed to send your inquiry. Please try again later.");
        }
    }

    private String buildHtmlBody(InquiryRequestDTO request) {
        return """
                <html>
                <body style="font-family: Arial, sans-serif; color: #333; line-height: 1.6;">
                    <h2 style="color: #2c5f2d;">New Contact Inquiry — KFD Website</h2>
                    <hr style="border: 1px solid #ddd;">
                    <table style="width: 100%%; border-collapse: collapse;">
                        <tr>
                            <td style="padding: 8px; font-weight: bold; width: 140px;">Name:</td>
                            <td style="padding: 8px;">%s</td>
                        </tr>
                        <tr>
                            <td style="padding: 8px; font-weight: bold;">Email:</td>
                            <td style="padding: 8px;"><a href="mailto:%s">%s</a></td>
                        </tr>
                        <tr>
                            <td style="padding: 8px; font-weight: bold;">Inquiry Type:</td>
                            <td style="padding: 8px;">%s</td>
                        </tr>
                        <tr>
                            <td style="padding: 8px; font-weight: bold;">Subject:</td>
                            <td style="padding: 8px;">%s</td>
                        </tr>
                    </table>
                    <hr style="border: 1px solid #ddd;">
                    <h3>Message:</h3>
                    <div style="background: #f9f9f9; padding: 16px; border-radius: 8px; white-space: pre-wrap;">%s</div>
                    <hr style="border: 1px solid #ddd;">
                    <p style="font-size: 12px; color: #999;">
                        This email was sent automatically from the KFD public website contact form.
                        You can reply directly to this email to respond to the sender.
                    </p>
                </body>
                </html>
                """.formatted(
                escapeHtml(request.senderName()),
                escapeHtml(request.senderEmail()),
                escapeHtml(request.senderEmail()),
                escapeHtml(request.inquiryType()),
                escapeHtml(request.subject()),
                escapeHtml(request.message())
        );
    }

    private String escapeHtml(String input) {
        if (input == null) return "";
        return input
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
