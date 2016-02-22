package eu.transkribus.core.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;
import org.apache.commons.net.io.CopyStreamAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FTPUtils {
	private final static Logger logger = LoggerFactory.getLogger(FTPUtils.class);
		
	public static abstract class FTPTransferListener extends CopyStreamAdapter {
		int percent = 0;
		FTPFile fileToDownload;
		boolean canceled=false;
		OutputStream os;
		MyFTPClient client;
		
		public FTPTransferListener(FTPFile fileToDownload) {
			this.fileToDownload = fileToDownload;
		}
		
		public abstract void downloaded(int percent);
		
		@Override public void bytesTransferred(long totalBytesTransferred, int bytesTransferred, long streamSize) {
			int newP = (int) (totalBytesTransferred * 100 / fileToDownload.getSize());
			if (newP != percent) {
				percent = newP;
				downloaded(percent);
			}
		}
		
		public void setCanceled(boolean canceled) { this.canceled = canceled; }
		public boolean isCanceled() { return canceled; }
		
		public boolean abort() {
			setCanceled(true);
			if (/*os != null &&*/ client != null) {
				try {
//					os.close();
					return client.abort();
				} catch (IOException e) {
					logger.error("Error aborting FTP download: "+e.getMessage(), e);
				}
			}
			return false;
		}
		
		public void setOutputStream(OutputStream outputStream) {
			this.os = outputStream;
		}		
		public void setFTPClient(MyFTPClient client) { this.client = client; }
	}
	
	public static abstract class FTPUploadListener extends CopyStreamAdapter {
		int percent = 0;
		File fileToUpload;
		boolean canceled=false;
		public InputStream is;
		public Exception error;
		
		public FTPUploadListener(File fileToUpload) {
			this.fileToUpload = fileToUpload;
		}
		
		public abstract void uploaded(int percent);
		
		@Override public void bytesTransferred(long totalBytesTransferred, int bytesTransferred, long streamSize) {
			int newP = (int) (totalBytesTransferred * 100 / fileToUpload.length());
			if (newP != percent) {
				percent = newP;
				uploaded(percent);
			}
		}
		

		public void setCanceled(boolean canceled) {
			this.canceled = canceled;
		}
		public boolean isCanceled() { return canceled; }

		public boolean abort() {
//			setCanceled(true);
			if (is != null) {
				try {
					logger.debug("closing input stream!");
					is.close();
					logger.debug("done!");
					return true;
				} catch (IOException e) {
					logger.error("Error aborting FTP upload: "+e.getMessage(), e);
				}
			}
			return false;
		}

		public void setInputStream(InputStream is) {
			this.is = is;
		}
	}	
	
	public static class MyFTPClient extends FTPSClient implements Closeable {
		private final static Logger logger = LoggerFactory.getLogger(FTPUtils.MyFTPClient.class);
		
		public MyFTPClient() { super(); }
		
		public MyFTPClient(String host, int port, String username, String pw) throws SocketException, IOException {
			super();
			connectToFTP(this, host, port);
			loginToFTP(this, username, pw);
		}

		@Override public void close() throws IOException {
			if (isConnected()) {
				try {
					disconnect();
					logout();
				} catch (IOException ioe) {
				}
			}
		}
		
		public void downloadFile(String directory, FTPFile fileToDownload, File destFile, int fileType) throws IOException, InterruptedException {
			FTPTransferListener transferL = new FTPTransferListener(fileToDownload) {
				@Override public void downloaded(int percent) {
					logger.trace("Downloaded "+percent+"% of "+fileToDownload.getName());
				}
			};			
			downloadFile(directory, fileToDownload, destFile, fileType, transferL);
		}
		
		public void downloadFile(String directory, FTPFile fileToDownload, File destFile, int fileType, FTPTransferListener transferL) throws IOException, InterruptedException {
			try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(destFile))) {
				logger.debug("setting filetype to: "+fileType);
				setFileType(fileType);
				enterLocalPassiveMode();
				setCopyStreamListener(transferL);
				transferL.setOutputStream(outputStream);
				transferL.setFTPClient(this);
				
				directory = FilenameUtils.normalizeNoEndSeparator(directory)+"/";
				
				boolean success = retrieveFile(directory + fileToDownload.getName(), outputStream);
				
				if (!success) {
					throw new IOException("Error while downloading " + fileToDownload.getName());
				} else if (transferL.isCanceled()) {
					throw new InterruptedException("Download cancelled!");
				} else {
					transferL.downloaded(100);
				}

				logout();
			} catch (IOException e) {
				throw e;
			}
		}
		
		public void uploadFile(String directory, File sourceFile, FTPUploadListener uploadL) throws IOException, InterruptedException {
			try (InputStream is = new BufferedInputStream(new FileInputStream(sourceFile))) {
				setFileType(FTP.BINARY_FILE_TYPE);
				enterLocalPassiveMode();
				setCopyStreamListener(uploadL);
				uploadL.setInputStream(is);
				
				boolean success = true;
				directory = FilenameUtils.normalizeNoEndSeparator(directory);
				if(directory != null && !directory.isEmpty()){
					success = changeWorkingDirectory(directory + "/");
					
					logger.info(success ? "Changed dir to " + printWorkingDirectory() : "Could not change dir to: " + directory);
				}
				if(success){
					success = storeFile(sourceFile.getName(), is);
				}
				
				if (!success) {
					throw new IOException("Error while uploading " + sourceFile.getName());
				} else if (uploadL.isCanceled()) {
					throw new InterruptedException("Upload cancelled!");
				} else {
					uploadL.uploaded(100);
				}

				logout();
			} catch (IOException e) {
				throw e;
			}
		}
	}
	
	public static void connectToFTP(FTPSClient ftp, String ftpServer, int ftpPort) throws SocketException, IOException {
		ftp.connect(ftpServer, ftpPort);
		// After connection attempt, you should check the reply code to verify
		// success.
		int reply = ftp.getReplyCode();

		if (!FTPReply.isPositiveCompletion(reply)) {
			ftp.disconnect();
			throw new IOException("FTP server refused connection, reply code: " + reply);
		}
		logger.debug("Connected to " + ftpServer + "."+ftp.getReplyString());
	}
	
	public static void loginToFTP(FTPSClient ftp, String ftpUser, String ftpPw) throws IOException {
		if (!ftp.login(ftpUser, ftpPw)) {
			throw new IOException("Unable to login!");
		}
	}
	
	public static void downloadFile(String host, int port, String username, String pw, String directory, int fileType, final FTPFile fileToDownload, File destFile) throws IOException, InterruptedException {
		FTPTransferListener transferL = new FTPTransferListener(fileToDownload) {
			@Override public void downloaded(int percent) {
				logger.debug("Downloaded "+percent+"% of "+fileToDownload.getName());
			}
		};
		downloadFile(host, port, username, pw, directory, fileType, fileToDownload, destFile, transferL);
	}
	
	public static void downloadFile(String host, int port, String username, String pw, String directory, int fileType, final FTPFile fileToDownload, File destFile, FTPTransferListener transferL) throws IOException, InterruptedException {
		try (MyFTPClient ftp = new MyFTPClient(host, port, username, pw);) {
			ftp.downloadFile(directory, fileToDownload, destFile, fileType, transferL);
		} catch (IOException e) {
			throw e;
		}
	}
	
	public static void uploadFile(String host, int port, String username, String pw, final String directory, 
		final File sourceFile, FTPUploadListener uploadL) throws IOException, InterruptedException {
		try (InputStream input = new FileInputStream(sourceFile);
			MyFTPClient ftp = new MyFTPClient(host, port, username, pw)) {
			ftp.uploadFile(directory, sourceFile, uploadL);
		} catch (IOException e) {
			throw e;	
		}
	}
	
	public static void uploadFile(String host, int port, String username, String pw, final File sourceFile, 
			FTPUploadListener uploadL) throws IOException, InterruptedException {
		uploadFile(host, port, username, pw, null, sourceFile, uploadL);
	}
	
	public static void uploadFile(String host, int port, String username, String pw, final String directory, 
		final File sourceFile) throws IOException, InterruptedException {
		FTPUploadListener uploadL = new FTPUploadListener(sourceFile) {
			@Override public void uploaded(int percent) {
				logger.debug("Uploaded "+percent+"% of "+ sourceFile.getName());
			}
		};
		uploadFile(host, port, username, pw, directory, sourceFile, uploadL);
	}
}
