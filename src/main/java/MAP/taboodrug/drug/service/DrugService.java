package MAP.taboodrug.drug.service;

import MAP.taboodrug.drug.domain.DrugInfo;
import MAP.taboodrug.drug.dto.DrugRequest;
import MAP.taboodrug.drug.repository.BasicDrugRepository;
import MAP.taboodrug.drug.repository.DrugInfoRepository;
import MAP.taboodrug.drug.util.DrugUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.Element;

import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class DrugService {

    @Value("${tabooUrl}")
    private String tabooUrl;
    
    @Value("${pregnancyUrl}")
    private String pregnancyUrl;

    @Value("${oldTabooUrl}")
    private String oldTabooUrl;

    @Value("${drugListKey}")
    private String drugKey;

    private final DrugInfoRepository drugInfoRepository;

    private final BasicDrugRepository basicDrugRepository;

    // Entity 객체를 JSON으로 변환해주는 매퍼
    private final ObjectMapper objectMapper;

    private final CommonService commonService;

    // DB에 전달받은 약품 이름에 대한 정보가 있다면, 그대로 반환
    // 그렇지 않다면 setData() 호출
    public String drugInfoList(DrugRequest drugRequest) throws Exception {
        log.info("drugInfoList(), 입력받은 약품명: {}", drugRequest.getDrugName());

        Optional<DrugInfo> drug = drugInfoRepository.findDrugByItemName(drugRequest.getDrugName());

        if (drug.isEmpty())
            return setData(drugRequest);

        return objectMapper.writeValueAsString(drug);
    }

    // 자동완성을 위한 약품명 목록을 DB에 저장, 반환
    public void initBasicDrug() {
        log.info("initBasicDrug(), 약품명 목록 초기화");
        DrugUtil drugUtil = new DrugUtil();
        basicDrugRepository.saveAll(drugUtil.setBasicDrug());
    }

    public String basicDrugList() throws Exception {
        log.info("basicDrugList()");
        return objectMapper.writeValueAsString(basicDrugRepository.findAll());
    }

    // private methods

    // 병용금기 정보 조회 API 호출 후 필요한 정보 추출
    private ArrayList<String> parsingInfo(String result) throws Exception {
        Element eElement = commonService.initElement(commonService.initDocument(result));

        if (checkCount(eElement) == 0) return null;

        ArrayList<String> resultList = new ArrayList<>();
        resultList.add(commonService.getTagValue("CHART", eElement));
        resultList.add(commonService.getTagValue("FORM_NAME", eElement));
        resultList.add(commonService.getTagValue("CLASS_NAME", eElement));
        resultList.add(commonService.getTagValue("MIXTURE_ITEM_NAME", eElement));
        resultList.add(commonService.getTagValue("MIXTURE_CLASS_NAME", eElement));
        resultList.add(commonService.getTagValue("MIXTURE_CHART", eElement));
        resultList.add(commonService.getTagValue("PROHBT_CONTENT", eElement));
        resultList.add(commonService.getTagValue("REMARK", eElement));

        return resultList;
    }

    // 임부금기, 노인주의 정보 조회 API 호출 후 해당사항이 있다면 정보 추출
    private ArrayList<String> parsingBan(String result) throws Exception {
        Element eElement = commonService.initElement(commonService.initDocument(result));

        if (checkCount(eElement) == 0) return null;

        ArrayList<String> resultList = new ArrayList<>();

        resultList.add(commonService.getTagValue("PROHBT_CONTENT", eElement));
        resultList.add(commonService.getTagValue("REMARK", eElement));

        return resultList;
    }

    // 임부금기, 노인주의 정보 조회 API 호출 시, 해당 사항이 있는지 확인
    private int checkCount(Element element) {
        return Integer.parseInt(Objects.requireNonNull(commonService.getTagValue("totalCount", element)));
    }

    // API 요청을 위한 URL 생성
    private String fillRequest(String type, DrugRequest drugRequest) throws UnsupportedEncodingException {
        String url = null;
        
        switch (type) {
            case "병용금기":
                url = tabooUrl;
                break;
            case "임부금기":
                url = pregnancyUrl;
                break;
            case "노인주의":
                url = oldTabooUrl;
        }

        return url + "?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + drugKey + /*Service Key*/
                "&" + URLEncoder.encode("typeName", "UTF-8") + "=" + URLEncoder.encode(type, "UTF-8") +
                "&" + URLEncoder.encode("itemName", "UTF-8") + "=" + URLEncoder.encode(drugRequest.getDrugName(), "UTF-8") + /*품목명*/
                "&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(1), "UTF-8") + /*페이지 번호, 기본 값 1*/
                "&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(1), "UTF-8") + /*한 페이지 결과 수, 기본 값 1*/
                "&" + URLEncoder.encode("type", "UTF-8") + "=" + URLEncoder.encode("xml", "UTF-8");
    }

    // DB에 없는 약품에 대한 모든 정보를 DB에 저장
    private String setData(DrugRequest drugRequest) throws Exception {
        ArrayList<String> drugInfo = parsingInfo(fillRequest("병용금기", drugRequest));
        ArrayList<String> pregInfo = parsingBan(fillRequest("임부금기", drugRequest));
        ArrayList<String> oldInfo = parsingBan(fillRequest("노인주의", drugRequest));

        DrugInfo drug = new DrugInfo();

        drug.setDrugName(drugRequest.getDrugName());
        if (drugInfo != null) drug.setDrugInfo(drugInfo);
        drug.setPregInfo(pregInfo);
        drug.setOldInfo(oldInfo);

        log.info("저장할 약품 이름: {}, 임부 금기 여부: {}, 노인 금기 여부: {}", drug.getItemName(), drug.isPregnancyBan(), drug.isOldBan());
        drugInfoRepository.save(drug);

        return objectMapper.writeValueAsString(drug);
    }
}
