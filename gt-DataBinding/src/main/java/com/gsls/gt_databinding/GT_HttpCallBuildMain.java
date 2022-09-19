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

    private List<String> filtrationList;

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
        DataBindingUtils.log("GSLS_King");
        DataBindingUtils.log("roundEnv" + roundEnv);

        filtrationList = Arrays.asList(DataBindingUtils.filtrationArray);
        DataBindingUtils.log("filtrationList:" + filtrationList);
        DataBindingUtils.log("filtrationListSize:" + filtrationList.size());

        for (Element element : roundEnv.getElementsAnnotatedWith(GT_HttpCallBuild.class)) {
            DataBindingUtils.log("element:" + element);
            DataBindingUtils.log("elementGet1:" + element.getEnclosedElements());
            DataBindingUtils.log("elementGet2:" + element.getSimpleName());
            DataBindingUtils.log("elementGet3:" + element.getKind());
            DataBindingUtils.log("elementGet4:" + element.getModifiers());
            DataBindingUtils.log("elementGet6:" + element.getEnclosingElement());

            GT_HttpCallBuild annotation = element.getAnnotation(GT_HttpCallBuild.class);

            BindingBean bindingBean = new BindingBean();
            bindingBean.setPackClassPath(element.toString());
            bindingBean.setClassName(element.getSimpleName().toString());//获取类名
            bindingBean.setPackName(element.getEnclosingElement().toString());//设置包名
            bindingBean.setResourcePackName(DataBindingUtils.pageName(bindingBean.getPackName()));//设置包名


            //获取jar包完整路径
            String path = getClass().getResource("").getPath();
            DataBindingUtils.log("path1:" + path);

            //获取当前项目名称
            String projectName = System.getProperty("user.dir");
            DataBindingUtils.log("projectName:" + projectName);
            DataBindingUtils.androidBean.setProjectPath(projectName);

            //获取项目中所有模块
            List<String> filesAllName = FileUtils.getFilesAllName(DataBindingUtils.androidBean.getProjectPath());
            DataBindingUtils.log("filesAllName:" + filesAllName);


            assert filesAllName != null;
            for (String filePath : filesAllName) {
                String[] split = filePath.split("\\\\");
                String fileName = split[split.length - 1];
                if (FileUtils.fileIsDirectory(filePath) && !filtrationList.contains(fileName)) {
                    DataBindingUtils.log("FileDir:" + filePath);
                    split = filePath.split("\\\\");
                    DataBindingUtils.androidBean.addJavaLibraryName(split[split.length - 1]);
                }
            }
            DataBindingUtils.log("bindingBean1:" + bindingBean);


            for (String libraryName : DataBindingUtils.androidBean.getJavaLibraryNames()) {
                String classPath = DataBindingUtils.androidBean.getProjectPath() + "\\" + libraryName + "\\src\\main\\java\\" + bindingBean.getPackClassPath().replaceAll("\\.", "\\\\") + ".java";
                String classPath2 = DataBindingUtils.androidBean.getProjectPath() + "\\" + libraryName + "\\src\\main\\java\\" + bindingBean.getPackClassPath().replaceAll("\\.", "\\\\") + ".kt";

                //Java
                DataBindingUtils.log("classPath:" + classPath);
                //Kotlin
                DataBindingUtils.log("classPath2:" + classPath2);

                //Java
                if (FileUtils.fileExist(classPath)) {
                    DataBindingUtils.log("Yes1:" + classPath);
                    bindingBean.setJavaLibraryName(libraryName);
                    bindingBean.setClassPath(classPath);
                    String query = FileUtils.query(bindingBean.getClassPath());
                    DataBindingUtils.log("query1:" + query);
                    bindingBean.setClassCode(query);//设置源码
                    DataBindingUtils.log("query11:" + query);
                    break;
                }

                //Kotlin
                if (FileUtils.fileExist(classPath2)) {
                    DataBindingUtils.log("Yes2:" + classPath2);
                    bindingBean.setJavaLibraryName(libraryName);
                    bindingBean.setClassPath(classPath2);
                    String query = FileUtils.query(bindingBean.getClassPath());
                    DataBindingUtils.log("query2:" + query);
                    bindingBean.setClassCode(query);//设置包名
                    DataBindingUtils.log("query22:" + query);
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
            DataBindingUtils.log("classCode2:" + classCode);
            DataBindingUtils.log("Pack:" + bindingBean.getPackClassPath());

            String code = analysisJavaCode(classCode, bindingBean.getPackClassPath());
            DataBindingUtils.log("DATA:" + code);

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

            DataBindingUtils.log("bindingBean:" + builder.toString());

            //生成最终添加好的代码
            try {
                JavaFileObject source = processingEnv.getFiler().createSourceFile(bindingBean.getPackName() + "." + bindingBean.getClassName() + "Binding");
                Writer writer = source.openWriter();
                writer.write(builder.toString());
                writer.flush();
                writer.close();
            } catch (IOException e) {
                DataBindingUtils.log("Automatic code generation failed:" + e);
            }

//            DataBindingUtils.log("bindingBean:" + bindingBean.toString());

        }


        return true;
    }

    private static String analysisJavaCode(String javaCode, String classPackName) {
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

        DataBindingUtils.log("returnValue:" + returnValue);
        if(returnValue.replaceAll("\\s*","").length() == 0){
            DataBindingUtils.log("返回了:" + returnValue);
            return returnValue;
        }
        returnValue = returnValue.substring(0, returnValue.length() - 1);

        returnValue = apiUrl + returnValue;

        //去除多余的格式
        returnValue = returnValue.substring(0, returnValue.length() - 9);

        return returnValue;
    }

}
