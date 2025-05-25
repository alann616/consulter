package com.alann616.consulter.util;

import com.alann616.consulter.enums.DocumentType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
public class DocumentSummary {
        private LocalDateTime timestamp;
        private Long documentId;
        private DocumentType documentType;
        private String documentName;
        private String doctorName;
        // Constructor
        public DocumentSummary(LocalDateTime timestamp, Long documentId, DocumentType documentType, String documentName, String doctorName) {
            this.timestamp = timestamp;
            this.documentId = documentId;
            this.documentType = documentType;
            this.documentName = documentName;
            this.doctorName = doctorName;
        }
}
