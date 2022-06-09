package MAP.taboodrug.drug.service;

import MAP.taboodrug.drug.domain.DrugInfo;
import MAP.taboodrug.drug.dto.UserDrugRequest;
import MAP.taboodrug.drug.repository.BasicDrugRepository;
import MAP.taboodrug.drug.repository.DetailInfoRepository;
import MAP.taboodrug.drug.repository.DrugInfoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserDrugService extends DrugInfoService {
    ArrayList<String> mixture;
    ArrayList<Boolean> pregnancy;
    ArrayList<Boolean> old;

    public UserDrugService(ObjectMapper objectMapper, DetailInfoRepository detailInfoRepository, DrugInfoRepository drugInfoRepository, BasicDrugRepository basicDrugRepository) {
        super(objectMapper, detailInfoRepository, drugInfoRepository, basicDrugRepository);
    }

    public String infoList(UserDrugRequest userDrugRequest) throws Exception {
        log.info("infoList(), 약품명 목록: {}", userDrugRequest.getDrugs().toString());

        List<String> drugs = userDrugRequest.getDrugs();
        mixture = new ArrayList<>();
        pregnancy = new ArrayList<>();
        old = new ArrayList<>();

        for (String drug : drugs) {
            Optional<DrugInfo> drugInfo = drugInfoRepository.findDrugByItemName(drug);

            if (drugInfo.isEmpty()) {
                setList(drug, drugs);
            } else {
                DrugInfo drugInfoResult = drugInfo.get();
                fillResult(drugInfoResult, drugs);
            }
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("병용금기약품", mixture);
        jsonObject.put("임부금기여부", pregnancy);
        jsonObject.put("노인주의여부", old);

        return jsonObject.toJSONString();
    }

    // DB에 없는 약품에 대한 모든 정보를 DB에 저장
    private void setList(String drugName, List<String> drugs) throws Exception {
        DrugInfo drug = makeDrugData(drugName);

        log.info("저장할 약품 이름: {}, 임부 금기 여부: {}, 노인 금기 여부: {}", drug.getItemName(), drug.isPregnancyBan(), drug.isOldBan());
        drugInfoRepository.save(drug);

        fillResult(drug, drugs);
    }

    private void fillResult(DrugInfo drugInfo, List<String> drugs) {
        mixture.add(drugs.contains(drugInfo.getMixtureItemName()) ? drugInfo.getMixtureItemName() : "null");
        pregnancy.add(drugInfo.isPregnancyBan());
        old.add(drugInfo.isOldBan());
    }
}
