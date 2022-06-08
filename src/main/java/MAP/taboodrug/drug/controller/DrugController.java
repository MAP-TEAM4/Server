package MAP.taboodrug.drug.controller;

import MAP.taboodrug.drug.dto.DrugRequest;
import MAP.taboodrug.drug.service.DrugService;
import MAP.taboodrug.drug.util.DrugUtil;
import io.swagger.annotations.ApiOperation;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping(value= "/api/drug")
@RestController
public class DrugController {

    private final DrugService drugService;

    @ApiOperation(value="약 정보 조회", notes="전달받은 약품에 대한 정보를 JSON형태로 반환")
    @PostMapping("/info")
    public String drugInfoApi(@RequestBody DrugRequest drugRequest) throws Exception {
        return drugService.drugInfoList(drugRequest);
    }

    @ApiOperation(value="약품명 목록 DB 초기화", notes="DB에 약품명 목록을 초기화, 호출할 일 X")
    @GetMapping("/init")
    public void init() {
        drugService.initBasicDrug();
    }

    @ApiOperation(value="약품명 목록 조회", notes="약품명 목록을 반환, 자동완성 기능에 사용 가능")
    @GetMapping("basicDrugList")
    public String basicDrugList() throws Exception {
        return drugService.basicDrugList();
    }
}
