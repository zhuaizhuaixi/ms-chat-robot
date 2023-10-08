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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author zzx
 * @date 2023/3/31
 */
@Component
public class GroupFamiliarHandler implements MessageHandler {

    private final Logger logger = LoggerFactory.getLogger(GroupTowerHandler.class);

    private final Random random = new Random(System.currentTimeMillis());

    @Autowired
    private MessageSender messageSender;

    @Override
    public boolean match(String type, String message) {
        return "group".equals(type) && message.startsWith("抽张怪怪卡");
    }

    @Override
    public void handle(WebSocketSession session, JsonNode message) {
        try {

            String messageString = message.get(MessageCommand.CQ_KEY_MESSAGE).asText();
            logger.info(messageString);

            String firstLine = RANK_UNIQUE.get(random.nextInt(RANK_UNIQUE.size()));
            String secondLine = RANK_EPIC.get(random.nextInt(RANK_EPIC.size()));

            String result = firstLine + "\n" + secondLine;

            ObjectNode groupMessage = MessageConstructor.newGroupMessage(message.get("group_id").asText(), result);
            messageSender.send(session, new TextMessage(groupMessage.toString()));

        } catch (Exception e) {
            logger.error("抽张怪怪卡失败: ", e);
        }
    }

    private static final List<String> RANK_UNIQUE = new ArrayList<String>() {{
        add("STR: +8");
        add("DEX: +8");
        add("INT: +8");
        add("LUK: +8");
        add("Max HP: +25");
        add("Max MP: +25");
        add("Movement Speed: +8");
        add("Jump: +8");
        add("ATT: +8");
        add("Magic ATT: +8");
        add("DEF: +25");
        add("DEF: +25");
        add("STR: +7");
        add("DEX: +7");
        add("INT: +7");
        add("LUK: +7");
        add("Max HP: +22");
        add("Max MP: +22");
        add("Movement Speed: +7");
        add("Jump: +7");
        add("ATT: +7");
        add("Magic ATT: +7");
        add("DEF: +22");
        add("DEF: +22");
        add("STR: +6%");
        add("DEX: +6%");
        add("INT: +6%");
        add("LUK: +6%");
        add("Max HP: +6%");
        add("Max MP: +6%");
        add("ATT: +6%");
        add("Magic ATT: +6%");
        add("DEF: +6%");
        add("DEF: +6%");
        add("Critical Rate: +6%");
        add("Critical Damage: +3%");
        add("Critical Damage: +3%");
        add("STR: +5%");
        add("DEX: +5%");
        add("INT: +5%");
        add("LUK: +5%");
        add("Max HP: +5%");
        add("Max MP: +5%");
        add("ATT: +5%");
        add("Magic ATT: +5%");
        add("DEF: +5%");
        add("DEF: +5%");
        add("Critical Rate: +5%");
        add("Critical Damage: +2%");
        add("Total Damage: +6%");
        add("Critical Damage: +2%");
        add("Total Damage: +5%");
        add("All Stats: +4");
        add("All Stats: +3%");
        add("All Stats: +2%");
        add("All Skill Levels: +2");
        add("All Skill Levels: +3");
        add("All Skill Levels: +1");
        add("All Elemental Resistances: +5");
        add("Abnormal Status Resistance: +5%");
        add("Attacks ignore 35% Monster DEF");
        add("Attacks ignore 40% Monster DEF");
        add("Attacks ignore 30% Monster DEF");
        add("Attacks have a 10% chance to ignore 20% damage");
        add("Attacks have a 10% chance to ignore 40% damage");
        add("Attacks have a 10% chance to ignore 30% damage");
        add("Attacks have a 10% chance to ignore 25% damage");
        add("When hit, gain +3 sec of invincibility");
        add("4% chance to become invincible for 5 seconds when attacked.");
        add("3% chance to become invincible for 5 seconds when attacked.");
        add("10% chance to reflect 20% damage");
        add("10% chance to reflect 30% damage");
        add("Hitting an enemy has a 15% chance to restore 45 HP");
        add("Hitting an enemy has a 15% chance to restore 45 MP");
        add("All Skill MP Costs: -5%");
        add("All Skill MP Costs: -10%");
        add("HP Recovery Items and Skills: +20%");
        add("HP Recovery Items and Skills: +15%");
        add("Boss Damage: +30%");
        add("Boss Damage: +35%");
        add("Boss Damage: +40%");
        add("Mesos Obtained: +10");
        add("Item Acquisition Rate: +10%");
        add("3% chance to Auto Steal");
        add("5% chance to Auto Steal");
        add("7% chance to Auto Steal");
        add("4% chance to Auto Steal");
        add("6% chance to Auto Steal");
        add("Continually restores a large amount of HP & MP");
        add("Increases Item and Meso Drop Rate by a large amount");
        add("Continually restores a large amount of HP to party members");
        add("Continually restores a large amount of MP to party members");
        add("Continually restores a large amount of HP & MP to party members");
        add("Increases party members' Speed");
        add("Increases party members' Jump");
        add("Increases party members' Defense by a large amount");
        add("Increases party members' Defense by a large amount");
        add("Continually restores a large amount of HP to nearby allies");
        add("Continually restores a large amount of MP to nearby allies");
        add("Continually restores a large amount of HP & MP to nearby allies");
        add("Increases the Speed of nearby allies by a large amount");
        add("Increases the Jump of nearby allies by a large amount");
        add("Increases the Defense of nearby allies by a large amount");
        add("Increases the Defense of nearby allies by a large amount");
        add("Increases Speed, Jump, DEX, and Defense by a small amount");
        add("Increases your party's Speed and Jump");
        add("Increases your party's Defense");
        add("Increases your party's STR, INT, DEX, and LUK");
        add("Increases STR, INT, DEX, and LUK of players on the same map");
        add("Continually restores a large amount of HP & MP");
        add("Increases Item and Meso Drop Rate by a large amount");
        add("Continually restores a large amount of HP to party members");
        add("Continually restores a large amount of MP to party members");
        add("Continually restores a large amount of HP & MP to party members");
        add("Increases party members' Speed");
        add("Increases party members' Jump");
        add("Increases party members' Defense by a large amount");
        add("Increases party members' Defense by a large amount");
        add("Continually restores a large amount of HP to nearby allies");
        add("Continually restores a large amount of MP to nearby allies");
        add("Continually restores a large amount of HP & MP to nearby allies");
        add("Increases the Speed of nearby allies by a large amount");
        add("Increases the Jump of nearby allies by a large amount");
        add("Increases the Defense of nearby allies by a large amount");
        add("Increases the Defense of nearby allies by a large amount");
        add("Increases Speed, Jump, DEX, and Defense by a small amount");
        add("Increases your party's Speed and Jump");
        add("Increases your party's Defense");
        add("Increases your party's STR, INT, DEX, and LUK");
        add("Shrouds your character in darkness");
        add("Turns your character red");
        add("Outlines your character in black");
        add("Outlines your character in red");
    }};

    private static final List<String> RANK_EPIC = new ArrayList<String>() {{
        add("STR: +6");
        add("DEX: +6");
        add("INT: +6");
        add("LUK: +6");
        add("Max HP: +20");
        add("Max MP: +20");
        add("Movement Speed: +6");
        add("Jump: +6");
        add("ATT: +6");
        add("Magic ATT: +6");
        add("DEF: +20");
        add("DEF: +20");
        add("STR: +5");
        add("DEX: +5");
        add("INT: +5");
        add("LUK: +5");
        add("Max HP: +18");
        add("Max MP: +18");
        add("Movement Speed: +5");
        add("Jump: +5");
        add("ATT: +5");
        add("Magic ATT: +5");
        add("DEF: +18");
        add("DEF: +18");
        add("STR: +3%");
        add("DEX: +3%");
        add("INT: +3%");
        add("LUK: +3%");
        add("Max HP: +3%");
        add("Max MP: +3%");
        add("ATT: +3%");
        add("Magic ATT: +3%");
        add("DEF: +3%");
        add("DEF: +3%");
        add("Critical Rate: +3%");
        add("Total Damage: +3%");
        add("All Stats: +2%");
        add("All Skill Levels: +1");
        add("All Skill Levels: +2");
        add("Restores 4 HP every 4 sec");
        add("Restores 4 MP every 4 sec");
        add("Attacks have a 3% chance to restore 25 HP");
        add("Attacks have a 3% chance to restore 25 MP");
        add("Attacks have a 10% chance to inflict Lv. 1 Poison");
        add("Attacks have a 5% chance to inflict Lv. 1 Stun");
        add("Attacks have a 10% chance to inflict Lv. 1 Slow");
        add("Attacks have a 10% chance to inflict Lv. 1 Blind");
        add("Attacks have a 5% chance to inflict Lv. 1 Freeze");
        add("Attacks have a 5% chance to inflict Lv. 1 Seal");
        add("Attacks ignore 30% Monster DEF");
        add("Attacks ignore 20% Monster DEF");
        add("Attacks have a 5% chance to ignore 20% damage");
        add("Attacks have a 5% chance to ignore 40% damage");
        add("Attacks have a 3% chance to ignore 20% damage");
        add("Attacks have a 5% chance to ignore 20% damage");
        add("Attacks have a 7% chance to ignore 20% damage");
        add("When hit, get +2 sec of invincibility");
        add("2% chance to become invincible for 5 seconds when attacked.");
        add("Hitting an enemy has a 15% chance to restore 40 HP");
        add("Hitting an enemy has a 15% chance to restore 40 MP");
        add("HP Recovery Items and Skills: +10%");
        add("Damage to Bosses: +20%");
        add("Damage to Bosses: +30%");
        add("1% chance to Auto Steal");
        add("2% chance to Auto Steal");
        add("Continually restores a large amount of HP");
        add("Continually restores a large amount of MP");
        add("Continually restores HP and MP");
        add("Increases Speed and Jump by a large amount");
        add("Increases Defense by a large amount");
        add("Increases Meso Drop Rate by a large amount");
        add("Increases Item Drop Rate by a large amount");
        add("Increases Item and Meso Drop Rate");
        add("Continually restores HP to party members");
        add("Continually restores MP to party members");
        add("Continually restores HP & MP to party members");
        add("Increases party members' Speed");
        add("Increases party members' Jump");
        add("Increases party members' Defense");
        add("Increases party members' Defense");
        add("Continually restores HP to nearby allies");
        add("Continually restores MP to nearby allies");
        add("Continually restores HP & MP to nearby allies");
        add("Increases the Speed of nearby allies");
        add("Increases the Jump of nearby allies");
        add("Increases the Defense of nearby allies");
        add("Increases the Defense of nearby allies");
        add("Shrouds your character in darkness");
        add("Turns your character red");
        add("Outlines your character in black");
        add("Outlines your character in red");
        add("Continually restores HP");
        add("Continually restores MP");
        add("Continually restores HP and MP");
        add("Increases Speed and Jump");
        add("Increases Defense");
        add("Increases Meso Drop Rate");
        add("Increases Item Drop Rate");
        add("Increases Item and Meso Drop Rate by a small amount");
        add("Continually restores the party's HP by a small amount");
        add("Continually restores the party's MP by a small amount");
        add("Continually restores the party's HP & MP by a small amount");
        add("Increases party members' Speed by a small amount");
        add("Increases party members' Jump by a small amount");
        add("Increases party members' Defense by a small amount");
        add("Increases party members' Defense by a small amount");
        add("Continually restores a small amount of HP to nearby allies");
        add("Continually restores a small amount of MP to nearby allies");
        add("Continually restores a small amount of HP & MP to nearby allies");
        add("Increases the Speed of nearby allies by a small amount");
        add("Increases the Jump of nearby allies by a small amount");
        add("Increases the Defense of nearby allies by a small amount");
        add("Increases the Defense of nearby allies by a small amount");
    }};

}
