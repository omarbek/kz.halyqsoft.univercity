package kz.halyqsoft.univercity.modules.pdf;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import org.r3a.common.entity.ID;

public class CustomField {

    ID id;
    TextArea textField;
    ComboBox xComboBox;
    ComboBox yComboBox;
    ComboBox fontComboBox;
    ComboBox textSizeComboBox;
    TextField pdfTitle;
    TextField title;
    TextField order;

    static final String BOLD = "Bold";
    static final String ITALIC = "Italic";
    static final String NORMAL = "Normal";
    static final String UNDERLINE = "Underline";

    public CustomField() {
        this.textField = new TextArea("Текст:");
        this.xComboBox = new ComboBox();
        this.yComboBox = new ComboBox();
        this.fontComboBox = new ComboBox();
        this.textSizeComboBox = new ComboBox();
        this.pdfTitle = new TextField();
        this.title = new TextField();
        this.order = new TextField();

        this.xComboBox.setCaption("X:");
        this.yComboBox.setCaption("Y:");
        this.fontComboBox.setCaption("Шрифт:");
        this.textSizeComboBox.setCaption("Размер");
        this.title.setCaption("Заголовок");
        this.pdfTitle.setCaption("Название файла");
        this.order.setCaption("Очередь");

        this.textField.setRequired(true);
        this.xComboBox.setRequired(true);
        this.yComboBox.setRequired(true);
        this.fontComboBox.setRequired(true);
        this.textSizeComboBox.setRequired(true);
        this.title.setRequired(true);
        this.pdfTitle.setRequired(true);
        this.order.setRequired(true);

        this.textField.setImmediate(true);
        this.xComboBox.setImmediate(true);
        this.yComboBox.setImmediate(true);
        this.fontComboBox.setImmediate(true);
        this.textSizeComboBox.setImmediate(true);
        this.title.setImmediate(true);
        this.pdfTitle.setImmediate(true);
        this.order.setImmediate(true);

        this.fontComboBox.addItem(BOLD);
        this.fontComboBox.addItem(ITALIC);
        this.fontComboBox.addItem(NORMAL);
        this.fontComboBox.addItem(UNDERLINE);

        for(int i = -90; i <= 200; i+=3){
            this.xComboBox.addItem(i);

        }
        for(int i = 0; i<=500;i+=3){
            this.yComboBox.addItem(i);
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
}
