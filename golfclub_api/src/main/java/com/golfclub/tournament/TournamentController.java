package com.golfclub.tournament;

import com.golfclub.member.Member;
import com.golfclub.member.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/tournaments")
public class TournamentController {

    @Autowired
    private TournamentService tournamentService;

    @GetMapping
    public List<Tournament> searchTournaments(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {
        return tournamentService.searchTournaments(location, startDate);
    }

    @PostMapping
    public Tournament addTournament(@RequestBody Tournament tournament) {
        return tournamentService.createTournament(tournament);
    }

    @GetMapping("/{id}")
    public Tournament getTournament(@PathVariable Long id) {
        return tournamentService.getTournamentById(id)
                .orElseThrow(() -> new TournamentNotFoundException("Tournament not found"));
    }

    @PutMapping("/{id}")
    public Tournament updateTournament(@PathVariable Long id, @RequestBody Tournament tournament) {
        return tournamentService.updateTournament(id, tournament);
    }

    @DeleteMapping("/{id}")
    public void deleteTournament(@PathVariable Long id) {
        tournamentService.deleteTournament(id);
    }

    @PostMapping("/{tournamentId}/members/{memberId}")
    public Tournament addMemberToTournament(@PathVariable Long tournamentId, @PathVariable Long memberId) {
        return tournamentService.addMemberToTournament(tournamentId, memberId);
    }

    @DeleteMapping("/{tournamentId}/members/{memberId}")
    public Tournament removeMemberFromTournament(@PathVariable Long tournamentId, @PathVariable Long memberId) {
        return tournamentService.removeMemberFromTournament(tournamentId, memberId);
    }

    @GetMapping("/{id}/members")
    public Set<Member> getTournamentMembers(@PathVariable Long id) {
        return tournamentService.getTournamentParticipants(id);
    }

    @GetMapping("/upcoming")
    public List<Tournament> getUpcomingTournaments() {
        return tournamentService.getUpcomingTournaments();
    }

    @GetMapping("/active")
    public List<Tournament> getActiveTournaments() {
        return tournamentService.getActiveTournaments();
    }

    @GetMapping("/{id}/prize-pool")
    public float getTotalPrizePool(@PathVariable Long id) {
        return tournamentService.calculateTotalPrizePool(id);
    }
}
