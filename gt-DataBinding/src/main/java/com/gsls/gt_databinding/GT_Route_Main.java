package com.gsls.gt_databinding;

import com.google.auto.service.AutoService;
import com.gsls.gt_databinding.annotation.GT_Route;
import com.gsls.gt_databinding.bean.BindingBean;
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
public class GT_Route_Main extends AbstractProcessor {

    private List<String> filtrationList;

    /**
     * 必须要的
     *
     * @return
     */
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new HashSet<>();
        types.add(GT_Route.class.getCanonicalName());
        return types;
    }

    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> elementsAnnotatedWith = roundEnv.getElementsAnnotatedWith(GT_Route.class);
        if(elementsAnnotatedWith.size() == 0) return true;

        DataBindingUtils.log("GSLS_King:" + elementsAnnotatedWith.size());
        DataBindingUtils.log("roundEnv1" + roundEnv);
        DataBindingUtils.log("annotations:" + DataBindingUtils.toStrings(annotations));
        DataBindingUtils.log("roundEnv2:" + DataBindingUtils.toStrings(roundEnv));

        filtrationList = Arrays.asList(DataBindingUtils.filtrationArray);

        int count = 0;


        /**

         ClassSymbol{
             members_field=Scope[onCreate(android.os.Bundle), JavaActivity()],
             fullname=com.example.myapplication.JavaActivity,
             flatname=com.example.myapplication.JavaActivity,
             sourcefile=SimpleFileObject[D:\Work\Android\MyApplication\app\src\main\java\com\example\myapplication\JavaActivity.java],
             classfile=null,
             trans_local=null,
             annotationTypeMetadata=Not an annotation type,
             recordComponents=,
             permitted=,
             isPermittedExplicit=false
         }

         **/

        for (Element element : elementsAnnotatedWith) {
            count++;
            DataBindingUtils.log("element:" + element);
            DataBindingUtils.log("elementGet1:" + element.getEnclosedElements());
            DataBindingUtils.log("elementGet2:" + element.getSimpleName());
            DataBindingUtils.log("elementGet3:" + element.getKind());
            DataBindingUtils.log("elementGet4:" + element.getModifiers());
            DataBindingUtils.log("elementGet6:" + element.getEnclosingElement());
            DataBindingUtils.log("elementGetAll:" + DataBindingUtils.toStrings(element));

            GT_Route annotation = element.getAnnotation(GT_Route.class);
            String path1 = annotation.path();
            DataBindingUtils.log("path1:" + path1);




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

            DataBindingUtils.log("bindingBean1:" + DataBindingUtils.toStrings(bindingBean));
            DataBindingUtils.log("close tag");

            if(count < elementsAnnotatedWith.size()) continue;//是解析最后一个 路由才进行生成数据

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

            String className = bindingBean.getClassName() + "$$" + bindingBean.getJavaLibraryName();

            //根据不同的绑定类型 进行智能继承
            builder.append("\npublic class " + className + " {\n");

            builder.append("\n}\n"); // close class

            //生成最终添加好的代码
            try {
                JavaFileObject source = processingEnv.getFiler().createSourceFile(bindingBean.getPackName() + "." + className);
                Writer writer = source.openWriter();
                writer.write(builder.toString());
                writer.flush();
                writer.close();
            } catch (IOException e) {
                DataBindingUtils.log("Automatic code generation failed:" + e);
            }
        }
        return true;
    }

}
