package com.zzx.robot.domain.message.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.zzx.robot.domain.message.MessageCommand;
import com.zzx.robot.domain.message.MessageHandler;
import com.zzx.robot.domain.message.MessageSender;
import com.zzx.robot.websocket.message.MessageConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * @author zzx
 * @date 2023/4/13
 */
@Component
public class GroupStarForceHandler implements MessageHandler {

    private final Logger logger = LoggerFactory.getLogger(GroupStarForceHandler.class);

    Random random = new Random(System.currentTimeMillis());

    @Autowired
    private MessageSender messageSender;

    @Override
    public boolean match(String type, String message) {
        return "group".equals(type) && message.contains("上上星");
    }

    @Override
    public void handle(WebSocketSession session, JsonNode message) {

        try {

            // 关键词包含：保护、必成
            String messageString = message.get(MessageCommand.CQ_KEY_MESSAGE).asText();
            logger.info(messageString);

            boolean safeGuard = messageString.contains("保护");
            boolean success = messageString.contains("必成");
            boolean au = messageString.contains("200");

            int level = au ? 200 : 160;

            Integer[] belowTwentyRate = {300, 672, 21};
            Integer[] aboveTwentyRate = {300, 630, 70};


            Map<Integer, Map<Integer, Integer>> levelCostMap = new HashMap<Integer, Map<Integer, Integer>>() {{
                put(160, new HashMap<Integer, Integer>() {{
                    put(15, 36514500);
                    put(16, 43008300);
                    put(17, 50185100);
                    put(18, 58072700);
                    put(19, 66698700);
                    put(20, 76090000);
                    put(21, 86273300);
                }});
                put(200, new HashMap<Integer, Integer>() {{
                    put(15, 71316500);
                    put(16, 83999600);
                    put(17, 98016700);
                    put(18, 113422300);
                    put(19, 130270000);
                    put(20, 148612400);
                    put(21, 168501500);
                }});
            }};

            Map<Integer, Integer> costMap = levelCostMap.get(level);

            Long cost = 0L;

            int star = 15;
            List<String> progress = new ArrayList<>();
            progress.add(Integer.toString(star));

            int accumulateFail = 0;

            while (true) {
                if ((star == 15 && success) || accumulateFail == 2) {
                    cost += costMap.get(star);
                    star++;
                    accumulateFail = 0;
                    progress.add(star + "[必成]");
                    continue;
                }

                boolean mayDestroy = true;

                int currentStarCost = costMap.get(star);

                if (safeGuard && star <= 16) {
                    mayDestroy = false;
                    currentStarCost *= 2;
                }

                cost += currentStarCost;

                double successRate, destroyRate;

                if (star < 20) {
                    successRate = belowTwentyRate[0];
                    destroyRate = belowTwentyRate[2];
                } else {
                    successRate = aboveTwentyRate[0];
                    destroyRate = aboveTwentyRate[2];
                }


                if (!mayDestroy) {
                    destroyRate = 0;
                }

                int randomInt = random.nextInt(1000);
                if (randomInt < successRate) {
                    // 成功
                    star++;
                    accumulateFail = 0;
                    progress.add(Integer.toString(star));
                } else if (randomInt < successRate + destroyRate) {
                    // 爆炸
                    if (messageString.contains("死磕")) {
                        progress.add("destroyed -> 15");
                        star = 15;
                        continue;
                    } else {
                        progress.add("destroyed");
                        break;
                    }
                } else {
                    // 失败
                    if (star == 15 || star == 20) {
                        star = star;
                    } else {
                        star--;
                        accumulateFail++;
                    }
                    progress.add(Integer.toString(star));
                }

                if (star == 22) {
                    break;
                }
            }

            String result = String.join(" -> ", progress);

            BigDecimal c = BigDecimal.valueOf(((double) cost) / 1000 / 1000 / 1000);
            double costMesos = c.setScale(1, RoundingMode.HALF_UP).doubleValue();

            result = result + "\n\n总花费" + costMesos + "B（" + level + "级装备）";

            ObjectNode groupMessage = MessageConstructor.newGroupMessage(message.get("group_id").asText(), result);
            messageSender.send(session, new TextMessage(groupMessage.toString()));

        } catch (Exception e) {
            logger.error("上上星失败：", e);
        }

    }

}
