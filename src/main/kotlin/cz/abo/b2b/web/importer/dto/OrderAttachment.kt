package cz.abo.b2b.web.importer.dto

import org.apache.poi.ss.usermodel.Workbook

class OrderAttachment(var fileName: String, var workbook: Workbook)