package ru.yandex.yandexlavka.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.*;

@Entity
@Getter
@Setter
public class AssignedOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long groupOrderId;

    @ManyToOne(fetch = FetchType.LAZY)
    Courier courier;

    LocalDate date;

    @OneToMany
    @JoinColumn(name = "assigned_order_group_order_id")
    Set<Order> orders;

    public AssignedOrder() {
        this.orders = new HashSet<>();
    }

    public void addOrder(Order order) {
        Objects.requireNonNull(order);
        orders.add(order);
    }

    public Long findFirstOrderDelivered() {
        Optional<Order> min = orders.stream().filter(o -> o.getCompletedTime() != null).min(Comparator.comparing(Order::getCompletedTime));
        return min.map(order -> order.orderId).orElse(null);
    }
}
