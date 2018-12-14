package kz.halyqsoft.univercity.filter;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.CORPUS;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.DOCUMENT_TYPE;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.PDF_DOCUMENT_TYPE;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.ROOM_TYPE;
import org.r3a.common.vaadin.widget.filter2.AbstractFilterBean;

public final class FPdfDocumentFilter extends AbstractFilterBean {

	private String fileName;
	private PDF_DOCUMENT_TYPE pdfDocumentType;

	public FPdfDocumentFilter() {
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public PDF_DOCUMENT_TYPE getPdfDocumentType() {
		return pdfDocumentType;
	}

	public void setPdfDocumentType(PDF_DOCUMENT_TYPE pdfDocumentType) {
		this.pdfDocumentType = pdfDocumentType;
	}

	@Override
	public boolean hasFilter() {
		return false;
	}
}
