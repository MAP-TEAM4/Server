package MAP.taboodrug.drug.repository;

import MAP.taboodrug.drug.domain.Drug;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DrugRepository extends JpaRepository<Drug, String> {
    Optional<Drug> findDrugByItemName(String itemName);
}
