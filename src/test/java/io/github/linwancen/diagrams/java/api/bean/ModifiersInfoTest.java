package io.github.linwancen.diagrams.java.api.bean;

import io.github.linwancen.diagrams.java.api.dict.AccessEnum;
import io.github.linwancen.diagrams.java.api.dict.TypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * @see ModifiersInfo
 */
public class ModifiersInfoTest {

    private static final Logger LOG = LoggerFactory.getLogger(ModifiersInfoTest.class);

    @Test
    public void testMod() {
        ModifiersInfo mod = new ModifiersInfo();
        mod.access = AccessEnum.PUBLIC;
        mod.isAbstract = true;
        mod.isStatic = true;
        mod.isFinal = true;
        String modSymbol = mod.modSymbol();
        LOG.info(modSymbol);
        assertEquals(modSymbol, "asf+ ");
        String modStr = mod.modStr();
        LOG.info(modStr);
        assertEquals(modStr, "public abstract static final ");
    }
}