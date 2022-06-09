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

    private String chart;

    private String formName;

    private String className;

    private String mixtureItemName;

    private String mixtureClassName;

    private String mixtureChart;

    @Lob
    private String prohbtContent;

    @Lob
    private String remark;

    private boolean pregnancyBan;

    @Lob
    private String pregnancyProhbtContent;

    @Lob
    private String pregnancyRemark;

    private boolean oldBan;

    @Lob
    private String oldProhbtContent;

    @Lob
    private String oldRemark;

    public void setDrugName(String itemName) {
        this.itemName = itemName;
    }

    public void setDrugInfo(ArrayList<String> info) {
        this.chart = info.get(0);
        this.formName = info.get(1);
        this.className = info.get(2);
        this.mixtureItemName = info.get(3);
        this.mixtureClassName = info.get(4);
        this.mixtureChart = info.get(5);
        this.prohbtContent = info.get(6);
        this.remark = info.get(7);
    }

    public void setPregInfo(ArrayList<String> info) {
        if (info == null) this.pregnancyBan = false;
        else {
            this.pregnancyBan = true;
            this.pregnancyProhbtContent = info.get(0);
            this.pregnancyRemark = info.get(1);
        }
    }

    public void setOldInfo(ArrayList<String> info) {
        if (info == null) this.oldBan = false;
        else {
            this.oldBan = true;
            this.oldProhbtContent = info.get(0);
            this.oldRemark = info.get(1);
        }
    }
}
