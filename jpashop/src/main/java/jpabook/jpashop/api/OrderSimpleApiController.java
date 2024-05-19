package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * xToOne (ManyToOne, OneToOne ...)
 * Order
 * Order -> Member (ManyToOne)
 * Order -> Delivery (OneToOne)
 * */

@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;
    private final OrderSimpleQueryRepository orderSimpleQueryRepository;

    /**
     * V1. 엔티티 직접 노출 (Deprecated)
     * - Hibernate5Module 모듈 등록, LAZY=null 처리
     * - 양방향 관계 문제 발생 -> @JsonIgnore
     *
     * jackson 라이브러리와 뷰 프레임워크 부분에서 문제가 발생할 수 있음
     *  > 엔티티를 직접 노출 시키기 위해서는 Hibernate5Module 등록과 @JsonIgnore 처리 필요
     *  > 즉시 로딩 때문에 연관관계가 필요 없는 경우에도 데이터를 조회해서 성능 문제가 발생할 수 있음
     *   - 즉시 로딩은 성능 튜닝이 어렵기 떄문에
     *   - 지연 로딩을 기본으로 하고, 성능 최적화가 필요한 경우에는 폐치 조인을 사용(V3)
     */
    @GetMapping("/api/v1/simple-orders")
    public List<Order> orderV1() {
        List<Order> all = orderRepository.findAllByCriteria(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName(); //Lazy 강제 초기화
            order.getDelivery().getAddress(); //Lazy 강제 초기화
        }
        return all;
    }

    /**
     * V2. 엔티티를 조회해서 DTO로 변환(fetch join 사용X) (Deprecated)
     * - 단점: 지연로딩으로 쿼리 N번 호출
     * - order 결과가 4개면 최악의 경우 1 + 4(member) + 4(delivery) 실행 됨
     */
    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> orderV2() {
        List<Order> orders = orderRepository.findAllByCriteria(new OrderSearch());

        /**
         * N + 1문제(주문 쿼리 1개에 N번의 쿼리가 추가 실행 됨)
         * 주문결과가 2ROW여서 아래 2번 실행
         * SimpleOrderDto.LAZY 초기화에 의해 member, delivery 쿼리 각각 실행
         * */
        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());

        return result;
    }

    /**
     * V3. 엔티티를 조회해서 DTO로 변환(fetch join 사용O) (Recommended)
     * - fetch join으로 쿼리 1번 호출
     * - 엔티티를 폐치 조인 처리해서 1번에 조회
     *  > 폐치 조인으로 order -> member, order -> delivery 가 이미 조회 된 상태 이므로 지연로딩X
     */
    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> orderV3() {
        List<Order> orders = orderRepository.findAllWithMemberDelivery();
        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());
        return result;
    }

    /**
     * V4. JPA에서 DTO로 바로 조회
     * - 쿼리 1번 호출
     * - select 절에서 원하는 데이터만 선택해서 조회
     */
    @GetMapping("/api/v4/simple-orders")
    public List<OrderSimpleQueryDto> orderV4() {
        return orderSimpleQueryRepository.findOrderDtos();
    }

    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName(); //LAZY 초기화 
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress(); //LAZY 초기화
        }
    }
}
