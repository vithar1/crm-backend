package cs.vsu.repository;

import cs.vsu.domain.Image;
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
public class ImageRepositoryWithBagRelationshipsImpl implements ImageRepositoryWithBagRelationships {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Image> fetchBagRelationships(Optional<Image> image) {
        return image.map(this::fetchProducts);
    }

    @Override
    public Page<Image> fetchBagRelationships(Page<Image> images) {
        return new PageImpl<>(fetchBagRelationships(images.getContent()), images.getPageable(), images.getTotalElements());
    }

    @Override
    public List<Image> fetchBagRelationships(List<Image> images) {
        return Optional.of(images).map(this::fetchProducts).orElse(Collections.emptyList());
    }

    Image fetchProducts(Image result) {
        return entityManager
            .createQuery("select image from Image image left join fetch image.products where image is :image", Image.class)
            .setParameter("image", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<Image> fetchProducts(List<Image> images) {
        return entityManager
            .createQuery("select distinct image from Image image left join fetch image.products where image in :images", Image.class)
            .setParameter("images", images)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
    }
}
