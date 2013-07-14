
package org.solmix.commons.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Solomon
 * 
 */
public class SumJavaCode
{

    static long fileCont = 0;

    static long normalLines = 0; // 空行

    static long commentLines = 0; // 注释行

    static long whiteLines = 0; // 代码行

    static List<String> packList = new ArrayList<String>();

    public static void main(String[] args) throws IOException {
        SumJavaCode sjc = new SumJavaCode();
        // File f = new File(System.getProperty("user.dir") + System.getProperty("file.separator") + "src");
        // File f = new File("M:\\workspace\\platform\\core\\trunk\\solmix-web\\solmix-app\\src");
        File f = new File("E:\\o\\solmix4");
        // File f = new File("E:\\o\\e200pro\\src\\com\\ieslab");
        System.out.println(f.getName());
        sjc.treeFile(f);
        System.out.println("文件数目");
        System.out.println("空行");
        System.out.println("注释行");
        System.out.println("代码行");
        System.out.println(fileCont);
        System.out.println(whiteLines);
        System.out.println(commentLines);
        System.out.println(normalLines);
        // System.out.println(System.getProperty("file.separator"));
        for (String pack : packList) {
            // System.out.println(pack);
        }
    }

    /**
     * 查找出一个目录下所有的.java文件
     * 
     * @param f 要查找的目录
     * @throws IOException
     */
    private void treeFile(File f) throws IOException {
        File[] childs = f.listFiles();
        // int count = 0;
        // int sum = 0;
        for (File child : childs) {
            if (child.getCanonicalPath().equals("M:\\workspace\\platform\\core\\trunk\\bundles")) {
                continue;
            }
            if (child.isDirectory()) {
                treeFile(child);
                if (child.listFiles().length > 0) {
                    try {
                        if (!packList.contains(child.getCanonicalPath().toString()))
                            packList.add(child.getCanonicalPath().toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                sumCode(child);

            }
        }
    }

    /**
     * 计算一个.java文件中的代码行，空行，注释行
     * 
     * @param file 要计算的.java文件
     */
    private void sumCode(File file) {
        BufferedReader br = null;
        long begin = normalLines;
        boolean comment = false;
        if (!(file.getName().endsWith(".jsp") || file.getName().endsWith(".java") || file.getName().endsWith(".xml"))) {
            return;
        }
        if (file.getName().endsWith(".ds.xml") || file.getName().endsWith(".data.xml"))
            return;
        fileCont++;
        try {
            br = new BufferedReader(new FileReader(file));
            String line = "";
            try {
                while ((line = br.readLine()) != null) {
                    line = line.trim();
                    if (line.matches("^[\\s&&[^\\n]]*$")) {
                        whiteLines++;
                    } else if (line.startsWith("/*") && !line.endsWith("*/")) {
                        commentLines++;
                        comment = true;
                    } else if (true == comment) {
                        commentLines++;
                        if (line.endsWith("*/")) {
                            comment = false;
                        }
                    } else if (line.startsWith("//")) {
                        commentLines++;
                    } else {
                        normalLines++;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                    br = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println(normalLines - begin + ":" + file.getName());
    }
}
