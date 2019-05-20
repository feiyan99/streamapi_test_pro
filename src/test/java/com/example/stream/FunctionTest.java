package com.example.stream;

import cn.hutool.core.lang.Console;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.function.Function;
import java.util.stream.Stream;

/**
 * @author zlf
 * @Description TODO
 * @createTime 2019年05月18日 16:50:00
 */
@SpringBootTest
public class FunctionTest {

    Stream<String> stream = Stream.of("I", "LOVE", "YOU", "TOO");

    /*
        Function<T, R>  T 为入参泛型， R为出参泛型
     */
    final Function<String, String> concat = x -> x.concat("TEST");

    // 入参为字符，，出参为该字符的长度 ，类型转变
    final Function<String, Integer> convert = String::length;

    @Test
    public void applyTest() {
        String apply = concat.apply("HELLO_");
        //先执行 concat 再执行 convert
        Integer andThen = concat.andThen(convert).apply("HELLO_");

        // 和andThen 执行顺序相反 注意调用者顺序。。。。
        Integer compose = convert.compose(concat).apply("注意顺序");

        Console.log("compose [{}]", compose);
        Console.log("apply [{}]", apply);
        Console.log("andThen [{}]", andThen);
    }

}
