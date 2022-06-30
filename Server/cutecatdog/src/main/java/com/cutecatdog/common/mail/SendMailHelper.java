package com.cutecatdog.common.mail;

import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;

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
      msg.setFrom(new InternetAddress(sendMailId, "CUTE CAT DOG"));

      msg.addRecipient(Message.RecipientType.TO, new InternetAddress(toAddress));

      msg.setSubject(String.format("CUTE CAT DOG 인증 코드", "text/html; charset=UTF-8"));
      String contents = "<div id=\"readFrame\"><p></p><table align=\"center\" width=\"670\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"border-top: 2px solid #60b9ce; border-right: 1px solid #e7e7e7; border-left: 1px solid #e7e7e7; border-bottom: 1px solid #e7e7e7;\"><tbody><tr><td style=\"background-color: #ffffff; padding: 40px 30px 0 35px; text-align: center;\"><table width=\"605\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"text-align: left; font-family: '맑은 고딕','돋움';\"><tbody><tr><td style=\"color: #2daad1; font-size: 25px; text-align: left; width: 400px; word-spacing: -1px; vertical-align: top;\">인증 번호 확인 후<br>이메일 인증을 완료해 주세요.</td><td rowspan=\"3\"><img src=\"https://j6d103.p.ssafy.io/images/logo.png\" loading=\"lazy\" width=\"200px\"></td></tr><tr><td height=\"39\"><img src=\"http://mobis.recruiter.co.kr/resources/css/cus/images/pattern/registerbar.png\" loading=\"lazy\"></td></tr><tr><td style=\"font-size: 17px; vertical-align: bottom; height: 27px;\">안녕하세요 &#128049;&#128054;</td></tr><tr><td style=\"font-size: 17px; vertical-align: bottom; height: 27px;\">맞춤형 통합 케어 솔루션 앱 ㅋㅋㄷ입니다.</td></tr><tr><td colspan=\"2\" style=\"font-size: 13px; word-spacing: -1px; height: 30px;\">아래 인증번호를 입력하시고 인증을 완료해주세요.</td></tr></tbody></table></td></tr><tr><td style=\"padding: 39px 196px 70px;\"><table width=\"278\" style=\"background-color: #3cbfaf; font-family: '맑은 고딕','돋움';\"><tbody><tr><td height=\"49\" style=\"text-align: center; color: #fff\">인증번호 : <span>"+code+"</span></td></tr></tbody></table></td></tr></tbody></table><p></p><img height=\"1\" width=\"1\" border=\"0\" style=\"display:none;\" src=\"http://ems.midasit.com:4121/6I-110098I-41E-8153246449I-4uPmuPzeI-4I-3\" loading=\"lazy\"></div>";
      msg.setDataHandler(new DataHandler(new ByteArrayDataSource(contents, "text/html;charset=UTF-8")));

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
