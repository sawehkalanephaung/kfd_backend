package com.kfd.api.kfd_backend.department;

import com.kfd.api.kfd_backend.cms.post.Post;
import com.kfd.api.kfd_backend.cms.post.PostRepository;
import com.kfd.api.kfd_backend.global.exception.ResourceNotFoundException;
import com.kfd.api.kfd_backend.team_member.TeamMember;
import com.kfd.api.kfd_backend.team_member.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final DepartmentContactRepository departmentContactRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final PostRepository postRepository;

    // ─── Mappers ────────────────────────────────────────────────────────────────

    private TeamMemberSummaryDTO toTeamMemberSummary(TeamMember m) {
        if (m == null) return null;
        return TeamMemberSummaryDTO.builder()
                .id(m.getId())
                .firstName(m.getFirstName())
                .lastName(m.getLastName())
                .title(m.getTitle())
                .headshotUrl(m.getHeadshotUrl())
                .displayOrder(m.getDisplayOrder())
                .isActive(m.getIsActive())
                .build();
    }

    private DepartmentContactResponseDTO toContactDto(DepartmentContact c) {
        return DepartmentContactResponseDTO.builder()
                .id(c.getId())
                .departmentId(c.getDepartment().getId())
                .name(c.getName())
                .role(c.getRole())
                .email(c.getEmail())
                .phone(c.getPhone())
                .address(c.getAddress())
                .websiteUrl(c.getWebsiteUrl())
                .socialLinks(c.getSocialLinks())
                .additionalDetails(c.getAdditionalDetails())
                .orderIndex(c.getOrderIndex())
                .build();
    }

    private PostSummaryDTO toPostSummary(Post p) {
        return PostSummaryDTO.builder()
                .id(p.getId())
                .title(p.getTitle())
                .slug(p.getSlug())
                .excerpt(p.getExcerpt())
                .featuredImageUrl(p.getFeaturedImageUrl())
                .status(p.getStatus() != null ? p.getStatus().name() : null)
                .publishedAt(p.getPublishedAt())
                .build();
    }

    private DepartmentAdminResponseDTO toAdminDto(Department d) {
        return DepartmentAdminResponseDTO.builder()
                .id(d.getId())
                .slug(d.getSlug())
                .name(d.getName())
                .headMember(toTeamMemberSummary(d.getHeadMember()))
                .bodyContent(d.getBodyContent())
                .logoId(d.getLogoId())
                .heroImageId(d.getHeroImageId())
                .status(d.getStatus())
                .orderIndex(d.getOrderIndex())
                .createdBy(d.getCreatedBy())
                .lastUpdatedBy(d.getLastUpdatedBy())
                .createdAt(d.getCreatedAt())
                .updatedAt(d.getUpdatedAt())
                .build();
    }

    private DepartmentPublicResponseDTO toPublicDto(Department d) {
        List<DepartmentContactResponseDTO> contacts = departmentContactRepository
                .findByDepartmentIdOrderByOrderIndex(d.getId())
                .stream().map(this::toContactDto).collect(Collectors.toList());

        List<TeamMemberSummaryDTO> members = teamMemberRepository
                .findByDepartmentIdOrderByDisplayOrderAsc(d.getId())
                .stream().map(this::toTeamMemberSummary).collect(Collectors.toList());

        List<PostSummaryDTO> posts = postRepository
                .findByDepartmentIdOrderByPublishedAtDesc(d.getId())
                .stream().map(this::toPostSummary).collect(Collectors.toList());

        return DepartmentPublicResponseDTO.builder()
                .id(d.getId())
                .slug(d.getSlug())
                .name(d.getName())
                .headMember(toTeamMemberSummary(d.getHeadMember()))
                .bodyContent(d.getBodyContent())
                .logoId(d.getLogoId())
                .heroImageId(d.getHeroImageId())
                .status(d.getStatus())
                .orderIndex(d.getOrderIndex())
                .contacts(contacts)
                .teamMembers(members)
                .posts(posts)
                .build();
    }

    // ─── Admin Operations ────────────────────────────────────────────────────────

    public Page<DepartmentAdminResponseDTO> getAllAdmin(Pageable pageable) {
        return departmentRepository.findAll(pageable).map(this::toAdminDto);
    }

    public DepartmentAdminResponseDTO getByIdAdmin(UUID id) {
        return toAdminDto(findOrThrow(id));
    }

    @Transactional
    public DepartmentAdminResponseDTO create(DepartmentAdminRequestDTO dto) {
        TeamMember headMember = resolveHeadMember(dto.getHeadMemberId());
        Department department = Department.builder()
                .slug(dto.getSlug())
                .name(dto.getName())
                .headMember(headMember)
                .bodyContent(dto.getBodyContent())
                .logoId(dto.getLogoId())
                .heroImageId(dto.getHeroImageId())
                .status(dto.getStatus() != null ? dto.getStatus() : "ACTIVE")
                .orderIndex(dto.getOrderIndex() != null ? dto.getOrderIndex() : 0)
                .build();
        return toAdminDto(departmentRepository.save(department));
    }

    @Transactional
    public DepartmentAdminResponseDTO update(UUID id, DepartmentAdminRequestDTO dto) {
        Department department = findOrThrow(id);
        department.setSlug(dto.getSlug());
        department.setName(dto.getName());
        department.setHeadMember(resolveHeadMember(dto.getHeadMemberId()));
        department.setBodyContent(dto.getBodyContent());
        department.setLogoId(dto.getLogoId());
        department.setHeroImageId(dto.getHeroImageId());
        if (dto.getStatus() != null) department.setStatus(dto.getStatus());
        if (dto.getOrderIndex() != null) department.setOrderIndex(dto.getOrderIndex());
        return toAdminDto(departmentRepository.save(department));
    }

    @Transactional
    public void delete(UUID id) {
        departmentRepository.delete(findOrThrow(id));
    }

    // ─── Public Operations ───────────────────────────────────────────────────────

    public List<DepartmentPublicResponseDTO> getAllPublic() {
        return departmentRepository.findAll().stream()
                .filter(d -> "ACTIVE".equalsIgnoreCase(d.getStatus()))
                .map(this::toPublicDto)
                .collect(Collectors.toList());
    }

    public DepartmentPublicResponseDTO getBySlugPublic(String slug) {
        Department d = departmentRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Department", "slug", slug));
        return toPublicDto(d);
    }

    // ─── Department Contacts ─────────────────────────────────────────────────────

    public List<DepartmentContactResponseDTO> getContacts(UUID departmentId) {
        findOrThrow(departmentId);
        return departmentContactRepository.findByDepartmentIdOrderByOrderIndex(departmentId)
                .stream().map(this::toContactDto).collect(Collectors.toList());
    }

    @Transactional
    public DepartmentContactResponseDTO addContact(UUID departmentId, DepartmentContactRequestDTO dto) {
        Department department = findOrThrow(departmentId);
        DepartmentContact contact = DepartmentContact.builder()
                .department(department)
                .name(dto.getName())
                .role(dto.getRole())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .address(dto.getAddress())
                .websiteUrl(dto.getWebsiteUrl())
                .socialLinks(dto.getSocialLinks())
                .additionalDetails(dto.getAdditionalDetails())
                .orderIndex(dto.getOrderIndex() != null ? dto.getOrderIndex() : 0)
                .build();
        return toContactDto(departmentContactRepository.save(contact));
    }

    @Transactional
    public DepartmentContactResponseDTO updateContact(UUID contactId, DepartmentContactRequestDTO dto) {
        DepartmentContact contact = departmentContactRepository.findById(contactId)
                .orElseThrow(() -> new ResourceNotFoundException("DepartmentContact", "id", contactId));
        contact.setName(dto.getName());
        contact.setRole(dto.getRole());
        contact.setEmail(dto.getEmail());
        contact.setPhone(dto.getPhone());
        contact.setAddress(dto.getAddress());
        contact.setWebsiteUrl(dto.getWebsiteUrl());
        contact.setSocialLinks(dto.getSocialLinks());
        contact.setAdditionalDetails(dto.getAdditionalDetails());
        if (dto.getOrderIndex() != null) contact.setOrderIndex(dto.getOrderIndex());
        return toContactDto(departmentContactRepository.save(contact));
    }

    @Transactional
    public void deleteContact(UUID contactId) {
        DepartmentContact contact = departmentContactRepository.findById(contactId)
                .orElseThrow(() -> new ResourceNotFoundException("DepartmentContact", "id", contactId));
        departmentContactRepository.delete(contact);
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────────

    private Department findOrThrow(UUID id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", id));
    }

    /**
     * Resolves a TeamMember by UUID.
     * Returns null if headMemberId is null — departments can exist without a head assigned yet.
     */
    private TeamMember resolveHeadMember(UUID headMemberId) {
        if (headMemberId == null) return null;
        return teamMemberRepository.findById(headMemberId)
                .orElseThrow(() -> new ResourceNotFoundException("TeamMember", "id", headMemberId));
    }
}
