package jpabook.jpashop.service;

import com.sun.javadoc.MemberDoc;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
//@AllArgsConstructor
// AllArgsConstructor는 모든 멤버변수를 갖고 생성자를 만들지만
// RequiredArgsConstructor final 멤버변수만 갖고 생성자를 만든다
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
/*

    //생성자가 하나만 있는 경우에는 AUTO INJECTION
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }
*/

    /**
     * 회원 가입
     */
    @Transactional
    public Long join(Member member) {
        validateDuplicateMember(member);//중복회원검증
        memberRepository.save(member);
        return member.getId();
    }

    /*
    *  중복회원검증
    *  */
    private void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    //회원 전체 조회
    @Transactional(readOnly = true)
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Member findOne(Long memberId) {
        return memberRepository.findOne(memberId);
    }

    @Transactional
    public void update(Long id, String name) {
        Member member = memberRepository.findOne(id); // 영속 상태
        member.setName(name); // 영속 상태 엔티티 값이 변경돼어 Transactional에 의해 DB반영
    }
}
