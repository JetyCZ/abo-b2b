package cz.abo.b2b.web.importer.dto

import org.xml.sax.InputSource
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.net.URL

class ImportSource(val path: String, val type: ImportSourceType) {
    fun newInputStream(): InputStream {
        if (ImportSourceType.URL.equals(type)) {
            return URL(path).openStream()
        } else if (ImportSourceType.FILE.equals(type)){
            return FileInputStream(File(path))
        } else {
            return ImportSource::class.java.getResourceAsStream(path)
        }

    }


    companion object {
        @JvmStatic
        fun fromFile(filePath: String): ImportSource {
            return ImportSource(filePath, ImportSourceType.FILE)
        }

        fun fromPath(path: String): ImportSource {
            if (path.startsWith("http")) {
                return ImportSource(path, ImportSourceType.URL);
            } else {
                return ImportSource(path, ImportSourceType.CLASSPATH_RESOURCE);
            }
        }
    }

}