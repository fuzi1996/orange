package com.github.freakchick.orange.token;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * @program: orange
 * @description:
 * @author: fuzi1996
 * @create: 2022/3/5
 **/
public class TokenParserTest {

    public static class VariableTokenHandler implements TokenHandler {
        private Map<String, String> variables = new HashMap<>();

        VariableTokenHandler(Map<String, String> variables) {
            this.variables = variables;
        }

        @Override
        public String handleToken(String content) {
            return variables.get(content);
        }
    }

    @Test
    public void testShouldDemonstrateGenericTokenReplacement() {
        TokenParser parser = new TokenParser("${", "}", new VariableTokenHandler(new HashMap<String, String>() {
            {
                put("first_name", "James");
                put("initial", "T");
                put("last_name", "Kirk");
                put("var{with}brace", "Hiya");
                put("", "");
            }
        }));

        assertThat(parser.parse("${first_name} ${initial} ${last_name} reporting."), is("James T Kirk reporting."));
        assertThat(parser.parse("Hello captain ${first_name} ${initial} ${last_name}"), is("Hello captain James T Kirk"));
        assertThat(parser.parse("${first_name} ${initial} ${last_name}"), is("James T Kirk"));
        assertThat(parser.parse("${first_name}${initial}${last_name}"), is("JamesTKirk"));
        assertThat(parser.parse("{}${first_name}${initial}${last_name}"), is("{}JamesTKirk"));
        assertThat(parser.parse("}${first_name}${initial}${last_name}"), is("}JamesTKirk"));

        assertThat(parser.parse("}${first_name}{{${initial}}}${last_name}"), is("}James{{T}}Kirk"));
        assertThat(parser.parse("}${first_name}}${initial}{${last_name}"), is("}James}T{Kirk"));
        assertThat(parser.parse("}${first_name}}${initial}{${last_name}"), is("}James}T{Kirk"));
        assertThat(parser.parse("}${first_name}}${initial}{${last_name}{{}}"), is("}James}T{Kirk{{}}"));
        assertThat(parser.parse("}${first_name}}${initial}{${last_name}{{}}${}"), is("}James}T{Kirk{{}}"));

        assertThat(parser.parse("{$$something}${first_name}${initial}${last_name}"), is("{$$something}JamesTKirk"));
        assertThat(parser.parse("${"), is("${"));
        assertThat(parser.parse("${\\}"), is("${\\}"));
        assertThat(parser.parse("${var{with\\}brace}"), is("Hiya"));
        assertThat(parser.parse("${}"), is(""));
        assertThat(parser.parse("}"), is("}"));
        assertThat(parser.parse("Hello ${ this is a test."), is("Hello ${ this is a test."));
        assertThat(parser.parse("Hello } this is a test."), is("Hello } this is a test."));
        assertThat(parser.parse("Hello } ${ this is a test."), is("Hello } ${ this is a test."));
    }

    @Test
    public void testShallNotInterpolateSkippedVaiables() {
        TokenParser parser = new TokenParser("${", "}", new VariableTokenHandler(new HashMap<>()));

        assertThat(parser.parse("\\${skipped} variable"), is("${skipped} variable"));
        assertThat(parser.parse("This is a \\${skipped} variable"), is("This is a ${skipped} variable"));
        assertThat(parser.parse("${skipped} \\${skipped} variable"), is("null ${skipped} variable"));
        assertThat(parser.parse("The ${skipped} is \\${skipped} variable"), is("The null is ${skipped} variable"));
    }

    @Test
    public void testShouldParseFastOnJdk7u6() {
        Assertions.assertTimeout(Duration.ofMillis(1000), () -> {
            TokenParser parser = new TokenParser("${", "}", new VariableTokenHandler(new HashMap<String, String>() {
                {
                    put("first_name", "James");
                    put("initial", "T");
                    put("last_name", "Kirk");
                    put("", "");
                }
            }));

            StringBuilder input = new StringBuilder();
            for (int i = 0; i < 10000; i++) {
                input.append("${first_name} ${initial} ${last_name} reporting. ");
            }
            StringBuilder expected = new StringBuilder();
            for (int i = 0; i < 10000; i++) {
                expected.append("James T Kirk reporting. ");
            }
            assertThat(parser.parse(input.toString()), is(expected.toString()));
        });
    }
}