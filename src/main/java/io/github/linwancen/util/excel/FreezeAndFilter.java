package io.github.linwancen.util.excel;

import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

public class FreezeAndFilter implements SheetWriteHandler {

    private final int colSplit;
    private final int rowSplit;
    private final int leftmostColumn;
    private final int topRow;
    private final String autoFilterRange;

    public FreezeAndFilter() {
        this(0, 1, 0, 1, "1:1");
    }

    public FreezeAndFilter(int colSplit, int rowSplit, int leftmostColumn, int topRow, String autoFilterRange) {
        this.colSplit = colSplit;
        this.rowSplit = rowSplit;
        this.leftmostColumn = leftmostColumn;
        this.topRow = topRow;
        this.autoFilterRange = autoFilterRange;
    }

    @Override
    public void beforeSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
        // not need
    }

    @Override
    public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
        Sheet sheet = writeSheetHolder.getSheet();
        sheet.createFreezePane(colSplit, rowSplit, leftmostColumn, topRow);
        sheet.setAutoFilter(CellRangeAddress.valueOf(autoFilterRange));
    }
}