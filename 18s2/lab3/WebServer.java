import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;

public class WebServer {
    public static final int PORT = 8080;

    public static void main(String[] args) {
        WebServer server = new WebServer();
        server.start();
    }

    public void start() {
        Socket client;
        BufferedReader bf;
        try {
            //创建一个服务器端socket，指定绑定的端口号，并监听此端口
            ServerSocket serverSocket = new ServerSocket(PORT);
            //调用accept()方法开始监听，等待客户端的连接
            System.out.println("********** server start，wait client *************");
            while (true) {
                try {
                    client = serverSocket.accept();
                    //读取客户端信息,把字节流转换成字符流,为字符流增加缓冲区
                    bf = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    String line = bf.readLine();
                    //注意，此处路径可能不符 idea的路径不需要 '/'符号  如果不显示请把 '+1'去掉
                    String requestFile = line.substring(line.indexOf('/') + 1,
                            line.lastIndexOf('/') - 5);
                    requestFile = URLDecoder.decode(requestFile, "UTF-8");
                    System.out.println("requestFile is:" + requestFile);
                    //解析请求
                    transferRequest(requestFile, client);
                } catch (Exception e) {
                    System.out.println("HTTP error:" + e);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void transferRequest(String path, Socket client) {
        try {
            //默认为请求html网页，根据文件名后缀修改contentType
            String contentType = "Content_Type:text/html";
            if (path.endsWith(".jpg")){
                contentType = "Content_Type:image/jpeg";
            } else if (path.endsWith(".png")) {
                contentType = "Content_Type:image/png";
            }
            File file = new File(path);
            PrintStream writer = new PrintStream(client.getOutputStream());
            if (file.exists()) {
                InputStream in = new FileInputStream(file);
                byte[] buf = new byte[in.available()];
                in.read(buf);
                writer.println("HTTP/1.0 200 OK");// 返回应答消息,并结束应答
                writer.println(contentType);
                writer.println("Content_Length:" + file.length());// 返回内容字节数
                writer.println();// 根据 HTTP 协议, 空行将结束头信息\
                writer.write(buf);
                in.close();
            } else {
                String error="<html><head><title>page loss</title></head><body><h1>Page does not exist</h1></body></html>";
                writer.println("HTTP/1.0 404 no found");
                writer.println("Content_Type:text/html");
                writer.println("Content_Length:"+error.length());
                writer.println();
                writer.println(error);
                writer.flush();
            }
            writer.close();
        } catch (Exception e){
            System.out.println("transferRequest error: " + e);
        }

    }

}
