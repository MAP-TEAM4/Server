package MAP.taboodrug.drug.service;

import MAP.taboodrug.drug.domain.DetailInfo;
import MAP.taboodrug.drug.dto.DrugRequest;
import MAP.taboodrug.drug.repository.BasicDrugRepository;
import MAP.taboodrug.drug.repository.DetailInfoRepository;
import MAP.taboodrug.drug.repository.DrugInfoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.w3c.dom.Element;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Optional;

@Slf4j
@Service
public class DetailInfoService extends CommonService {

    public DetailInfoService(ObjectMapper objectMapper, DetailInfoRepository detailInfoRepository, DrugInfoRepository drugInfoRepository, BasicDrugRepository basicDrugRepository) {
        super(objectMapper, detailInfoRepository, drugInfoRepository, basicDrugRepository);
    }

    public String detailInfoList(DrugRequest drugRequest) throws Exception {
        // DB에 전달받은 약품 이름에 대한 정보가 있다면, 그대로 반환
        // 그렇지 않다면 setData() 호출
        log.info("detailInfoList(), 입력받은 약품명: {}", drugRequest.getDrugName());

        Optional<DetailInfo> detailInfo = detailInfoRepository.findDetailInfoByItemName(drugRequest.getDrugName());

        if (detailInfo.isEmpty())
            return setData(drugRequest);

        return objectMapper.writeValueAsString(detailInfo);
    }

    // 서버 최초 실행 시 1회 실행하여 해당 약품에 대한 모든 정보를 DB에 저장
    private String setData(DrugRequest drugRequest) throws Exception {
        String result = drugInfoUrl + "?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + drugInfoKey + /*Service Key*/
                "&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(1), "UTF-8") + /*페이지 번호, 기본 값 1*/
                "&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(1), "UTF-8") + /*한 페이지 결과 수, 기본 값 1*/
                "&" + URLEncoder.encode("itemName", "UTF-8") + "=" + URLEncoder.encode(drugRequest.getDrugName(), "UTF-8") + /*품목명*/
                "&" + URLEncoder.encode("type", "UTF-8") + "=" + URLEncoder.encode("xml", "UTF-8");

        Element eElement = initElement(initDocument(result));

        ArrayList<String> resultList = new ArrayList<>();

        resultList.add(getTagValue("entpName", eElement));
        resultList.add(parsingValue(getTagValue("efcyQesitm", eElement)));
        resultList.add(parsingValue(getTagValue("useMethodQesitm", eElement)));
        resultList.add(parsingValue(getTagValue("atpnWarnQesitm", eElement)));
        resultList.add(parsingValue(getTagValue("atpnQesitm", eElement)));
        resultList.add(parsingValue(getTagValue("intrcQesitm", eElement)));
        resultList.add(parsingValue(getTagValue("seQesitm", eElement)));
        resultList.add(parsingValue(getTagValue("depositMethodQesitm", eElement)));

        DetailInfo detailInfo = new DetailInfo();

        detailInfo.setDrugName(drugRequest.getDrugName());
        detailInfo.setDetailInfo(resultList);

        log.info("상세 정보 저장할 약품 이름: {}", detailInfo.getItemName());
        detailInfoRepository.save(detailInfo);

        return objectMapper.writeValueAsString(detailInfo);
    }

    private String parsingValue(String value) {
        if (value == null) return "";
        return value.replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "");
    }
}
