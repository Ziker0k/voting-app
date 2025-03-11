package com.ziker0k.voting.server.util;

import ch.qos.logback.core.joran.sanity.Pair;
import com.ziker0k.voting.server.model.ParsedCommand;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class ArgumentParser {
    private static final Pattern PATTERN = Pattern.compile(
            "-t=(?:\"([^\"]+)\"|(\\S+))(?:\\s+-v=(?:\"([^\"]+)\"|(\\S+)))?"
    );

    public static ParsedCommand parse(String input) {
        Matcher matcher = PATTERN.matcher(input);

        if (matcher.find()) {
            String topic = matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
            String vote = matcher.group(3) != null ? matcher.group(3) : matcher.group(4);
            return new ParsedCommand(topic, vote);
        }
        return null;
    }

}
