package com.gsls.gt_databinding;

import com.google.auto.service.AutoService;
import com.gsls.gt_databinding.annotation.GT_DaoBuild;
import com.gsls.gt_databinding.bean.BindingBean;
import com.gsls.gt_databinding.utils.DataBindingUtils;
import com.gsls.gt_databinding.utils.FileUtils;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;


@AutoService(Processor.class)//编译时运行这个类
public class GT_DaoMain extends AbstractProcessor {


    /**
     * 必须要的
     *
     * @return
     */
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new HashSet<>();
        types.add(GT_DaoBuild.class.getCanonicalName());
        return types;
    }

    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        DataBindingUtils.init();

        for (Element element : roundEnv.getElementsAnnotatedWith(GT_DaoBuild.class)) {
            BindingBean bindingBean = new BindingBean();
            bindingBean.setPackClassPath(element.toString());
            bindingBean.setClassName(element.getSimpleName().toString());//获取类名
            bindingBean.setPackName(element.getEnclosingElement().toString());//设置包名
            bindingBean.setResourcePackName(DataBindingUtils.pageName(bindingBean.getPackName()));//设置包名
            //获取当前项目名称
            DataBindingUtils.androidBean.setProjectPath(System.getProperty("user.dir"));

            //初始化 项目名称 和项目路径
            if(!DataBindingUtils.filtrationArray.isEmpty()){
                for(String library : DataBindingUtils.filtrationArray){
                    String libraryName = library;
                    String libraryPath = library;
                    if(library.contains(":")){
                        libraryName = library.replaceAll(":", "\\$");
                        libraryPath = library.replaceAll(":", "\\\\");
                    }
                    DataBindingUtils.androidBean.addJavaLibraryName(libraryName);
                    DataBindingUtils.androidBean.addJavaLibraryPath(libraryPath);
                }
            }

            for (int libraryNameIndex = 0; libraryNameIndex < DataBindingUtils.androidBean.getJavaLibraryPaths().size(); libraryNameIndex++) {
                String libraryName = DataBindingUtils.androidBean.getJavaLibraryNames().get(libraryNameIndex);
                String libraryPath = DataBindingUtils.androidBean.getJavaLibraryPaths().get(libraryNameIndex);
                String classPath = DataBindingUtils.androidBean.getProjectPath() + "\\" + libraryPath + "\\src\\main\\java\\" + bindingBean.getPackClassPath().replaceAll("\\.", "\\\\") + ".java";
                String classPath2 = DataBindingUtils.androidBean.getProjectPath() + "\\" + libraryPath + "\\src\\main\\java\\" + bindingBean.getPackClassPath().replaceAll("\\.", "\\\\") + ".kt";

                //Java
                if (FileUtils.fileExist(classPath)) {
                    bindingBean.setJavaLibraryName(libraryName);
                    bindingBean.setClassPath(classPath);
                    if(!FileUtils.fileExist(bindingBean.getClassPath())) continue;
                    String query = FileUtils.query(bindingBean.getClassPath());//ok
                    bindingBean.setClassCode(query);//设置源码
                    break;
                }

                //Kotlin
                if (FileUtils.fileExist(classPath2)) {
                    bindingBean.setJavaLibraryName(libraryName);
                    bindingBean.setClassPath(classPath2);
                    if(!FileUtils.fileExist(bindingBean.getClassPath())) continue;
                    String query = FileUtils.query(bindingBean.getClassPath());//ok
                    bindingBean.setClassCode(query);//设置包名
                    break;
                }


            }

            //生成包名
            StringBuilder builder = new StringBuilder();
            builder.append("package " + bindingBean.getPackName() + ";\n\n");

            builder.append("\n");//导入的包与逻辑代码换行

            //添加文件注解
            builder.append("/**\n" +
                    " * This class is automatically generated and cannot be modified\n" +
                    " * GT-DataBinding class, inherited\n" +
                    " */");

            //根据不同的绑定类型 进行智能继承
            builder.append("\npublic class " + bindingBean.getClassName() + "Binding {\n");


            /**
             单个方法形参名称的唯一路径为：方法名 + 形参类型 + 形参类型的顺序
             如：isExist-String-int[phone,a] isExist-int-String[a,phone]
             */

            //class 文件代码
            String classCode = bindingBean.getClassCode();

            //开始解析代码
            int indexOf = classCode.indexOf("@" + GT_DaoBuild.class.getSimpleName());
            classCode = classCode.substring(indexOf);

            String code = analysisDaoCallJavaCode(classCode, bindingBean.getPackClassPath());

            if(code.contains("@GT.HttpCall.Query(")){
                builder.append("\n\t//[@GT_HttpCallBuild] + [@GT.HttpCall.Query] These two annotations do not exist together");
                //解析的 ClassCode
                builder.append("\n\tpublic static final String CODE = " + null + ";\n");
            }else{
                builder.append("\n\t//Parsing parameters for all methods of the interface");
                //解析的 ClassCode
                builder.append("\n\tpublic static final String CODE = \n\t\t\t\t" + "\"" + code + "\"" + ";\n");
            }

            //toString
            builder.append("\n\t@Override\n" +
                    "\tpublic String toString() {\n" +
                    "\t\treturn CODE;\n" +
                    "\t}");

            builder.append("\n}\n"); // close class

            //生成最终添加好的代码
            try {
                JavaFileObject source = processingEnv.getFiler().createSourceFile(bindingBean.getPackName() + "." + bindingBean.getClassName() + "Binding");
                Writer writer = source.openWriter();
                writer.write(builder.toString());
                writer.flush();
                writer.close();
            } catch (IOException e) {
            }
        }


        return true;
    }


    private static String analysisDaoCallJavaCode(String javaCode, String classPackName) {
        List<String> list = new ArrayList<>();
        try {
            while (javaCode.contains("GT.Hibernate")) {
                // 获取方法的信息
                int indexOf = javaCode.indexOf("GT.Hibernate");
                int indexOf2 = javaCode.indexOf(");", indexOf) + 1;
                String substring = javaCode.substring(indexOf, indexOf2);
                substring = substring.split("\n")[1];
                int lastIndexOf = substring.lastIndexOf("(");
                int lastIndexOf2 = substring.lastIndexOf(" ", lastIndexOf);
                substring = substring.substring(lastIndexOf2 + 1, substring.indexOf(")") + 1);
                list.add(substring);
                javaCode = javaCode.substring(indexOf2);
            }
        } catch (Exception e) {

        }

        String returnValue2 = "";

        for (int i = 0; i < list.size(); i++) {
            String code = list.get(i);

            String returnValue = "";

            if(i != 0){
                returnValue = "\t\t\t\t\"" + classPackName + "+" + code.substring(0, code.indexOf("(")) + "-";
            }else{
                returnValue = "" + classPackName + "+" + code.substring(0, code.indexOf("(")) + "-";
            }

            // 解析方法形参
            String parameters = code.substring(code.indexOf("(") + 1, code.length() - 1);
            String value = "";
            if (parameters.contains(",")) {
                parameters = parameters.replaceAll(", ", ",");
                String[] split = parameters.split(",");
                for (String str : split) {
                    String[] split2 = str.split(" ");
                    String type = split2[0];
                    value += split2[1] + ",";
                    returnValue += type + "-";
                }
                returnValue = returnValue.substring(0, returnValue.length() - 1) + "[";
                value = value.substring(0, value.length() - 1) + "] \"+";
            } else {
                String[] split = parameters.split(" ");
                String type = split[0];
                if (type.length() == 0) {
                    type = "null";
                } else {
                    value = split[1] + "] \"+";
                }
                if (type.equals("null")) {
                    returnValue += type + "[] \"+";
                } else {
                    returnValue += type + "[";
                }
            }
            returnValue2 += returnValue + value + " \n";
        }

        int lastIndexOf = returnValue2.lastIndexOf("\"+");

        returnValue2 = returnValue2.substring(0,lastIndexOf);

        return returnValue2;
    }


}
