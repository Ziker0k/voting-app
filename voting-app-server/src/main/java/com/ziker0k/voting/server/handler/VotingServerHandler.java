package com.ziker0k.voting.server.handler;

import com.ziker0k.voting.common.dto.DoVoteDto;
import com.ziker0k.voting.common.service.VotingService;
import com.ziker0k.voting.server.model.UserSession;
import com.ziker0k.voting.server.service.CommandProcessor;
import com.ziker0k.voting.server.service.SessionManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class VotingServerHandler extends SimpleChannelInboundHandler<String> {
    private static final Logger log = LoggerFactory.getLogger(VotingServerHandler.class);
    private final SessionManager sessionManager = SessionManager.getInstance();
    private final VotingService votingService = VotingService.getInstance();
    private final CommandProcessor commandProcessor = new CommandProcessor();

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.writeAndFlush("""
                Welcome to voting app! Please login using the following command:
                login -u=<username>
                """);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) {
        UserSession userSession = sessionManager.getSession(ctx);

        switch (userSession.getState()) {
            case VOTING -> handleVoting(userSession, msg);
            case CREATING_VOTE -> ctx.fireChannelRead(msg);
            default -> processCommand(ctx, userSession, msg);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        UserSession userSession = sessionManager.getSession(ctx);
        sessionManager.destroySession(userSession);
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("Error in server", cause);
        ctx.close();
    }

    private void processCommand(ChannelHandlerContext ctx, UserSession userSession, String msg) {
        // Убираем CreateVoteHandler, если он есть в пайплайне
        if (ctx.channel().pipeline().get(CreateVoteHandler.class) != null) {
            ctx.channel().pipeline().remove(CreateVoteHandler.class);
        }

        // Обрабатываем обычную команду
        commandProcessor.process(userSession, votingService, msg);
    }

    private void handleVoting(UserSession session, String msg) {
        // Получаем варианты голосования
        List<String> options = session.getOptions();

        try {
            int choice = Integer.parseInt(msg.trim());
            if (choice < 1 || choice > options.size()) {
                sendInvalidChoiceMessage(session);
                return;
            }

            // Получаем выбранный вариант
            String selectedOption = options.get(choice - 1);
            DoVoteDto doVoteDto = buildVoteDto(session, selectedOption);

            // Осуществляем голосование
            votingService.vote(doVoteDto);
            sendVoteConfirmation(session, selectedOption);

            // Очищаем сессию голосования
            session.clearActiveVote();
        } catch (NumberFormatException e) {
            sendInvalidInputMessage(session);
        }
    }

    private DoVoteDto buildVoteDto(UserSession session, String selectedOption) {
        DoVoteDto doVoteDtoFromSession = session.getDoVoteDto();
        return DoVoteDto.builder()
                .topicName(doVoteDtoFromSession.getTopicName())
                .voteName(doVoteDtoFromSession.getVoteName())
                .voter(session.getUsername())
                .option(selectedOption)
                .build();
    }

    private void sendInvalidChoiceMessage(UserSession session) {
        session.getChannelHandlerContext().writeAndFlush("Invalid choice. Please enter a valid number.\n");
    }

    private void sendInvalidInputMessage(UserSession session) {
        session.getChannelHandlerContext().writeAndFlush("Invalid input. Please enter a number corresponding to an option.\n");
    }

    private void sendVoteConfirmation(UserSession session, String selectedOption) {
        session.getChannelHandlerContext().writeAndFlush("Your vote has been recorded for \"" + selectedOption + "\".\n");
    }
}