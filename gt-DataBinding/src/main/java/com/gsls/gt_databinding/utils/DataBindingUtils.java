package com.gsls.gt_databinding.utils;

import com.gsls.gt_databinding.bean.AndroidBean;
import com.gsls.gt_databinding.bean.XmlBean;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

public class DataBindingUtils {
    private static TypeSpec logTypeSpec;
    private static JavaFile.Builder builder;
    public static final AndroidBean androidBean = new AndroidBean();
    private static final boolean isLog = false;//是否日志
    public static final List<String> filtrationArray = new ArrayList<>();//默认过滤文件名单

    private static final List<String> routeName = new ArrayList<>();

    //初始化 获取所有模块
    public static void init() {
        String projectName = System.getProperty("user.dir");
        List<String> filesAllName = FileUtils.getFilesAllName(projectName);
        if(filesAllName == null || filesAllName.isEmpty()) return;
        for (String path : filesAllName) {
            if (path.contains("settings.gradle")) {
                if(!FileUtils.fileExist(path)) continue;
                String code = FileUtils.query(path);//ok
                List<String> modelNames = analysisModelName(code);
                filtrationArray.addAll(modelNames);
                break;
            }
        }
    }


    /**
     * 解析模块名称
     * @param data
     * @return
     */
    private static List<String> analysisModelName(String data) {
        List<String> list = new ArrayList<>();
        if(!data.contains("include")) return list;
        int index1 = data.indexOf("include");
        data = data.substring(index1, data.length());
        while (data.contains("include")) {
            int indexOf = data.indexOf("include");
            if (indexOf == -1) break;
            int indexOf2 = data.indexOf(":", indexOf) + 1;
            int indexOf3 = data.indexOf("'", indexOf2);
            if(indexOf3 == -1) {
                indexOf3 = data.indexOf("\"", indexOf2);
            }
            if (indexOf3 == -1 || indexOf2 >= indexOf3) break;
            String modeName = data.substring(indexOf2, indexOf3);
            if (!list.contains(modeName) && !modeName.equals("gt") && !modeName.equals("gt-DataBinding")) {
                list.add(modeName);
            }
            data = data.substring(indexOf3);
        }
        return list;
    }

    public static void startLog(Elements mElementsUtil, TypeElement element) {
        logTypeSpec = TypeSpec.annotationBuilder("GT_Log").build();
        builder = JavaFile.builder(getPackageName(mElementsUtil, element), logTypeSpec);
    }

    /**
     * 打印日志
     *
     * @param msg
     */
    public static void logs(Object msg) {
        builder.addFileComment("TODO " + msg + "\n");
    }

    public static void log(Object msg) {
        if (!isLog) return;
        System.out.println(msg);
    }

    public static void closeLog(ProcessingEnvironment PE) {
        JavaFile logFile = builder.build();
        try {
            logFile.writeTo(PE.getFiler());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getPackageName(Elements mElementsUtil, TypeElement type) {
        return mElementsUtil.getPackageOf(type).getQualifiedName().toString();
    }

    /**
     * 解析xml
     *
     * @param xmlData
     */
    public static void analysisXml(String xmlData, List<XmlBean> xmlBeanList) {
        try {
            //去掉xml布局中 被注释的代码
            if (xmlData.contains("<!--")) {
                String start = xmlData.substring(0, xmlData.indexOf("<!--"));
                String end = xmlData.substring(xmlData.indexOf("-->") + 3);
                xmlData = start + end;
            }

            if (xmlData.contains("android:id=\"@+id/")) {
                XmlBean xmlBean = new XmlBean();
                int indexOf = xmlData.indexOf("android:id=\"@+id/") + "android:id=\"@+id/".length();
                int lastIndexOf = xmlData.lastIndexOf("<", indexOf) + 1;
                int indexOf2 = xmlData.indexOf(" ", lastIndexOf);
                // 寻找View名称
                String viewName = xmlData.substring(lastIndexOf, indexOf2).replaceAll("\\s*", "");
                if (viewName.equals("view")) {
                    viewName = "View";
                    int indexOf3 = xmlData.lastIndexOf("<view", indexOf);
                    int indexOf4 = xmlData.indexOf("class=\"", indexOf3) + ("class=\"".length());
                    int indexOf5 = xmlData.indexOf("\"", indexOf4);
                    String substring = xmlData.substring(indexOf4, indexOf5);
                    if (substring.contains("$")) {
                        viewName = substring.replaceAll("\\$", "\\.");
                    }
                    xmlBean.setViewPackage(viewName);
                    xmlBean.setViewName(viewName);
                } else {
                    if ("include".equals(viewName)) {
                        xmlBean.setViewPackage("android.widget.View");// 特殊情况使用该获取方式
                        xmlBean.setViewName("View");
                        //获取引入布局的布局名称
                        try {
                            int indexOfStart = xmlData.lastIndexOf("<" + viewName, indexOf);
                            int indexOfClose = xmlData.indexOf("/>", indexOfStart) + "/>".length();
                            String specialCode = xmlData.substring(indexOfStart, indexOfClose);
                            int lastIndexOf2 = specialCode.lastIndexOf("@layout/") + "@layout/".length();
                            String layout = specialCode.substring(lastIndexOf2, specialCode.indexOf("\"", lastIndexOf2));
                            xmlBean.setLayout(layout);
                        } catch (Exception e) {
                            log("e:" + e);
                        }
                    } else {
                        xmlBean.setViewPackage("android.widget." + viewName);// 不可靠的获取方式，但特殊情况才用到这个属性
                        xmlBean.setViewName(viewName);
                    }

                }

                // 寻找ViewID
                int indexOf3 = xmlData.indexOf("\"", indexOf);
                String idName = xmlData.substring(indexOf, indexOf3).replaceAll("\\s*", "");
                xmlBean.setIdName(idName);

                //如果本次解析里没有相同的 id 名称那就进行添加
                boolean isExist = false;
                for (XmlBean xmlBean1 : xmlBeanList)
                    if (xmlBean.getIdName().equals(xmlBean1.getIdName())) {
                        isExist = true;
                        break;
                    }
                if (!isExist) xmlBeanList.add(xmlBean);

                if (!xmlData.contains("android:id=\"@+id/")) return;
//                xmlData = xmlData.substring(indexOf + "android:id=\"@+id/".length());
//                log("xmlBean:" + xmlBean);
//                log("delete1:" + xmlData);

                int indexOf4 = xmlData.indexOf(">", indexOf) + 1;
                xmlData = xmlData.substring(indexOf4);
//                log("delete2:" + xmlData);
                analysisXml(xmlData, xmlBeanList);
            } else {
                return;
            }

            //判断是 id 先还是 layout 引入先
            /*if (idIndex < layoutIndex) {

                if (xmlData.contains("layout=\"@layout/")) {
                    log("xmlData222:" + xmlData);
                    try {
//                        int start = xmlData.indexOf("layout=\"@layout/") + "layout=\"@layout/".length();
//                        int end = xmlData.indexOf(">");
//                        String substring = xmlData.substring(start, end);
//                        log("layoutName:" + substring);
                    } catch (Exception e) {
                        log("error:" + e);
                    }


                }

            }*/


        } catch (Exception e) {
            log("e:" + e);
        }
    }

    /**
     * 解析xml 布局文件
     *
     * @param xmlData
     * @return
     */
    public static List<XmlBean> analysisXmlAll(String xmlData) {

        List<List<XmlBean>> lists = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            lists.add(new ArrayList<XmlBean>());
        }
        List<XmlBean> xmlBeanList0 = lists.get(0);
        analysisXml(xmlData, xmlBeanList0);
        List<XmlBean> xmlBeanListAll = new ArrayList<>(xmlBeanList0);
        lists.set(0, xmlBeanList0);

        for (int i = 1; i < lists.size(); i++) {
            List<XmlBean> xmlBeanList = lists.get(i);
            for (XmlBean xmlBean : lists.get(i - 1)) {
                String layout = xmlBean.getLayout();
                if (layout != null) {
                    //遍历寻找正确的模块布局路径
                    for (String libraryName : androidBean.getJavaLibraryNames()) {
                        String layoutPath = androidBean.getProjectPath() + "\\" + libraryName + "\\src\\main\\res\\layout\\" + layout + ".xml";
                        if (FileUtils.fileExist(layoutPath)) {
                            if(!FileUtils.fileExist(layoutPath)) continue;
                            xmlData = FileUtils.query(layoutPath);//ok
                            analysisXml(xmlData, xmlBeanList);
                            lists.set(i, xmlBeanList);
                            break;
                        }
                    }
                }
            }

            if (xmlBeanList.size() == 0) break;//如果遍历完了那就提前退出循环

            //筛选相同ID名称的View
            if (xmlBeanListAll.size() == 0) {
                xmlBeanListAll.addAll(xmlBeanList);
            } else {
                List<XmlBean> xmlBeans = new ArrayList<>();
                for (XmlBean xmlBean : xmlBeanList) {
                    for (XmlBean xmlBean1 : xmlBeanListAll) {
                        if (!xmlBean.getIdName().equals(xmlBean1.getIdName())) {
                            xmlBeans.add(xmlBean);
                            break;
                        }
                    }
                }
                xmlBeanListAll.addAll(xmlBeans);
            }
        }

        /*log("return xmlBeanListSize:" + xmlBeanListAll.size());
        for (XmlBean xmlBean : xmlBeanListAll) {
            log("xmlBean:" + xmlBean);
        }*/
        return xmlBeanListAll;
    }

    public static String getLowercaseLetter(String data) {
        if (data != null && data.length() > 0) {
            if (data.length() == 1) {
                data = data.toLowerCase();
            } else {
                String initial = data.substring(0, 1).toLowerCase();
                data = initial + data.substring(1);
            }
            return data;
        } else {
            return data;
        }
    }

    /**
     * 获取 Android 的包名
     *
     * @param data
     * @return
     */
    public static String pageName(String data) {
        if (data == null) return null;
        String packName = data;
        if (data.contains(".")) {
            String[] split = data.split("\\.");
            if (split.length <= 3) {
                packName = data;
            } else {
                packName = split[0] + "." + split[1] + "." + split[2];
            }
        }
        return packName;
    }

    public static String toStrings(Object obj) {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            Class<?> javaClass = obj.getClass();
            String className = javaClass.getSimpleName();
            stringBuilder.append(className + "{");
            for (Field field : javaClass.getDeclaredFields()) {
                field.setAccessible(true);
                Class<?> type = field.getType();
                String name = field.getName();
                Object value = field.get(obj);
                if (type == String.class) {
                    stringBuilder.append(name + "='" + value + "', ");
                } else if (type == Boolean.class) {
                    stringBuilder.append(name + "=" + value + ", ");
                } else if (type == Short.class) {
                    stringBuilder.append(name + "=" + value + ", ");
                } else if (type == Integer.class) {
                    stringBuilder.append(name + "=" + value + ", ");
                } else if (type == Long.class) {
                    stringBuilder.append(name + "=" + value + ", ");
                } else if (type == Float.class) {
                    stringBuilder.append(name + "=" + value + ", ");
                } else if (type == Double.class) {
                    stringBuilder.append(name + "=" + value + ", ");
                } else if (type == ArrayList.class) {
                    stringBuilder.append(name + "=" + value + ", ");
                } else if (type == Map.class) {
                    stringBuilder.append(name + "=" + value + ", ");
                } else {
                    stringBuilder.append(name + "=" + value + ", ");
                }
            }
            String toString = stringBuilder.toString();
            toString = toString.substring(0, toString.length() - 2) + "}";
            return toString;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }

}
