package ru.yandex.yandexlavka.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.yandexlavka.model.Courier;

@Repository
public interface CourierRepository extends JpaRepository<Courier, Long> {
}
