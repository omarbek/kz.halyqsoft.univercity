package kz.halyqsoft.univercity.modules.pdfgeneration;

import com.vaadin.server.StreamResource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class CustomSource implements StreamResource.StreamSource {


    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

    public CustomSource() {
    }


    public CustomSource(ByteArrayOutputStream byteArrayOutputStream) {
        this.byteArrayOutputStream = byteArrayOutputStream;
    }

    public ByteArrayOutputStream getByteArrayOutputStream() {
        return byteArrayOutputStream;
    }

    public void setByteArrayOutputStream(ByteArrayOutputStream byteArrayOutputStream) {
        this.byteArrayOutputStream = byteArrayOutputStream;
    }

    @Override
    public InputStream getStream() {

        ByteArrayInputStream bInput = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        return bInput;
    }
}