package com.gsls.gt_databinding;

import com.google.auto.service.AutoService;
import com.gsls.gt_databinding.annotation.GT_R_Build;
import com.gsls.gt_databinding.bean.BindingBean;
import com.gsls.gt_databinding.utils.DataBindingUtils;
import com.gsls.gt_databinding.utils.FileUtils;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;


@AutoService(Processor.class)//编译时运行这个类
public class GT_R_Main extends AbstractProcessor {

    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new HashSet<>();
        types.add(GT_R_Build.class.getCanonicalName());
        return types;
    }

    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        DataBindingUtils.init();
        for (Element element : roundEnv.getElementsAnnotatedWith(GT_R_Build.class)) {
            GT_R_Build annotation = element.getAnnotation(GT_R_Build.class);
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
                String libraryPath = DataBindingUtils.androidBean.getJavaLibraryPaths().get(libraryNameIndex);
                String classPath = DataBindingUtils.androidBean.getProjectPath() + "\\" + libraryPath + "\\build\\intermediates\\runtime_symbol_list";

                //Java
                if (FileUtils.fileExist(classPath)) {
                    if(FileUtils.fileIsDirectory(classPath)){
                        List<String> R_List = FileUtils.getFilesAllName(classPath);
                        if(R_List != null && !R_List.isEmpty()){
                            String type = annotation.type();//R文件绑定类型
                            if(type == null || type.isEmpty()){//默认第一个
                                classPath =  R_List.get(0) + "\\processDebugResources\\R.txt";
                            }else{//自定了 R文件 build类型
                                for (String rPath : R_List) {
                                    if(rPath.contains(type)){
                                        classPath = rPath + "\\processDebugResources\\R.txt";
                                        break;
                                    }
                                }
                            }
                            bindingBean.setClassPath(classPath);
                            if(!FileUtils.fileExist(bindingBean.getClassPath())) continue;
                            String query = FileUtils.query(bindingBean.getClassPath());//ok
                            bindingBean.setClassCode(query);
                        }
                    }
                    break;
                }
            }

            //生成包名
            StringBuilder builder = new StringBuilder();
            builder.append("package " + bindingBean.getPackName() + ";\n\n");
            builder.append("import com.gsls.gt.GT;\n");
            builder.append("\n");//导入的包与逻辑代码换行

            //添加文件注解
            builder.append("/**\n" +
                    " * This class is automatically generated and cannot be modified\n" +
                    " * GT-DataBinding class, inherited\n" +
                    " */");

            String className = annotation.value();
            if(className == null || className.isEmpty()){
                className = GT_R_Build.R;
            }

            //根据不同的绑定类型 进行智能继承
            builder.append("\npublic class " + className + " {\n");
            /**
             单个方法形参名称的唯一路径为：方法名 + 形参类型 + 形参类型的顺序
             如：isExist-String-int[phone,a] isExist-int-String[a,phone]
             */
            //class 文件代码
            String classCode = bindingBean.getClassCode();
            String code = analysisJavaCode(classCode, className);
            builder.append(code);
            builder.append("\n}\n"); // close class

            //生成最终添加好的代码
            try {
                JavaFileObject source = processingEnv.getFiler().createSourceFile(bindingBean.getPackName() + "." + className);
                Writer writer = source.openWriter();
                writer.write(builder.toString());
                writer.flush();
                writer.close();
            } catch (IOException e) {
            }

        }


        return true;
    }

    private static String analysisJavaCode(String classCode,String name) {
        if(classCode == null) return "";
        Map<String, List<String>> resMap = new HashMap<String, List<String>>();
        //第一次解析
        List<String> recordList = new ArrayList<String>();
        String[] classCodes = classCode.split("\n");
        String oldType = "";
        List<String> resList = new ArrayList<String>();
        for (String code : classCodes) {// 循环全部
            String[] codeSp = code.split(" ");
            if (codeSp.length < 4)
                continue;
            String resType = codeSp[1];
            StringBuilder resCode = new StringBuilder();
            for (int i = 0; i < codeSp.length; i++) {
                switch (i) {
                    case 0: {
                        String string = codeSp[i];
                        if (string.equals("int"))
                            resCode.append("public static final " + codeSp[i] + " ");
                        else
                            resCode.append("public final " + codeSp[i] + " ");
                        break;
                    }
                    case 1:
                        continue;// 跳过资源类型
                    case 2: {
                        resCode.append(codeSp[i] + " = ");
                        break;
                    }
                    default: {
                        resCode.append(codeSp[i] + "");
                        break;
                    }
                }
            }

            String index = "";//暂时不加入 资源索引
            resCode.append("; " + index);
            if(recordList.isEmpty()) {
                oldType = resType;
                recordList.add(resType);
            }else if (!recordList.contains(resType)) {
                resMap.put(oldType, resList);
                resList = new ArrayList<String>();
                recordList.add(resType);
                oldType = resType;
            }
            resList.add(resCode.toString());
        }
        resMap.put(oldType, resList);

        //第二次解析
        StringBuilder resSB = new StringBuilder();
        for(String type :resMap.keySet()) {
            List<String> list = resMap.get(type);
            resSB.append("\r\n\tclass " + type + " {\n");
            for(String code : list) {
                resSB.append("\t\t" + code + "\r\n");
            }
            resSB.append("\t}");
        }
        return resSB.toString();
    }


}
