package MAP.taboodrug.drug.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import java.util.ArrayList;

@NoArgsConstructor
@Getter
@Entity
public class DetailInfo {

    @Id
    @Column(name = "itemName")
    private String itemName;

    @Lob
    private String efficacy;

    @Lob
    private String useMethod;

    @Lob
    private String warn;

    @Lob
    private String caution;

    @Lob
    private String interact;

    @Lob
    private String sideEffect;

    @Lob
    private String depositMethod;

    public void setDrugName(String itemName) {
        this.itemName = itemName;
    }

    public void setDetailInfo(ArrayList<String> info) {
        this.efficacy = info.get(0);
        this.useMethod = info.get(1);
        this.warn = info.get(2);
        this.caution = info.get(3);
        this.interact = info.get(4);
        this.sideEffect = info.get(5);
        this.depositMethod = info.get(6);
    }
}
