package MAP.taboodrug.drug.dto;

import lombok.Getter;

import java.util.Date;

@Getter
public class DrugRequest {

    private String durCode;

    private String drugName;

    private int pageNum;

    private int resultCount;

    // 병용금기 정보조회에서만 사용하는 변수
    private String startDate;
    // 병용금기 정보조회에서만 사용하는 변수
    private String endDate;
}
