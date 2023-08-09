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

    private List<String> filtrationList;

    /**
     * 必须要的
     *
     * @return
     */
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new HashSet<>();
        types.add(GT_R_Build.class.getCanonicalName());
        return types;
    }

    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        DataBindingUtils.log("GSLS_King");
        DataBindingUtils.log("roundEnv" + roundEnv);

        filtrationList = Arrays.asList(DataBindingUtils.filtrationArray);
        DataBindingUtils.log("filtrationList:" + filtrationList);
        DataBindingUtils.log("filtrationListSize:" + filtrationList.size());

        for (Element element : roundEnv.getElementsAnnotatedWith(GT_R_Build.class)) {
            DataBindingUtils.log("element:" + element);
            DataBindingUtils.log("elementGet1:" + element.getEnclosedElements());
            DataBindingUtils.log("elementGet2:" + element.getSimpleName());
            DataBindingUtils.log("elementGet3:" + element.getKind());
            DataBindingUtils.log("elementGet4:" + element.getModifiers());
            DataBindingUtils.log("elementGet6:" + element.getEnclosingElement());

            GT_R_Build annotation = element.getAnnotation(GT_R_Build.class);

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
                String classPath = DataBindingUtils.androidBean.getProjectPath() + "\\" + libraryName + "\\build\\intermediates\\runtime_symbol_list";

                //Java
                DataBindingUtils.log("classPath:" + classPath);
                //Java
                if (FileUtils.fileExist(classPath)) {
                    DataBindingUtils.log("Yes1:" + classPath);
                    if(FileUtils.fileIsDirectory(classPath)){
                        List<String> R_List = FileUtils.getFilesAllName(classPath);
                        DataBindingUtils.log("R_List:" + R_List);
                        if(R_List != null && R_List.size() > 0){
                            String type = annotation.type();//R文件绑定类型
                            if(type == null || type.length() == 0){//默认第一个
                                classPath =  R_List.get(0) + "\\R.txt";
                            }else{//自定了 R文件 build类型
                                for (String rPath : R_List) {
                                    if(rPath.contains(type)){
                                        classPath = rPath + "\\R.txt";
                                        break;
                                    }
                                }
                            }
                            DataBindingUtils.log("classPath:" + classPath);
                            bindingBean.setClassPath(classPath);
                            String query = FileUtils.query(bindingBean.getClassPath());
                            DataBindingUtils.log("query:" + query);
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
            if(className == null || className.length() == 0){
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
            //开始解析代码
            DataBindingUtils.log("classCode:" + classCode);

         /*   classCode = "int id bounceStart 0x7f080061\r\n" +
                    "int id btn 0x7f080062\r\n" +
                    "int anim abc_fade_in 0x7f010000\r\n" +
                    "int anim abc_fade_out 0x7f010001\r\n" +
                    "int color m3_ref_palette_dynamic_primary0 0x7f0500aa\r\n" +
                    "int color m3_ref_palette_dynamic_primary10 0x7f0500ab\r\n" +
                    "int[] styleable ViewBackgroundHelper { 0x010100d4, 0x7f03004a, 0x7f03004b }\r\n" +
                    "int styleable ViewTransition_viewTransitionMode 14\r\n";*/

            String code = analysisJavaCode(classCode, className);
            DataBindingUtils.log("DATA:" + code);
            builder.append(code);

            builder.append("\n}\n"); // close class

            DataBindingUtils.log("bindingBean:" + builder.toString());

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

//            DataBindingUtils.log("bindingBean:" + bindingBean.toString());

        }


        return true;
    }

    private static String analysisJavaCode(String classCode,String name) {
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
            /*switch (resType){
                case "id":
                case "layout":
                    index = "@GT.Annotations.GT_Res.GT_Index(R." + resType + "." + codeSp[2] + ")";
                    break;
            }*/
            DataBindingUtils.log("index:" + index);

            resCode.append("; " + index);
            if(recordList.size() == 0) {
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
