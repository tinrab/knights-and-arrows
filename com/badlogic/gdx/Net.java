package com.badlogic.gdx;

import com.badlogic.gdx.net.HttpStatus;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.net.ServerSocketHints;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public interface Net {
   void sendHttpRequest(Net.HttpRequest var1, Net.HttpResponseListener var2);

   ServerSocket newServerSocket(Net.Protocol var1, int var2, ServerSocketHints var3);

   Socket newClientSocket(Net.Protocol var1, String var2, int var3, SocketHints var4);

   void openURI(String var1);

   public interface HttpMethods {
      String GET = "GET";
      String POST = "POST";
      String PUT = "PUT";
      String DELETE = "DELETE";
   }

   public static class HttpRequest {
      private final String httpMethod;
      private String url;
      private Map<String, String> headers;
      private int timeOut = 0;
      private String content;
      private InputStream contentStream;
      private long contentLength;

      public HttpRequest(String httpMethod) {
         this.httpMethod = httpMethod;
         this.headers = new HashMap();
      }

      public void setUrl(String url) {
         this.url = url;
      }

      public void setHeader(String name, String value) {
         this.headers.put(name, value);
      }

      public void setContent(String content) {
         this.content = content;
      }

      public void setContent(InputStream contentStream, long contentLength) {
         this.contentStream = contentStream;
         this.contentLength = contentLength;
      }

      public void setTimeOut(int timeOut) {
         this.timeOut = timeOut;
      }

      public int getTimeOut() {
         return this.timeOut;
      }

      public String getMethod() {
         return this.httpMethod;
      }

      public String getUrl() {
         return this.url;
      }

      public String getContent() {
         return this.content;
      }

      public InputStream getContentStream() {
         return this.contentStream;
      }

      public long getContentLength() {
         return this.contentLength;
      }

      public Map<String, String> getHeaders() {
         return this.headers;
      }
   }

   public interface HttpResponse {
      byte[] getResult();

      String getResultAsString();

      InputStream getResultAsStream();

      HttpStatus getStatus();
   }

   public interface HttpResponseListener {
      void handleHttpResponse(Net.HttpResponse var1);

      void failed(Throwable var1);
   }

   public static enum Protocol {
      TCP;
   }
}
