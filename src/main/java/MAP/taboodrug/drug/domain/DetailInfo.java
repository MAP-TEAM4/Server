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

    private String entpName;

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

    public DetailInfo(String itemName, String entpName, String efficacy, String useMethod, String warn, String caution, String interact, String sideEffect, String depositMethod) {
        this.itemName = itemName;
        this.entpName = entpName;
        this.efficacy = efficacy;
        this.useMethod = useMethod;
        this.warn = warn;
        this.caution = caution;
        this.interact = interact;
        this.sideEffect = sideEffect;
        this.depositMethod = depositMethod;
    }

    public void setDrugName(String itemName) {
        this.itemName = itemName;
    }

    public void setDetailInfo(ArrayList<String> info) {
        this.entpName = info.get(0);
        this.efficacy = info.get(1);
        this.useMethod = info.get(2);
        this.warn = info.get(3);
        this.caution = info.get(4);
        this.interact = info.get(5);
        this.sideEffect = info.get(6);
        this.depositMethod = info.get(7);
    }
}
