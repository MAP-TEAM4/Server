package MAP.taboodrug.drug.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.ArrayList;

@NoArgsConstructor
@Getter
@Entity
public class Drug {
    @Id
    @Column(name = "itemName")
    private String itemName;

    private String chart;

    private String formName;

    private String className;

    private String mixtureItemName;

    private String mixtureClassName;

    private String mixtureChart;

    private String prohbtContent;

    private String remark;

    private boolean pregnancyBan;

    private String pregnancyProhbtContent;

    private String pregnancyRemark;

    private boolean oldBan;

    private String oldProhbtContent;

    private String oldRemark;

    public Drug(String itemName, String chart, String formName, String className, String mixtureItemName, String mixtureClassName, String mixtureChart, String prohbtContent, String remark, boolean pregnancyBan, String pregnancyProhbtContent, String pregnancyRemark, boolean oldBan, String oldProhbtContent, String oldRemark) {
        this.itemName = itemName;
        this.chart = chart;
        this.formName = formName;
        this.className = className;
        this.mixtureItemName = mixtureItemName;
        this.mixtureClassName = mixtureClassName;
        this.mixtureChart = mixtureChart;
        this.prohbtContent = prohbtContent;
        this.remark = remark;
        this.pregnancyBan = pregnancyBan;
        this.pregnancyProhbtContent = pregnancyProhbtContent;
        this.pregnancyRemark = pregnancyRemark;
        this.oldBan = oldBan;
        this.oldProhbtContent = oldProhbtContent;
        this.oldRemark = oldRemark;
    }

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
