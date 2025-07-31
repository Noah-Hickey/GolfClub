package com.golfclub.tournament;

import com.golfclub.member.Member;
import com.golfclub.member.MemberRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class TournamentService{

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private MemberRepository memberRepository;

    public List<Tournament> searchTournaments(String location, LocalDate startDate) {
        if (location != null && !location.trim().isEmpty()) {
            return tournamentRepository.findByLocationContainingIgnoreCase(location.trim());
        }
        if (startDate != null) {
            return tournamentRepository.findByStartDate(startDate);
        }
        return tournamentRepository.findAll();
    }

    public List<Tournament> getAllTournaments() {
        return tournamentRepository.findAll();
    }

    public Optional<Tournament> getTournamentById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Tournament ID cannot be null");
        }
        return tournamentRepository.findById(id);
    }

    public Tournament createTournament(Tournament tournament) {
        validateTournament(tournament);
        return tournamentRepository.save(tournament);
    }

    public Tournament updateTournament(Long id, Tournament updatedTournament) {
        Tournament existingTournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new TournamentNotFoundException("Tournament with ID " + id + " not found"));

        validateTournament(updatedTournament);

        // Update fields //
        existingTournament.setStartDate(updatedTournament.getStartDate());
        existingTournament.setEndDate(updatedTournament.getEndDate());
        existingTournament.setLocation(updatedTournament.getLocation());
        existingTournament.setEntryFee(updatedTournament.getEntryFee());
        existingTournament.setCashPrize(updatedTournament.getCashPrize());

        return tournamentRepository.save(existingTournament);
    }

    public void deleteTournament(Long id) {
        Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new TournamentNotFoundException("Tournament with ID " + id + " not found"));

        // Check if tournament has already started //
        if (tournament.getStartDate() != null &&
                (tournament.getStartDate().isBefore(LocalDate.now()) || tournament.getStartDate().isEqual(LocalDate.now()))) {
            throw new IllegalStateException("Cannot delete a tournament that has already started");
        }

        tournamentRepository.delete(tournament);
    }

    public Tournament addMemberToTournament(Long tournamentId, Long memberId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new TournamentNotFoundException("Tournament with ID " + tournamentId + " not found"));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("Member with ID " + memberId + " not found"));

        // Business rule validations //
        validateMemberRegistration(tournament, member);

        tournament.getParticipants().add(member);
        return tournamentRepository.save(tournament);
    }

    public Tournament removeMemberFromTournament(Long tournamentId, Long memberId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new TournamentNotFoundException("Tournament with ID " + tournamentId + " not found"));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("Member with ID " + memberId + " not found"));

        // Check if tournament has already started
        if (tournament.getStartDate() != null && tournament.getStartDate().isBefore(LocalDate.now())) {
            throw new IllegalStateException("Cannot remove members from a tournament that has already started");
        }

        if (!tournament.getParticipants().contains(member)) {
            throw new IllegalArgumentException("Member is not registered for this tournament");
        }

        tournament.getParticipants().remove(member);
        return tournamentRepository.save(tournament);
    }

    public Set<Member> getTournamentParticipants(Long tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new TournamentNotFoundException("Tournament with ID " + tournamentId + " not found"));

        return tournament.getParticipants();
    }

    public int getParticipantCount(Long tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new TournamentNotFoundException("Tournament with ID " + tournamentId + " not found"));

        return tournament.getParticipants().size();
    }

    public boolean isMemberRegistered(Long tournamentId, Long memberId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new TournamentNotFoundException("Tournament with ID " + tournamentId + " not found"));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("Member with ID " + memberId + " not found"));

        return tournament.getParticipants().contains(member);
    }

    public List<Tournament> getUpcomingTournaments() {
        List<Tournament> allTournaments = tournamentRepository.findAll();
        return allTournaments.stream()
                .filter(tournament -> tournament.getStartDate() != null &&
                        tournament.getStartDate().isAfter(LocalDate.now()))
                .sorted((t1, t2) -> t1.getStartDate().compareTo(t2.getStartDate()))
                .toList();
    }

    public List<Tournament> getActiveTournaments() {
        LocalDate today = LocalDate.now();
        List<Tournament> allTournaments = tournamentRepository.findAll();

        return allTournaments.stream()
                .filter(tournament -> {
                    if (tournament.getStartDate() == null) return false;

                    LocalDate endDate = tournament.getEndDate() != null ?
                            tournament.getEndDate() : tournament.getStartDate();

                    return (tournament.getStartDate().isBefore(today) || tournament.getStartDate().isEqual(today)) &&
                            (endDate.isAfter(today) || endDate.isEqual(today));
                })
                .toList();
    }

    public List<Tournament> getPastTournaments() {
        LocalDate today = LocalDate.now();
        List<Tournament> allTournaments = tournamentRepository.findAll();

        return allTournaments.stream()
                .filter(tournament -> {
                    LocalDate endDate = tournament.getEndDate() != null ?
                            tournament.getEndDate() : tournament.getStartDate();
                    return endDate != null && endDate.isBefore(today);
                })
                .sorted((t1, t2) -> {
                    LocalDate end1 = t1.getEndDate() != null ? t1.getEndDate() : t1.getStartDate();
                    LocalDate end2 = t2.getEndDate() != null ? t2.getEndDate() : t2.getStartDate();
                    return end2.compareTo(end1); // Most recent first
                })
                .toList();
    }

    public float calculateTotalPrizePool(Long tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new TournamentNotFoundException("Tournament with ID " + tournamentId + " not found"));

        float entryFeeTotal = tournament.getEntryFee() * tournament.getParticipants().size();
        return entryFeeTotal + tournament.getCashPrize();
    }

    public List<Tournament> getTournamentsByDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start date and end date cannot be null");
        }

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }

        List<Tournament> allTournaments = tournamentRepository.findAll();
        return allTournaments.stream()
                .filter(tournament -> tournament.getStartDate() != null &&
                        !tournament.getStartDate().isBefore(startDate) &&
                        !tournament.getStartDate().isAfter(endDate))
                .sorted((t1, t2) -> t1.getStartDate().compareTo(t2.getStartDate()))
                .toList();
    }

    private void validateTournament(Tournament tournament) {
        if (tournament == null) {
            throw new IllegalArgumentException("Tournament cannot be null");
        }

        if (tournament.getStartDate() == null) {
            throw new IllegalArgumentException("Tournament start date is required");
        }

        if (tournament.getStartDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Tournament start date cannot be in the past");
        }

        if (tournament.getEndDate() != null && tournament.getEndDate().isBefore(tournament.getStartDate())) {
            throw new IllegalArgumentException("Tournament end date cannot be before start date");
        }

        if (tournament.getLocation() == null || tournament.getLocation().trim().isEmpty()) {
            throw new IllegalArgumentException("Tournament location is required");
        }

        if (tournament.getEntryFee() < 0) {
            throw new IllegalArgumentException("Entry fee cannot be negative");
        }

        if (tournament.getCashPrize() < 0) {
            throw new IllegalArgumentException("Cash prize cannot be negative");
        }
    }

    private void validateMemberRegistration(Tournament tournament, Member member) {
        // Check if tournament registration is still open //
        if (tournament.getStartDate() != null && tournament.getStartDate().isBefore(LocalDate.now())) {
            throw new IllegalStateException("Cannot register for a tournament that has already started");
        }

        // Check if member is already registered //
        if (tournament.getParticipants().contains(member)) {
            throw new IllegalArgumentException("Member is already registered for this tournament");
        }

        // Check if member's membership is active //
        if (member.getStartDate() != null && member.getDurationMonths() != null) {
            LocalDate membershipEnd = member.getStartDate().plusMonths(member.getDurationMonths());
            if (LocalDate.now().isAfter(membershipEnd)) {
                throw new IllegalStateException("Member's membership has expired. Cannot register for tournaments.");
            }
        }
    }
}

// Exception classes //
class TournamentNotFoundException extends RuntimeException {
    public TournamentNotFoundException(String message) {
        super(message);
    }
}

class MemberNotFoundException extends RuntimeException {
    public MemberNotFoundException(String message) {
        super(message);
    }
}
