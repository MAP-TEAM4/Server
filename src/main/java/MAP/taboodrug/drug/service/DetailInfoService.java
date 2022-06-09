package MAP.taboodrug.drug.service;

import MAP.taboodrug.drug.domain.BasicDrug;
import MAP.taboodrug.drug.domain.DetailInfo;
import MAP.taboodrug.drug.domain.DrugInfo;
import MAP.taboodrug.drug.dto.DrugRequest;
import MAP.taboodrug.drug.repository.BasicDrugRepository;
import MAP.taboodrug.drug.repository.DetailInfoRepository;
import MAP.taboodrug.drug.repository.DrugInfoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import org.w3c.dom.Element;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Optional;

@Slf4j
@Service
public class DetailInfoService extends CommonService {
    JSONObject jsonObject;

    private final DrugInfoService drugInfoService;

    public DetailInfoService(ObjectMapper objectMapper, DetailInfoRepository detailInfoRepository, DrugInfoRepository drugInfoRepository, BasicDrugRepository basicDrugRepository, DrugInfoService drugInfoService) {
        super(objectMapper, detailInfoRepository, drugInfoRepository, basicDrugRepository);
        this.drugInfoService = drugInfoService;
    }

    public String detailInfoList(DrugRequest drugRequest) throws Exception {
        // DB에 전달받은 약품 이름에 대한 정보가 있다면, 그대로 반환
        // 그렇지 않다면 setData() 호출
        log.info("detailInfoList(), 입력받은 약품명: {}", drugRequest.getDrugName());

        Optional<DetailInfo> detailInfo = detailInfoRepository.findDetailInfoByItemName(drugRequest.getDrugName());

        jsonObject = new JSONObject();

        if (detailInfo.isEmpty())
            setData(drugRequest);

        jsonObject.put("itemName", drugRequest.getDrugName());

        return fillResult(drugRequest.getDrugName());
    }

    // 서버 최초 실행 시 1회 실행하여 해당 약품에 대한 모든 정보를 DB에 저장
    private void setData(DrugRequest drugRequest) throws Exception {
        String result = drugInfoUrl + "?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + drugInfoKey + /*Service Key*/
                "&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(1), "UTF-8") + /*페이지 번호, 기본 값 1*/
                "&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(1), "UTF-8") + /*한 페이지 결과 수, 기본 값 1*/
                "&" + URLEncoder.encode("itemName", "UTF-8") + "=" + URLEncoder.encode(drugRequest.getDrugName(), "UTF-8") + /*품목명*/
                "&" + URLEncoder.encode("type", "UTF-8") + "=" + URLEncoder.encode("xml", "UTF-8");

        Element eElement = initElement(initDocument(result));

        ArrayList<String> resultList = new ArrayList<>();

        resultList.add(parsingValue(getTagValue("efcyQesitm", eElement)));
        resultList.add(parsingValue(getTagValue("useMethodQesitm", eElement)));
        resultList.add(parsingValue(getTagValue("atpnQesitm", eElement)));
        resultList.add(parsingValue(getTagValue("seQesitm", eElement)));
        resultList.add(parsingValue(getTagValue("depositMethodQesitm", eElement)));

        DetailInfo detailInfo = new DetailInfo();

        detailInfo.setDrugName(drugRequest.getDrugName());
        detailInfo.setDetailInfo(resultList);

        log.info("상세 정보 저장할 약품 이름: {}", detailInfo.getItemName());
        detailInfoRepository.save(detailInfo);
    }

    private String parsingValue(String value) {
        if (value == null) return "";
        return value.replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "");
    }

    // DB에 접근해서 반환해야 할 상세 정보 생성
    private String fillResult(String drugName) throws Exception {
        BasicDrug basicDrug = basicDrugRepository.findBasicDrugByItemName(drugName);
        Optional<DetailInfo> detailInfo = detailInfoRepository.findDetailInfoByItemName(drugName);
        Optional<DrugInfo> drugInfo = drugInfoRepository.findDrugByItemName(drugName);

        jsonObject.put("entpName", basicDrug.getEntpName());
        jsonObject.put("medicImageUrl", basicDrug.getMedicImageUrl());

        if (detailInfo.isEmpty()) {
            fillDetailInfo(drugName);
        } else {
            DetailInfo drug = detailInfo.get();
            jsonObject.put("efficacy",drug.getEfficacy());
            jsonObject.put("useMethod", drug.getUseMethod());
            jsonObject.put("caution",drug.getCaution());
            jsonObject.put("sideEffect", drug.getSideEffect());
            jsonObject.put("depositMethod",drug.getDepositMethod());
        }

        if (drugInfo.isEmpty()) {
            jsonObject = drugInfoService.fillDrugInfo(drugName, jsonObject);
        } else {
            DrugInfo drug = drugInfo.get();
            jsonObject = drugInfoService.fillObject(drug, jsonObject);
        }

        return jsonObject.toJSONString();
    }

    private void fillDetailInfo(String drugName) throws Exception {
        String result = drugInfoUrl + "?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + drugInfoKey + /*Service Key*/
                "&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(1), "UTF-8") + /*페이지 번호, 기본 값 1*/
                "&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(1), "UTF-8") + /*한 페이지 결과 수, 기본 값 1*/
                "&" + URLEncoder.encode("itemName", "UTF-8") + "=" + URLEncoder.encode(drugName, "UTF-8") + /*품목명*/
                "&" + URLEncoder.encode("type", "UTF-8") + "=" + URLEncoder.encode("xml", "UTF-8");

        Element eElement = initElement(initDocument(result));

        ArrayList<String> resultList = new ArrayList<>();

        String tmp = convertNull(parsingValue(getTagValue("efficacy", eElement)));
        jsonObject.put("efficacy", tmp);
        resultList.add(tmp);

        tmp = convertNull(parsingValue(getTagValue("useMethod", eElement)));
        jsonObject.put("useMethod", tmp);
        resultList.add(tmp);

        tmp = convertNull(parsingValue(getTagValue("caution", eElement)));
        jsonObject.put("caution", parsingValue(getTagValue("caution", eElement)));
        resultList.add(tmp);

        tmp = convertNull(parsingValue(getTagValue("sideEffect", eElement)));
        jsonObject.put("sideEffect", parsingValue(getTagValue("sideEffect", eElement)));
        resultList.add(tmp);

        tmp = convertNull(parsingValue(getTagValue("depositMethod", eElement)));
        jsonObject.put("depositMethod", parsingValue(getTagValue("depositMethod", eElement)));
        resultList.add(tmp);

        DetailInfo detailInfo = new DetailInfo();
        detailInfo.setDrugName(drugName);
        detailInfo.setDetailInfo(resultList);

        detailInfoRepository.save(detailInfo);
    }
}
