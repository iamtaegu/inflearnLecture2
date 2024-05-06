package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepositoryOld;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

// junit 실행할 때 spring하고 같이 실행
@RunWith(SpringRunner.class)
// springboot를 띄운 상태에서 테스트, 없으면 @Autowired 다 실패
@SpringBootTest
// 테스트 끝나면 db에 commit이 아닌 rollback 하겠다
@Transactional
public class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired
    MemberRepositoryOld memberRepository;

    @Test
    @Rollback(false)
    public void 회원가입() throws Exception {
        //given
        Member member = new Member();
        member.setName("Oh");

        //when
        Long saveId = memberService.join(member);

        //then
        Assertions.assertEquals(member, memberRepository.findOne(saveId));
     }
     
     @Test(expected = IllegalStateException.class)
     public void 중복_회원_예외() throws Exception {
         //given
         Member member1 = new Member();
         member1.setName("oh1");

         Member member2 = new Member();
         member2.setName("oh1");
         //when
         memberService.join(member1);
         memberService.join(member2);
         //then
         fail("예외가 발생해야 한다.");
      }
}
