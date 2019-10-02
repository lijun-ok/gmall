package com.atguigu.gmall.manager;

import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallManagerWebApplicationTests {
    /**
     * 测试 fdfs
     */
    @Test
    public void contextLoads() throws IOException, MyException {
            //配置fafs的全局信息
        String file = GmallManagerWebApplicationTests.class.getClassLoader().getResource("tracker.conf").getFile();
        ClientGlobal.init(file);
            //获得tracker
        TrackerClient trackerClient = new TrackerClient();
        TrackerServer connection = trackerClient.getConnection();
        //通过tracker获得storage
        StorageClient storageClient = new StorageClient(connection, null);
        //通过storage上传文件
        String[] jpgs = storageClient.upload_file("C:\\Users\\ne'w\\Pictures\\Saved Pictures\\1.jpg", "jpg", null);
        String url="http://192.168.231.151";
       for (String jpg : jpgs) {
            url=url+"/"+jpg;
        }
        System.out.println(url);

    }

}
