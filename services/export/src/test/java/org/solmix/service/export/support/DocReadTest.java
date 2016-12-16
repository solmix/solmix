package org.solmix.service.export.support;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Bookmark;
import org.apache.poi.hwpf.usermodel.Bookmarks;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.hwpf.usermodel.Section;
import org.apache.poi.hwpf.usermodel.Table;
import org.apache.poi.hwpf.usermodel.TableCell;
import org.apache.poi.hwpf.usermodel.TableIterator;
import org.apache.poi.hwpf.usermodel.TableRow;
import org.junit.Assert;
import org.junit.Test;
import org.solmix.runtime.Container;
import org.solmix.runtime.ContainerFactory;
import org.solmix.runtime.resource.InputStreamResource;
import org.solmix.runtime.resource.ResourceManager;

public class DocReadTest {

	public StringBuffer sb = new StringBuffer();
	int tables=0;
	@Test
	public void test() throws IOException {
		Container c  =  ContainerFactory.getDefaultContainer();
		ResourceManager is  = c.getExtension(ResourceManager.class);
		InputStreamResource isr =is.getResourceAsStream("/org/solmix/homo/server/1.doc");
		Assert.assertNotNull(isr);
		
		HWPFDocument doc = null;
        try {
//        WordExtractor doc = new WordExtractor(isr.getInputStream()); 
//        sb.append(doc.getText());
        	doc= new HWPFDocument(isr.getInputStream());
//        	 this.printInfo(doc.getBookmarks());  
             //输出文本  
//             sb.append(doc.getDocumentText());  
             Range range = doc.getRange();  
//           this.insertInfo(range);  
//             this.printInfo(range);  
             //读表格  
             this.readTable(range);  
             //读列表  
//             this.readList(range);  
             //删除range  
//             Range r = new Range(2, 5, doc);  
//             r.delete();//在内存中进行删除，如果需要保存到文件中需要再把它写回文件  
             //把当前HWPFDocument写到输出流中  
        } catch (IOException e1) {
        e1.printStackTrace();
        }
        File file = new File("11.csv");
        if(!file.exists()){
        	file.createNewFile();
        
        }else{
        	file.delete();
        	file.createNewFile();
        }
    	FileInputStream fi =new FileInputStream(file);
    	FileWriter fw  = new FileWriter(file);
    	fw.write(sb.toString());
    	fw.flush();
    	System.out.println(tables);
	}
	
	private void closeStream(InputStream is) {  
	      if (is != null) {  
	         try {  
	            is.close();  
	         } catch (IOException e) {  
	            e.printStackTrace();  
	         }  
	      }  
	   }  
	    
	   /** 
	    * 输出书签信息 
	    * @param bookmarks 
	    */  
	   private void printInfo(Bookmarks bookmarks) {  
	      int count = bookmarks.getBookmarksCount();  
	      sb.append("书签数量：" + count);  
	      Bookmark bookmark;  
	      for (int i=0; i<count; i++) {  
	         bookmark = bookmarks.getBookmark(i);  
	         sb.append("书签" + (i+1) + "的名称是：" + bookmark.getName());  
	         sb.append("开始位置：" + bookmark.getStart());  
	         sb.append("结束位置：" + bookmark.getEnd());  
	      }  
	   }  
	    
	   /** 
	    * 读表格 
	    * 每一个回车符代表一个段落，所以对于表格而言，每一个单元格至少包含一个段落，每行结束都是一个段落。 
	    * @param range 
	    */  
	   private void readTable(Range range) {  
	      //遍历range范围内的table。  
	      TableIterator tableIter = new TableIterator(range);  
	      Table table;  
	      TableRow row;  
	      TableCell cell;  
	      int kk=0;
	      int jjj=0;
	      int fff=0;
	      boolean ggg=false;
	      String tttt ="";
	      while (tableIter.hasNext()) {  
	         table = tableIter.next();  
	         int rowNum = table.numRows();  
	         for (int j=0; j<rowNum; j++) {  
	            row = table.getRow(j);  
	            int cellNum = row.numCells();  
	            kk=-1;
	           boolean b=false;
	            for (int k=0; k<cellNum; k++) {  
	                cell = row.getCell(k);  
	                //输出单元格的文本  
	                String text =cell.text().trim();
	                if("代码".equals(text)){
//	                	sb.append(text+"\n");
	                	kk=k+1;
	                	jjj=1;
	                }
	               
	                if(ggg&&k<fff){
	                	
	                	fff=6;
	                	if(!b){
	                		b=true;
	                		sb.append(tttt);
		 	        		sb.append(",");
	                	}
	                	 
	                	if(k==0)
	                		sb.append("\n");
	                	if(k!=4){
	                			text=text.replace(",", "#");
	                		sb.append(text);
	                		
	                		sb.append(",");
	                	}
	                }
	                if(k==kk){
	                	if(!includeHZ(text)){
//	                		sb.append(text+"\n");
		                	tttt=text;
		                	tables++;
	                	}
	                		
	                	if("数据类型".equals(text)){
	                		ggg=true;
	                		fff=k;
	                	}
	                }
	               
	            }  
	         }  
	         ggg=false;
	      }  
	   }  
	    
	   /** 
	    * 读列表 
	    * @param range 
	    */  
	   private void readList(Range range) {  
	      int num = range.numParagraphs();  
	      Paragraph para;  
	      for (int i=0; i<num; i++) {  
	         para = range.getParagraph(i);  
	         if (para.isInList()) {  
	            sb.append("list: " + para.text());  
	         }  
	      }  
	   }  
	    public boolean includeHZ(String code){
	    	String regEx = "[\u4e00-\u9fa5]";
	        Pattern p = Pattern.compile(regEx);
	        int num = 0;//汉字长度
	        for(int i=0;i<code.length();i++){
	            if(p.matches(regEx, code.substring(i, i + 1))){
	               return true;
	            }
	        }
	        return false;
	    }
	   /** 
	    * 输出Range 
	    * @param range 
	    */  
	   private void printInfo(Range range) {  
	      //获取段落数  
	      int paraNum = range.numParagraphs();  
	      sb.append(paraNum);  
	      for (int i=0; i<paraNum; i++) {  
//	       this.insertInfo(range.getParagraph(i));  
	         sb.append("段落" + (i+1) + "：" + range.getParagraph(i).text());  
	         if (i == (paraNum-1)) {  
	            this.insertInfo(range.getParagraph(i));  
	         }  
	      }  
	      int secNum = range.numSections();  
	      sb.append(secNum);  
	      Section section;  
	      for (int i=0; i<secNum; i++) {  
	         section = range.getSection(i);  
	         sb.append(section.getMarginLeft());  
	         sb.append(section.getMarginRight());  
	         sb.append(section.getMarginTop());  
	         sb.append(section.getMarginBottom());  
	         sb.append(section.getPageHeight());  
	         sb.append(section.text());  
	      }  
	   }  
	    
	   /** 
	    * 插入内容到Range，这里只会写到内存中 
	    * @param range 
	    */  
	   private void insertInfo(Range range) {  
	      range.insertAfter("Hello");  
	   }  

}
