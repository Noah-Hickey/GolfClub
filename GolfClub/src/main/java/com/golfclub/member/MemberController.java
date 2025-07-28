package com.golfclub.member;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class MemberController {

    @Autowired
    private MemberRepository memberRepository;

    @GetMapping
    public List<Member> searchMembers() {
        @RequestParam(required = false) String name,
        @RequestParam(required = false) String phone,
        @RequestParam(required = false) String email){
            // Add logic for searching
            if (name != null) return memberRepo.findByNameContainingIgnoreCase(name);
            if (phone != null) return memberRepo.findByPhone(phone);
            if (email != null) return memberRepo.findByEmail(email);
            return memberRepo.findAll();
        }
    }

    @PostMapping
    public Member addMember(@RequestBody Member member) {
        return memberRepo.save(member);
    }

    @GetMapping("/{id}")
    public Member getMember(@PathVariable Long id) {
        return memberRepo.findById(id).orElseThrow();
    }
}

