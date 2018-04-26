package kz.halyqsoft.univercity.modules.pdf;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TextField;
import org.r3a.common.entity.ID;

public class CustomField {

    ID id;
    TextField textField;
    TextField xIntegerField;
    TextField yIntegerField;
    ComboBox fontComboBox;
    ComboBox styleComboBox;
    TextField textSize;
    TextField pdfTitle;
    TextField title;



    public CustomField() {
        this.textField = new TextField("Текст:");
        this.xIntegerField = new TextField();
        this.yIntegerField = new TextField();
        this.fontComboBox = new ComboBox();
        this.styleComboBox = new ComboBox();
        this.textSize = new TextField();
        this.pdfTitle = new TextField();
        this.title = new TextField();

        this.xIntegerField.setCaption("X:");
        this.yIntegerField.setCaption("Y:");
        this.fontComboBox.setCaption("Шрифт:");
        this.styleComboBox.setCaption("Стиль:");
        this.textSize.setCaption("Размер");
        this.title.setCaption("Заголовок");
        this.pdfTitle.setCaption("Название файла");

        this.textField.setRequired(true);
        this.xIntegerField.setRequired(true);
        this.yIntegerField.setRequired(true);
        this.fontComboBox.setRequired(true);
        this.styleComboBox.setRequired(true);
        this.textSize.setRequired(true);
        this.title.setRequired(true);
        this.pdfTitle.setRequired(true);

        this.textField.setImmediate(true);
        this.xIntegerField.setImmediate(true);
        this.yIntegerField.setImmediate(true);
        this.fontComboBox.setImmediate(true);
        this.styleComboBox.setImmediate(true);
        this.textSize.setImmediate(true);
        this.title.setImmediate(true);
        this.pdfTitle.setImmediate(true);

        this.fontComboBox.addItem("Bold");
        this.fontComboBox.addItem("Italic");
        this.fontComboBox.addItem("Normal");

        this.styleComboBox.addItem("Times_Roman");
        this.styleComboBox.addItem("Helvetica");
        this.styleComboBox.addItem("Courier");

    }

    public TextField getTextField() {
        return textField;
    }

    public void setTextField(TextField textField) {
        this.textField = textField;
    }

    public TextField getxIntegerField() {
        return xIntegerField;
    }

    public void setxIntegerField(TextField xIntegerField) {
        this.xIntegerField = xIntegerField;
    }

    public TextField getyIntegerField() {
        return yIntegerField;
    }

    public void setyIntegerField(TextField yIntegerField) {
        this.yIntegerField = yIntegerField;
    }

    public ComboBox getFontComboBox() {
        return fontComboBox;
    }

    public void setFontComboBox(ComboBox fontComboBox) {
        this.fontComboBox = fontComboBox;
    }

    public ComboBox getStyleComboBox() {
        return styleComboBox;
    }

    public void setStyleComboBox(ComboBox styleComboBox) {
        this.styleComboBox = styleComboBox;
    }

    public TextField getTextSize() {
        return textSize;
    }

    public void setTextSize(TextField textSize) {
        this.textSize = textSize;
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

    public ID getId() {
        return id;
    }

    public void setId(ID id) {
        this.id = id;
    }
}
