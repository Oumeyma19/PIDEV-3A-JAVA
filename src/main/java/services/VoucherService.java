package services;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class VoucherService {
    public void generateVoucher(String filePath, String userName, String offerName, String startDate, String endDate, double totalPrice) {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);
            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            // ✅ QR Code Generation
            String qrText = "Reservation for " + userName + "\nOffer: " + offerName + "\nDates: " + startDate + " - " + endDate;
            String qrPath =  "src/main/voucher/qrcode.png";
            try {
                qrPath = generateQRCode(qrText, qrPath);
            } catch (WriterException | IOException e) {
                e.printStackTrace();
                System.out.println("Error generating QR code.");
            }
            // ✅ Load QR Code Image
            PDImageXObject qrCodeImage = PDImageXObject.createFromFile(qrPath, document);

            // 🔹 Background Rectangle for Border Effect
            contentStream.setNonStrokingColor(Color.LIGHT_GRAY);
            contentStream.addRect(50, 50, 500, 700);
            contentStream.fill();

            // 🔹 Header
            contentStream.setNonStrokingColor(Color.BLACK);
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 20);
            contentStream.newLineAtOffset(200, 700);
            contentStream.showText("VOUCHER CONFIRMATION");
            contentStream.endText();

            // 🔹 User Details
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 14);
            contentStream.newLineAtOffset(100, 650);
            contentStream.showText("Customer: " + userName);
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("Offer: " + offerName);
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("Dates: " + startDate + " to " + endDate);
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("Total Price: " + totalPrice + " €");
            contentStream.newLineAtOffset(0, -40);
            contentStream.showText("Thank you for your reservation!");
            contentStream.endText();

            // 🔹 Draw QR Code
            contentStream.drawImage(qrCodeImage, 200, 300, 150, 150);

            contentStream.close();
            document.save(filePath);
            System.out.println("Voucher generated: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error generating voucher.");
        }
    }
    private String generateQRCode(String text, String filePath) throws WriterException, IOException {
        int width = 200;
        int height = 200;
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
        Path path = FileSystems.getDefault().getPath(filePath);
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
        return filePath;
    }

}

