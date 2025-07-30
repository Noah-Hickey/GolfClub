package com.golfclub.tournament;

import com.golfclub.member.Member;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Tournament {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate startDate;
    private LocalDate endDate;
    private String location;
    private float entryFee;
    private float cashPrize;

    @ManyToMany
    @JoinTable(
            name = "tournament_participants",
            joinColumns = @JoinColumn(name = "tournament_id"),
            inverseJoinColumns = @JoinColumn(name = "member_id")
    )
    private Set<Member> participants = new HashSet<>();

    // Constructors
    public Tournament() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public float getEntryFee() {
        return entryFee;
    }

    public void setEntryFee(float entryFee) {
        this.entryFee = entryFee;
    }

    public float getCashPrize() {
        return cashPrize;
    }

    public void setCashPrize(float cashPrize) {
        this.cashPrize = cashPrize;
    }

    public Set<Member> getParticipants() {
        return participants;
    }

    public void setParticipants(Set<Member> participants) {
    }
}