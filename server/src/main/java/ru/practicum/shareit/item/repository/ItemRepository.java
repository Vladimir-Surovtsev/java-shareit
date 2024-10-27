package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerId(long ownerId);

    @Query("SELECT i FROM Item i WHERE i.available = true " +
            "AND (i.name ILIKE CONCAT('%', :text, '%') " +
            "OR i.description ILIKE CONCAT('%', :text, '%'))")
    List<Item> searchByText(@Param("text") String text);

    @Query("SELECT i FROM Item AS i " +
            "WHERE(i.request.id) IN :requestIds")
    List<Item> findByRequestId(List<Long> requestIds);

    void deleteByIdAndOwnerId(long userId, long itemId);

    List<Item> findAllByRequestId(long requestId);
}
