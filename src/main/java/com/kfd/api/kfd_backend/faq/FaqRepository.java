package com.kfd.api.kfd_backend.faq;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;


@Repository
public interface FaqRepository extends JpaRepository<Faq, UUID> {
    // find all FAQs matching a specific status, order by display order
    List<Faq> findByStatusOrderByDisplayOrderAsc(FaqStatus status);

}
