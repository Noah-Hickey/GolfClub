package com.golfclub.member;

import com.golfclub.tournament.Tournament;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;

import java.time.LocalDate;
import java.util.HashSet;

@Entity
public class Member {
    @Id
    private Long id;
    private String name;
    private String address;
    private String email;
    private String phone;
    private LocalDate startDate;
    private Integer durationMonths;



}
