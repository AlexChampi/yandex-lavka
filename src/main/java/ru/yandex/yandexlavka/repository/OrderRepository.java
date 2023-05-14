package ru.yandex.yandexlavka.repository;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.yandexlavka.model.AssignedOrder;
import ru.yandex.yandexlavka.model.Courier;
import ru.yandex.yandexlavka.model.Order;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByCompletedTimeAndAssignedOrder(LocalDateTime completedTime, AssignedOrder assignedOrder);

    List<Order> findAllByCompletedTimeBetweenAndAssignedOrder_Courier(
            LocalDateTime start,
            LocalDateTime end,
            Courier courier);

    List<Order> findAllByRegionsAndCompletedTimeAndAssignedOrder(@NotNull Integer regions, LocalDateTime completedTime, AssignedOrder assignedOrder);
}

