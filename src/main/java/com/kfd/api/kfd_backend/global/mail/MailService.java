package com.kfd.api.kfd_backend.global.mail;

import com.kfd.api.kfd_backend.inquiry.InquiryRequestDTO;

/**
 * Service interface for sending emails.
 */
public interface MailService {

    /**
     * Sends an inquiry email to the configured KFD staff recipient.
     */
    void sendInquiryEmail(InquiryRequestDTO request);

    /**
     * Sends a password reset email to the given user.
     */
    void sendPasswordResetEmail(String toEmail, String resetLink);
}
