package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
// @AllArgsConstructor 3. 롬복 모든 멤버변수
@RequiredArgsConstructor // 4. fianl 멤버변수만 생성자 생성 & 주입
public class MemberRepositoryOld {

//    @PersistenceContext
//    @Autowired // 1. @PersistenceContext를 지원

    private final EntityManager em;
    /*
    // 2. 생성자 주입
    public MemberRepository(EntityManager em) {
        this.em = em;
    }*/

    public void save(Member member){ em.persist(member); }

    public Member findOne(Long id) { return em.find(Member.class, id); }

    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    public List<Member> findByName(String name) {
        return em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", name)
                .getResultList();
    }

}
