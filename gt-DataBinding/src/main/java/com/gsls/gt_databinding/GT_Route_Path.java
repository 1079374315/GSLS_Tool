package com.gsls.gt_databinding;

import com.google.auto.service.AutoService;
import com.gsls.gt_databinding.annotation.GT_DataBinding;
import com.gsls.gt_databinding.bean.BindingBean;
import com.gsls.gt_databinding.route.ClassType;
import com.gsls.gt_databinding.route.annotation.GT_Route;
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
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.JavaFileObject;


//主要用于生成路由路径
@AutoService(Processor.class)//编译时运行这个类
public class GT_Route_Path extends AbstractProcessor {

    private final List<BindingBean> bindingBeanList = new ArrayList<>();
    private final Map<String, String> bindingmap = new HashMap<>();

    /**
     * 必须要的
     *
     * @return
     */
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new HashSet<>();
        types.add(GT_DataBinding.class.getCanonicalName());
        types.add(GT_Route.class.getCanonicalName());
        return types;
    }

    private Elements elementUtils;
    private ProcessingEnvironment processingEnv;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        elementUtils = processingEnv.getElementUtils();
        this.processingEnv = processingEnv;
    }

    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        for (Element element : roundEnv.getElementsAnnotatedWith(GT_DataBinding.class)) {
            GT_DataBinding annotation = element.getAnnotation(GT_DataBinding.class);
            String type = annotation.setBindingType();
            String className = element.getSimpleName().toString();
            switch (type) {
                case "ClassType.ACTIVITY":
                    bindingmap.put(className, ClassType.ACTIVITY);
                    break;
                case "ClassType.VIEW":
                    bindingmap.put(className, ClassType.VIEW);
                    break;
                case "ClassType.DIALOG_FRAGMENT_X":
                    bindingmap.put(className, ClassType.DIALOG_FRAGMENT_X);
                    break;
                case "ClassType.FRAGMENT_X":
                    bindingmap.put(className, ClassType.FRAGMENT_X);
                    break;
                case "ClassType.VIEW_MODEL":
                    bindingmap.put(className, ClassType.VIEW_MODEL);
                    break;


                case "ClassType.BASE_VIEW":
                    bindingmap.put(className, ClassType.BASE_VIEW);
                    break;
                case "ClassType.INTERCEPTOR":
                    bindingmap.put(className, ClassType.INTERCEPTOR);
                    break;
                case "ClassType.PROVIDER":
                    bindingmap.put(className, ClassType.PROVIDER);
                    break;
                case "ClassType.FLOATING_WINDOW":
                    bindingmap.put(className, ClassType.FLOATING_WINDOW);
                    break;
                case "ClassType.POPUP_WINDOW":
                    bindingmap.put(className, ClassType.POPUP_WINDOW);
                    break;
                case "ClassType.NOTIFICATION":
                    bindingmap.put(className, ClassType.NOTIFICATION);
                    break;
                case "ClassType.WEB_VIEW":
                    bindingmap.put(className, ClassType.WEB_VIEW);
                    break;
                case "ClassType.ADAPTER":
                    bindingmap.put(className, ClassType.ADAPTER);
                    break;


                case "ClassType.SERVICE":
                    bindingmap.put(className, ClassType.SERVICE);
                    break;
                case "ClassType.CONTENT_PROVIDER":
                    bindingmap.put(className, ClassType.CONTENT_PROVIDER);
                    break;
            }


        }
        Set<? extends Element> elementsAnnotatedWith = roundEnv.getElementsAnnotatedWith(GT_Route.class);
        if (elementsAnnotatedWith.isEmpty()) return true;

        DataBindingUtils.init();

        int count = 0;
        bindingBeanList.clear();
        for (Element element : elementsAnnotatedWith) {
            count++;

            GT_Route annotation = element.getAnnotation(GT_Route.class);
            String path1 = annotation.value();
            String extras = annotation.extras();
            String[] interceptors = annotation.interceptors();

            BindingBean bindingBean = new BindingBean();
            bindingBean.setAnnotateValue(path1);
            bindingBean.setPackClassPath(element.toString());
            bindingBean.setClassName(element.getSimpleName().toString());//获取类名
            bindingBean.setPackName(element.getEnclosingElement().toString());//设置包名
            bindingBean.setResourcePackName(DataBindingUtils.pageName(bindingBean.getPackName()));//设置包名

            //判断对象类型
            if (elementUtils != null) {
                TypeMirror activityYM = elementUtils.getTypeElement(ClassType.ACTIVITY).asType();
                TypeMirror viewYM = elementUtils.getTypeElement(ClassType.VIEW).asType();
                TypeMirror fragmentYM = elementUtils.getTypeElement(ClassType.FRAGMENT).asType();
                TypeMirror fragmentYM_X = elementUtils.getTypeElement(ClassType.FRAGMENT_X).asType();
                TypeMirror dialogYM = elementUtils.getTypeElement(ClassType.DIALOG_FRAGMENT).asType();
                TypeMirror dialogYM_X = elementUtils.getTypeElement(ClassType.DIALOG_FRAGMENT_X).asType();
                TypeMirror viewModelYM_X = elementUtils.getTypeElement(ClassType.VIEW_MODEL).asType();

                TypeMirror providerYM = elementUtils.getTypeElement(ClassType.PROVIDER).asType();
                TypeMirror interceptorYM = elementUtils.getTypeElement(ClassType.INTERCEPTOR).asType();
                TypeMirror baseViewYM = elementUtils.getTypeElement(ClassType.BASE_VIEW).asType();
                TypeMirror floatingWindowYM = elementUtils.getTypeElement(ClassType.FLOATING_WINDOW).asType();
                TypeMirror popupWindowYM = elementUtils.getTypeElement(ClassType.POPUP_WINDOW).asType();
                TypeMirror notificationYM = elementUtils.getTypeElement(ClassType.NOTIFICATION).asType();
                TypeMirror webViewYM = elementUtils.getTypeElement(ClassType.WEB_VIEW).asType();
                TypeMirror adapterYM = elementUtils.getTypeElement(ClassType.ADAPTER).asType();

                TypeMirror serviceYM = elementUtils.getTypeElement(ClassType.SERVICE).asType();
//                TypeMirror content_providerYM = elementUtils.getTypeElement(ClassType.CONTENT_PROVIDER).asType();

                Types types = processingEnv.getTypeUtils();
                TypeMirror typeMirror = element.asType();

                if (bindingmap.containsKey(bindingBean.getClassName())) {
                    bindingBean.setClassType(bindingmap.get(bindingBean.getClassName()));
                } else {
                    if (types.isSubtype(typeMirror, activityYM)) {
                        bindingBean.setClassType(ClassType.ACTIVITY);
                    } else if (types.isSubtype(typeMirror, viewYM)) {
                        bindingBean.setClassType(ClassType.VIEW);
                    } else if (types.isSubtype(typeMirror, dialogYM)) {
                        bindingBean.setClassType(ClassType.DIALOG_FRAGMENT);
                    } else if (types.isSubtype(typeMirror, dialogYM_X)) {
                        bindingBean.setClassType(ClassType.DIALOG_FRAGMENT_X);
                    } else if (types.isSubtype(typeMirror, fragmentYM)) {
                        bindingBean.setClassType(ClassType.FRAGMENT);
                    } else if (types.isSubtype(typeMirror, fragmentYM_X)) {
                        bindingBean.setClassType(ClassType.FRAGMENT_X);
                    } else if (types.isSubtype(typeMirror, viewModelYM_X)) {
                        bindingBean.setClassType(ClassType.VIEW_MODEL);
                    } else if (types.isSubtype(typeMirror, interceptorYM)) {
                        bindingBean.setClassType(ClassType.INTERCEPTOR);
                    } else if (types.isSubtype(typeMirror, providerYM)) {
                        bindingBean.setClassType(ClassType.PROVIDER);
                    } else if (types.isSubtype(typeMirror, baseViewYM)) {
                        bindingBean.setClassType(ClassType.BASE_VIEW);
                    } else if (types.isSubtype(typeMirror, floatingWindowYM)) {
                        bindingBean.setClassType(ClassType.FLOATING_WINDOW);
                    } else if (types.isSubtype(typeMirror, popupWindowYM)) {
                        bindingBean.setClassType(ClassType.POPUP_WINDOW);
                    } else if (types.isSubtype(typeMirror, notificationYM)) {
                        bindingBean.setClassType(ClassType.NOTIFICATION);
                    } else if (types.isSubtype(typeMirror, webViewYM)) {
                        bindingBean.setClassType(ClassType.WEB_VIEW);
                    } else if (types.isSubtype(typeMirror, adapterYM)) {
                        bindingBean.setClassType(ClassType.ADAPTER);
                    } else if (types.isSubtype(typeMirror, serviceYM)) {
                        bindingBean.setClassType(ClassType.SERVICE);
                    } else {
                        bindingBean.setClassType(ClassType.UNKNOWN);
                    }
                }

            }

            //获取当前项目名称
            String projectName = System.getProperty("user.dir");
            DataBindingUtils.androidBean.setProjectPath(projectName);

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

                //frame:frame_mvvm 将这个转为  frame\frame_mvvm 的路径即可
                String classPath = DataBindingUtils.androidBean.getProjectPath() + "\\" + libraryPath + "\\src\\main\\java\\" + bindingBean.getPackClassPath().replaceAll("\\.", "\\\\") + ".java";
                String classPath2 = DataBindingUtils.androidBean.getProjectPath() + "\\" + libraryPath + "\\src\\main\\java\\" + bindingBean.getPackClassPath().replaceAll("\\.", "\\\\") + ".kt";

                //Java
                if (FileUtils.fileExist(classPath)) {
                    bindingBean.setJavaLibraryName(libraryName);
                    bindingBean.setClassPath(classPath);
                    String query = FileUtils.query(bindingBean.getClassPath());
                    bindingBean.setClassCode(query);//设置源码
                    break;
                }

                //Kotlin
                if (FileUtils.fileExist(classPath2)) {
                    bindingBean.setJavaLibraryName(libraryName);
                    bindingBean.setClassPath(classPath2);
                    String query = FileUtils.query(bindingBean.getClassPath());
                    bindingBean.setClassCode(query);//设置包名
                    break;
                }

            }

            bindingBean.setExtras(extras + " ↓");
            bindingBean.setInterceptors(interceptors);

            bindingBeanList.add(bindingBean);

            if (count < elementsAnnotatedWith.size()) continue;//是解析最后一个 路由才进行生成数据

            //生成包名
            StringBuilder builder = new StringBuilder();
            builder.append("package " + bindingBean.getResourcePackName() + ";\n\n");
            builder.append("import java.util.Map;\n");
            builder.append("import java.util.HashMap;\n");
            builder.append("import com.gsls.gt.GT.ARouter.IProvider;\n");
            builder.append("import com.gsls.gt.GT;\n");

            //引入注解类路径 packClassPath
            for (BindingBean bean : bindingBeanList) {
                builder.append("import " + bean.getPackClassPath() + ";\n");
            }

            builder.append("import com.gsls.gt_databinding.route.GT_RouteMeta;\n");
            builder.append("import com.gsls.gt_databinding.route.ClassType;\n");
            builder.append("\n");//导入的包与逻辑代码换行

            //添加文件注解
            builder.append("/**\n" +
                    " * This class is automatically generated and cannot be modified\n" +
                    " * GT-DataBinding class, inherited\n" +
                    " */");

            String className = "ARouter$$" + bindingBean.getJavaLibraryName();
            //根据不同的绑定类型 进行智能继承
            builder.append("\npublic class " + className + "{\n\n");
            builder.append("\tpublic Map<String, GT_RouteMeta> loadInto() {\n");

            builder.append("\t\tMap<String, GT_RouteMeta> atlas = new HashMap<>();\n");


            for (BindingBean bean : bindingBeanList) {
                String classType = "ClassType." + bean.getClassType().toString();
                switch (bean.getClassType()) {
                    case ClassType.ACTIVITY:
                        classType = "ClassType.ACTIVITY";
                        break;
                    case ClassType.VIEW:
                        classType = "ClassType.VIEW";
                        break;
                    case ClassType.DIALOG_FRAGMENT:
                        classType = "ClassType.DIALOG_FRAGMENT";
                        break;
                    case ClassType.DIALOG_FRAGMENT_X:
                        classType = "ClassType.DIALOG_FRAGMENT_X";
                        break;
                    case ClassType.FRAGMENT:
                        classType = "ClassType.FRAGMENT";
                        break;
                    case ClassType.FRAGMENT_X:
                        classType = "ClassType.FRAGMENT_X";
                        break;
                    case ClassType.VIEW_MODEL:
                        classType = "ClassType.VIEW_MODEL";
                        break;

                    case ClassType.BASE_VIEW:
                        classType = "ClassType.BASE_VIEW";
                        break;
                    case ClassType.INTERCEPTOR:
                        classType = "ClassType.INTERCEPTOR";
                        bean.setInterceptors(new String[]{bean.getAnnotateValue().toString()});
                        break;
                    case ClassType.PROVIDER:
                        classType = "ClassType.PROVIDER";
                        break;
                    case ClassType.FLOATING_WINDOW:
                        classType = "ClassType.FLOATING_WINDOW";
                        break;
                    case ClassType.POPUP_WINDOW:
                        classType = "ClassType.POPUP_WINDOW";
                        break;
                    case ClassType.NOTIFICATION:
                        classType = "ClassType.NOTIFICATION";
                        break;
                    case ClassType.WEB_VIEW:
                        classType = "ClassType.WEB_VIEW";
                        break;
                    case ClassType.ADAPTER:
                        classType = "ClassType.ADAPTER";
                        break;


                    case ClassType.SERVICE:
                        classType = "ClassType.SERVICE";
                        break;
                    case ClassType.CONTENT_PROVIDER:
                        classType = "ClassType.CONTENT_PROVIDER";
                        break;
                    case ClassType.UNKNOWN:
                        classType = "ClassType.UNKNOWN";
                        break;
                }

                builder.append("\t\t//" + bean.getExtras() + "\n");

                //解析拦截器
                StringBuilder interceptorStr = null;
                String[] interceptors1 = bean.getInterceptors();
                for (int interceptorsIndex = 0; interceptorsIndex < interceptors1.length; interceptorsIndex++) {
                    if (interceptorStr == null) {
                        interceptorStr = new StringBuilder("new String[]{");
                    }
                    interceptorStr.append("\"" + interceptors1[interceptorsIndex] + "\"");
                    if (interceptorsIndex != interceptors1.length - 1) {
                        interceptorStr.append(", ");
                    } else {
                        interceptorStr.append("}");
                    }

                }

                //核心
                builder.append("\t\tatlas.put(\"" +
                        bean.getAnnotateValue() + "\", GT_RouteMeta.build(" +
                        classType + ", " +
                        bean.getClassName() + ".class, \"" +
                        bean.getAnnotateValue() + "\", \"" +
                        bean.getJavaLibraryName() + "\",  \"" +
                        bean.getPackClassPath() + "\",  " +
                        interceptorStr + ")" +
                        ");\n");
            }

            builder.append("\t\treturn atlas;\n");
            builder.append("\t}");

            builder.append("\n\n}\n"); // close class

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
