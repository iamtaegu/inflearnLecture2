package com.example.jpabook.jpashop.web;

import com.example.jpabook.jpashop.domain.Member;
import com.example.jpabook.jpashop.domain.Order;
import com.example.jpabook.jpashop.domain.OrderSearch;
import com.example.jpabook.jpashop.domain.item.Item;
import com.example.jpabook.jpashop.repository.ItemRepository;
import com.example.jpabook.jpashop.repository.MemberRepository;
import com.example.jpabook.jpashop.repository.OrderRepository;
import com.example.jpabook.jpashop.service.ItemService;
import com.example.jpabook.jpashop.service.MemberServcie;
import com.example.jpabook.jpashop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final MemberServcie memberServcie;
    private final OrderService orderService;
    private final ItemService itemService;


    @GetMapping(value = "/order")
    public String createForm(Model model){
        List<Member> members = memberServcie.findMembers();
        List<Item> items = itemService.findItems();

        model.addAttribute("members",members);
        model.addAttribute("items", items);

        return "order/orderForm";
    }

    @PostMapping(value = "/order")
    public String order(@RequestParam("memberId") Long memberId, @RequestParam("itemId") Long itemId,
                        @RequestParam("count") int count) {
        orderService.order(memberId, itemId, count);
        return "redirect:/orders";
    }

    @GetMapping(value = "/orders")
    public String orderList(@ModelAttribute("orderSearch") OrderSearch orderSearch, Model model) {

        List<Order> orders = orderService.findOrders(orderSearch);
        model.addAttribute("orders", orders);

        return "order/orderList";
    }

    @PostMapping(value = "/order/{orderId}/cancel")
    public String cancelOrder(@PathVariable("orderId") Long orderId) {

        orderService.cancelOrder(orderId);

        return "redirect:/orders";
    }
}
