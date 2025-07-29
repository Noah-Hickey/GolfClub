package com.golfclub.member;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/members")
public class MemberController {

        @Autowired
        private MemberService memberService;

    @GetMapping
    public List<Member> searchMembers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String email) {
        return memberService.searchMembers(name, phone, email);
    }

    @PostMapping
    public Member addMember(@RequestBody Member member) {
        return memberService.createMember(member);
    }

    @GetMapping("/{id}")
    public Member getMember(@PathVariable Long id) {
        return memberService.getMemberById(id)
                .orElseThrow(() -> new MemberNotFoundException("Member not found"));
    }

    @PutMapping("/{id}")
    public Member updateMember(@PathVariable Long id, @RequestBody Member member) {
        return memberService.updateMember(id, member);
    }

    @DeleteMapping("/{id}")
    public void deleteMember(@PathVariable Long id) {
        memberService.deleteMember(id);
    }

    @GetMapping("/{id}/active")
    public boolean isMembershipActive(@PathVariable Long id) {
        return memberService.isMembershipActive(id);
    }

    @PostMapping("/{id}/renew")
    public Member renewMembership(@PathVariable Long id, @RequestParam Integer months) {
        return memberService.renewMembership(id, months);
    }
}

