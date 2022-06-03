package MAP.taboodrug.drug.controller;

import MAP.taboodrug.drug.dto.DrugRequest;
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

    @ApiOperation(value="병용금기 정보조회 요청", notes="전달받은 약품명에 대한 정보와 병용 금기 약품 정보 반환")
    @PostMapping("/contraindicate")
    public String contraindicatedDrugsApi(@RequestBody DrugRequest drugRequest) throws Exception {
        return drugService.contraindicatedDrugList(drugRequest);
    }

    @ApiOperation(value="임부금기 정보조회 요청", notes="전달받은 약품의 임부금기 여부와 금기 정보 반환")
    @PostMapping("/pregnancy")
    public String pregnancyTabooDrugApi(@RequestBody DrugRequest drugRequest) throws Exception {
        return drugService.pregnancyTabooDrugList(drugRequest);
    }

    @ApiOperation(value="노인주의 정보조회 요청", notes="전달받은 약품의 노인주의 여부와 ")
    @PostMapping("/old")
    public String oldTabooDrugApi(@RequestBody DrugRequest drugRequest) throws Exception {
        return drugService.oldTabooDrugList(drugRequest);
    }

}
