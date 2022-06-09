package MAP.taboodrug.drug.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import java.util.ArrayList;

@NoArgsConstructor
@Getter
@Entity
public class DrugInfo {
    @Id
    private String itemName;

    private String mixtureItemName;

    @Lob
    private String prohbtContent;

    private boolean pregnancyBan;

    @Lob
    private String pregnancyProhbtContent;

    private boolean oldBan;

    @Lob
    private String oldProhbtContent;

    public void setDrugName(String itemName) {
        this.itemName = itemName;
    }

    public void setDrugInfo(ArrayList<String> info) {
        this.mixtureItemName = info.get(0);
        this.prohbtContent = info.get(1);
    }

    public void setPregInfo(String info) {
        if (info == null) {
            this.pregnancyBan = false;
            this.pregnancyProhbtContent = "";
        }
        else {
            this.pregnancyBan = true;
            this.pregnancyProhbtContent = info;
        }
    }

    public void setOldInfo(String info) {
        if (info == null) {
            this.oldBan = false;
            this.oldProhbtContent = "";
        }
        else {
            this.oldBan = true;
            this.oldProhbtContent = info;
        }
    }
}
