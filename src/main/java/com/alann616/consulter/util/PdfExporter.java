package com.alann616.consulter.util;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class PdfExporter {

    // Tamaño Letter en píxeles para 96 dpi
    private static final double LETTER_WIDTH_PX = 816;
    private static final double LETTER_HEIGHT_PX = 1056;

    /**
     * Exporta un solo nodo a un archivo PDF
     */
    public static void export(Node nodeToExport, String outputPath) throws IOException {
        export(List.of(nodeToExport), outputPath);
    }

    /**
     * Exporta múltiples nodos a un archivo PDF, cada uno como una página separada
     */
    public static void export(List<Node> nodesToExport, String outputPath) throws IOException {
        // Paso 1: Crear documento PDF
        try (PDDocument doc = new PDDocument()) {

            // Paso 2: Para cada nodo, crear una página
            for (Node nodeToExport : nodesToExport) {
                // Configurar tamaño del nodo
                if (nodeToExport instanceof Region) {
                    Region region = (Region) nodeToExport;
                    region.setPrefSize(LETTER_WIDTH_PX, LETTER_HEIGHT_PX);
                    region.setMinSize(LETTER_WIDTH_PX, LETTER_HEIGHT_PX);
                    region.setMaxSize(LETTER_WIDTH_PX, LETTER_HEIGHT_PX);
                }

                // Contenedor temporal (invisible para el usuario)
                StackPane container = new StackPane(nodeToExport);
                container.setPrefSize(LETTER_WIDTH_PX, LETTER_HEIGHT_PX);
                container.setMinSize(LETTER_WIDTH_PX, LETTER_HEIGHT_PX);
                container.setMaxSize(LETTER_WIDTH_PX, LETTER_HEIGHT_PX);

                container.applyCss();
                container.layout();

                // Tomar snapshot
                WritableImage image = new WritableImage((int) LETTER_WIDTH_PX, (int) LETTER_HEIGHT_PX);
                container.snapshot(new SnapshotParameters(), image);

                // Guardar imagen temporal
                File tempImage = File.createTempFile("pdf_export_", ".png");
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", tempImage);

                // Crear página PDF
                PDPage page = new PDPage(new PDRectangle((float) LETTER_WIDTH_PX, (float) LETTER_HEIGHT_PX));
                doc.addPage(page);

                // Añadir imagen a la página
                PDImageXObject pdImage = PDImageXObject.createFromFileByContent(tempImage, doc);
                try (PDPageContentStream contents = new PDPageContentStream(doc, page)) {
                    contents.drawImage(pdImage, 0, 0, (float) LETTER_WIDTH_PX, (float) LETTER_HEIGHT_PX);
                }

                // Limpiar archivo temporal
                tempImage.delete();
            }

            // Guardar documento PDF
            doc.save(outputPath);
        }
    }
}