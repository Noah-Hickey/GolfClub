package com.golfclub.tournament;

import com.golfclub.member.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

public class TournamentController {

    @Autowired
    private TournamentRepository tournamentRepo;
    @Autowired
    private MemberRepository memberRepo;

    @GetMapping
    public List<Tournament> searchTournaments(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {
        if (location != null) return tournamentRepo.findByLocationContainingIgnoreCase(location);
        if (startDate != null) return tournamentRepo.findByStartDate(startDate);
        return tournamentRepo.findAll();
    }

    @PostMapping
    public Tournament addTournament(@RequestBody Tournament tournament) {
        return tournamentRepo.save(tournament);
    }

    @GetMapping("/{id}")
    public Tournament getTournament(@PathVariable Long id) {
        return tournamentRepo.findById(id).orElseThrow();
    }

    @PostMapping("/{tournamentId}/members/{memberId}")
    public Tournament addMemberToTournament(@PathVariable Long tournamentId, @PathVariable Long memberId) {
        Tournament tournament = tournamentRepo.findById(tournamentId).orElseThrow();
        Member member = memberRepo.findById(memberId).orElseThrow();
        tournament.getParticipants().add(member);
        return tournamentRepo.save(tournament);
    }

    @GetMapping("/{id}/members")
    public Set<Member> getTournamentMembers(@PathVariable Long id) {
        Tournament tournament = tournamentRepo.findById(id).orElseThrow();
        return tournament.getParticipants();
    }
}
