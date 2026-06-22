package com.kfd.api.kfd_backend.global.mail;

import com.kfd.api.kfd_backend.inquiry.InquiryRequestDTO;
import com.kfd.api.kfd_backend.settings.contact.ContactSettingsService;
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
    private final ContactSettingsService contactSettingsService;

    public ResendMailServiceImpl(
            @Value("${resend.api.key}") String apiKey,
            ContactSettingsService contactSettingsService) {
        this.contactSettingsService = contactSettingsService;
        this.restClient = RestClient.builder()
                .baseUrl("https://api.resend.com")
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
    }

    @Override
    public void sendInquiryEmail(InquiryRequestDTO request) {
        String recipientEmail = contactSettingsService.getDefaultSettings().contactEmail();

        ResendEmailRequest payload = new ResendEmailRequest(
                "KFD Website <onboarding@resend.dev>",
                List.of(recipientEmail),
                "[KFD Inquiry - " + request.inquiryType() + "] " + request.subject(),
                buildInquiryHtmlBody(request),
                request.senderEmail()
        );
        sendViaResend(payload, "Inquiry from " + request.senderEmail());
    }

    @Override
    public void sendPasswordResetEmail(String toEmail, String resetLink) {
        ResendEmailRequest payload = new ResendEmailRequest(
                "KFD Admin <onboarding@resend.dev>",
                List.of(toEmail),
                "Reset Your KFD Admin Password",
                buildPasswordResetHtmlBody(resetLink),
                "noreply@kfd.org"
        );
        sendViaResend(payload, "Password reset for " + toEmail);
    }

    private void sendViaResend(ResendEmailRequest payload, String contextLog) {
        try {
            restClient.post()
                    .uri("/emails")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .toBodilessEntity();
            log.info("Email sent via Resend successfully: {}", contextLog);
        } catch (RestClientResponseException e) {
            log.error("Resend API error [{}]: {}", e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new MailSendFailedException("Failed to send email. Please try again later.");
        } catch (Exception e) {
            log.error("Unexpected error calling Resend API: {}", e.getMessage(), e);
            throw new MailSendFailedException("Failed to send email. Please try again later.");
        }
    }

    private String buildInquiryHtmlBody(InquiryRequestDTO request) {
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

    private String buildPasswordResetHtmlBody(String resetLink) {
        return """
                <html>
                <body style="font-family: Arial, sans-serif; color: #333; line-height: 1.6;">
                    <h2 style="color: #2c5f2d;">Password Reset Request</h2>
                    <p>We received a request to reset your password for the KFD Admin Dashboard.</p>
                    <p>Click the button below to set a new password. This link will expire in 15 minutes.</p>
                    <br>
                    <a href="%s" style="background-color: #2c5f2d; color: white; padding: 12px 24px; text-decoration: none; border-radius: 4px; font-weight: bold;">Reset Password</a>
                    <br><br>
                    <p>If the button doesn't work, you can copy and paste this link into your browser:</p>
                    <p><a href="%s">%s</a></p>
                    <p>If you did not request this reset, please ignore this email.</p>
                </body>
                </html>
                """.formatted(resetLink, resetLink, resetLink);
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
