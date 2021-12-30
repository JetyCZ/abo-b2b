package cz.abo.b2b.web.importer.xls.controller;

import com.google.common.net.HttpHeaders;
import cz.abo.b2b.web.importer.xls.controller.dto.FileAttachment;
import cz.abo.b2b.web.importer.xls.service.ProcessorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;
import java.util.UUID;

/**
 * Class used to importItemsFromFile the .xls or .xlsx file
 *
 * @author Tomas Kodym
 */
@Controller
public class ImportController
{
    private static final String UPLOADING_DIR = System.getProperty("user.dir") + "/uploadingDir/";

    private static final Logger LOGGER = LoggerFactory.getLogger(ImportController.class);

    @Autowired
    private ProcessorService processorService;


    @GetMapping("/download-filled/{supplierId}")
    public ResponseEntity<Resource> downloadFilledFile(@PathVariable UUID supplierId) {

        try {
            FileAttachment fileAttachment = processorService.getFilledPriceListWithOrder(supplierId);
            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(fileAttachment.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileAttachment.getFilename() + "\"")
                .body(new ByteArrayResource(fileAttachment.getContent()));
        } catch (IOException e) {
            throw new IllegalStateException("Cannot write file", e);
        }

    }

}
