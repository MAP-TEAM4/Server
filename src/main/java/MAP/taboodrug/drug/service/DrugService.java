package MAP.taboodrug.drug.service;

import MAP.taboodrug.drug.dto.DrugRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.*;

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

    public String contraindicatedDrugList(DrugRequest drugRequest) throws Exception {
        return callApi(fillRequest("병용금기", drugRequest));
    }

    public String pregnancyTabooDrugList(DrugRequest drugRequest) throws Exception {
        return callApi(fillRequest("임부금기", drugRequest));
    }

    public String oldTabooDrugList(DrugRequest drugRequest) throws Exception {
        return callApi(fillRequest("노인주의", drugRequest));
    }

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
                "&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(drugRequest.getPageNum() == 0 ? 1 : drugRequest.getPageNum()), "UTF-8") + /*페이지 번호, 전달하지 않았다면 기본 값 1*/
                "&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(drugRequest.getResultCount() == 0 ? 10 : drugRequest.getResultCount()), "UTF-8") + /*한 페이지 결과 수, 전달하지 않았다면 기본 값 10*/
                "&" + URLEncoder.encode("start_change_date", "UTF-8") + "=" + URLEncoder.encode(drugRequest.getStartDate() == null ? "" : drugRequest.getStartDate(), "UTF-8") + /*변경일자시작일*/
                "&" + URLEncoder.encode("end_change_date", "UTF-8") + "=" + URLEncoder.encode(drugRequest.getEndDate() == null ? "" : drugRequest.getEndDate(), "UTF-8") + /*변경일자종료일*/
                "&" + URLEncoder.encode("type", "UTF-8") + "=" + URLEncoder.encode("xml", "UTF-8");
    }

    private String callApi(String urlBuilder) throws Exception {
        URL url = new URL(urlBuilder);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");

        log.info("Response code: {}", conn.getResponseCode());

        BufferedReader rd;
        if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }

        rd.close();
        conn.disconnect();
        return sb.toString();
    }
}
