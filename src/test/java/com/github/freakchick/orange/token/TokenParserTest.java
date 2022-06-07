package com.github.freakchick.orange.token;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

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
    void shouldDemonstrateGenericTokenReplacement() {
        TokenParser parser = new TokenParser("${", "}", new VariableTokenHandler(new HashMap<String, String>() {
            {
                put("first_name", "James");
                put("initial", "T");
                put("last_name", "Kirk");
                put("var{with}brace", "Hiya");
                put("", "");
            }
        }));

        assertEquals("James T Kirk reporting.", parser.parse("${first_name} ${initial} ${last_name} reporting."));
        assertEquals("Hello captain James T Kirk", parser.parse("Hello captain ${first_name} ${initial} ${last_name}"));
        assertEquals("James T Kirk", parser.parse("${first_name} ${initial} ${last_name}"));
        assertEquals("JamesTKirk", parser.parse("${first_name}${initial}${last_name}"));
        assertEquals("{}JamesTKirk", parser.parse("{}${first_name}${initial}${last_name}"));
        assertEquals("}JamesTKirk", parser.parse("}${first_name}${initial}${last_name}"));

        assertEquals("}James{{T}}Kirk", parser.parse("}${first_name}{{${initial}}}${last_name}"));
        assertEquals("}James}T{Kirk", parser.parse("}${first_name}}${initial}{${last_name}"));
        assertEquals("}James}T{Kirk", parser.parse("}${first_name}}${initial}{${last_name}"));
        assertEquals("}James}T{Kirk{{}}", parser.parse("}${first_name}}${initial}{${last_name}{{}}"));
        assertEquals("}James}T{Kirk{{}}", parser.parse("}${first_name}}${initial}{${last_name}{{}}${}"));

        assertEquals("{$$something}JamesTKirk", parser.parse("{$$something}${first_name}${initial}${last_name}"));
        assertEquals("${", parser.parse("${"));
        assertEquals("${\\}", parser.parse("${\\}"));
        assertEquals("Hiya", parser.parse("${var{with\\}brace}"));
        assertEquals("", parser.parse("${}"));
        assertEquals("}", parser.parse("}"));
        assertEquals("Hello ${ this is a test.", parser.parse("Hello ${ this is a test."));
        assertEquals("Hello } this is a test.", parser.parse("Hello } this is a test."));
        assertEquals("Hello } ${ this is a test.", parser.parse("Hello } ${ this is a test."));
    }

    @Test
    void shallNotInterpolateSkippedVaiables() {
        TokenParser parser = new TokenParser("${", "}", new VariableTokenHandler(new HashMap<>()));

        assertEquals("${skipped} variable", parser.parse("\\${skipped} variable"));
        assertEquals("This is a ${skipped} variable", parser.parse("This is a \\${skipped} variable"));
        assertEquals("null ${skipped} variable", parser.parse("${skipped} \\${skipped} variable"));
        assertEquals("The null is ${skipped} variable", parser.parse("The ${skipped} is \\${skipped} variable"));
    }

    @Test
    void shouldParseFastOnJdk7u6() {
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
            assertEquals(expected.toString(), parser.parse(input.toString()));
        });
    }
}