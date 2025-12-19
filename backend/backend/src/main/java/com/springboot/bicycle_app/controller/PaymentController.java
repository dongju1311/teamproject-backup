package com.springboot.bicycle_app.controller;

import com.springboot.bicycle_app.dto.purchase.CartItemRequestDto;
import com.springboot.bicycle_app.dto.purchase.OrderRequestDto;
import com.springboot.bicycle_app.dto.purchase.OrderResponseDto;
import com.springboot.bicycle_app.dto.purchase.TossPayDto;
import com.springboot.bicycle_app.service.purchase.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/payment")
public class PaymentController {
    private final OrderService orderService;

    @Autowired
    public PaymentController(OrderService orderService){
        this.orderService = orderService;
    }

    private void setUidFromSession(OrderRequestDto dto, UserDetails user) {
        if (user == null) {
            throw new RuntimeException("로그인이 필요한 서비스입니다.");
        }
        dto.setUserId(user.getUsername());
    }

    @PostMapping("/request")
    public ResponseEntity<?> requestPayment(@RequestBody OrderRequestDto dto) {
        try {
            var order = orderService.createOrder(dto);
            return ResponseEntity.ok(Map.of(
                    "message", "주문 생성 완료",
                    "orderId", order.getOrderId()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/confirm")
    public ResponseEntity<?> confirmPayment(@RequestBody TossPayDto dto) {
        try {
            Object response = orderService.confirmPayment(dto);
            return ResponseEntity.ok(response);

        } catch (PaymentFailedException e) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", e.getMessage(), "code", "PAYMENT_ERR"));

        } catch (Exception e) {
            return ResponseEntity
                    .internalServerError()
                    .body(Map.of("message", "서버 내부 오류가 발생했습니다."));
        }
    }
    @PostMapping("/order")
    public List<OrderResponseDto> findList(
            @RequestBody(required = false) OrderRequestDto dto,
            @AuthenticationPrincipal UserDetails user) {
        if (user == null) {
            return List.of();
        }

        if (dto == null) {
            dto = new OrderRequestDto();
        }
        dto.setUserId(user.getUsername());

        return orderService.findList(dto);
        }
}
