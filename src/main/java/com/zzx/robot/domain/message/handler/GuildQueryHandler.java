package com.zzx.robot.domain.message.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.zzx.robot.domain.message.MessageHandler;
import com.zzx.robot.util.MessageConstants;
import com.zzx.robot.websocket.message.MessageConstructor;
import lombok.SneakyThrows;
import org.apache.camel.Message;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.style.Styler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URL;
import java.util.*;
import java.util.List;

/**
 * @author zzx
 * @date 2023/3/24
 */
@Component
public class GuildQueryHandler implements MessageHandler {

    private final Logger logger = LoggerFactory.getLogger(GuildQueryHandler.class);

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${maple-info.storage-dir}")
    private String mapleInfoStorageDir;

    @Override
    @SneakyThrows
    public ObjectNode handle(Message message) {

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");

        ObjectMapper om = new ObjectMapper();

        String messageString = message.getBody(String.class);
        logger.info(messageString);
        String characterName = messageString.replace("查询", "").trim();
        logger.info("请求 " + "https://api.maplestory.gg/v2/public/character/gms/" + characterName);
        // query character info
        RequestEntity<String> requestEntity = new RequestEntity<>(headers, HttpMethod.GET, new URI("https://api.maplestory.gg/v2/public/character/gms/" + characterName));
        ResponseEntity<String> result = restTemplate.exchange(requestEntity, String.class);
        JsonNode queryResult = om.readTree(result.getBody());

        logger.info("接收到来自maple.gg的消息" + queryResult.toString());

        String imageUrl = queryResult.get("CharacterData").get("CharacterImageURL").asText();
        String job = queryResult.get("CharacterData").get("Class").asText();
        String server = queryResult.get("CharacterData").get("Server").asText();
        String level = queryResult.get("CharacterData").get("Level").asText() + "(" + queryResult.get("CharacterData").get("EXPPercent").asText() + "%)";
        String jobRank = queryResult.get("CharacterData").get("ServerClassRanking").asText();
        String serverRank = queryResult.get("CharacterData").get("ServerRank").asText();
        String legionLevel = queryResult.get("CharacterData").get("LegionLevel").asText();
        String legionRank = queryResult.get("CharacterData").get("LegionRank").asText();
        String legionPower = queryResult.get("CharacterData").get("LegionPower").asText();
        String legionCoins = queryResult.get("CharacterData").get("LegionCoinsPerDay").asText();

        //设置图片大小
        BufferedImage background = new BufferedImage(1000, 700, BufferedImage.TYPE_INT_RGB);
        background.getGraphics().drawImage(ImageIO.read(new File(mapleInfoStorageDir + "/maple-background-clear.png")).getScaledInstance(1000, 700, Image.SCALE_SMOOTH), 0, 0, null);
        Graphics2D g = background.createGraphics();

        // 表格边框
        BufferedImage labelImage = new BufferedImage(250, 40, BufferedImage.TYPE_INT_ARGB);
        labelImage.getGraphics().drawImage(ImageIO.read(new File(mapleInfoStorageDir + "/label.png")).getScaledInstance(250, 30, Image.SCALE_SMOOTH), 0, 0, null);

        // 上方背景框
        BufferedImage upBackground = new BufferedImage(900, 300, BufferedImage.TYPE_INT_ARGB);
        upBackground.getGraphics().drawImage(ImageIO.read(new File(mapleInfoStorageDir + "/maple-leaf-border.png")).getScaledInstance(900, 300, Image.SCALE_SMOOTH), 0, 0, null);
        g.drawImage(new BufferedImage(800, 250, BufferedImage.TYPE_INT_ARGB), 50, 35, new Color(250, 240, 230, 100), null);
        g.drawImage(upBackground, 0, 15, null);

        BufferedImage characterImage;
        try {
            characterImage = ImageIO.read(new URL(imageUrl));
        } catch (IOException e) {
            characterImage = ImageIO.read(new File(mapleInfoStorageDir + "/default.png"));
        }

        // 放头像
        g.drawImage(characterImage.getScaledInstance(200, 200, BufferedImage.TYPE_INT_RGB), 80, 60, null);
        g.setColor(Color.black);
        g.setFont(new Font("Songti SC", Font.PLAIN, 16));
        g.drawImage(labelImage, 330, 70, null);
        g.drawString("角色名：\t" + characterName, 350, 90);
        g.drawImage(labelImage, 580, 70, null);
        g.drawString("职业：\t" + job, 600, 90);
        g.drawImage(labelImage, 330, 110, null);
        g.drawString("等级：\t" + level, 350, 130);
        g.drawImage(labelImage, 580, 110, null);
        g.drawString("服务器：\t" + server, 600, 130);
        g.drawImage(labelImage, 330, 150, null);
        g.drawString("职业排名：\t" + jobRank, 350, 170);
        g.drawImage(labelImage, 580, 150, null);
        g.drawString("总排名：\t" + serverRank, 600, 170);
        g.drawImage(labelImage, 330, 190, null);
        g.drawString("联盟等级：\t" + legionLevel, 350, 210);
        g.drawImage(labelImage, 580, 190, null);
        g.drawString("联盟排名：\t" + legionRank, 600, 210);
        g.drawImage(labelImage, 330, 230, null);
        g.drawString("战斗力：\t" + legionPower, 350, 250);
        g.drawImage(labelImage, 580, 230, null);
        g.drawString("每日币：\t" + legionCoins, 600, 250);


        // 下方背景框
        BufferedImage downBackground = new BufferedImage(900, 350, BufferedImage.TYPE_INT_ARGB);
        downBackground.getGraphics().drawImage(ImageIO.read(new File(mapleInfoStorageDir + "/maple-leaf-border.png")).getScaledInstance(900, 350, Image.SCALE_SMOOTH), 0, 0, null);

        g.drawImage(new BufferedImage(800, 300, BufferedImage.TYPE_INT_ARGB), 50, 300, new Color(250, 240, 230, 100), null);
        g.drawImage(downBackground, 0, 270, null);

        // 构建直方图
        CategoryChart chart = new CategoryChartBuilder().width(870).height(270)
                .xAxisTitle("日期")
                .yAxisTitle("经验值")
                .build();

        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNW);
        chart.getStyler().setAvailableSpaceFill(0.8)
                .setLabelsVisible(true)
                .setXAxisLabelRotation(20)
                .setXAxisTitleVisible(false)
                .setYAxisTitleVisible(false)
                .setPlotGridLinesVisible(false)
                .setYAxisTicksVisible(false)
                .setChartBackgroundColor(new Color(255, 255, 255, 0))
                .setPlotBackgroundColor(new Color(255, 255, 255, 0))
                .setPlotBorderVisible(false)
                .setLegendVisible(false)
                .setSeriesColors(new Color[]{new Color(237, 145, 33)});
        chart.getStyler().setyAxisTickLabelsFormattingFunction(value -> {
            BigDecimal b = BigDecimal.valueOf(value / 1000 / 1000 / 1000 / 1000);
            double f1 = b.setScale(2, RoundingMode.HALF_UP).doubleValue();
            return f1 + "T";
        });

        Map<String, Double> graphData = new TreeMap<>();

        List<JsonNode> graphDataList = new ArrayList<>();
        queryResult.get("CharacterData").get("GraphData").elements().forEachRemaining(graphDataList::add);

        for (int i = 1; i < graphDataList.size(); i++) {
            JsonNode dataJSONObject = graphDataList.get(i);
            JsonNode preDataJSONObject = graphDataList.get(i - 1);
            graphData.put(dataJSONObject.get("DateLabel").asText().substring(5, 10), preDataJSONObject.get("EXPDifference").asDouble());
        }

        List<Double> yData = new ArrayList<>(graphData.values());
        List<String> xData = new ArrayList<>(graphData.keySet());

        // 加一行空的
        xData.add(" ");
        yData.add(0D);

        chart.addSeries("经验值", xData, yData);

        BufferedImage lineChartImage = new BufferedImage(780, 270, BufferedImage.TYPE_INT_ARGB);
        Graphics2D lineChart = lineChartImage.createGraphics();
        chart.paint(lineChart, 870, 270);

        g.drawImage(lineChartImage, 50, 320, 780, 270, null);

        String picName = "info_" + characterName;
        File out = new File(mapleInfoStorageDir + "/" + picName + ".jpg");
        ImageIO.write(background, "jpg", out);

        g.dispose();

        String robotMessage = ("[CQ:image,file=" + out.getName() + ",url=file://" + out.getAbsolutePath() + "]");

        return MessageConstructor.newGuildMessage(
                message.getHeader(MessageConstants.Header.GUILD_ID, String.class),
                message.getHeader(MessageConstants.Header.CHANNEL_ID, String.class),
                robotMessage
        );

    }

}
