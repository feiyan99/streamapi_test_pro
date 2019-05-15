package com.example.stream;

import cn.hutool.core.collection.CollUtil;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

//@RunWith(SpringRunner.class)
@SpringBootTest
public class StreamAPI_入门 {
    Stream<String> stream = Stream.of("I", "love", "you", "too");

    @Test
    public void forEachTest() {
        // 使用Stream.forEach()迭代
        stream.forEach(str -> System.out.println(str));
    }

    @Test
    public void filterTest() {
        // 保留长度等于3的字符串
        stream.filter(str -> str.length()==3)
                .forEach(str -> System.out.println(str));
    }

    /*
        sorted()
        排序函数有两个，一个是用自然顺序排序，一个是使用自定义比较器排序，函数原型分别为
        Stream<T>　sorted() 和 Stream<T>　sorted(Comparator<? super T> comparator)。
     */
    @Test
    public void sortedTest() {
        stream.sorted((str1, str2) -> str1.length()-str2.length())
                .forEach(str -> System.out.println(str));
    }

    /*
        函数原型为Stream<T> distinct()，作用是返回一个去除重复元素之后的Stream。
     */
    @Test
    public void distinctTest() {
        Stream<String> stream= Stream.of("I", "love", "you", "too", "too");
        stream.distinct()
                .forEach(str -> System.out.println(str));
    }

    /*
        函数原型为<R> Stream<R> map(Function<? super T,? extends R> mapper)，
        作用是返回一个对当前所有元素执行执行mapper之后的结果组成的Stream。
        直观的说，就是对每个元素按照某种操作进行转换，
        **转换前后Stream中元素的个数不会改变** ，但元素的类型取决于转换之后的类型。
     */
    @Test
    public void mapTest() {
        stream.map(str -> str.toUpperCase()) // 转大写
                .forEach(str -> System.out.println(str));
    }

    /*
        函数原型为<R> Stream<R> flatMap(Function<? super T,? extends Stream<? extends R>> mapper)，
        作用是对每个元素执行mapper指定的操作，并用所有mapper返回的Stream中的元素组成一个新的Stream作为最终返回结果。
        说起来太拗口，通俗的讲flatMap()的作用就相当于把原stream中的所有元素都"摊平"之后组成的Stream，
        转换前后元素的个数和类型都可能会改变。
     */
    @Test
    public void flatMapTest() {
//        Stream<List<Integer>> stream = Stream.of(Arrays.asList(1,2), Arrays.asList(3, 4, 5));
//        stream.flatMap(list -> list.stream())
//                .forEach(i -> System.out.println(i));

        List<Integer> inner1 = CollUtil.newArrayList(1);
        List<Integer> inner2 = CollUtil.newArrayList(2);
        List<Integer> inner3 = CollUtil.newArrayList(3);
        List<Integer> inner4 = CollUtil.newArrayList(4);
        List<Integer> inner5 = CollUtil.newArrayList(5);

        List<List<Integer>> outer = CollUtil.newArrayList(inner1, inner2, inner3, inner4, inner5);
                                                                                // 在原值基础上+2
        List<Integer> result = outer.stream().flatMap(inner -> inner.stream().map(i -> i + 2)).collect(toList());
        result.forEach(System.out::print);
    }
}
