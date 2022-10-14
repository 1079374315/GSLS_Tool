package com.gsls.gt_databinding;

import com.google.auto.service.AutoService;
import com.gsls.gt_databinding.annotation.GT_DataBinding;
import com.gsls.gt_databinding.bean.BindingBean;
import com.gsls.gt_databinding.bean.XmlBean;
import com.gsls.gt_databinding.utils.DataBindingUtils;
import com.gsls.gt_databinding.utils.FileUtils;

import java.io.IOException;
import java.io.Writer;
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
public class GT_DataBindingMain extends AbstractProcessor {

    private List<String> filtrationList;

    /**
     * 必须要的
     *
     * @return
     */
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new HashSet<>();
        types.add(GT_DataBinding.class.getCanonicalName());
        return types;
    }

    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        DataBindingUtils.log("GSLS_King");
        DataBindingUtils.log("roundEnv" + roundEnv);

        filtrationList = Arrays.asList(DataBindingUtils.filtrationArray);
        DataBindingUtils.log("filtrationList:" + filtrationList);
        DataBindingUtils.log("filtrationListSize:" + filtrationList.size());

        for (Element element : roundEnv.getElementsAnnotatedWith(GT_DataBinding.class)) {
            DataBindingUtils.log("element:" + element);
            DataBindingUtils.log("elementGet1:" + element.getEnclosedElements());
            DataBindingUtils.log("elementGet2:" + element.getSimpleName());
            DataBindingUtils.log("elementGet3:" + element.getKind());
            DataBindingUtils.log("elementGet4:" + element.getModifiers());
            DataBindingUtils.log("elementGet6:" + element.getEnclosingElement());

            GT_DataBinding annotation = element.getAnnotation(GT_DataBinding.class);

            BindingBean bindingBean = new BindingBean();
            bindingBean.setPackClassPath(element.toString());
            bindingBean.setClassName(element.getSimpleName().toString());//获取类名
            bindingBean.setPackName(element.getEnclosingElement().toString());//设置包名
            bindingBean.setResourcePackName(DataBindingUtils.pageName(bindingBean.getPackName()));//设置包名

            DataBindingUtils.log("annotation:" + annotation);
            if (annotation != null) {
                DataBindingUtils.log("setLayout:" + annotation.setLayout());
                bindingBean.setLayoutName(annotation.setLayout() + ".xml");//设置布局文件名称
                bindingBean.setBingingType(annotation.setBindingType());//设置布局绑定的类型

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
                        DataBindingUtils.log("Yes:" + classPath);
                        bindingBean.setJavaLibraryName(libraryName);
                        bindingBean.setClassPath(classPath);
                        String query = FileUtils.query(bindingBean.getClassPath());
                        bindingBean.setClassCode(query);//设置源码
                        DataBindingUtils.log("query1:" + query);
                        int R_Index = query.indexOf(".R;");
                        DataBindingUtils.log("R_Index1:" + R_Index);
                        if(R_Index != -1){
                            int lastIndexOf = query.lastIndexOf("import ",R_Index);
                            query = query.substring(lastIndexOf + "import ".length(),R_Index);
                        }else{
                            query = query.substring(0, query.indexOf(";") + 1);
                            query = query.replaceAll("package", "");
                            query = query.replaceAll(" ", "");
                            query = query.replaceAll(";", "");
                        }
                        bindingBean.setResourcePackName(query);//设置包名
                        DataBindingUtils.log("query:" + query);
                        break;
                    }

                    //Kotlin
                    if (FileUtils.fileExist(classPath2)) {
                        DataBindingUtils.log("Yes:" + classPath2);
                        bindingBean.setJavaLibraryName(libraryName);
                        bindingBean.setClassPath(classPath2);
                        String query = FileUtils.query(bindingBean.getClassPath());
                        bindingBean.setClassCode(query);//设置源码
                        DataBindingUtils.log("query1:" + query);
                        int R_Index = query.indexOf(".R;");
                        DataBindingUtils.log("R_Index2:" + R_Index);
                        if(R_Index != -1){
                            int lastIndexOf = query.lastIndexOf("import ",R_Index);
                            query = query.substring(lastIndexOf + "import ".length(),R_Index);
                        }else{
                            query = query.substring(0, query.indexOf(";") + 1);
                            query = query.replaceAll("package", "");
                            query = query.replaceAll(" ", "");
                            query = query.replaceAll(";", "");
                        }
                        bindingBean.setResourcePackName(query);//设置包名
                        DataBindingUtils.log("query:" + query);
                        break;
                    }


                }


                bindingBean.setLayoutPath(projectName + "\\" + bindingBean.getJavaLibraryName() + "\\src\\main\\res\\layout\\");//存储布局路径
                bindingBean.setLayoutAbsolutePath(bindingBean.getLayoutPath() + bindingBean.getLayoutName());

                projectName = projectName.substring(projectName.lastIndexOf('\\') + 1);
                DataBindingUtils.log("projectname2:" + projectName);
                DataBindingUtils.androidBean.setProjectName(projectName);

                int lastIndexOf = path.lastIndexOf("\\", path.length() - 2) + 1;
                String javaLibraryName = path.substring(lastIndexOf, path.length() - 1);
                DataBindingUtils.log("javaLibraryName:" + javaLibraryName);

                //查询出布局路径下所有的布局文件
                List<String> xmlFileName = FileUtils.getFilesAllName(bindingBean.getLayoutPath());
                if (xmlFileName == null || xmlFileName.size() == 0) {
                    continue;
                }
                DataBindingUtils.log("xmlFileNameSize:" + xmlFileName.size());

                for (String layoutPath : xmlFileName) {
                    String[] split = layoutPath.split("\\\\");
                    if (split[split.length - 1].equals(bindingBean.getLayoutName())) {
                        bindingBean.setLayoutAbsolutePath(layoutPath);
                        DataBindingUtils.androidBean.addBindingBean(bindingBean);
                        break;
                    }
                }

                //获取注解布局的源码
                String queryData = FileUtils.query(bindingBean.getLayoutPath(), bindingBean.getLayoutName());

                List<XmlBean> xmlBeanList = DataBindingUtils.analysisXmlAll(queryData);//解析xml布局源码
                /*DataBindingUtils.log("xml close:" + xmlBeanList.size());
                for(int i = 0; i < xmlBeanList.size(); i++){

                    DataBindingUtils.log("xmlBean:" +  xmlBeanList.get(i));
                }*/
                bindingBean.setXmlBeanList(xmlBeanList);

                DataBindingUtils.log("bindingBean:" + bindingBean);

                DataBindingUtils.androidBean.addBindingBean(bindingBean);

                //生成包名
                StringBuilder builder = new StringBuilder();
                builder.append("package " + bindingBean.getPackName() + ";\n\n")
                        .append("import android.os.*;\n")
                        .append("import android.view.*;\n")
                        .append("import android.webkit.*;\n")
                        .append("import com.*;\n")
                        .append("import androidx.*;\n")
                        .append("import com.gsls.gt.*;\n")
                        .append("import android.widget.*;\n");

                //导入特定的包
                switch (bindingBean.getBingingType()) {
                    case GT_DataBinding.Activity:
                        break;
                    case GT_DataBinding.Fragment:
                        break;
                    case GT_DataBinding.DialogFragment:
                        break;
                    case GT_DataBinding.PopupWindow:
                    case GT_DataBinding.View:
                        builder.append("import android.content.Context;\n");
                        break;
                    case GT_DataBinding.FloatingWindow:
                        break;
                    case GT_DataBinding.Adapter:
                        DataBindingUtils.log("bindingBean:" + bindingBean);
                        builder.append("import android.content.Context;\n")
                                //导入****.R
                                .append("import " + bindingBean.getResourcePackName() + ".R;\n")
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
                    case GT_DataBinding.Activity:
                        builder.append("\npublic class " + bindingBean.getClassName() + "Binding<VM> extends GT.GT_Activity.DataBindingActivity {\n\n");
                        break;
                    case GT_DataBinding.Fragment:
                        builder.append("\npublic class " + bindingBean.getClassName() + "Binding<VM> extends GT.GT_Fragment.DataBindingFragment {\n\n");
                        break;
                    case GT_DataBinding.DialogFragment:
                        builder.append("\npublic class " + bindingBean.getClassName() + "Binding<VM> extends GT.GT_Dialog.DataBindingDialogFragment {\n\n");
                        break;
                    case GT_DataBinding.FloatingWindow:
                        builder.append("\npublic class " + bindingBean.getClassName() + "Binding<VM> extends GT.GT_FloatingWindow.DataBindingFloatingWindow {\n\n");
                        break;
                    case GT_DataBinding.PopupWindow:
                        builder.append("\npublic class " + bindingBean.getClassName() + "Binding<VM> extends GT.GT_PopupWindow.DataBindingPopupWindow {\n\n");
                        break;
                    case GT_DataBinding.Adapter:
                        builder.append("\npublic class " + bindingBean.getClassName() + "Binding<T> extends GT.Adapters.DataBindingAdapter<T, " + bindingBean.getClassName() + "Binding." + bindingBean.getClassName() + "ViewHolder> {\n");
                        break;
                    case GT_DataBinding.View:
                        builder.append("\npublic class " + bindingBean.getClassName() + "Binding<VM> extends GT.GT_View.DataBindingView {\n\n");
                        break;

                }


                //生成变量名
                if (!GT_DataBinding.Adapter.equals(bindingBean.getBingingType())) {
                    for (int i = 0; i < xmlBeanList.size(); i++) {
                        XmlBean xmlBean = xmlBeanList.get(i);
                        String viewName = xmlBean.getViewName();
                        builder.append("\tpublic " + viewName + " " + xmlBean.getIdName() + ";\n");
                    }
                }

                if (!GT_DataBinding.Adapter.equals(bindingBean.getBingingType())){
                    builder.append("\tprotected VM viewModel;\n");//添加 ViewModel
                    //生成GT DataBinding 类
                    builder.append("\n\tprivate " + bindingBean.getClassName() + "Binding " + DataBindingUtils.getLowercaseLetter(bindingBean.getClassName()) + "Binding;\n\n");
                }



                //根据不同的绑定类型 进行类组件的初始化
                switch (bindingBean.getBingingType()) {
                    case GT_DataBinding.Activity://通过
                        builder.append("\tprotected void initView(Bundle savedInstanceState) {\n" +
                                "\t\tsuper.initView(savedInstanceState);\n" +
                                "\t\t" + DataBindingUtils.getLowercaseLetter(bindingBean.getClassName()) + "Binding = GT.DataBindingUtil.setContentView(this);\n");
                        break;

                    case GT_DataBinding.FloatingWindow://通过
                        builder.append("\tprotected void initView(View view) {\n" +
                                "\t\tsuper.initView(view);\n" +
                                "\t\t" + DataBindingUtils.getLowercaseLetter(bindingBean.getClassName()) + "Binding = GT.DataBindingUtil.setContentView(this, view, context);\n");
                        break;

                    case GT_DataBinding.PopupWindow://通过
                        builder.append("\tpublic " + bindingBean.getClassName() + "Binding() {\n" +
                                "\t}\n\n");

                        builder.append("\tpublic " + bindingBean.getClassName() + "Binding(Context context) {\n" +
                                "\t\tsuper(context);\n" +
                                "\t}\n\n");

                        builder.append("\tprotected void initView(View view, PopupWindow popWindow) {\n" +
                                "\t\t super.initView(view, popWindow);\n" +
                                "\t\t" + DataBindingUtils.getLowercaseLetter(bindingBean.getClassName()) + "Binding = GT.DataBindingUtil.setContentView(this, view, context);\n");
                        break;

                    case GT_DataBinding.Adapter://可以参考

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

                        builder.append("\tprotected " + bindingBean.getClassName() + "ViewHolder onCreateViewHolder(View itemView) {\n" +
                                "\t\treturn new " + bindingBean.getClassName() + "ViewHolder(itemView);\n" +
                                "\t}\n\n");

                        break;

                    case GT_DataBinding.View://通过

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

                    case GT_DataBinding.Fragment://通过
                        builder.append("\tprotected void initView(View view, Bundle savedInstanceState) {\n" +
                                "\t\tsuper.initView(view, savedInstanceState);\n" +
                                "\t\t" + DataBindingUtils.getLowercaseLetter(bindingBean.getClassName()) + "Binding = GT.DataBindingUtil.setContentView(this, view);\n");
                        break;
                    case GT_DataBinding.DialogFragment://通过
                        builder.append("\tprotected void initView(View view, Bundle savedInstanceState) {\n" +
                                "\t\tsuper.initView(view, savedInstanceState);\n" +
                                "\t\t" + DataBindingUtils.getLowercaseLetter(bindingBean.getClassName()) + "Binding = GT.DataBindingUtil.setContentView(this, view, activity);\n");
                        break;
                }


                //进行组件赋值
                if (GT_DataBinding.Adapter.equals(bindingBean.getBingingType())) {
                    builder.append("\tpublic static class " + bindingBean.getClassName() + "ViewHolder extends BaseHolder {\n\n");
                    for (int i = 0; i < xmlBeanList.size(); i++) {
                        XmlBean xmlBean = xmlBeanList.get(i);
                        String viewName = xmlBean.getViewName();
                        builder.append("\t\t@GT.Annotations.GT_View(R.id." + xmlBean.getIdName() + ")\n");
                        builder.append("\t\tpublic " + viewName + " " + xmlBean.getIdName() + ";\n");
                    }
                    builder.append("\n\t\tpublic " + bindingBean.getClassName() + "ViewHolder(View itemView) {\n");
                    builder.append("\t\t\tsuper(itemView);\n\t\t}\n\n\t}\n\n");
                } else {
                    //判空
                    builder.append("\t\tif (" + DataBindingUtils.getLowercaseLetter(bindingBean.getClassName()) + "Binding != null){\n");

                    //遍历组件赋值
                    for (int i = 0; i < xmlBeanList.size(); i++) {
                        builder.append("\t\t\t" + xmlBeanList.get(i).getIdName() + " = " + DataBindingUtils.getLowercaseLetter(bindingBean.getClassName()) + "Binding." + xmlBeanList.get(i).getIdName() + ";\n");
                    }

                    if (!GT_DataBinding.Adapter.equals(bindingBean.getBingingType())) {
                        builder.append("\t\t}\n\t\tviewModel = GT.DataBindingUtil.dataBinding(this);\n\t}\n\n");
                    } else {
                        builder.append("\t\t}\n\t}\n\n");
                    }


                }

                //除去适配器 ，其余的都加上
                if (!GT_DataBinding.Adapter.equals(bindingBean.getBingingType())) {
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


                builder.append("\n}\n"); // close class

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

            }
        }

        DataBindingUtils.log("AndroidBean:" + DataBindingUtils.androidBean);
        return true;
    }


}
