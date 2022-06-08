package MAP.taboodrug.drug.util;

import MAP.taboodrug.drug.domain.BasicDrug;
import MAP.taboodrug.drug.repository.BasicDrugRepository;
import MAP.taboodrug.drug.repository.DrugInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class DrugUtil {
    public List<BasicDrug> setBasicDrug() {
        File file = new File("/Users/bsu/Desktop/drug_info.csv");
        List<BasicDrug> result = new ArrayList<>();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file, Charset.forName("EUC-KR")));
            String line;

            // 속성 정보는 제외(품목명, ...)
            line =  br.readLine();

            while((line = br.readLine()) != null) {
                String[] info = line.split(",");

                BasicDrug basicDrug = new BasicDrug(info[0], info[1], info[2], info[3]);

                result.add(basicDrug);
            }
        } catch (IOException e) {
            throw new RuntimeException();
        }

        return result;
    }
}
