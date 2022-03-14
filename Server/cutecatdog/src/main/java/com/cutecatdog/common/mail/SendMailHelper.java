package com.cutecatdog.common.mail;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Component;

@Component
public class SendMailHelper {
  private String sendMailId = null;
  private String sendMailPassword = null;
  private Properties prop = null;
  private Session session = null;

  public SendMailHelper() {
    initAccount();
    initProperties();
    initSession();
  }

  private void initAccount() {
    this.sendMailId = "sh.kim7141";
    this.sendMailPassword = "1q2w3e4r!@";
  }

  private void initProperties() {
    prop = new Properties();
    prop.put("mail.smtp.host", "smtp.gmail.com");
    prop.put("mail.smtp.port", 465);
    prop.put("mail.smtp.auth", "true");
    prop.put("mail.smtp.ssl.enable", "true");
    prop.put("mail.smtp.ssl.trust", "smtp.gmail.com");
  }

  private void initSession() {
    session = Session.getInstance(prop, new Authenticator() {
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(sendMailId, sendMailPassword);
      }
    });
  }

  public boolean SendMail(String toAddress, String code) {
    boolean isSuccess;

    try {
      MimeMessage msg = new MimeMessage(session);
      msg.setFrom(new InternetAddress(sendMailId, "CUTE CAT DOT"));

      msg.addRecipient(Message.RecipientType.TO, new InternetAddress(toAddress));

      msg.setSubject(String.format("CUTE CAT DOT 인증 값"));
      msg.setText(String.format("인증 값 %s", code));

      Transport.send(msg);

      System.out.println("전송");
      isSuccess = true;
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
      isSuccess = false;
    }

    return isSuccess;
  }
}
