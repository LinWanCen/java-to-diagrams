package io.github.linwancen.diagrams.java.parser;

import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.nodeTypes.NodeWithRange;
import com.github.javaparser.javadoc.Javadoc;
import com.github.javaparser.javadoc.JavadocBlockTag;
import com.github.javaparser.resolution.declarations.*;
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserAnnotationDeclaration;
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserMethodDeclaration;
import com.github.javaparser.symbolsolver.reflectionmodel.ReflectionMethodDeclaration;
import io.github.linwancen.diagrams.java.api.bean.JavaInfo;
import io.github.linwancen.diagrams.java.api.bean.MemberInfo;
import io.github.linwancen.diagrams.java.api.bean.TypeInfo;
import io.github.linwancen.diagrams.java.api.dict.AccessEnum;
import io.github.linwancen.diagrams.java.api.dict.MemberEnum;
import io.github.linwancen.diagrams.java.api.dict.TypeEnum;
import io.github.linwancen.util.java.CommentUtils;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * JavaInfo 信息补充工具类
 * <br>ResolvedXXX = XXXDeclaration.resolve()
 * <br>前者没有文档注释，后者来源于选中的文件
 */
class InfoUtils {

    private InfoUtils() {}

    /**
     * 解析类签名
     */
    static String resolvedTypeSign(ResolvedTypeDeclaration rt) {
        return rt.getQualifiedName();
    }

    /**
     * 补充类 sign name type access
     */
    static void addResolvedTypeInfo(TypeInfo info, ResolvedTypeDeclaration rt) {
        info.sign = resolvedTypeSign(rt);
        info.name = rt.getName();
        info.genLowFirstName();
        if (rt.isClass()) {
            info.type = TypeEnum.CLAZZ;
        } else if (rt.isEnum()) {
            info.type = TypeEnum.ENUM;
        } else if (rt.isInterface()) {
            info.type = TypeEnum.INTERFACE;
        } else if (rt instanceof JavaParserAnnotationDeclaration) {
            info.type = TypeEnum.ANNOTATION;
        } else {
            info.type = TypeEnum.UNKNOWN;
        }
        if (rt instanceof HasAccessSpecifier) {
            info.access = AccessEnumUtils.toEnum(((HasAccessSpecifier) rt).accessSpecifier());
        } else if (info.access == null) {
            info.access = AccessEnum.UNKNOWN;
        }
    }

    static String typeSign(TypeDeclaration<?> type) {
        Optional<String> o = type.getFullyQualifiedName();
        if (o.isPresent()) {
            return o.get();
        } else {
            throw new RuntimeException("type.getFullyQualifiedName() can null");
        }
    }

    /**
     * 补充类 javadoc abstract static, sign name type access
     */
    static void addTypeInfo(TypeInfo info, TypeDeclaration<?> type) {
        info.sign = typeSign(type);
        if (info.name == null) {
            info.name = type.getName().asString();
            info.genLowFirstName();
        }
        if (type.isClassOrInterfaceDeclaration()) {
            ClassOrInterfaceDeclaration cid = type.asClassOrInterfaceDeclaration();
            if (cid.isInterface()) {
                info.type = TypeEnum.INTERFACE;
            } else {
                info.type = TypeEnum.CLAZZ;
            }
            info.isAbstract = cid.isAbstract();
        } else if (type.isEnumDeclaration()) {
            info.type = TypeEnum.ENUM;
        } else if (type.isAnnotationDeclaration()) {
            info.type = TypeEnum.ANNOTATION;
        } else {
            info.type = TypeEnum.UNKNOWN;
        }
        info.isStatic = type.isStatic();
        info.access = AccessEnumUtils.toEnum(type.getAccessSpecifier());

        addLine(info, type);

        type.getJavadoc().ifPresent(v -> {
            info.comment = CommentUtils.textFrom(v.getDescription().toText());
            info.authorList = JavadocUtils.tagComments(v, JavadocBlockTag.Type.AUTHOR, null);
            info.commentLines = CommentUtils.splitToLines(info.comment);
            Consumer<JavaInfo> copyComment = copyComment(info);
            info.implInfo.values().forEach(copyComment);
            info.childInfo.values().forEach(copyComment);
        });
    }

    static void addLine(JavaInfo info, NodeWithRange<?> node) {
        node.getRange().ifPresent(p -> info.lineCount = p.getLineCount());
    }

    /**
     * 重写方法拷贝文档注释
     */
    private static Consumer<JavaInfo> copyComment(JavaInfo info) {
        return overInfo -> {
            if (overInfo.comment == null) {
                overInfo.comment = info.comment;
                overInfo.commentLines = info.commentLines;
            }
        };
    }

    /**
     * 补充字段信息 name, static access
     */
    static void addResolvedFieldInfo(MemberInfo info, ResolvedFieldDeclaration r) {
        info.name = r.getName();
        info.genLowFirstName();
        info.isStatic = r.isStatic();
        info.returnType = r.getType().describe();
        info.access = AccessEnumUtils.toEnum(r.accessSpecifier());
    }

    /**
     * 补充字段 javadoc final, static access
     */
    static void addFieldInfo(MemberInfo info, FieldDeclaration d) {
        info.isFinal = d.isFinal();
        info.isStatic = d.isStatic();
        info.access = AccessEnumUtils.toEnum(d.getAccessSpecifier());

        d.getJavadoc().ifPresent(v -> info.comment = CommentUtils.textFrom(v.getDescription().toText()));
        info.commentLines = CommentUtils.splitToLines(info.comment);
    }

    /**
     * 解析方法签名
     */
    static String methodSign(ResolvedMethodLikeDeclaration r) {
        // https://github.com/nidi3/graphviz-java/issues/172
        return r.getQualifiedSignature().replace(" extends java.lang.Object", "");
    }

    /**
     * 补充方法 sign, name static abstract access
     */
    static void addResolvedMethodInfo(MemberInfo info, ResolvedMethodLikeDeclaration r) {
        info.sign = methodSign(r);
        info.name = r.getName();
        info.genLowFirstName();
        if (r instanceof JavaParserMethodDeclaration) {
            JavaParserMethodDeclaration md = (JavaParserMethodDeclaration) r;
            info.isStatic = md.isStatic();
            info.isAbstract = md.isAbstract();
        } else if (r instanceof ReflectionMethodDeclaration) {
            ReflectionMethodDeclaration rmd = (ReflectionMethodDeclaration) r;
            info.isStatic = rmd.isStatic();
            info.isAbstract = rmd.isAbstract();
        }
        // 修正接口方法默认共有
        if (TypeEnum.INTERFACE.equals(info.typeInfo.type)) {
            info.access = AccessEnum.PUBLIC;
            // 避免打印多余的符号
            info.isAbstract = false;
        } else {
            info.access = AccessEnumUtils.toEnum(r.accessSpecifier());
        }
    }

    /**
     * 补充方法 javadoc final, name static access abstract
     */
    static void addMethodInfo(MemberInfo info, CallableDeclaration<?> d) {
        if (info.name == null) {
            info.name = d.getNameAsString();
            info.genLowFirstName();
        }
        info.isFinal = d.isFinal();
        info.isStatic = d.isStatic();
        // 修正接口方法默认共有
        if (TypeEnum.INTERFACE.equals(info.typeInfo.type)) {
            info.access = AccessEnum.PUBLIC;
        } else {
            info.access = AccessEnumUtils.toEnum(d.getAccessSpecifier());
            info.isAbstract = d.isAbstract();
        }

        addLine(info, d);

        Optional<Javadoc> javadoc = d.getJavadoc();
        javadoc.ifPresent(v -> {
            info.comment = CommentUtils.textFrom(v.getDescription().toText());
            info.commentLines = CommentUtils.splitToLines(info.comment);
            Consumer<JavaInfo> copyComment = copyComment(info);
            info.implInfo.values().forEach(copyComment);
            info.childInfo.values().forEach(copyComment);
        });

        for (Parameter param : d.getParameters()) {
            info.paramTypes.add(param.getType().toString());
            String name = param.getName().toString();
            info.paramNames.add(name);
            if (javadoc.isPresent()) {
                String paramComment = JavadocUtils.tagComment(javadoc.get(), JavadocBlockTag.Type.PARAM, name);
                if (!"".equals(paramComment)) {
                    info.haveParamComments = true;
                }
                info.paramComments.add(CommentUtils.textFrom(paramComment));
            } else {
                info.paramComments.add("");
            }
        }
        if (d.isMethodDeclaration()) {
            MethodDeclaration rmd = d.asMethodDeclaration();
            info.returnType = rmd.getType().asString();
            javadoc.ifPresent(v -> {
                info.returnComment = JavadocUtils.tagComment(v, JavadocBlockTag.Type.RETURN, null);
                info.returnComment = CommentUtils.textFrom(info.returnComment);
                info.commentLines = CommentUtils.splitToLines(info.comment);
                Consumer<MemberInfo> copyComment = overInfo -> {
                    if (overInfo.returnComment == null) {
                        overInfo.returnComment = info.returnComment;
                    }
                };
                info.implInfo.values().forEach(copyComment);
                info.childInfo.values().forEach(copyComment);
            });
        }
    }

    /**
     * 是否 GetSet 方法
     *
     * @param type nullable
     * @param rt   nullable
     */
    static void isGetSet(MemberInfo methodInfo, TypeDeclaration<?> type, ResolvedReferenceTypeDeclaration rt) {
        String name = null;
        if (methodInfo.name.length() > 3 && (methodInfo.name.startsWith("get") || methodInfo.name.startsWith("set"))) {
            name = methodInfo.name.substring(3);
        } else if (methodInfo.name.length() > 2 && methodInfo.name.startsWith("is")) {
            name = methodInfo.name.substring(2);
        }
        if (name != null) {
            String lowFirstName = name.substring(0, 1).toLowerCase() + name.substring(1);
            if (type != null) {
                type.getFieldByName(lowFirstName).ifPresent(e -> methodInfo.memberType = MemberEnum.GET_SET);
            } else if (rt != null) {
                ResolvedFieldDeclaration field;
                try {
                    field = rt.getField(lowFirstName);
                } catch (Exception e) {
                    // 框架找不到抛异常所以这里直接返回
                    return;
                }
                if (field != null) {
                    methodInfo.memberType = MemberEnum.GET_SET;
                }
            }
        }
    }
}
