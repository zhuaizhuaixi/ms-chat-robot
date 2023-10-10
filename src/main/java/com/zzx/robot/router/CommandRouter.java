package com.zzx.robot.router;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.zzx.robot.domain.message.MessageHandler;
import com.zzx.robot.domain.message.handler.*;
import com.zzx.robot.domain.message.handler.entry.GroupAddHandler;
import com.zzx.robot.domain.message.handler.entry.GroupDeleteHandler;
import com.zzx.robot.domain.message.handler.entry.GroupListHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.PredicateBuilder;
import org.apache.camel.builder.endpoint.EndpointRouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import static com.zzx.robot.util.MessageConstants.Header.*;


/**
 * @author zzx
 * @date 2023/10/9
 */
@Slf4j
@Component
public class CommandRouter extends EndpointRouteBuilder {

    public static final String ROUTE_PATH_COMMAND = "/command";

    @Autowired
    private ApplicationContext context;

    @Override
    public void configure() throws Exception {
        from(direct(ROUTE_PATH_COMMAND))
                .filter(header(POST_TYPE).isEqualTo("message"))
                // 过滤交易群
                .filter(PredicateBuilder.not(header(GROUP_ID).in("293385059", "154681251", "578160115", "232112159")))
                .choice()
                    .when(header(MESSAGE_TYPE).isEqualTo("group"))
                    .choice()
                        .when(header(BRIEF_COMMAND).startsWith("绑定")).process(new InnerProcessor(context.getBean(GroupBindHandler.class)))
                        .when(header(BRIEF_COMMAND).isEqualTo("找个线")).process(new InnerProcessor(context.getBean(GroupChannelHandler.class)))
                        .when(header(BRIEF_COMMAND).isEqualTo("抽张怪怪卡")).process(new InnerProcessor(context.getBean(GroupFamiliarHandler.class)))
                        .when(header(BRIEF_COMMAND).startsWith("来张")).process(new InnerProcessor(context.getBean(GroupPictureHandler.class)))
                        .when(header(BRIEF_COMMAND).startsWith("查询")).process(new InnerProcessor(context.getBean(GroupQueryHandler.class)))
                        .when(header(BRIEF_COMMAND).contains("上上星")).process(new InnerProcessor(context.getBean(GroupStarForceHandler.class)))
                        .when(header(BRIEF_COMMAND).isEqualTo("爬个塔")).process(new InnerProcessor(context.getBean(GroupTowerHandler.class)))
                        .when(header(BRIEF_COMMAND).isEqualTo("添加词条")).process(new InnerProcessor(context.getBean(GroupAddHandler.class)))
                        .when(header(BRIEF_COMMAND).isEqualTo("删除词条")).process(new InnerProcessor(context.getBean(GroupDeleteHandler.class)))
                        .when(header(BRIEF_COMMAND).isEqualTo("查")).process(new InnerProcessor(context.getBean(com.zzx.robot.domain.message.handler.entry.GroupQueryHandler.class)))
                        .when(header(BRIEF_COMMAND).isEqualTo("词条列表")).process(new InnerProcessor(context.getBean(GroupListHandler.class)))
                    .endChoice()
                    .when(header(MESSAGE_TYPE).isEqualTo("guild"))
                    .choice()
                        .when(header(BRIEF_COMMAND).startsWith("查询")).process(new InnerProcessor(context.getBean(GuildQueryHandler.class)))
                    .endChoice()
                .endChoice()
                .end()
                .to(direct(SenderRouter.ROUTE_PATH_SENDER));
    }

    private static class InnerProcessor implements Processor {

        private final MessageHandler messageHandler;

        public InnerProcessor(MessageHandler messageHandler) {
            this.messageHandler = messageHandler;
        }

        @Override
        public void process(Exchange exchange) throws Exception {
            ObjectNode result = messageHandler.handle(exchange.getMessage());
            exchange.getMessage().setBody(result);
            exchange.getMessage().setHeader(PROCESSED, true);
        }
    }

}
