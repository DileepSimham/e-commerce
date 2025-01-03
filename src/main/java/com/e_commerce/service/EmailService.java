package com.e_commerce.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.e_commerce.entity.Order;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendMail(String to, String subject, String text, Order order) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true); // true enables multipart message

        try {
        	// Set the from email address with a custom display name
            helper.setFrom("dileep.simham483@gmail.com", "E-Commerce Team");
            helper.setTo(to);
            helper.setSubject(subject);
            
            
            // Generate PDF from the Order object
            log.info("pdf is being generated");
            ByteArrayOutputStream pdfStream = generateOrderPdf(order);
            log.info("pdf generated");
            String productName=order.getProduct().getName();
            String quantity=order.getQuantity()+"";
            String price=order.getTotalPrice()+"";
            
            // Professionally styled HTML email for order delivery notification
            String htmlContent = "<html><body style='font-family: Arial, sans-serif; color: #333;'>"
                    + "<div style='background-color: #f8f8f8; padding: 20px; border-radius: 10px;'>"
                    + "<h2 style='color: #4CAF50;'>Order Delivered</h2>"
                    + "<p style='font-size: 16px;'>Dear <strong>" + to + "</strong>,</p>"
                    + "<p style='font-size: 14px;'>We are pleased to inform you that your order has been successfully delivered.</p>"
                    + "<p style='font-size: 14px;'>Here are the details of your order:</p>"
                    + "<table style='width: 100%; border-collapse: collapse; margin-bottom: 20px;'>"
                    + "<tr><th style='text-align: left; padding: 8px; background-color: #f4f4f4;'>Product</th>"
                    + "<th style='text-align: left; padding: 8px; background-color: #f4f4f4;'>Quantity</th>"
                    + "<th style='text-align: left; padding: 8px; background-color: #f4f4f4;'>Price</th></tr>"
                    + "<tr><td style='padding: 8px;'>" + productName + "</td><td style='padding: 8px;'>" + quantity + "</td><td style='padding: 8px;'>$" + price + "</td></tr>"
                    + "</table>"
                    + "<p style='font-size: 14px;'>Thank you for shopping with us. We hope you enjoy your purchase!</p>"
                    + "<p style='font-size: 14px; color: #888;'>If you have any questions, feel free to contact our support team.</p>"
                    + "<div style='padding-top: 20px; border-top: 2px solid #ddd; text-align: center;'>"
                    + "<p style='font-size: 12px; color: #888;'>Best regards,</p>"
                    + "<p style='font-size: 12px; color: #888;'>The E-Commerce Team</p>"
                    + "</div>"
                    + "</div>"
                    + "</body></html>";

            helper.setText(htmlContent, true); // Set HTML content
            
            // Attach the generated PDF
            helper.addAttachment("OrderDetails.pdf", new ByteArrayResource(pdfStream.toByteArray()));
//
            mailSender.send(message);
           log.info("Email has been delivered successfully");

        } catch (Exception e) {
        	
        	log.error("Error occurred while sending the email {}", e.getMessage());
            
        }
    }
    
    public ByteArrayOutputStream generateOrderPdf(Order order) throws IOException, DocumentException {
        // Create the PDF document
        ByteArrayOutputStream pdfStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, pdfStream);
        document.open();
        
        // Add Order Title
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
        Paragraph title = new Paragraph("Order Details", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        // Add Product Name
        Font productFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
        Paragraph productName = new Paragraph("Product: " + order.getProduct().getName(), productFont);
        productName.setSpacingAfter(10);
        document.add(productName);

        // Add Quantity
        Paragraph quantity = new Paragraph("Quantity: " + order.getQuantity(), productFont);
        quantity.setSpacingAfter(10);
        document.add(quantity);

        // Add Total Price
        Paragraph price = new Paragraph("Total Price: $" + order.getTotalPrice(), productFont);
        price.setSpacingAfter(10);
        document.add(price);

        // Close the document
        document.close();
        
        return pdfStream;
    }

    
}
