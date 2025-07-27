package com.golfclub.member;

import java.util.List;

public interface MemberRepository {

    public Member findByNameContainingIgnoreCase(String name);
    public Member findByPhone(String phone);
    public Member findByEmail(String email);
}
