package cz.abo.b2b.web.importer.xls.controller.dto;

public class PriceListDTO {
    private byte[] content;
    private String filename;
    private String contentType;

    public PriceListDTO(byte[] content, String filename, String contentType) {
        this.content = content;
        this.filename = filename;
        this.contentType = contentType;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
