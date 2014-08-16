package org.solmix.test.performance;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.solmix.api.datasource.DataSource;
import org.solmix.api.datasource.DataSourceManager;
import org.solmix.api.exception.SlxException;
import org.solmix.fmk.SlxContext;
import org.solmix.runtime.SystemContext;
import org.solmix.test.SolmixTestCase;

public class GetDataSource  extends SolmixTestCase{

	@Test
	public void per() throws SlxException {
		
		log("=============================" + new Date().toLocaleString()
				+ "=============================", null);
		long begin = System.currentTimeMillis();
		SystemContext sc = SlxContext.getSystemContext();
		log("context初始化"+sc.getId(), (System.currentTimeMillis() - begin) + "");
		begin = System.currentTimeMillis();
		for (int i = 0; i < 10000; i++) {
			SlxContext.getSystemContext();
		}
		log("context获取10000次", (System.currentTimeMillis() - begin) + "");
		System.out.println(System.getenv());
		begin = System.currentTimeMillis();
		DataSourceManager dsm = sc.getExtension(DataSourceManager.class);
		log("获取DataSourceManager", (System.currentTimeMillis() - begin) + "");
		begin = System.currentTimeMillis();
		for (int i = 0; i < 10000; i++) {
		dsm.getUnpooledDataSource("mybatis/mybatis");
		}
		log("获取UnPooledDataSource10000次", (System.currentTimeMillis() - begin) + "");
		begin = System.currentTimeMillis();
		DataSource ds = dsm.get("mybatis/mybatis");
		dsm.free(ds);
		log("获取DataSource", (System.currentTimeMillis() - begin) + "");
		begin = System.currentTimeMillis();
		for (int i = 0; i < 10000; i++) {
			DataSource dsa = dsm.get("mybatis/mybatis");
			dsm.free(dsa);
//			DataSource b = dsm.get("SYSINIT");
//			dsm.free(b);
		}
		log("获取DataSource10000次", (System.currentTimeMillis() - begin) + "");
		
		DataSource dsa = dsm.get("mybatis/mybatis");
		Map<String,DataSource> cahe=new HashMap<String,DataSource>();
		begin = System.currentTimeMillis();
		cahe.put(dsa.getName(), dsa);
		for (int i = 0; i < 10000; i++) {
			DataSource a=cahe.get(dsa.getName());
		}
		log("Map 10000次", (System.currentTimeMillis() - begin) + "");
	}
	@Test
	public void per2() throws SlxException {
	
	}
	private void log(String content,String value){
		try {
			RandomAccessFile file = new RandomAccessFile("time.text","rw");
			long fileLength=file.length();
			file.seek(fileLength);
			file.write(content.getBytes("UTF-8"));
			if(value!=null){
			file.writeBytes("=");
			file.write(value.getBytes("UTF-8"));
			}
			file.writeBytes("\n");
			file.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
