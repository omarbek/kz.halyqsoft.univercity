package kz.halyqsoft.univercity.modules.pdfgeneration;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TextField;

public class CustomField {

    TextField textField;
    TextField xIntegerField;
    TextField yIntegerField;
    ComboBox fontComboBox;
    ComboBox styleComboBox;


    public CustomField() {
        this.textField = new TextField("Текст:");
        this.xIntegerField = new TextField();
        this.yIntegerField = new TextField();
        this.fontComboBox = new ComboBox();
        this.styleComboBox = new ComboBox();

        this.xIntegerField.setCaption("X:");
        this.yIntegerField.setCaption("Y:");
        this.fontComboBox.setCaption("Шрифт:");
        this.styleComboBox.setCaption("Стиль:");

        this.textField.setRequired(true);
        this.xIntegerField.setRequired(true);
        this.yIntegerField.setRequired(true);
        this.fontComboBox.setRequired(true);
        this.styleComboBox.setRequired(true);

        this.textField.setImmediate(true);
        this.xIntegerField.setImmediate(true);
        this.yIntegerField.setImmediate(true);
        this.fontComboBox.setImmediate(true);
        this.styleComboBox.setImmediate(true);

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
}
