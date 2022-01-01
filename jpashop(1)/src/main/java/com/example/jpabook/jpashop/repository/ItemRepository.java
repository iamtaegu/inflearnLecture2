package com.example.jpabook.jpashop.repository;

import com.example.jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {

    private final EntityManager em;

    public void save(Item item) {
            if (item.getId() == null) {
                // 식별자가 없으면 영속화 
                em.persist(item);
            } else {
                // 식별자가 있으면 병합
                // 상품 수정에 준영속 상태인 상품 엔티티는 id 값이 있어 병합 수행
                em.merge(item);
            }
    }

    public Item findOne(Long id) {
        return em.find(Item.class, id);
    }

    public List<Item> findAll() {
        return em.createQuery("select i from Item i", Item.class)
                .getResultList();
    }

}
