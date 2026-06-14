package com.kfd.api.kfd_backend.inquiry;

/**
 * Service interface for sending inquiry emails.
 * Abstracts the mail-sending logic to allow easy testing and swapping of implementations.
 */
public interface MailService {

    /**
     * Sends an inquiry email to the configured KFD staff recipient.
     *
     * @param request the inquiry details from the public form submission
     */
    void sendInquiryEmail(InquiryRequestDTO request);
}
