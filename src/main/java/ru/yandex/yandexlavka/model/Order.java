package ru.yandex.yandexlavka.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity(name = "`order`")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long orderId;
    @NotNull
    Float weight;
    @NotNull
    Integer regions;
    @ElementCollection
    List<TimeInterval> deliveryHours;
    @NotNull
    Integer cost;
    LocalDateTime completedTime;
    @ManyToOne(fetch = FetchType.LAZY)
    AssignedOrder assignedOrder;
}