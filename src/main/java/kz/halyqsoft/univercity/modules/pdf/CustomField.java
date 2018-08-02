package kz.halyqsoft.univercity.modules.pdf;
import com.vaadin.data.Property;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.CheckBox;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.r3a.common.entity.ID;

public class CustomField {

    ID id;
    TextArea textField;
    ComboBox xComboBox;
    ComboBox yComboBox;
    ComboBox fontComboBox;
    ComboBox textSizeComboBox;
    CheckBox centerCheckBox;
    CheckBox rightCheckBox;
    CheckBox customCheckBox;
    TextField pdfTitle;
    TextField title;
    TextField deadlineDays;
    TextField order;

    static final String BOLD = "Bold";
    static final String ITALIC = "Italic";
    static final String NORMAL = "Normal";
    static final String UNDERLINE = "Underline";
    static final String BOLDITALIC = "BoldItalic";

    public CustomField() {
        this.xComboBox = new ComboBox();
        this.yComboBox = new ComboBox();
        this.fontComboBox = new ComboBox();
        this.textSizeComboBox = new ComboBox();
        this.pdfTitle = new TextField();
        this.title = new TextField();
        this.order = new TextField();
        this.deadlineDays = new TextField();
        deadlineDays.setConverter(Integer.class);
        deadlineDays.setValue("0");
        deadlineDays.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                if(!NumberUtils.isDigits(deadlineDays.getValue()))
                {
                    deadlineDays.setValue("0");
                }
            }
        });
        this.centerCheckBox = new CheckBox();
        this.rightCheckBox = new CheckBox();
        this.customCheckBox = new CheckBox();

        this.textField = new TextArea(CommonUtils.getUILocaleUtil().getCaption("text") + ":");
        this.yComboBox.setCaption(CommonUtils.getUILocaleUtil().getCaption("space.top") + ":");
        this.xComboBox.setCaption(CommonUtils.getUILocaleUtil().getCaption("space.right") + ":");
        this.fontComboBox.setCaption(CommonUtils.getUILocaleUtil().getCaption("font") + ":");
        this.textSizeComboBox.setCaption(CommonUtils.getUILocaleUtil().getCaption("text.size") + ":");
        this.title.setCaption(CommonUtils.getUILocaleUtil().getCaption("title") + ":");
        this.pdfTitle.setCaption(CommonUtils.getUILocaleUtil().getCaption("file.name") + ":");
        this.order.setCaption(CommonUtils.getUILocaleUtil().getCaption("text.order") + ":");
        this.deadlineDays.setCaption(CommonUtils.getUILocaleUtil().getCaption("pdf.period") + ":");
        this.centerCheckBox.setCaption(CommonUtils.getUILocaleUtil().getCaption("text.center") + ":");
        this.rightCheckBox.setCaption(CommonUtils.getUILocaleUtil().getCaption("text.right") + ":");
        this.customCheckBox.setCaption(CommonUtils.getUILocaleUtil().getCaption("text.custom") + ":");

        this.textField.setRequired(true);
        this.xComboBox.setRequired(true);
        this.yComboBox.setRequired(true);
        this.fontComboBox.setRequired(true);
        this.textSizeComboBox.setRequired(true);
        this.title.setRequired(true);
        this.pdfTitle.setRequired(true);
        this.order.setRequired(true);
        this.deadlineDays.setRequired(true);

        this.textField.setImmediate(true);
        this.xComboBox.setImmediate(true);
        this.yComboBox.setImmediate(true);
        this.fontComboBox.setImmediate(true);
        this.textSizeComboBox.setImmediate(true);
        this.title.setImmediate(true);
        this.pdfTitle.setImmediate(true);
        this.order.setImmediate(true);
        this.deadlineDays.setImmediate(true);
        this.centerCheckBox.setImmediate(true);
        this.rightCheckBox.setImmediate(true);
        this.customCheckBox.setImmediate(true);

        this.fontComboBox.addItem(BOLD);
        this.fontComboBox.addItem(ITALIC);
        this.fontComboBox.addItem(NORMAL);
        this.fontComboBox.addItem(UNDERLINE);
        this.fontComboBox.addItem(BOLDITALIC);

        this.textField.setWidth(450, Sizeable.Unit.PIXELS);
        this.textField.setHeight(150, Sizeable.Unit.PIXELS);

        for(int i = -210; i <= 100; i+=3){
            this.yComboBox.addItem(i);

        }
        for(int i = 0; i<=700;i+=3){
            this.xComboBox.addItem(i);
        }
        for(int i = 8; i <= 72; i+=2){
            this.textSizeComboBox.addItem(i);
        }

    }


    public ComboBox getFontComboBox() {
        return fontComboBox;
    }

    public TextField getPdfTitle() {
        return pdfTitle;
    }

    public void setPdfTitle(TextField pdfTitle) {
        this.pdfTitle = pdfTitle;
    }

    public TextField getTitle() {
        return title;
    }

    public void setTitle(TextField title) {
        this.title = title;
    }

    public ComboBox getxComboBox() {
        return xComboBox;
    }

    public void setxComboBox(ComboBox xComboBox) {
        this.xComboBox = xComboBox;
    }

    public ComboBox getyComboBox() {
        return yComboBox;
    }

    public void setyComboBox(ComboBox yComboBox) {
        this.yComboBox = yComboBox;
    }

    public ID getId() {
        return id;
    }

    public void setId(ID id) {
        this.id = id;
    }

    public ComboBox getTextSizeComboBox() {
        return textSizeComboBox;
    }

    public void setTextSizeComboBox(ComboBox textSizeComboBox) {
        this.textSizeComboBox = textSizeComboBox;
    }

    public TextArea getTextField() {
        return textField;
    }

    public void setTextField(TextArea textField) {
        this.textField = textField;
    }

    public TextField getOrder() {
        return order;
    }

    public void setOrder(TextField order) {
        this.order = order;
    }

    public CheckBox getCenterCheckBox() {
        return centerCheckBox;
    }

    public void setCenterCheckBox(CheckBox centerCheckBox) {
        this.centerCheckBox = centerCheckBox;
    }

    public TextField getDeadlineDays() {
        return deadlineDays;
    }

    public void setDeadlineDays(TextField deadlineDays) {
        this.deadlineDays = deadlineDays;
    }

    public CheckBox getRightCheckBox() {
        return rightCheckBox;
    }

    public void setRightCheckBox(CheckBox rightCheckBox) {
        this.rightCheckBox = rightCheckBox;
    }

    public CheckBox getCustomCheckBox() {
        return customCheckBox;
    }

    public void setCustomCheckBox(CheckBox customCheckBox) {
        this.customCheckBox = customCheckBox;
    }
}
