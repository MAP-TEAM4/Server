package MAP.taboodrug.drug.repository;

import MAP.taboodrug.drug.domain.DrugInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DrugInfoRepository extends JpaRepository<DrugInfo, String> {
    Optional<DrugInfo> findDrugByItemName(String itemName);
}
