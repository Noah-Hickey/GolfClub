package com.golfclub.member;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MemberService {
    @Autowired
    private MemberRepository memberRepository;

    public List<Member> searchMembers(String name, String phone, String email) {
        if (name != null && !name.trim().isEmpty()) {
            return memberRepository.findByNameContainingIgnoreCase(name.trim());
        }
        if (phone != null && !phone.trim().isEmpty()) {
            return memberRepository.findByPhone(phone.trim());
        }
        if (email != null && !email.trim().isEmpty()) {
            return memberRepository.findByEmail(email.trim());
        }
        return memberRepository.findAll();
    }

    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    public Optional<Member> getMemberById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Member ID cannot be null");
        }
        return memberRepository.findById(id);
    }

    public Member createMember(Member member) {
        validateMember(member);

        // Check if email already exists //
        if (member.getEmail() != null && !memberRepository.findByEmail(member.getEmail()).isEmpty()) {
            throw new IllegalArgumentException("Member with email " + member.getEmail() + " already exists");
        }

        // Set start date if not provided //
        if (member.getStartDate() == null) {
            member.setStartDate(LocalDate.now());
        }

        return memberRepository.save(member);
    }

    public Member updateMember(Long id, Member updatedMember) {
        Member existingMember = memberRepository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException("Member with ID " + id + " not found"));

        validateMember(updatedMember);

        // Check if email is being changed to one that already exists //
        if (updatedMember.getEmail() != null &&
                !updatedMember.getEmail().equals(existingMember.getEmail()) &&
                !memberRepository.findByEmail(updatedMember.getEmail()).isEmpty()) {
            throw new IllegalArgumentException("Member with email " + updatedMember.getEmail() + " already exists");
        }

        // Update fields //
        existingMember.setName(updatedMember.getName());
        existingMember.setAddress(updatedMember.getAddress());
        existingMember.setEmail(updatedMember.getEmail());
        existingMember.setPhone(updatedMember.getPhone());
        existingMember.setDurationMonths(updatedMember.getDurationMonths());

        // Don't update start date through this method //
        if (updatedMember.getStartDate() != null) {
            existingMember.setStartDate(updatedMember.getStartDate());
        }

        return memberRepository.save(existingMember);
    }

    public void deleteMember(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException("Member with ID " + id + " not found"));

        // Check if member is enrolled in any active tournaments //
        if (!member.getTournaments().isEmpty()) {
            throw new IllegalStateException("Cannot delete member who is enrolled in tournaments. Remove from tournaments first.");
        }

        memberRepository.delete(member);
    }

    public boolean isMembershipActive(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("Member with ID " + memberId + " not found"));

        if (member.getStartDate() == null || member.getDurationMonths() == null) {
            return false;
        }

        LocalDate endDate = member.getStartDate().plusMonths(member.getDurationMonths());
        return LocalDate.now().isBefore(endDate) || LocalDate.now().isEqual(endDate);
    }

    public LocalDate getMembershipExpiryDate(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("Member with ID " + memberId + " not found"));

        if (member.getStartDate() == null || member.getDurationMonths() == null) {
            return null;
        }

        return member.getStartDate().plusMonths(member.getDurationMonths());
    }

    public Member renewMembership(Long memberId, Integer additionalMonths) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("Member with ID " + memberId + " not found"));

        if (additionalMonths == null || additionalMonths <= 0) {
            throw new IllegalArgumentException("Additional months must be positive");
        }

        if (member.getDurationMonths() == null) {
            member.setDurationMonths(additionalMonths);
        } else {
            member.setDurationMonths(member.getDurationMonths() + additionalMonths);
        }

        return memberRepository.save(member);
    }

    public List<Member> getExpiredMembers() {
        List<Member> allMembers = memberRepository.findAll();
        return allMembers.stream()
                .filter(member -> {
                    if (member.getStartDate() == null || member.getDurationMonths() == null) {
                        return false;
                    }
                    LocalDate endDate = member.getStartDate().plusMonths(member.getDurationMonths());
                    return LocalDate.now().isAfter(endDate);
                })
                .toList();
    }

    public List<Member> getMembersExpiringWithin(int days) {
        LocalDate cutoffDate = LocalDate.now().plusDays(days);
        List<Member> allMembers = memberRepository.findAll();

        return allMembers.stream()
                .filter(member -> {
                    if (member.getStartDate() == null || member.getDurationMonths() == null) {
                        return false;
                    }
                    LocalDate endDate = member.getStartDate().plusMonths(member.getDurationMonths());
                    return endDate.isAfter(LocalDate.now()) && endDate.isBefore(cutoffDate);
                })
                .toList();
    }

    private void validateMember(Member member) {
        if (member == null) {
            throw new IllegalArgumentException("Member cannot be null");
        }

        if (member.getName() == null || member.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Member name is required");
        }

        if (member.getEmail() == null || member.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Member email is required");
        }

        if (!isValidEmail(member.getEmail())) {
            throw new IllegalArgumentException("Invalid email format");
        }

        if (member.getDurationMonths() != null && member.getDurationMonths() <= 0) {
            throw new IllegalArgumentException("Duration months must be positive");
        }
    }

    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");
    }
}

// Exception class //
class MemberNotFoundException extends RuntimeException {
    public MemberNotFoundException(String message) {
        super(message);
    }
}
