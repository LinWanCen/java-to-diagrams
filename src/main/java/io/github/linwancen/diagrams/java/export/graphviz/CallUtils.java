package io.github.linwancen.diagrams.java.export.graphviz;

import io.github.linwancen.diagrams.java.api.bean.MemberInfo;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * 重写关系（待优化）
 */
class CallUtils {

    private CallUtils() {}

    /**
     * 重写方法联系
     */
    static <T> void forOver(List<MemberInfo[]> overList,
                                   LinkedHashMap<String, LinkedHashMap<String, T>> typeMethodMap,
                                   BiConsumer<T, T> link) {
        for (MemberInfo[] infos : overList) {
            MemberInfo overInfo = infos[0];
            MemberInfo parentInfo = infos[1];
            LinkedHashMap<String, T> parentMethodMap = typeMethodMap.get(parentInfo.typeInfo.sign);
            // 如果实现的接口不在扫描的类中则跳过
            if (parentMethodMap == null) {
                continue;
            }
            LinkedHashMap<String, T> overMethodMap = typeMethodMap.get(overInfo.typeInfo.sign);
            T parent = parentMethodMap.get(parentInfo.sign);
            T over = overMethodMap.get(overInfo.sign);
            if (parent == null || over == null) {
                return;
            }
            link.accept(parent, over);
        }
    }

}
