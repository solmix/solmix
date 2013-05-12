/*
 * SOLMIX PROJECT
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.gnu.org/licenses/ 
 * or see the FSF site: http://www.fsf.org. 
 */

package org.solmix.test.datasource;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

/**
 * 
 * @author Administrator
 * @version 110035 2013-1-10
 */

public class PostMessageDSCall
{

    public static void main(String[] args) throws Exception {
        // if (args.length != 1) {
        // System.out.println("File path not given");
        // System.exit(1);
        // }
        HttpClient httpclient = new DefaultHttpClient();
        try {
            HttpPost httppost = new HttpPost("http://127.0.0.1:8888/data/bin/fetch/advance/chart/LineLoseChart.ds");

            // File file = new File(args[0]);

            // InputStreamEntity reqEntity = new InputStreamEntity(
            // new FileInputStream(file), -1);
            // reqEntity.setContentType("application/json");
            // reqEntity.setChunked(true);
            String request = "{" + "\"transactionNum\":1, " + "\"operations\":{" + "\"elem\":[" + "{" + "\"appID\":\"defaultApplication\", "
                + "\"componentId\":\"isc_ListGrid_0\", " + "\"startRow\":0, " + "\"endRow\":75, " + "\"textMatchStyle\":\"substring\", "
                + "\"requestId\":\"O_ORG$6270\", " + "\"dataSource\":\"O_ORG\", " + "\"operationType\":\"fetch\", " + "\"criteria\":{" + " }" + "}"
                + " ]" + "}" + "}";

            StringEntity reqEntity = new StringEntity(request);
            // It may be more appropriate to use FileEntity class in this particular
            // instance but we are using a more generic InputStreamEntity to demonstrate
            // the capability to stream out data from any arbitrary source
            //
            // FileEntity entity = new FileEntity(file, "binary/octet-stream");

            httppost.setEntity(reqEntity);

            System.out.println("executing request " + httppost.getRequestLine());
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity resEntity = response.getEntity();

            System.out.println("----------------------------------------");
            System.out.println(response.getStatusLine());
            if (resEntity != null) {
                System.out.println("Response content length: " + resEntity.getContentLength());
                System.out.println("Chunked?: " + resEntity.isChunked());
                if (resEntity instanceof StringEntity) {
                    System.out.println(((StringEntity) resEntity).toString());
                }
                System.out.println(resEntity.isStreaming());
            }
            EntityUtils.consume(resEntity);
        } finally {
            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
            httpclient.getConnectionManager().shutdown();
        }
    }
}
