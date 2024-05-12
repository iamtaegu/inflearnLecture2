package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.MemberRepositoryOld;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
/**
 * 트랜잭션, 영속성 컨텍스트
 * readOnly=true 영속성 컨텍스트를 플러시 하지 않으므로 성능 향상
 */
@Transactional
//@AllArgsConstructor
// AllArgsConstructor는 모든 멤버변수를 갖고 생성자를 만들지만
// RequiredArgsConstructor final 멤버변수만 갖고 생성자를 만든다
@RequiredArgsConstructor
public class MemberService {

    // fianl 키워드를 추가하면 컴파일 시점에 memberRepository를 설정하지 않는 오류 체크 가능
    private final MemberRepository memberRepository;
/*
 * 필드 주입 대신에 생성자 주입을 사용하자

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
        return memberRepository.findById(memberId).get();
    }

    @Transactional
    public void update(Long id, String name) {
        Member member = memberRepository.findById(id).get(); // 영속 상태
        //Member member = memberRepository.findOne(id); // memberRepositoryOld
        member.setName(name); // 영속 상태 엔티티 값이 변경돼어 Transactional에 의해 DB반영
    }

    @Transactional
    public void delete(Long id) {
        memberRepository.delete(findOne(id));
    }
}
