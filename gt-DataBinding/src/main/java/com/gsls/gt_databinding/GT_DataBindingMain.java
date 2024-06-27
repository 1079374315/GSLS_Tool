package com.gsls.gt_databinding;

import com.google.auto.service.AutoService;
import com.gsls.gt_databinding.annotation.GT_DataBinding;
import com.gsls.gt_databinding.bean.BindingBean;
import com.gsls.gt_databinding.bean.XmlBean;
import com.gsls.gt_databinding.utils.DataBindingUtils;
import com.gsls.gt_databinding.utils.FileUtils;

import java.io.IOException;
import java.io.Writer;
import java.net.URL;
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
public class GT_DataBindingMain extends AbstractProcessor {

    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new HashSet<>();
        types.add(GT_DataBinding.class.getCanonicalName());
        return types;
    }

    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        DataBindingUtils.init();
        boolean isKT = false;//是否为 KT
        for (Element element : roundEnv.getElementsAnnotatedWith(GT_DataBinding.class)) {
            GT_DataBinding annotation = element.getAnnotation(GT_DataBinding.class);
            BindingBean bindingBean = new BindingBean();
            bindingBean.setPackClassPath(element.toString());
            bindingBean.setClassName(element.getSimpleName().toString());//获取类名
            bindingBean.setPackName(element.getEnclosingElement().toString());//设置包名
            bindingBean.setResourcePackName(DataBindingUtils.pageName(bindingBean.getPackName()));//设置包名

            if (annotation != null) {
                bindingBean.setLayoutName(annotation.setLayout() + ".xml");//设置布局文件名称
                bindingBean.setBingingType(annotation.setBindingType());//设置布局绑定的类型

                //获取jar包完整路径
                String path = "";
                URL resource = getClass().getResource("");
                if(resource != null){
                    path = resource.getPath();
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

                    String classPath = DataBindingUtils.androidBean.getProjectPath() + "\\" + libraryPath + "\\src\\main\\java\\" +
                            bindingBean.getPackClassPath().replaceAll("\\.", "\\\\") + ".java";

                    String classPath2 = DataBindingUtils.androidBean.getProjectPath() + "\\" + libraryPath + "\\src\\main\\java\\" +
                            bindingBean.getPackClassPath().replaceAll("\\.", "\\\\") + ".kt";

                    //Java
                    if (FileUtils.fileExist(classPath)) {
                        isKT = false;
                        bindingBean.setJavaLibraryName(libraryName);
                        bindingBean.setJavaLibraryPath(libraryPath);
                        bindingBean.setClassPath(classPath);
                        if(!FileUtils.fileExist(bindingBean.getClassPath())) continue;
                        String query = FileUtils.query(bindingBean.getClassPath());//ok
                        bindingBean.setClassCode(query);//设置源码
                        int R_Index = query.indexOf(".R;");
                        if (R_Index > -1) {
                            query = getPackName(query, false);
                        } else {
                            query = bindingBean.getPackName();
                        }
                        bindingBean.setResourcePackName(query);//设置包名
                        break;
                    }

                    //Kotlin
                    if (FileUtils.fileExist(classPath2)) {
                        isKT = true;
                        bindingBean.setJavaLibraryName(libraryName);
                        bindingBean.setJavaLibraryPath(libraryPath);
                        bindingBean.setClassPath(classPath2);
                        if(!FileUtils.fileExist(bindingBean.getClassPath())) continue;
                        String query = FileUtils.query(bindingBean.getClassPath());//ok
                        bindingBean.setClassCode(query);//设置源码
                        int R_Index = query.indexOf(".R");
                        if (R_Index > -1) {
                            query = getPackName(query, true);
                        } else {
                            query = bindingBean.getPackName();
                        }
                        bindingBean.setResourcePackName(query);//设置包名
                        break;
                    }
                }

                bindingBean.setLayoutPath(projectName + "\\" + bindingBean.getJavaLibraryPath() + "\\src\\main\\res\\layout\\");//存储布局路径
                bindingBean.setLayoutAbsolutePath(bindingBean.getLayoutPath() + bindingBean.getJavaLibraryPath());

                projectName = projectName.substring(projectName.lastIndexOf('\\') + 1);
                DataBindingUtils.androidBean.setProjectName(projectName);

                int lastIndexOf = path.lastIndexOf("\\", path.length() - 2) + 1;
                //查询出布局路径下所有的布局文件
                List<String> xmlFileName = FileUtils.getFilesAllName(bindingBean.getLayoutPath());
                if (xmlFileName == null || xmlFileName.isEmpty()) {
                    continue;
                }

                for (String layoutPath : xmlFileName) {
                    String[] split = layoutPath.split("\\\\");
                    if (split[split.length - 1].equals(bindingBean.getJavaLibraryPath())) {
                        bindingBean.setLayoutAbsolutePath(layoutPath);
                        DataBindingUtils.androidBean.addBindingBean(bindingBean);
                        break;
                    }
                }

                //获取注解布局的源码
                String queryData = FileUtils.query(bindingBean.getLayoutPath(), bindingBean.getLayoutName());
                List<XmlBean> xmlBeanList = DataBindingUtils.analysisXmlAll(queryData);//解析xml布局源码
                bindingBean.setXmlBeanList(xmlBeanList);
                DataBindingUtils.androidBean.addBindingBean(bindingBean);

                //生成包名
                StringBuilder builder = new StringBuilder();
                if(!isKT){
                    builder.append("package " + bindingBean.getPackName() + ";\n\n")
                            .append("import " + bindingBean.getPackName() + ".*;\n")
                            .append("import androidx.annotation.*;\n")
                            .append("import androidx.appcompat.*;\n")
                            .append("import androidx.core.*;\n")
                            .append("import androidx.fragment.*;\n")
                            .append("import androidx.lifecycle.*;\n")
                            .append("import androidx.recyclerview.*;\n")
                            .append("import android.content.*;\n")
                            .append("import android.database.*;\n")
                            .append("import android.accessibilityservice.*;\n")
                            .append("import android.animation.*;\n")
                            .append("import android.app.*;\n")
                            .append("import android.graphics.*;\n")
                            .append("import android.hardware.*;\n")
                            .append("import android.media.*;\n")
                            .append("import android.net.*;\n")
                            .append("import android.provider.*;\n")
                            .append("import android.renderscript.*;\n")
                            .append("import android.telephony.*;\n")
                            .append("import android.text.*;\n")
                            .append("import android.util.*;\n")
                            .append("import android.view.*;\n")
                            .append("import android.os.*;\n")
                            .append("import android.view.*;\n")
                            .append("import android.webkit.*;\n")
                            .append("import android.widget.*;\n")
                            .append("import com.gsls.gt.*;\n");

                }else{//是 kt
                    builder.append("package " + bindingBean.getPackName() + ";\n\n")
                            .append("import " + bindingBean.getPackName() + ".*;\n")
                            .append("import androidx.annotation.*;\n")
                            .append("import androidx.appcompat.*;\n")
                            .append("import androidx.core.*;\n")
                            .append("import androidx.fragment.*;\n")
                            .append("import androidx.lifecycle.*;\n")
                            .append("import androidx.recyclerview.*;\n")
                            .append("import android.content.*;\n")
                            .append("import android.database.*;\n")
                            .append("import android.accessibilityservice.*;\n")
                            .append("import android.animation.*;\n")
                            .append("import android.app.*;\n")
                            .append("import android.graphics.*;\n")
                            .append("import android.hardware.*;\n")
                            .append("import android.media.*;\n")
                            .append("import android.net.*;\n")
                            .append("import android.provider.*;\n")
                            .append("import android.renderscript.*;\n")
                            .append("import android.telephony.*;\n")
                            .append("import android.text.*;\n")
                            .append("import android.util.*;\n")
                            .append("import android.view.*;\n")
                            .append("import android.os.*;\n")
                            .append("import android.view.*;\n")
                            .append("import android.webkit.*;\n")
                            .append("import android.widget.*;\n")
                            .append("import com.gsls.gt.*;\n");
                }


                //导入特定的包
                switch (bindingBean.getBingingType()) {
                    case GT_DataBinding.ACTIVITY:
                        break;
                    case GT_DataBinding.FRAGMENT:
                        break;
                    case GT_DataBinding.DIALOG_FRAGMENT:
                        break;
                    case GT_DataBinding.POPUP_WINDOW:
                    case GT_DataBinding.BASE_VIEW:
                    case GT_DataBinding.VIEW:
                    case GT_DataBinding.NOTIFICATION:
                        builder.append("import android.content.Context;\n");
                        break;
                    case GT_DataBinding.FLOATING_WINDOW:
                        break;
                    case GT_DataBinding.ADAPTER:
                        builder.append("import android.content.Context;\n")
                                //导入****.R
                                .append("import " + bindingBean.getResourcePackName() + ".R;\n")
//                                .append("import com.gsls.gtk.R;\n")
                                .append("import java.util.List;\n")
                                .append("import androidx.recyclerview.widget.RecyclerView;\n");
                        break;
                }

                builder.append("\n");//导入的包与逻辑代码换行

                //添加文件注解
                builder.append("/**\n" +
                        " * This class is automatically generated and cannot be modified\n" +
                        " * GT-DataBinding class, inherited\n" +
                        " */");

                //根据不同的绑定类型 进行智能继承
                switch (bindingBean.getBingingType()) {
                    case GT_DataBinding.ACTIVITY:
                        builder.append("\npublic class " + bindingBean.getClassName() + "Binding<VM> extends GT.GT_Activity.DataBindingActivity {\n\n");
                        break;
                    case GT_DataBinding.FRAGMENT:
                        builder.append("\npublic class " + bindingBean.getClassName() + "Binding<VM> extends GT.GT_Fragment.DataBindingFragment {\n\n");
                        break;
                    case GT_DataBinding.DIALOG_FRAGMENT:
                        builder.append("\npublic class " + bindingBean.getClassName() + "Binding<VM> extends GT.GT_Dialog.DataBindingDialogFragment {\n\n");
                        break;
                    case GT_DataBinding.FLOATING_WINDOW:
                        builder.append("\npublic class " + bindingBean.getClassName() + "Binding<VM> extends GT.GT_FloatingWindow.DataBindingFloatingWindow {\n\n");
                        break;
                    case GT_DataBinding.POPUP_WINDOW:
                        builder.append("\npublic class " + bindingBean.getClassName() + "Binding<VM> extends GT.GT_PopupWindow.DataBindingPopupWindow {\n\n");
                        break;
                    case GT_DataBinding.ADAPTER:
                        builder.append("\npublic class " + bindingBean.getClassName() + "Binding<T> extends GT.Adapters.DataBindingAdapter<T, " + bindingBean.getClassName() + "Binding." + bindingBean.getClassName() + "ViewHolder> {\n");
                        break;
                    case GT_DataBinding.BASE_VIEW:
                    case GT_DataBinding.VIEW:
                        builder.append("\npublic class " + bindingBean.getClassName() + "Binding<VM> extends GT.GT_View.DataBindingView {\n\n");
                        break;

                    case GT_DataBinding.NOTIFICATION:
                        builder.append("\npublic class " + bindingBean.getClassName() + "Binding<VM> extends GT.GT_Notification.DataBindingNotification {\n\n");
                        break;

                }


                //生成变量名
                if (!GT_DataBinding.ADAPTER.equals(bindingBean.getBingingType())) {
                    for (int i = 0; i < xmlBeanList.size(); i++) {
                        XmlBean xmlBean = xmlBeanList.get(i);
                        String viewName = xmlBean.getViewName();
                        builder.append("\tpublic " + viewName + " " + xmlBean.getIdName() + ";\n");
                    }
                }

                if (!GT_DataBinding.ADAPTER.equals(bindingBean.getBingingType())) {
                    builder.append("\tprotected VM viewModel;\n");//添加 ViewModel
                    //生成GT DataBinding 类
                    builder.append("\n\tprivate " + bindingBean.getClassName() + "Binding " + DataBindingUtils.getLowercaseLetter(bindingBean.getClassName()) + "Binding;\n\n");
                }


                //根据不同的绑定类型 进行类组件的初始化
                switch (bindingBean.getBingingType()) {
                    case GT_DataBinding.ACTIVITY://通过
                        builder.append("\tprotected void initView(Bundle savedInstanceState) {\n" +
                                "\t\tsuper.initView(savedInstanceState);\n" +
                                "\t\t" + DataBindingUtils.getLowercaseLetter(bindingBean.getClassName()) + "Binding = GT.DataBindingUtil.setContentView(this);\n");
                        break;

                    case GT_DataBinding.FLOATING_WINDOW://通过
                        builder.append("\tprotected void initView(View view) {\n" +
                                "\t\tsuper.initView(view);\n" +
                                "\t\t" + DataBindingUtils.getLowercaseLetter(bindingBean.getClassName()) + "Binding = GT.DataBindingUtil.setContentView(this, view, context);\n");
                        break;

                    case GT_DataBinding.POPUP_WINDOW://通过
                        builder.append("\tpublic " + bindingBean.getClassName() + "Binding() {\n" +
                                "\t}\n\n");

                        builder.append("\tpublic " + bindingBean.getClassName() + "Binding(Context context) {\n" +
                                "\t\tsuper(context);\n" +
                                "\t}\n\n");

                        builder.append("\tpublic " + bindingBean.getClassName() + "Binding(Context context, Bundle bundle) {\n" +
                                "\t\tsuper(context, bundle);\n" +
                                "\t}\n\n");

                        builder.append("\tprotected void initView(View view, PopupWindow popWindow) {\n" +
                                "\t\t super.initView(view, popWindow);\n" +
                                "\t\t" + DataBindingUtils.getLowercaseLetter(bindingBean.getClassName()) + "Binding = GT.DataBindingUtil.setContentView(this, view, context);\n");
                        break;

                    case GT_DataBinding.ADAPTER://可以参考

                        builder.append("\n\tpublic " + bindingBean.getClassName() + "Binding() {}" +
                                "\n\n");

                        builder.append("\tpublic " + bindingBean.getClassName() + "Binding(Context context) {\n" +
                                "\t\tsuper(context);\n" +
                                "\t}\n\n");

                        builder.append("\tpublic " + bindingBean.getClassName() + "Binding(Context context, List<T> beanList) {\n" +
                                "\t\tsuper(context, beanList);\n" +
                                "\t}\n\n");

                        builder.append("\tpublic " + bindingBean.getClassName() + "Binding(Context context, RecyclerView rv, List<T> beanList, int layout_V_OR_H) {\n" +
                                "\t\tsuper(context, beanList);\n" +
                                "\t}\n\n");

                        builder.append("\tpublic " + bindingBean.getClassName() + "Binding(Context context, RecyclerView rv, List<T> beanList, int layout_V_OR_H, int layout) {\n" +
                                "\t\tsuper(context, rv, beanList, layout_V_OR_H, layout);\n" +
                                "\t}\n\n");

                        builder.append("\tprivate int viewType  = 0;\n");
                        builder.append("\tprivate final RecyclerView.RecycledViewPool recycledViewPool = new RecyclerView.RecycledViewPool();\n\n");

                        builder.append("\tprotected " + bindingBean.getClassName() + "ViewHolder onCreateViewHolder(View itemView) {\n" +
                                "\t\tRecyclerView.ViewHolder recycledView = recycledViewPool.getRecycledView(viewType);\n" +
                                "\t\tif(recycledView != null){\n" +
                                "\t\t\treturn (" + bindingBean.getClassName() + "ViewHolder) recycledView;\n" +
                                "\t\t}else{\n" +
                                "\t\t\t" + bindingBean.getClassName() + "ViewHolder adapterViewHolder = new " + bindingBean.getClassName() + "ViewHolder(itemView);\n" +
                                "\t\t\trecycledViewPool.putRecycledView(adapterViewHolder);\n" +
                                "\t\treturn adapterViewHolder;\n" +
                                "\t\t}\n" +
                                "\t}\n\n");

                        builder.append("\tpublic " + bindingBean.getClassName() + "ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {\n" +
                                "\t\tthis.viewType = viewType;\n" +
                                "\t\treturn super.onCreateViewHolder(parent, viewType);\n" +
                                "\t}\n\n");

                        break;

                    case GT_DataBinding.BASE_VIEW://通过
                    case GT_DataBinding.VIEW://通过

                        builder.append("\tpublic " + bindingBean.getClassName() + "Binding() {\n" +
                                "\t}\n\n");

                        builder.append("\tpublic " + bindingBean.getClassName() + "Binding(Context context) {\n" +
                                "\t\tsuper(context);\n" +
                                "\t}\n\n");

                        builder.append("\tpublic " + bindingBean.getClassName() + "Binding(Context context, ViewGroup viewGroup) {\n" +
                                "\t\tsuper(context, viewGroup);\n" +
                                "\t}\n\n");

                        builder.append("\tprotected void initView(View view) {\n" +
                                "\t\t super.initView(view);\n" +
                                "\t\t" + DataBindingUtils.getLowercaseLetter(bindingBean.getClassName()) + "Binding = GT.DataBindingUtil.setContentView(this, view, context);\n");
                        break;

                    case GT_DataBinding.FRAGMENT://通过
                        builder.append("\tprotected void initView(View view, Bundle savedInstanceState) {\n" +
                                "\t\tsuper.initView(view, savedInstanceState);\n" +
                                "\t\t" + DataBindingUtils.getLowercaseLetter(bindingBean.getClassName()) + "Binding = GT.DataBindingUtil.setContentView(this, view);\n");
                        break;
                    case GT_DataBinding.DIALOG_FRAGMENT://通过
                        builder.append("\tprotected void initView(View view, Bundle savedInstanceState) {\n" +
                                "\t\tsuper.initView(view, savedInstanceState);\n" +
                                "\t\t" + DataBindingUtils.getLowercaseLetter(bindingBean.getClassName()) + "Binding = GT.DataBindingUtil.setContentView(this, view, activity);\n");
                        break;

                    case GT_DataBinding.NOTIFICATION:
                        builder.append("\tprotected void initView(Context context) {\n" +
                                "\t\t super.initView(context);\n" +
                                "\t\t" + DataBindingUtils.getLowercaseLetter(bindingBean.getClassName()) + "Binding = GT.DataBindingUtil.setContentView(this, null, context);\n");
                        break;
                }


                //进行组件赋值
                if (GT_DataBinding.ADAPTER.equals(bindingBean.getBingingType())) {
                    builder.append("\tpublic static class " + bindingBean.getClassName() + "ViewHolder extends BaseHolder {\n\n");
                    for (int i = 0; i < xmlBeanList.size(); i++) {
                        XmlBean xmlBean = xmlBeanList.get(i);
                        String viewName = xmlBean.getViewName();
                        builder.append("\t\t@GT.Annotations.GT_View(R.id." + xmlBean.getIdName() + ")\n");
                        builder.append("\t\tpublic " + viewName + " " + xmlBean.getIdName() + ";\n");
                    }
                    builder.append("\n\t\tpublic " + bindingBean.getClassName() + "ViewHolder(View itemView) {\n");
                    builder.append("\t\t\tsuper(itemView);\n\t\t}\n\n\t}\n\n");

                    builder.append("\tpublic void close() {\n");
                    builder.append("\t\trecycledViewPool.clear();\n");
                    builder.append("\t\tsuper.close();\n");
                    builder.append("\t}\n");
                } else {
                    //判空
                    builder.append("\t\tif (" + DataBindingUtils.getLowercaseLetter(bindingBean.getClassName()) + "Binding != null){\n");

                    //遍历组件赋值
                    for (int i = 0; i < xmlBeanList.size(); i++) {
                        builder.append("\t\t\t" + xmlBeanList.get(i).getIdName() + " = " + DataBindingUtils.getLowercaseLetter(bindingBean.getClassName()) + "Binding." + xmlBeanList.get(i).getIdName() + ";\n");
                    }

                    if (!GT_DataBinding.ADAPTER.equals(bindingBean.getBingingType())) {
                        builder.append("\t\t}\n\t\tviewModel = GT.DataBindingUtil.dataBinding(this);\n\t}\n\n");
                    } else {
                        builder.append("\t\t}\n\t}\n\n");
                    }


                }

                //除去适配器 ，其余的都加上
                if (!GT_DataBinding.ADAPTER.equals(bindingBean.getBingingType())) {
                    //get、set
                    builder.append("\tpublic " + bindingBean.getClassName() + "Binding get" + bindingBean.getClassName() + "Binding() {\n" +
                            "\t\treturn " + DataBindingUtils.getLowercaseLetter(bindingBean.getClassName()) + "Binding;\n" +
                            "\t}\n" +
                            "\n" +
                            "\tpublic void set" + bindingBean.getClassName() + "Binding(" + bindingBean.getClassName() + "Binding " + DataBindingUtils.getLowercaseLetter(bindingBean.getClassName()) + "Binding) {\n" +
                            "\t\tthis." + DataBindingUtils.getLowercaseLetter(bindingBean.getClassName()) + "Binding = " + DataBindingUtils.getLowercaseLetter(bindingBean.getClassName()) + "Binding;\n" +
                            "\t}");

                    builder.append("\n\n\tpublic void bindingViewModel(VM viewModel) {\n" +
                            "\t\t this.viewModel = viewModel;\n" +
                            "\t}\n");

                    builder.append("\n\tpublic void onViewModeFeedback(Object... obj) {\n\n" +
                            "\t}\n");
                }

                //释放View资源
                switch (bindingBean.getBingingType()) {
                    case GT_DataBinding.FRAGMENT:
                    case GT_DataBinding.DIALOG_FRAGMENT:
                        builder.append("\n\tpublic void onDestroyView() {\n" +
                                "\t\tsuper.onDestroyView();\n");
                        for (int i = 0; i < xmlBeanList.size(); i++){
                            builder.append("\t\t\t" + xmlBeanList.get(i).getIdName() + " = null;\n");
                        }
                        builder.append("\t\t\t" + DataBindingUtils.getLowercaseLetter(bindingBean.getClassName()) + "Binding = null;\n");
                        builder.append("\t}\n");

                        builder.append("\n\tpublic void onDestroy() {\n" + "\t\tsuper.onDestroy();\n");
                        builder.append("\t\t\tviewModel = null;\n");
                        builder.append("\t}\n");

                        break;
                }

                //释放 Activity资源
                if(bindingBean.getBingingType().equals(GT_DataBinding.ACTIVITY) ){
                    builder.append("\n\tprotected void onDestroy() {\n" + "\t\tsuper.onDestroy();\n");
                    for (int i = 0; i < xmlBeanList.size(); i++){
                        builder.append("\t\t\t" + xmlBeanList.get(i).getIdName() + " = null;\n");
                    }
                    builder.append("\t\t\t" + DataBindingUtils.getLowercaseLetter(bindingBean.getClassName()) + "Binding = null;\n");
                    builder.append("\t\t\tviewModel = null;\n");
                    builder.append("\t}\n");
                }

                //释放 资源 View,FloatingWindow,PopupWindow
                switch (bindingBean.getBingingType()) {
                    case GT_DataBinding.VIEW:
                    case GT_DataBinding.BASE_VIEW:
                    case GT_DataBinding.FLOATING_WINDOW:
                    case GT_DataBinding.POPUP_WINDOW:
                        builder.append("\n\tpublic void finish() {\n" + "\t\tsuper.finish();\n");
                        for (int i = 0; i < xmlBeanList.size(); i++){
                            builder.append("\t\t\t" + xmlBeanList.get(i).getIdName() + " = null;\n");
                        }
                        builder.append("\t\t\t" + DataBindingUtils.getLowercaseLetter(bindingBean.getClassName()) + "Binding = null;\n");
                        builder.append("\t\t\tviewModel = null;\n");
                        builder.append("\t}\n");
                }

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

        }


        return true;
    }

    /**
     * 获取项目包路径算法
     * @param query
     * @param isKT
     * @return
     */
    private static String getPackName(String query, boolean isKT) {
        int importIndex1 = query.indexOf("import");
        if (importIndex1 > -1) {
            int packageIndex1 = query.lastIndexOf("package", importIndex1);
            String data = query.substring(packageIndex1, importIndex1).replaceAll("\\s*", "").replaceAll("package", "");
            String[] split = data.split("\\.");
            StringBuilder packName = new StringBuilder();
            for (String s : split) {
                packName.append(s).append(".");
                if (isKT) {
                    if (query.contains(packName + "R")) {
                        packName.delete(packName.length() - 1,packName.length());
                        break;
                    }
                } else {
                    if (query.contains(packName + "R;")) {
                        packName.delete(packName.length() - 1,packName.length());
                        break;
                    }
                }

            }
            return packName.toString();
        }
        return null;
    }

}
