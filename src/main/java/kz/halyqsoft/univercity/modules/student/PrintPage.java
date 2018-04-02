package kz.halyqsoft.univercity.modules.student;

import com.itextpdf.text.Document;

import java.awt.*;
import java.util.Locale;

/**
 * @author Omarbek
 * @created 19.05.2017.
 */
public interface PrintPage {

	void setGraphics(Graphics graphics);

	void setDocument(Document document, int offsetX, int offsetY);

	void setLocale(Locale locale);
}
