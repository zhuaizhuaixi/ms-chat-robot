package com.zzx.robot.domain.message;

import java.util.Objects;

/**
 * @author zzx
 * @date 2023/3/24
 */
public class MessageCommand {

    public static String CQ_KEY_POST_TYPE = "post_type";
    public static String CQ_KEY_MESSAGE = "message";
    public static String CQ_KEY_MESSAGE_TYPE = "message_type";

    /**
     * 群聊(group)、频道(guild)
     */
    private final String type;

    private final String command;

    public MessageCommand(String type, String command) {
        this.type = type;
        this.command = command;
    }

    public String getType() {
        return type;
    }

    public String getCommand() {
        return command;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageCommand that = (MessageCommand) o;
        return type.equals(that.type) && command.equals(that.command);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, command);
    }

    @Override
    public String toString() {
        return "MessageCommand{" +
                "type='" + type + '\'' +
                ", command='" + command + '\'' +
                '}';
    }
}
