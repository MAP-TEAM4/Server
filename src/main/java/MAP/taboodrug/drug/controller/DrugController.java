package MAP.taboodrug.drug.controller;

import MAP.taboodrug.drug.dto.DrugRequest;
import MAP.taboodrug.drug.service.DetailInfoService;
import MAP.taboodrug.drug.service.DrugService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping(value= "/api/drug")
@RestController
public class DrugController {

    private final DrugService drugService;
    private final DetailInfoService detailInfoService;

    @ApiOperation(value="약 정보 조회", notes="전달받은 약품에 대한 정보를 JSON형태로 반환")
    @PostMapping("/info")
    public String drugInfoApi(@RequestBody DrugRequest drugRequest) throws Exception {
        return drugService.drugInfoList(drugRequest);
    }

    @ApiOperation(value="약 상세 정보 조회", notes="전달받은 약품에 대한 상세 정보(주의사항, 부작용 등)를 JSON형태로 반환")
    @PostMapping("/detail")
    public String drugDetailInfoApi(@RequestBody DrugRequest drugRequest) throws Exception {
        return detailInfoService.detailInfoList(drugRequest);
    }
}
