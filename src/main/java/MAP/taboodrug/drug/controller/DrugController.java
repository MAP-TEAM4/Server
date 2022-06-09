package MAP.taboodrug.drug.controller;

import MAP.taboodrug.drug.dto.DrugRequest;
import MAP.taboodrug.drug.dto.UserDrugRequest;
import MAP.taboodrug.drug.service.DetailInfoService;
import MAP.taboodrug.drug.service.DrugInfoService;
import MAP.taboodrug.drug.service.UserDrugService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping(value= "/api/drug")
@RestController
public class DrugController {

    private final DrugInfoService drugInfoService;
    private final DetailInfoService detailInfoService;
    private final UserDrugService userDrugService;

    @ApiOperation(value="약 정보 조회", notes="전달받은 약품에 대한 정보를 JSON형태로 반환")
    @PostMapping("/info")
    public String drugInfoApi(@RequestBody DrugRequest drugRequest) throws Exception {
        return drugInfoService.drugInfoList(drugRequest);
    }

    @ApiOperation(value="약품명 목록 DB 초기화", notes="DB에 약품명 목록을 초기화, 호출할 일 X")
    @GetMapping("/init")
    public void init() {
        drugInfoService.initBasicDrug();
    }

    @ApiOperation(value="약품명 목록 조회", notes="약품명 목록을 반환, 자동완성 기능에 사용 가능")
    @GetMapping("basicDrugList")
    public String basicDrugList() throws Exception {
        return drugInfoService.basicDrugList();
    }
    
    @ApiOperation(value="약 상세 정보 조회", notes="전달받은 약품에 대한 상세 정보(주의사항, 부작용 등)를 JSON형태로 반환")
    @PostMapping("/detail")
    public String drugDetailInfoApi(@RequestBody DrugRequest drugRequest) throws Exception {
        return detailInfoService.detailInfoList(drugRequest);
    }

    @ApiOperation(value="사용자 등록 약품 정보 조회", notes="사용자가 등록한 약품명을 전달받아 병용금기, 임부금기, 노인주의 정보를 반환")
    @PostMapping("/userDrug")
    public String userDrugInfoList(@RequestBody UserDrugRequest userDrugRequest) throws Exception {
        return userDrugService.infoList(userDrugRequest);
    }
}
