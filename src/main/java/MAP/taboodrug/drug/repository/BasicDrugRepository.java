package MAP.taboodrug.drug.repository;

import MAP.taboodrug.drug.domain.BasicDrug;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BasicDrugRepository extends JpaRepository<BasicDrug, String> {
    BasicDrug findBasicDrugByItemName(String itemName);
}
