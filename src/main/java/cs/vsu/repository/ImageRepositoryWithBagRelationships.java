package cs.vsu.repository;

import cs.vsu.domain.Image;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface ImageRepositoryWithBagRelationships {
    Optional<Image> fetchBagRelationships(Optional<Image> image);

    List<Image> fetchBagRelationships(List<Image> images);

    Page<Image> fetchBagRelationships(Page<Image> images);
}
