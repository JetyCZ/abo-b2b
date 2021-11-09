package cz.abo.b2b.web.importer.xls.processor;

import cz.abo.b2b.web.importer.xls.dto.Item;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public abstract class AbstractSheetProcessor implements ISheetProcessor {

    @Override
    abstract public List<Item> disintegrateIntoItem(int rowNum, List<String> rowData);

    @Override
    public int getOrderColumnIdx() {
        return -1;
    }
}
