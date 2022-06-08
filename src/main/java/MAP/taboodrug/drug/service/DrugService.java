package MAP.taboodrug.drug.service;

import MAP.taboodrug.drug.domain.Drug;
import MAP.taboodrug.drug.dto.DrugRequest;
import MAP.taboodrug.drug.repository.DrugRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

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

    private final DrugRepository drugRepository;

    // Entity 객체를 JSON으로 변환해주는 매퍼
    private final ObjectMapper objectMapper;

    private final CommonService commonService;

    public String drugInfoList(DrugRequest drugRequest) throws Exception {
        // DB에 전달받은 약품 이름에 대한 정보가 있다면, 그대로 반환
        // 그렇지 않다면 setData() 호출

        Optional<Drug> drug = drugRepository.findDrugByItemName(drugRequest.getDrugName());

        if (drug.isEmpty())
            return setData(drugRequest);

        return objectMapper.writeValueAsString(drug);
    }

    // private methods

    // 병용금기 정보 조회 API 호출 후 필요한 정보 추출
    private ArrayList<String> parsingInfo(String result) throws Exception {
        Element eElement = commonService.initElement(commonService.initDocument(result));

        if (checkCount(eElement) == 0) return null;

        ArrayList<String> resultList = new ArrayList<>();
        resultList.add(commonService.getTagValue("ITEM_NAME", eElement));
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
                break;
        }

        // null인 경우 빈 값을 전달, int 변수를 받아야 하는 경우는 각각의 기본 값을 전달
        return url + "?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + drugKey + /*Service Key*/
                "&" + URLEncoder.encode("typeName", "UTF-8") + "=" + URLEncoder.encode(type, "UTF-8") +
                "&" + URLEncoder.encode("ingrCode", "UTF-8") + "=" + URLEncoder.encode(drugRequest.getDurCode() == null ? "" : drugRequest.getDurCode(), "UTF-8") + /*DUR성분코드*/
                "&" + URLEncoder.encode("itemName", "UTF-8") + "=" + URLEncoder.encode(drugRequest.getDrugName(), "UTF-8") + /*품목명*/
                "&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(drugRequest.getPageNum() == 0 ? 1 : drugRequest.getPageNum()), "UTF-8") + /*페이지 번호, 기본 값 1*/
                "&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(drugRequest.getResultCount() == 0 ? 1 : drugRequest.getResultCount()), "UTF-8") + /*한 페이지 결과 수, 기본 값 1*/
                "&" + URLEncoder.encode("start_change_date", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") + /*변경일자시작일*/
                "&" + URLEncoder.encode("end_change_date", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8") + /*변경일자종료일*/
                "&" + URLEncoder.encode("type", "UTF-8") + "=" + URLEncoder.encode("xml", "UTF-8");
    }

    // 서버 최초 실행 시 1회 실행하여 해당 약품에 대한 모든 정보를 DB에 저장
    private String setData(DrugRequest drugRequest) throws Exception {
        ArrayList<String> drugInfo = parsingInfo(fillRequest("병용금기", drugRequest));
        ArrayList<String> pregInfo = parsingBan(fillRequest("임부금기", drugRequest));
        ArrayList<String> oldInfo = parsingBan(fillRequest("노인주의", drugRequest));

        Drug drug = new Drug();

        if (drugInfo == null) drug.setDrugName(drugRequest.getDrugName());
        else drug.setDrugInfo(drugInfo);
        drug.setPregInfo(pregInfo);
        drug.setOldInfo(oldInfo);

        log.info("저장할 약품 이름: {}, 임부 금기 여부: {}, 노인 금기 여부: {}", drug.getItemName(), drug.isPregnancyBan(), drug.isOldBan());
        drugRepository.save(drug);

        return objectMapper.writeValueAsString(drug);
    }
}
