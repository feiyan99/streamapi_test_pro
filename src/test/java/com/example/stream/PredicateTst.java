package com.example.stream;

import cn.hutool.core.lang.Console;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.function.Predicate;

/**
 * @author zlf
 * @Description TODO
 * @createTime 2019年05月18日 17:14:00
 */
@SpringBootTest
public class PredicateTst {
    Predicate<Integer> p1 = (x) -> x > 5;

    Predicate<Integer> p2 = (x) -> x > 3;



    @Test
    public void test() {
        Console.log("p1 {}",p1.test(6));
        // negate like !
        Console.log("p1 negate {}",p1.negate().test(6));
        // and like &&
        Console.log("p1 and p2 {}", p1.and(p2).test(5));
        // or like ||
        Console.log("p1 or p2 {}", p1.or(p2).test(6));
    }
}
