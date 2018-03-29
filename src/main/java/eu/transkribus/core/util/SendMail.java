package eu.transkribus.core.util;

import java.io.File;
import java.net.InetAddress;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SendMail {
	private static Logger logger = LoggerFactory.getLogger(SendMail.class);

	private static final String MAIL_SMTP_AUTH_KEY = "mail.smtp.auth";

	String smtp;
	int smtpPort;
	String username;
	String password;
	String email;
	Properties props;
	Authenticator authenticator;

	public SendMail(String smtp, int port, String username, String password, String email) {
		this.smtp = smtp;
		this.smtpPort = port;
		this.username = username;
		this.password = password;
		this.email = email;

		props = new Properties();
		props.put("mail.smtp.host", smtp);
		props.put("mail.smtp.localhost", smtp); // FIXME: needed if localhost unknown...
		props.put("mail.smtp.socketFactory.port", "" + smtpPort);
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.port", "" + smtpPort);

		authenticator = initAuthenticator();
	}

	private Authenticator initAuthenticator() {
		return new javax.mail.Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		};
	}

	public String getSmtp() {
		return smtp;
	}

	public void setSmtp(String smtp) {
		this.smtp = smtp;
	}

	public int getSmtpPort() {
		return smtpPort;
	}

	public void setSmtpPort(int smtpPort) {
		this.smtpPort = smtpPort;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Properties getProps() {
		return props;
	}

	public void setProps(Properties props) {
		this.props = props;
	}

	private Session createSession(boolean useAuth) {
		props.put(MAIL_SMTP_AUTH_KEY, "" + useAuth);
		if (useAuth) {
			return Session.getDefaultInstance(props, authenticator);
		} else {
			return Session.getDefaultInstance(props);
		}
	}

	/**
	 * {@link #sendMailSSL(String, String, File[], String)}
	 */
	public void sendMailSSL(String toAddress, String subject, String messageText, String replyTo,
			boolean doAuthentication) throws MessagingException {
		sendMailSSL(toAddress, subject, messageText, null, replyTo, doAuthentication);
	}

	public static Multipart createMultiPartMessage(String message, File[] atts) throws MessagingException {
		MimeBodyPart messageBodyPart = new MimeBodyPart();
		// fill message
		messageBodyPart.setText(message);
		Multipart multipart = new MimeMultipart();
		multipart.addBodyPart(messageBodyPart);
		// Part 2-N is attachment
		for (File f : atts) {
			MimeBodyPart fileBodyPart = new MimeBodyPart();
			DataSource source = new FileDataSource(f);
			fileBodyPart.setDataHandler(new DataHandler(source));
			fileBodyPart.setFileName(f.getName());
			multipart.addBodyPart(fileBodyPart);
		}

		return multipart;
	}

	/**
	 * Sends a mail via SSL
	 * 
	 * @param subject
	 *            The subject of the mail
	 * @param messageText
	 *            The message of the mail
	 * @param attachment
	 *            The attachments of the mail - can be empty or null
	 * @param replyTo
	 *            The reply to addresses (comma seperated!) - can be empty of null
	 * @param doAuthentication
	 *            should be true in most cases! UIBK IT allows to send unlimited
	 *            mails when set to false
	 * @throws MessagingException
	 */
	public void sendMailSSL(String toAddress, String subject, String messageText, File[] attachment, String replyTo,
			boolean doAuthentication) throws MessagingException {
		Session session = createSession(doAuthentication);

		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(email));
		if (replyTo != null && !replyTo.isEmpty())
			message.setReplyTo(InternetAddress.parse(replyTo));
		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toAddress));
		message.setSubject(subject);
		message.setSentDate(new Date());
		if (attachment == null || attachment.length == 0) {
			message.setText(messageText);
		} else {
			Multipart multipart = createMultiPartMessage(messageText, attachment);
			message.setContent(multipart);
		}

		Transport.send(message);
	}

	public static void main(String[] args) {
		try {
			System.out.println(InetAddress.getLocalHost().getHostName());

			// SendMail sm = new SendMail("smtp.xxx.xxx", 1234, "whatever@whatever.com",
			// "password", "whatever@whatever.com");
			// sm.sendMailSSL(sm.getEmail(), "test", "tttttttttttttadsfasdf",
			// "bla@bla.com");
			logger.info("DONE");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
