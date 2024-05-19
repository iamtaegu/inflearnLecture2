package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.query.OrderFlatDto;
import jpabook.jpashop.repository.order.query.OrderQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;

    /**
     * V1. 엔티티 직접 노출 (Deprecated)
     * - Hiberante5Module 모듈 등록, LAZY=null 처리
     * - 양방향 관계 문제 발생 -> @JsonIgnore
     *
     * orderItem, item 관계를 직접 초기화하면 Hibernate5Module 설정에 의해 엔티티를 JSON으로 생성
     * 양방향 연관관계면 무한 루프에 걸리지 않게 한곳에 @JsonIgnore를 추가
     */
    @GetMapping("/api/v1/orders")
    public List<Order> orderV1() {
        List<Order> all = orderRepository.findAllByCriteria(new OrderSearch());
        //iter + tab
        for (Order order : all) {
            order.getMember().getName(); // db에서 Member 엔티티를 아직 가져오지 않았고, proxy 객체인 상태
            order.getDelivery().getAddress();
            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(o -> o.getItem().getName());
        }
        return all;
    }

    /**
     * V2. 엔티티를 조회해서 DTO로 변환 (fetch join 사용X)
     * - 지연 로딩으로 너무 많은 SQL 실행
     * - order 1번 / member,address N번(order 조회 수 만큼) / orderItem N번(order 조회 수 만큼)
     *  > item N번(orderItem 조회 수 만큼)
     * - 지연 로딩은 영속성 컨텍스트에 있으면 영속성 컨텍스트에 있는 엔티티를 사용하고
     *  > 없으면 SQL을 실행함
     *  > 영속성 컨텍스트에서 이미 로딩한 회원 엔티티를 추가로 조회하면 SQL을 실행하지 않음
     */
    @GetMapping("/api/v2/orders")
    public List<OrderDto> orderV2() {
        List<Order> orders = orderRepository.findAllByCriteria(new OrderSearch());
        List<OrderDto> collect = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(toList());
        return collect;
    }

    /**
     * V3. 엔티티를 조회해서 DTO로 변환(fetch join 사용O, 페이징 불가능)
     * - 페이징 시에는 N 부분을 포기해야함
     *  > 대신에 batch fetch size 옵션 주면 N -> 1 쿼리로 변경 가능
     * - 페이징 불가능
     *  > 컬렉션 폐치 조인을 사용하면 페이징이 불가능하기 때문에 하이버네이트는 모든 데이터를 DB에서 읽어오고
     *  > 메모리에서 페이징하기 때문에 위험함
     * - 컬렉션 폐치 조인은 1개만 사용할 수 있고, 둘 이상에 폐치 조인을 사용하면 안됨
     *  > 데이터가 부정합하게 조회될 수 있음
     */
    @GetMapping("/api/v3/orders")
    public List<OrderDto> orderV3() {
        List<Order> orders = orderRepository.findAllWithItem();

        for (Order order : orders) {
            System.out.println("order ref = " + order + " id=" + order.getId());
        }

        List<OrderDto> result = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(toList());

        return result;
    }

    /**
     * V3.1 엔티티를 조회해서 DTO로 변환 페이징 고려
     * - ToOne 관계만 우선 모두 폐치 조인으로 최적화
     * - 컬렉션 관계는 hibernate.default_batch_fetch_size, @BatchSize로 최적화
     *
     * 장점
     *  > 쿼리 호출 수가 1 + N -> 1 + 1로 최적화 됨
     *  > V3(조인)보다 DB데이터 전송량이 최적화 됨
     *  > V3보다 쿼리 호출 수는 증가하지만, DB 데이터 전송량이 감소하고, 페이징 가능
     */
    @GetMapping("/api/v3.1/orders")
    public List<OrderDto> orderV3_page(@RequestParam(value = "offset", defaultValue = "0")  int offset,
                                       @RequestParam(value = "limit", defaultValue = "100") int limit
                                       ) {
        List<Order> orders = orderRepository.findAllWithMemberDelivery(offset, limit);

        List<OrderDto> result = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(toList());

        return result;
    }

    /**
     * V4. JPA에서 DTO 직접 조회
     */
    @GetMapping("/api/v4/orders")
    public List<OrderQueryDto> orderV4() {
        return orderQueryRepository.findOrderQueryDtos();
    }

    /**
     * V5. JPA에서 DTO 직접 조회 - 컬렉션 조회 최적화
     */
    @GetMapping("/api/v5/orders")
    public List<OrderQueryDto> orderV5() {
        return orderQueryRepository.findALlByDto_optimization();
    }

    @GetMapping("/api/v6/orders")
    public List<OrderFlatDto> orderV6() {
        return orderQueryRepository.findALlByDto_flat();
    }

    @Getter
    private class OrderDto {

        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        private List<OrderItemDto> orderItems;

        public OrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName(); //LAZY 초기화
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress(); //LAZY 초기화
            orderItems = order.getOrderItems().stream()
                    .map(OrderItemDto::new)
                    .collect(toList());

            // 엔티티가 노출 되는 이슈
//            orderItems = order.getOrderItems();
//            orderItems.stream().forEach(o -> o.getItem().getName());
        }

        @Getter
        private class OrderItemDto {
            private String itemName; //상품 명
            private int orderPrice; //주문 가격
            private int count; //주문 수량
            public OrderItemDto(OrderItem orderItem) {
                itemName = orderItem.getItem().getName();
                orderPrice = orderItem.getOrderPrice();
                count = orderItem.getCount();
            }
        }

    }

}
