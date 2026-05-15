package com.ResumeScore.ATS.resume;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;

@Service
public class ResumeParserService {

    public String extractText(Path filePath) {
        try (PDDocument document = Loader.loadPDF(filePath.toFile())) {
            PDFTextStripper textStripper = new PDFTextStripper();
            return textStripper.getText(document).trim();
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to extract text from resume PDF");
        }
    }
}
