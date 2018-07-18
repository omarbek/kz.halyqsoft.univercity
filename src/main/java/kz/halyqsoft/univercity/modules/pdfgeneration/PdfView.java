package kz.halyqsoft.univercity.modules.pdfgeneration;

import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.vaadin.view.AbstractTaskView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class PdfView extends AbstractTaskView {
    private Button generateButton;
    private Button addComponentButton;
    private TextField pdfTitle;
    private TextField title;
    private ArrayList<CustomField> customFieldList = new ArrayList<CustomField>();

    private StreamResource.StreamSource streamSource;


    private StreamResource createResource() {
        String defaultName = "default.pdf";
        return new StreamResource(streamSource, defaultName);
    }


    public PdfView(AbstractTask task) throws Exception {
        super(task);
    }


    @Override
    public void initView(boolean b) throws Exception {

        generateButton = new Button(getUILocaleUtil().getCaption("generate"));
        addComponentButton = new Button("Добавить");

        pdfTitle = new TextField("Название файла");
        pdfTitle.setImmediate(true);
        pdfTitle.setRequired(true);

        title = new TextField("Заголовок:");
        title.setImmediate(true);
        title.setRequired(true);

        StreamResource myResource = createResource();
        FileDownloader fileDownloader = new FileDownloader(myResource);
        fileDownloader.extend(generateButton);

        generateButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {

                CustomDocument dc = new CustomDocument();
                dc.initialize(customFieldList , title.getValue());

                ByteArrayOutputStream byteArrayOutputStream = dc.getByteArrayOutputStream();
                streamSource = new CustomSource(byteArrayOutputStream);
                myResource.setStreamSource(streamSource);
                if(!pdfTitle.getValue().trim().equals(""))
                    myResource.setFilename(pdfTitle.getValue()+".pdf");
                else
                    myResource.setFilename("default.pdf");
            }

        });

        List<HorizontalLayout> horizontalLayoutList = new ArrayList<HorizontalLayout>();

        HorizontalLayout mainHL=new HorizontalLayout();
        mainHL.addComponent(pdfTitle);
        mainHL.addComponent(title);
        horizontalLayoutList.add(mainHL);

        addComponentButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                CustomField customField = new CustomField();

                HorizontalLayout textHL=new HorizontalLayout();
                textHL.addComponent(customField.getTextField());
                textHL.addComponent(customField.getxIntegerField());
                textHL.addComponent(customField.getyIntegerField());
                textHL.addComponent(customField.getStyleComboBox());
                textHL.addComponent(customField.getFontComboBox());

                customFieldList.add(customField);
                horizontalLayoutList.add(textHL);
                getContent().addComponent(textHL);
                getContent().setComponentAlignment(textHL, Alignment.MIDDLE_CENTER);
            }
        });


        HorizontalLayout acitivityHL = new HorizontalLayout();
        acitivityHL.addComponent(generateButton);
        acitivityHL.addComponent(addComponentButton);

        horizontalLayoutList.add(acitivityHL);

        getHL(horizontalLayoutList);


    }

    private void getHL(List<HorizontalLayout> horizontalLayoutList) {
        for(HorizontalLayout hl : horizontalLayoutList)
        {
            getContent().addComponent(hl);

            getContent().setComponentAlignment(hl, Alignment.MIDDLE_CENTER);
        }
    }

}