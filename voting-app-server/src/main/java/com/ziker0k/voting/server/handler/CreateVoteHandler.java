package com.ziker0k.voting.server.handler;

import com.ziker0k.voting.common.dto.CreateVoteDto;
import com.ziker0k.voting.common.exception.VotingException;
import com.ziker0k.voting.common.service.VotingService;
import com.ziker0k.voting.server.model.UserSession;
import com.ziker0k.voting.server.model.UserState;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public class CreateVoteHandler extends SimpleChannelInboundHandler<String> {
    private static final Logger log = LoggerFactory.getLogger(CreateVoteHandler.class);
    private final UserSession userSession;
    private final VotingService votingService;
    private final String topicTitle;

    private String voteTitle;
    private String voteDescription;
    private int numberOfChoices;
    private final Set<String> options = new HashSet<>();
    private int step = 0;

    public CreateVoteHandler(UserSession userSession, VotingService votingService, String topicTitle) {
        this.userSession = userSession;
        this.votingService = votingService;
        this.topicTitle = topicTitle;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, String msg) {
        if (userSession.getState().equals(UserState.CREATING_VOTE)) {
            processCreateVote(ctx, msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("Error occurred while handling create vote", cause);
        ctx.writeAndFlush("Error! " + cause.getMessage() + "\n");
        ctx.close();
    }

    private void processCreateVote(ChannelHandlerContext ctx, String msg) {
        switch (step) {
            case 0:
                promptVoteTitle(ctx);
                break;
            case 1:
                handleVoteTitle(ctx, msg);
                break;
            case 2:
                handleVoteDescription(ctx, msg);
                break;
            case 3:
                handleNumberOfChoices(ctx, msg);
                break;
            case 4:
                handleOption(ctx, msg);
                break;
            default:
                break;
        }
    }

    private void promptVoteTitle(ChannelHandlerContext ctx) {
        ctx.writeAndFlush("Type vote title:\n");
        step++;
    }

    private void handleVoteTitle(ChannelHandlerContext ctx, String msg) {
        String trimmedTitle = msg.trim();
        if (votingService.isVoteExistsInTopic(trimmedTitle, topicTitle)) {
            ctx.writeAndFlush("Vote \"" + trimmedTitle + "\" already exists in topic \"" + topicTitle + "\"\n");
            step--;
        } else {
            voteTitle = trimmedTitle;
            ctx.writeAndFlush("Type vote description:\n");
            step++;
        }
    }

    private void handleVoteDescription(ChannelHandlerContext ctx, String msg) {
        voteDescription = msg.trim();
        ctx.writeAndFlush("Enter number of possible options:\n");
        step++;
    }

    private void handleNumberOfChoices(ChannelHandlerContext ctx, String msg) {
        try {
            numberOfChoices = Integer.parseInt(msg.trim());
            if (numberOfChoices < 2) {
                ctx.writeAndFlush("Error! Number of options should be at least 2.\n");
                return;
            }
            ctx.writeAndFlush("Enter option №1:\n");
            step++;
        } catch (NumberFormatException e) {
            ctx.writeAndFlush("Error! Please enter a valid number.\n");
        }
    }

    private void handleOption(ChannelHandlerContext ctx, String msg) {
        String option = msg.trim();

        if (options.contains(option)) {
            ctx.writeAndFlush("Error! Option \"" + option + "\" already exists. Enter another option.\n");
            return;
        }

        options.add(option);

        if (options.size() < numberOfChoices) {
            ctx.writeAndFlush("Enter option №" + (options.size() + 1) + ":\n");
        } else {
            createVote(ctx);
        }
    }

    private void createVote(ChannelHandlerContext ctx) {
        CreateVoteDto createVoteDto = CreateVoteDto.builder()
                .topicTitle(topicTitle)
                .voteTitle(voteTitle)
                .creator(userSession.getUsername())
                .description(voteDescription)
                .options(options)
                .build();

        try {
            votingService.createVote(createVoteDto);
            ctx.writeAndFlush("Vote \"" + voteTitle + "\" created successfully!\n");
            log.info("Vote '{}' created successfully for topic '{}'", voteTitle, topicTitle);
        } catch (VotingException e) {
            ctx.writeAndFlush("Error! " + e.getMessage() + "\n");
            log.error("Error creating vote '{}' in topic '{}': {}", voteTitle, topicTitle, e.getMessage());
        } finally {
            userSession.setState(UserState.LOGGED_IN);
        }
    }
}