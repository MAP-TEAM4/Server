package MAP.taboodrug.drug.repository;

import MAP.taboodrug.drug.domain.BasicDrug;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BasicDrugRepository extends JpaRepository<BasicDrug, String> {
    Optional<BasicDrug> findBasicDrugByItemName(String itemName);
}
