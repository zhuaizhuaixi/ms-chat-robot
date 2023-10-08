package com.zzx.robot.framing;


import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.web.client.RestTemplate;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URI;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author zzx
 * @date 2023/3/27
 */
public class AutoFarmingMonitor {


    public void monitor() throws Exception {

        RestTemplate restTemplate = new RestTemplate();

        long lastPortalTimestamp = System.currentTimeMillis();

        while (true) {

            File file = new File("/Users/zzx/Downloads/auto-farming/meeting.jpg");
            BufferedImage image = ImageIO.read(file);

            int width = image.getWidth(null);
            int height = image.getHeight(null);
            int[] pixels = new int[width * height];
            PixelGrabber grabber = new PixelGrabber(image, 0, 0, width, height, pixels, 0, width);
            try {
                grabber.grabPixels();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            int runePixelCount = 0;
            int portalPixelCount = 0;
            int peoplePixelCount = 0;
            for (int pixel : pixels) {
                int red = (pixel & 0x00ff0000) >> 16;
                int green = (pixel & 0x0000ff00) >> 8;
                int blue = pixel & 0x000000ff;

//            System.out.println(red + "-" + green + "-" + blue);
                // 计算符文像素个数
                if (red >= 190 && red <= 210) {
                    if (green >= 110 && green <= 130) {
                        if (blue >= 230 && blue <= 250) {
                            runePixelCount++;
                        }
                    }
                }

                // 计算红色点像素个数
                if (red >= 105 && red <= 125) {
                    if (green >= 25 && green <= 45) {
                        if (blue >= 10 && blue <= 30) {
                            portalPixelCount++;
                        }
                    }
                }

                // 人
                if (red >= 190 && red <= 230) {
                    if (green <= 30) {
                        if (blue >= 25 && blue <= 45) {
                            peoplePixelCount++;
                        }
                    }
                }

            }

            System.out.println(runePixelCount);
            System.out.println(portalPixelCount);
            System.out.println(peoplePixelCount);

            if (runePixelCount > 10) {
                System.out.println("出现符文");
                try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                    ImageIO.write(image, "jpg", baos);
                    Map<String, String> map = new HashMap<>();
                    map.put("group_id", "531235766");
                    map.put("message", "出现符文\n[CQ:image,file=ring.png,subType=0,url=base64://" + Base64.getEncoder().encodeToString(baos.toByteArray()) + "]");
                    RequestEntity<Map<String, String>> requestEntity = new RequestEntity<>(map, HttpMethod.POST, new URI("http://121.204.150.32:8082/send_group_msg"));
                    restTemplate.exchange(requestEntity, Object.class);
                }
            }

            if (portalPixelCount > 10 && (System.currentTimeMillis() - lastPortalTimestamp) > 2 * 60 * 1000) {
                System.out.println("出现传送门");
                lastPortalTimestamp = System.currentTimeMillis();
                try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                    ImageIO.write(image, "jpg", baos);
                    Map<String, String> map = new HashMap<>();
                    map.put("group_id", "531235766");
                    map.put("message", "出现传送门\n[CQ:image,file=ring.png,subType=0,url=base64://" + Base64.getEncoder().encodeToString(baos.toByteArray()) + "]");
                    RequestEntity<Map<String, String>> requestEntity = new RequestEntity<>(map, HttpMethod.POST, new URI("http://121.204.150.32:8082/send_group_msg"));
                    restTemplate.exchange(requestEntity, Object.class);
                }
            }

            if (peoplePixelCount > 10) {
                System.out.println("来人了");
                try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                    ImageIO.write(image, "jpg", baos);
                    Map<String, String> map = new HashMap<>();
                    map.put("group_id", "531235766");
                    map.put("message", "来人了\n[CQ:image,file=ring.png,subType=0,url=base64://" + Base64.getEncoder().encodeToString(baos.toByteArray()) + "]");
                    RequestEntity<Map<String, String>> requestEntity = new RequestEntity<>(map, HttpMethod.POST, new URI("http://121.204.150.32:8082/send_group_msg"));
                    restTemplate.exchange(requestEntity, Object.class);
                }
            }

            System.out.println("finish");

            TimeUnit.SECONDS.sleep(11);

        }


    }


}
