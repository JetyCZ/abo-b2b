package cz.abo.b2b.web.order

import cz.abo.b2b.web.importer.xls.controller.dto.FileAttachment
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ByteArrayResource
import org.springframework.mail.MailException
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.mail.javamail.MimeMessagePreparator
import org.springframework.stereotype.Service
import javax.mail.internet.MimeMessage

@Service
class EmailService {
    companion object {
        init {
            if (System.getProperty("SPRING_MAIL_PASSWORD")==null) {
                System.setProperty("SPRING_MAIL_PASSWORD","")
            }
        }
    }

    @Value("\${mail.from}")
    val mailFrom: String? = null

    @Value("\${spring.mail.password}")
    val springMailPassword: String? = null

    @Autowired
    private val mailSender: JavaMailSender? = null
    fun sendMailWithAttachment(
        replyTo: String?,
        to: String?,
        cc: String?,
        subject: String?,
        body: String?,
        emailAttachment: FileAttachment?
    ): Boolean {
        val preparator = MimeMessagePreparator { mimeMessage: MimeMessage? ->
            val helper = MimeMessageHelper(mimeMessage, true)
            helper.setText(body)
            helper.setFrom(mailFrom!!, "Objednávka pro " + replyTo)
            helper.setReplyTo(replyTo)
            helper.setTo(to)
            if (!StringUtils.isEmpty(cc)) {
                helper.setCc(cc)
            }
            if (!StringUtils.isEmpty(subject)) {
                helper.setSubject(subject)
            }
            if (emailAttachment!=null) {
                helper.addAttachment(
                    emailAttachment.filename,
                    ByteArrayResource(emailAttachment.content),
                    emailAttachment.contentType
                )
            }

        }
        return try {
            mailSender!!.send(preparator)
            true
        } catch (ex: MailException) {
            // simply log it and go on...
            System.err.println(ex.message)
            false
        }
    }

}