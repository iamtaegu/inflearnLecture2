package com.example.jpabook.jpashop.service;

import com.example.jpabook.jpashop.domain.Member;
import com.example.jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
// 영속성 컨텍스트
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberServcie {

    // 필드 주입 지양
    //@Autowired MemberRepository memberRepository;
    // 생성자 주입 지향
    private final  MemberRepository memberRepository;

    /**
     * 회원가입
     */
    @Transactional //변경
    public Long join(Member member) {
        validateDuplicateMember(member); //중복 회원 검증
        memberRepository.save(member);
        return member.getId();
    }

    /**
     * 검증 로직이 있어도 멀티 쓰레드 상황을 고려해 테이블 회원명 컬럼에 유니크 제약 조건을 추가하는 것이 안전
     */
    private void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    /**
     * 전체 회원 조회
     */
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    public Member findOne(Long memberId) {
        return memberRepository.findOne(memberId);
    }

}
