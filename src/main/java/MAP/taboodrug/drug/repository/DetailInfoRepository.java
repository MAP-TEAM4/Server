package MAP.taboodrug.drug.repository;

import MAP.taboodrug.drug.domain.DetailInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DetailInfoRepository extends JpaRepository<DetailInfo, String> {
    Optional<DetailInfo> findDetailInfoByItemName(String itemName);
}
