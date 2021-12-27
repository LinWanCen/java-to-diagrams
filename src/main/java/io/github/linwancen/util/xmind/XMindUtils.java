package io.github.linwancen.util.xmind;

import org.xmind.core.*;
import org.xmind.core.style.IStyle;
import org.xmind.core.style.IStyleSheet;
import org.xmind.core.style.IStyled;
import org.xmind.core.util.Point;

public class XMindUtils {

    private XMindUtils() {}

    @SuppressWarnings("SpellCheckingInspection")
    public static final String XMIND = "xmind";

    @SuppressWarnings("SpellCheckingInspection")
    public static final String LOGIC_RIGHT = "org.xmind.ui.logic.right";
    /** 字体颜色 */
    public static final String FONT_COLOR = "fo:color";
    public static final String FILL_COLOR = "svg:fill";

    /**
     * 设置样式
     *
     * @param styleType in {@link IStyle}
     */
    public static void addStyle(IWorkbook workbook, String styleType,
                                IStyled styled, String key, String value) {
        IStyleSheet styleSheet = workbook.getStyleSheet();
        IStyle style = styleSheet.findStyle(styled.getStyleId());
        if (style == null) {
            style = styleSheet.createStyle(styleType);
            styleSheet.addStyle(style, IStyleSheet.NORMAL_STYLES);
        }
        style.setProperty(key, value);
        styled.setStyleId(style.getId());
    }

    /**
     * 添加链接
     */
    public static void addLink(ITopic usageTopic, ITopic hasTopic) {
        usageTopic.setHyperlink(XMIND + ":#" + hasTopic.getId());
    }

    /**
     * 添加连线
     */
    public static void addRel(IWorkbook workbook, ISheet primarySheet, ITopic usageTopic, ITopic hasTopic) {
        IRelationship r = workbook.createRelationship();
        r.setEnd1Id(usageTopic.getId());
        r.setEnd2Id(hasTopic.getId());
        r.getControlPoint(0).setPosition(new Point(1, 1));
        r.getControlPoint(1).setPosition(new Point(-1, 1));
        primarySheet.addRelationship(r);
    }

    /**
     * 设置笔记
     */
    public static void setNote(IWorkbook workbook, ITopic iTopic, String content) {
        IPlainNotesContent plainContent = (IPlainNotesContent) workbook.createNotesContent(INotes.PLAIN);
        plainContent.setTextContent(content);
        iTopic.getNotes().setContent(INotes.PLAIN, plainContent);
    }
}
