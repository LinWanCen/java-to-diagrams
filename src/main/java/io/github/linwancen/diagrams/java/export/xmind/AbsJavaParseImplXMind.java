package io.github.linwancen.diagrams.java.export.xmind;

import io.github.linwancen.diagrams.Conf;
import io.github.linwancen.diagrams.java.api.JavaParse;
import io.github.linwancen.util.xmind.XMindUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmind.core.*;

import java.io.File;

/**
 * XMind 思维导图/脑图 实现
 */
public abstract class AbsJavaParseImplXMind implements JavaParse {

    private static final Logger LOG = LoggerFactory.getLogger(AbsJavaParseImplXMind.class);

    protected final File outDir;
    protected final String outName;
    protected final String tipName;

    protected AbsJavaParseImplXMind(File outDir, String outName, String tipName) {
        this.outDir = outDir;
        this.outName = outName;
        this.tipName = tipName;
    }

    protected final IWorkbookBuilder workbookBuilder = Core.getWorkbookBuilder();
    protected final IWorkbook workbook = workbookBuilder.createWorkbook();
    protected final ISheet primarySheet = workbook.getPrimarySheet();
    protected final ITopic rootTopic = primarySheet.getRootTopic();

    protected final boolean showSymbol = "true".equals(Conf.DIAGRAMS_SHOW_SYMBOL.get());

    protected static void alone(ITopic rootTopic, ITopic alone) {
        alone.setTitleText("独立节点\nalone");
        alone.setFolded(true);
        for (ITopic t : rootTopic.getAllChildren()) {
            if (t.getAllChildren().isEmpty()) {
                alone.add(t);
            }
        }
        rootTopic.add(alone);
    }

    /**
     * 处理并生成思维导图文件
     */
    @Override
    public void end() {
        beforeSave();
        try {
            // 后缀大小写不对会导致打开软件没打开文件
            String path = new File(outDir, outName + "." + XMindUtils.XMIND).getCanonicalPath();
            workbook.save(path);
            path = path.replace('\\', '/');
            LOG.info("思维导图/脑图：{}\tfile:///{}", tipName, path);
        } catch (Exception e) {
            LOG.error("save fail, ", e);
        }
    }

    /**
     * 保存前处理
     */
    protected abstract void beforeSave();
}
