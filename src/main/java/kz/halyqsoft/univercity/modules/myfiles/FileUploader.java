package kz.halyqsoft.univercity.modules.myfiles;

import com.vaadin.server.Page;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.apache.commons.logging.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

@SuppressWarnings("serial")
public class FileUploader implements Upload.Receiver, Upload.SucceededListener, Upload.FailedListener {

	private File file;

	public File getFile() {
		return file;
	}

	public byte[] getByteArray() {
		FileInputStream fileInputStream;
		byte[] bFile = new byte[(int) file.length()];
		try {
			fileInputStream = new FileInputStream(file);
			fileInputStream.read(bFile);
			fileInputStream.close();
			for (byte aBFile : bFile) {
				System.out.print((char) aBFile);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bFile;
	}

	@Override
	public void uploadFailed(Upload.FailedEvent ev) {
		CommonUtils.LOG.error(": " + ev.getFilename());
		CommonUtils.LOG.error(String.valueOf(ev.getReason()));
	}

	@Override
	public void uploadSucceeded(SucceededEvent ev) {
	}

	@Override
	public OutputStream receiveUpload(String filename, String mimeType) {
		FileOutputStream fos = null;
		try {
			file = new File(filename);
			if (file.exists()) {
				file.delete();
			}
			fos = new FileOutputStream(file);
		} catch (Exception ex) {
			CommonUtils.LOG.error("Cannot upload file: " + filename, ex);
		}

		return fos;
	}
}