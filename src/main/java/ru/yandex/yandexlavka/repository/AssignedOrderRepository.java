package ru.yandex.yandexlavka.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.yandexlavka.model.AssignedOrder;
import ru.yandex.yandexlavka.model.Courier;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AssignedOrderRepository extends JpaRepository<AssignedOrder, Long> {
    List<AssignedOrder> findAssignedOrdersByDate(LocalDate date);
    List<AssignedOrder> findAssignedOrdersByDateAndCourier(LocalDate date, Courier courier);
    Optional<AssignedOrder> findAssignedOrdersByCourierCourierId(Long id);
}
