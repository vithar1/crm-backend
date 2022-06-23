package cs.vsu.repository;

import cs.vsu.domain.Order;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface OrderRepositoryWithBagRelationships {
    Optional<Order> fetchBagRelationships(Optional<Order> order);

    List<Order> fetchBagRelationships(List<Order> orders);

    Page<Order> fetchBagRelationships(Page<Order> orders);
}
