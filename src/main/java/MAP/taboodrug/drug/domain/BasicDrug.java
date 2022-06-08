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

    private String entpyName;

    private String medicImageUrl;

    private String className;

    public BasicDrug(String itemName, String entpyName, String medicImageUrl, String className) {
        this.itemName = itemName;
        this.entpyName = entpyName;
        this.medicImageUrl = medicImageUrl;
        this.className = className;
    }
}
