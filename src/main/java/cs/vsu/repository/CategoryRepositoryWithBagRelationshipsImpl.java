package cs.vsu.repository;

import cs.vsu.domain.Category;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hibernate.annotations.QueryHints;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class CategoryRepositoryWithBagRelationshipsImpl implements CategoryRepositoryWithBagRelationships {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Category> fetchBagRelationships(Optional<Category> category) {
        return category.map(this::fetchProducts);
    }

    @Override
    public Page<Category> fetchBagRelationships(Page<Category> categories) {
        return new PageImpl<>(fetchBagRelationships(categories.getContent()), categories.getPageable(), categories.getTotalElements());
    }

    @Override
    public List<Category> fetchBagRelationships(List<Category> categories) {
        return Optional.of(categories).map(this::fetchProducts).orElse(Collections.emptyList());
    }

    Category fetchProducts(Category result) {
        return entityManager
            .createQuery(
                "select category from Category category left join fetch category.products where category is :category",
                Category.class
            )
            .setParameter("category", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<Category> fetchProducts(List<Category> categories) {
        return entityManager
            .createQuery(
                "select distinct category from Category category left join fetch category.products where category in :categories",
                Category.class
            )
            .setParameter("categories", categories)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
    }
}
