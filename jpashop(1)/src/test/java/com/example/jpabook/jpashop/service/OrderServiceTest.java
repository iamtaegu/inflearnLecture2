package com.example.jpabook.jpashop.service;

import com.example.jpabook.jpashop.domain.Address;
import com.example.jpabook.jpashop.domain.Member;
import com.example.jpabook.jpashop.domain.Order;
import com.example.jpabook.jpashop.domain.OrderStatus;
import com.example.jpabook.jpashop.domain.item.Book;
import com.example.jpabook.jpashop.domain.item.Item;
import com.example.jpabook.jpashop.exception.NotEnoughStockException;
import com.example.jpabook.jpashop.repository.OrderRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class OrderServiceTest {

    @PersistenceContext
    EntityManager em;

    @Autowired OrderService orderService;
    @Autowired OrderRepository orderRepository;

    @Test
    public void 상품주문() throws Exception {
        //given
        Member member = createMember();
        Item item = createBook("테스트책", 10, 20000);
        int orderCount = 2;

        //when
        Long orderId = orderService.order(member.getId(), item.getId(), orderCount);

        //then
        Order getOrder = orderRepository.findOne(orderId);

        assertEquals(OrderStatus.ORDER, getOrder.getStatus());
        Assert.assertEquals("주문한 상품 종류수가 정확해야 한다.", 1, getOrder.getOrderItems().size());
        assertEquals(20000*2, getOrder.getTotalPrice());
        assertEquals(8, item.getStockQuantity());

     }

    @Test(expected = NotEnoughStockException.class)
    public void 상품주문_재고수량초과() throws Exception
    {
        //Given
        Member member = createMember();
        Item item = createBook("시골 JPA", 10, 10000);

        int orderCount = 11;

        //When
        orderService.order(member.getId(), item.getId(), orderCount);

        //Then
        fail("재고 수량 부족 예외가 발생해야 한다.");
    }

    @Test
    public void 주문취소() throws Exception {
        //given
        Member member = createMember();
        Item item = createBook("테스트 jpa", 10, 10000);
        int orderCount = 2;
        Long orderId = orderService.order(member.getId(), item.getId(), orderCount);

        //when
        orderService.cancelOrder(orderId);

        //then
        Order getOrder = orderRepository.findOne(orderId);

        assertEquals(OrderStatus.CANCEL, getOrder.getStatus());
        assertEquals(10, item.getStockQuantity());
     }

    private Item createBook(String name, int stockQuantity, int price) {
        Book book = new Book();
        book.setName(name);
        book.setStockQuantity(stockQuantity);
        book.setPrice(price);
        em.persist(book);
        return book;
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("서울", "경기", "123-123"));
        em.persist(member);
        return member;
    }
}
