package com.ziker0k.voting.server.util;

import com.ziker0k.voting.server.model.ParsedCommand;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Optional;
import java.util.stream.Stream;

class ArgumentParserTest {

    private static Stream<Arguments> getArguments() {
        return Stream.of(
                Arguments.of("-t=1 -v=2", new ParsedCommand("1", "2")),
                Arguments.of("-t=MyTopic", new ParsedCommand("MyTopic", null)),
                Arguments.of("-t=\"My Topic\" -v=2", new ParsedCommand("My Topic", "2")),
                Arguments.of("-t=Topic -v=\"My Vote\"", new ParsedCommand("Topic", "My Vote")),
                Arguments.of("", null),
                Arguments.of("adgadsipgsp fdapofids", null),
                Arguments.of("-v=2", null),
                Arguments.of("-t= -v=", null),
                Arguments.of("-t=hfdlfsj kdfjsf -v=usdfs fdsf", new ParsedCommand("hfdlfsj", null))
                );
    }

    @ParameterizedTest
    @MethodSource("getArguments")
    void parse(String input, ParsedCommand expectedValue) {
        ParsedCommand actualResult = ArgumentParser.parse(input);

        Assertions.assertThat(actualResult).isEqualTo(expectedValue);
    }
}