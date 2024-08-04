package com.badlogic.gdx.net;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.StreamUtils;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NetJavaImpl {
   private final ExecutorService executorService = Executors.newCachedThreadPool();

   public void sendHttpRequest(final Net.HttpRequest httpRequest, final Net.HttpResponseListener httpResponseListener) {
      if (httpRequest.getUrl() == null) {
         httpResponseListener.failed(new GdxRuntimeException("can't process a HTTP request without URL set"));
      } else {
         try {
            String method = httpRequest.getMethod();
            URL url;
            if (method.equalsIgnoreCase("GET")) {
               String queryString = "";
               String value = httpRequest.getContent();
               if (value != null && !"".equals(value)) {
                  queryString = "?" + value;
               }

               url = new URL(httpRequest.getUrl() + queryString);
            } else {
               url = new URL(httpRequest.getUrl());
            }

            final HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            final boolean doingOutPut = method.equalsIgnoreCase("POST") || method.equalsIgnoreCase("PUT");
            connection.setDoOutput(doingOutPut);
            connection.setDoInput(true);
            connection.setRequestMethod(method);
            Map<String, String> headers = httpRequest.getHeaders();
            Set<String> keySet = headers.keySet();
            Iterator var10 = keySet.iterator();

            while(var10.hasNext()) {
               String name = (String)var10.next();
               connection.addRequestProperty(name, (String)headers.get(name));
            }

            connection.setConnectTimeout(httpRequest.getTimeOut());
            connection.setReadTimeout(httpRequest.getTimeOut());
            this.executorService.submit(new Runnable() {
               public void run() {
                  try {
                     if (doingOutPut) {
                        String contentAsString = httpRequest.getContent();
                        InputStream contentAsStream = httpRequest.getContentStream();
                        OutputStream outputStream = connection.getOutputStream();
                        if (contentAsString != null) {
                           OutputStreamWriter writer = new OutputStreamWriter(outputStream);
                           writer.write(contentAsString);
                           writer.flush();
                           writer.close();
                        } else if (contentAsStream != null) {
                           StreamUtils.copyStream(contentAsStream, outputStream);
                           outputStream.flush();
                           outputStream.close();
                        }
                     }

                     connection.connect();
                     final NetJavaImpl.HttpClientResponse clientResponse = new NetJavaImpl.HttpClientResponse(connection);
                     Gdx.app.postRunnable(new Runnable() {
                        public void run() {
                           try {
                              httpResponseListener.handleHttpResponse(clientResponse);
                           } finally {
                              connection.disconnect();
                           }

                        }
                     });
                  } catch (final Exception var5) {
                     Gdx.app.postRunnable(new Runnable() {
                        public void run() {
                           connection.disconnect();
                           httpResponseListener.failed(var5);
                        }
                     });
                  }

               }
            });
         } catch (Exception var11) {
            httpResponseListener.failed(var11);
         }
      }
   }

   static class HttpClientResponse implements Net.HttpResponse {
      private HttpURLConnection connection;
      private HttpStatus status;
      private InputStream inputStream;

      public HttpClientResponse(HttpURLConnection connection) throws IOException {
         this.connection = connection;

         try {
            this.inputStream = connection.getInputStream();
         } catch (IOException var4) {
            this.inputStream = connection.getErrorStream();
         }

         try {
            this.status = new HttpStatus(connection.getResponseCode());
         } catch (IOException var3) {
            this.status = new HttpStatus(-1);
         }

      }

      public byte[] getResult() {
         ByteArrayOutputStream buffer = new ByteArrayOutputStream();
         byte[] data = new byte[16384];

         try {
            int nRead;
            while((nRead = this.inputStream.read(data, 0, data.length)) != -1) {
               buffer.write(data, 0, nRead);
            }

            buffer.flush();
         } catch (IOException var5) {
            return new byte[0];
         }

         return buffer.toByteArray();
      }

      public String getResultAsString() {
         BufferedReader reader = new BufferedReader(new InputStreamReader(this.inputStream));
         String line = "";

         try {
            String tmp;
            while((tmp = reader.readLine()) != null) {
               line = line + tmp;
            }

            reader.close();
            return line;
         } catch (IOException var5) {
            return "";
         }
      }

      public InputStream getResultAsStream() {
         return this.inputStream;
      }

      public HttpStatus getStatus() {
         return this.status;
      }
   }
}
