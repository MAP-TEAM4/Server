package MAP.taboodrug.drug.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class UserDrugRequest {
    private List<String> drugs;

    public UserDrugRequest(List<String> drugs) {
        this.drugs = drugs;
    }
}
