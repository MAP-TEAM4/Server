package MAP.taboodrug.drug.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@NoArgsConstructor
@Getter
@Entity
public class BasicDrug {

    @Id
    private String itemName;

    private String entpName;

    private String medicImageUrl;


    public BasicDrug(String itemName, String entpName, String medicImageUrl) {
        this.itemName = itemName;
        this.entpName = entpName;
        this.medicImageUrl = medicImageUrl;
    }
}
