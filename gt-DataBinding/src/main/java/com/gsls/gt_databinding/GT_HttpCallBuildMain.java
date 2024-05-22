package com.gsls.gt_databinding;

import com.google.auto.service.AutoService;
import com.gsls.gt_databinding.annotation.GT_HttpCallBuild;
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
public class GT_HttpCallBuildMain extends AbstractProcessor {
    /**
     * 必须要的
     *
     * @return
     */
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new HashSet<>();
        types.add(GT_HttpCallBuild.class.getCanonicalName());
        return types;
    }

    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        DataBindingUtils.init();

        for (Element element : roundEnv.getElementsAnnotatedWith(GT_HttpCallBuild.class)) {
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
            int indexOf = classCode.indexOf("@" + GT_HttpCallBuild.class.getSimpleName());
            classCode = classCode.substring(indexOf);
            String code = analysisHttpCallJavaCode(classCode, bindingBean.getPackClassPath());

            if(code.contains("@GT.HttpCall.Query(")){
                builder.append("\n\t//[@GT_HttpCallBuild] + [@GT.HttpCall.Query] These two annotations do not exist together");
                //解析的 ClassCode
                builder.append("\n\tpublic static final String CODE = " + null + ";\n");
            }else{
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

    private static String analysisHttpCallJavaCode(String javaCode, String classPackName) {
        List<String> list = new ArrayList<>();
        try{
            while (javaCode.contains("GT.HttpCall.Call")) {
                // 获取方法的信息
                int indexOf = javaCode.indexOf("GT.HttpCall.Call");
                int indexOf2 = javaCode.indexOf(");", indexOf) + 1;
                String substring = javaCode.substring(indexOf, indexOf2);
                substring = substring.substring(substring.indexOf(" ") + 1);
                list.add(substring);

                javaCode = javaCode.substring(indexOf2);
            }
        }catch (Exception e){

        }

        // 添加绝对接口
        String apiUrl = "";
        String returnValue = "";// 返回值
        for (String str : list) {
            // 获取方法名称
            String methodName = "";
            methodName = str.substring(0, str.indexOf("(")) + "-";

            // 解析方法形参
            String parameters = str.substring(str.indexOf("(") + 1, str.length() - 1);
            String[] split = parameters.split(", ");
            String parameterTypes = "";
            String parameterNames = "[";
            // 遍历赋值方法形参名称与类型
            for (int i = 0; i < split.length; i++) {
                String parameter = split[i];
                String[] split2 = parameter.split(" ");
                String parameterType = split2[0];
                String parameterName = split2[1];
                if (i == split.length - 1) {
                    parameterTypes += parameterType;
                    parameterNames += parameterName;
                } else {
                    parameterTypes += parameterType + "-";
                    parameterNames += parameterName + ",";
                }
            }
            parameterNames += "] \"+";
            returnValue += classPackName + "+";
            returnValue += methodName + parameterTypes + parameterNames + " \n \t\t\t\t\"";

        }

        if(returnValue.replaceAll("\\s*", "").isEmpty()){
            return returnValue;
        }
        returnValue = returnValue.substring(0, returnValue.length() - 1);

        returnValue = apiUrl + returnValue;

        //去除多余的格式
        returnValue = returnValue.substring(0, returnValue.length() - 9);

        return returnValue;
    }

}
