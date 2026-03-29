package Utils.mail;

import java.util.Properties;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

/**
 * Description
 *
 * @author Duong Thien Nhan - CE190741
 * @since 2026-02-11
 */
public class MailUtil {

    @SuppressWarnings("CallToPrintStackTrace")
    public static boolean sendOtp(String toEmail, String otp) {
        final String fromEmail = "nhanduong21779@gmail.com";
        final String appPassword = "qdng mcca yfne uzib";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.smtp.ssl.trust", "*");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, appPassword);
            }
        });

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(fromEmail));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            msg.setSubject("[RentHouse] OTP Verification");

            String content = "<html>"
                    + "<body style='font-family: Arial, sans-serif; background-color:#f4f6f8; padding:20px;'>"
                    + "  <table width='100%' cellpadding='0' cellspacing='0'>"
                    + "    <tr>"
                    + "      <td align='center'>"
                    + "        <table width='600px' style='background:#ffffff; border:1px solid #ddd; padding:30px;'>"
                    + "          <tr>"
                    + "            <td>"
                    + "              <h2 style='color:#2c3e50; margin-bottom:20px;'>RentHouse System</h2>"
                    + "              <p style='font-size:14px; color:#333;'>Dear User,</p>"
                    + "              <p style='font-size:14px; color:#333;'>"
                    + "                We received a request to log in to your account. Please use the One-Time Password (OTP) below to proceed:"
                    + "              </p>"
                    + "              <div style='text-align:center; margin:30px 0;'>"
                    + "                <span style='font-size:24px; font-weight:bold; letter-spacing:4px; color:#2c3e50;'>"
                    + otp
                    + "                </span>"
                    + "              </div>"
                    + "              <p style='font-size:14px; color:#333;'>"
                    + "                This OTP is valid for <strong>10 minutes</strong>. Do not share this code with anyone."
                    + "              </p>"
                    + "              <p style='font-size:14px; color:#333;'>"
                    + "                If you did not request this, please ignore this email."
                    + "              </p>"
                    + "              <hr style='margin:30px 0;'>"
                    + "              <p style='font-size:12px; color:#777;'>"
                    + "                This is an automated message. Please do not reply."
                    + "              </p>"
                    + "              <p style='font-size:12px; color:#777;'>"
                    + "                &copy; 2026 RentHouse. All rights reserved."
                    + "              </p>"
                    + "            </td>"
                    + "          </tr>"
                    + "        </table>"
                    + "      </td>"
                    + "    </tr>"
                    + "  </table>"
                    + "</body>"
                    + "</html>";

            msg.setContent(content, "text/html; charset=UTF-8");

            Transport.send(msg);
            return true;

        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return false;
    }
}