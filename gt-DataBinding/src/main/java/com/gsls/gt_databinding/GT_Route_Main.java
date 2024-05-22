package com.gsls.gt_databinding;

import com.google.auto.service.AutoService;
import com.gsls.gt_databinding.bean.BindingBean;
import com.gsls.gt_databinding.route.annotation.GT_Route;
import com.gsls.gt_databinding.route.annotation.GT_RoutePath;
import com.gsls.gt_databinding.utils.DataBindingUtils;
import com.gsls.gt_databinding.utils.FileUtils;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.JavaFileObject;


//主要用于生成主路由路径
@AutoService(Processor.class)//编译时运行这个类
public class GT_Route_Main extends AbstractProcessor {

    private final List<BindingBean> bindingBeanList = new ArrayList<>();

    /**
     * 必须要的
     *
     * @return
     */
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new HashSet<>();
        types.add(GT_RoutePath.class.getCanonicalName());
        return types;
    }

    private ProcessingEnvironment processingEnv;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.processingEnv = processingEnv;
    }

    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        DataBindingUtils.init();

        //待完成
        Set<? extends Element> elementsAnnotatedWith = roundEnv.getElementsAnnotatedWith(GT_RoutePath.class);
        if (elementsAnnotatedWith.isEmpty()) return true;


        int count = 0;
        bindingBeanList.clear();
        for (Element element : elementsAnnotatedWith) {
            count++;
            GT_RoutePath annotation = element.getAnnotation(GT_RoutePath.class);
            String name = annotation.name();
            String module = annotation.module();

            BindingBean bindingBean = new BindingBean();
            bindingBean.setPackClassPath(element.toString());
            bindingBean.setClassName(element.getSimpleName().toString());//获取类名
            bindingBean.setPackName(element.getEnclosingElement().toString());//设置包名
            bindingBean.setResourcePackName(DataBindingUtils.pageName(bindingBean.getPackName()));//设置包名

            //获取当前项目名称
            String projectName = System.getProperty("user.dir");
            DataBindingUtils.androidBean.setProjectPath(projectName);

            //初始化 项目名称 和项目路径
            if (!DataBindingUtils.filtrationArray.isEmpty()) {
                for (String library : DataBindingUtils.filtrationArray) {
                    String libraryName = library;
                    String libraryPath = library;
                    if (library.contains(":")) {
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

                //frame:frame_mvvm 将这个转为  frame\frame_mvvm 的路径即可
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

            bindingBeanList.add(bindingBean);

            if (count < elementsAnnotatedWith.size()) continue;//是解析最后一个 路由才进行生成数据

            //生成包名
            StringBuilder builder = new StringBuilder();
            builder.append("package " + bindingBean.getResourcePackName() + ";\n");
            builder.append("\n");//导入的包与逻辑代码换行
            //添加文件注解
            builder.append("/**\n" +
                    " * This class is automatically generated and cannot be modified\n" +
                    " * GT-DataBinding class, inherited\n" +
                    " */");

            StringBuilder aRouterName = new StringBuilder();
            //获取到所有 Library 的项目包名,并解析成为路由反射路径
            for (int arIndex = 0; arIndex < DataBindingUtils.androidBean.getJavaLibraryPaths().size(); arIndex++) {
                String javaLibraryPath = DataBindingUtils.androidBean.getJavaLibraryPaths().get(arIndex);
                String javaLibraryName = DataBindingUtils.androidBean.getJavaLibraryNames().get(arIndex);
                String libraryPath = DataBindingUtils.androidBean.getProjectPath() + "\\" + javaLibraryPath + "\\build.gradle";
                if(!FileUtils.fileExist(libraryPath)) continue;
                String query = FileUtils.query(libraryPath);//ok
                int namespaceIndex = query.indexOf("namespace");
                int comIndex = query.indexOf("com", namespaceIndex);
                int dotIndex = query.indexOf("'", comIndex);
                if (namespaceIndex != -1 && dotIndex != -1 && namespaceIndex < dotIndex) {
                    String modelPackName = query.substring(comIndex, dotIndex);
                    String className = modelPackName + ".ARouter$$" + javaLibraryName;//com.example.myapplication.ARouter$$app
                    if (arIndex == DataBindingUtils.androidBean.getJavaLibraryPaths().size() - 1) {
                        aRouterName.append("\"" + className + "\"");
                    } else {
                        aRouterName.append("\"" + className + "\"" + ",");
                    }

                }
            }
            String className = "$_$" + bindingBean.getJavaLibraryName();
            //根据不同的绑定类型 进行智能继承
            builder.append("\n@com.gsls.gt_databinding.route.annotation.GT_ARouterName({" + aRouterName + "})");
            builder.append("\npublic class " + className + "{}");

            //生成最终添加好的代码
            try {
                JavaFileObject source = processingEnv.getFiler().createSourceFile(bindingBean.getResourcePackName() + "." + className);
                Writer writer = source.openWriter();
                writer.write(builder.toString());
                writer.flush();
                writer.close();
            } catch (IOException e) {
            }

        }
        return true;
    }

}
