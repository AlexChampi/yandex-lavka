package ru.yandex.yandexlavka.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Courier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long courierId;
    @Enumerated(EnumType.STRING)
    CourierType courierType;
    @ElementCollection
    @CollectionTable
    List<Integer> regions;

    @ElementCollection
    @CollectionTable
    List<TimeInterval> workingHours;
}
