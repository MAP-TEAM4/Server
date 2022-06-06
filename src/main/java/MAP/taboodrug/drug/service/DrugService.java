package MAP.taboodrug.drug.service;

import MAP.taboodrug.drug.dto.DrugRequest;
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
import java.util.Collections;
import java.util.Objects;

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

    public String drugInfoList(DrugRequest drugRequest) throws Exception {
        // TODO DB에 전달받은 약품 이름에 대한 정보가 있다면, 그대로 반환
        // TODO 그렇지 않다면 setData() 호출

        return setData(drugRequest);
    }

    // priavte init & common methods

    private Document initDocument(String reqBuilder) throws Exception {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(reqBuilder);
        doc.getDocumentElement().normalize();
        return doc;
    }

    private Element initElement(Document doc) {
        NodeList nList = doc.getElementsByTagName("body");
        Node nNode = nList.item(0);
        return (Element) nNode;
    }

    // 태그 값을 추출 - commons로 옮길까 ...
    private String getTagValue(String tag, Element eElement) {
        // 결과를 저장할 result
        String result = "";

        // 태그 값을 읽을 수 없는 경우는 해당 결과가 없다는 의미
        if (eElement.getElementsByTagName(tag).item(0) == null) return null;

        NodeList nlList = eElement.getElementsByTagName(tag).item(0).getChildNodes();

        if (nlList.item(0) == null) return null;

        result = nlList.item(0).getTextContent();

        return result;
    }

    // private methods

    // 병용금기 정보 조회 API 호출 후 필요한 정보 추출
    private JSONObject parsingInfo(String result) throws Exception {
        Element eElement = initElement(initDocument(result));

        if (checkCount(eElement) == 0) return null;

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("품목명", getTagValue("ITEM_NAME", eElement));
        jsonObject.put("성상", getTagValue("CHART", eElement));
        jsonObject.put("제형", getTagValue("FORM_NAME", eElement));
        jsonObject.put("약효 분류", getTagValue("CLASS_NAME", eElement));
        jsonObject.put("병용금기품목명", getTagValue("MIXTURE_ITEM_NAME", eElement));
        jsonObject.put("병용금기약효분류", getTagValue("MIXTURE_CLASS_NAME", eElement));
        jsonObject.put("금기내용", getTagValue("PROHBT_CONTENT", eElement));
        jsonObject.put("비고", getTagValue("REMARK", eElement));
        jsonObject.put("병용금기성상", getTagValue("MIXTURE_CHART", eElement));

        return jsonObject;
    }

    // 임부금기, 노인주의 정보 조회 API 호출 후 해당사항이 있다면 정보 추출
    private JSONObject parsingBan(String result) throws Exception {
        Element eElement = initElement(initDocument(result));

        if (checkCount(eElement) == 0) return null;

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("금기 내용", getTagValue("PROHBT_CONTENT", eElement));
        jsonObject.put("비고", getTagValue("REMARK", eElement));

        return jsonObject;
    }

    // 임부금기, 노인주의 정보 조회 API 호출 시, 해당 사항이 있는지 확인
    private int checkCount(Element element) {
        return Integer.parseInt(Objects.requireNonNull(getTagValue("totalCount", element)));
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
        // TODO DB에 저장

        JSONObject jsonObject = new JSONObject();

        JSONObject tmp = parsingInfo(fillRequest("병용금기", drugRequest));
        jsonObject.put("약품 정보", tmp);

        tmp = parsingBan(fillRequest("임부금기", drugRequest));
        jsonObject.put("임부금기 정보", tmp);

        tmp = parsingBan(fillRequest("노인주의", drugRequest));
        jsonObject.put("노인주의 정보", tmp);

        return jsonObject.toJSONString();
    }
}
