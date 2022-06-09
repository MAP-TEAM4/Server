package MAP.taboodrug.drug.service;

import MAP.taboodrug.drug.repository.BasicDrugRepository;
import MAP.taboodrug.drug.repository.DetailInfoRepository;
import MAP.taboodrug.drug.repository.DrugInfoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommonService {

    @Value("${easyDrugInfoUrl}")
    public String drugInfoUrl;

    @Value("${easyDrugInfoKey}")
    public String drugInfoKey;

    @Value("${tabooUrl}")
    public String tabooUrl;

    @Value("${pregnancyUrl}")
    public String pregnancyUrl;

    @Value("${oldTabooUrl}")
    public String oldTabooUrl;

    @Value("${drugListKey}")
    public String drugKey;

    // Entity 객체를 JSON으로 변환해주는 매퍼
    public final ObjectMapper objectMapper;

    public final DetailInfoRepository detailInfoRepository;

    public final DrugInfoRepository drugInfoRepository;

    public final BasicDrugRepository basicDrugRepository;

    public Document initDocument(String reqBuilder) throws Exception {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(reqBuilder);
        doc.getDocumentElement().normalize();
        return doc;
    }

    public Element initElement(Document doc) {
        NodeList nList = doc.getElementsByTagName("body");
        Node nNode = nList.item(0);
        return (Element) nNode;
    }

    // 태그 값을 추출
    public String getTagValue(String tag, Element eElement) {
        // 결과를 저장할 result
        String result = "";

        // 태그 값을 읽을 수 없는 경우는 해당 결과가 없다는 의미
        if (eElement.getElementsByTagName(tag).item(0) == null) return null;

        NodeList nlList = eElement.getElementsByTagName(tag).item(0).getChildNodes();

        if (nlList.item(0) == null) return null;

        result = nlList.item(0).getTextContent();

        return result;
    }
}
