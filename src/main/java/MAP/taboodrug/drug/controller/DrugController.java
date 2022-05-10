package MAP.taboodrug.drug.controller;

import MAP.taboodrug.drug.dto.DrugRequest;
import MAP.taboodrug.drug.service.DrugService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping(value= "/api/drug")
@RestController
public class DrugController {

    private final DrugService drugService;

    @PostMapping("/contraindicate")
    public String contraindicatedDrugsApi(@RequestBody DrugRequest drugRequest) throws Exception {
        log.info("병용금기 정보조회 Post");
        return drugService.contraindicatedDrugList(drugRequest);
    }

    @PostMapping("/pregnancy")
    public String pregnancyTabooDrugApi(@RequestBody DrugRequest drugRequest) throws Exception {
        log.info("임부금기 정보조회 Post");
        return drugService.pregnancyTabooDrugList(drugRequest);
    }

    @PostMapping("/old")
    public String oldTabooDrugApi(@RequestBody DrugRequest drugRequest) throws Exception {
        log.info("노인주의 정보조회 Post");
        return drugService.oldTabooDrugList(drugRequest);
    }

}
