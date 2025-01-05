package kdg.be.backend.repository;

import kdg.be.backend.domain.customization.Customizable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CustomizableRepository extends JpaRepository<Customizable, UUID> {

}
