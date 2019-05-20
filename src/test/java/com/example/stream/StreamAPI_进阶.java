package com.example.stream;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Console;
import com.example.stream.entity.Student;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.*;

//@RunWith(SpringRunner.class)
@SpringBootTest
public class StreamAPI_进阶 {
    Stream<String> stream = Stream.of("I", "love", "you", "too");

    final int PASS_THRESHOLD = 18;
    final Consumer<Map.Entry> MAP_FORMAT_PRINT = (Map.Entry e) -> Console.log("k[{}] v[{}]", e.getKey(), e.getValue());

    /*
        reduce操作可以实现从一组元素中生成一个值，sum()、max()、min()、count()等都是reduce操作，
        将他们单独设为函数只是因为常用。reduce()的方法定义有三种重写形式：

        Optional<T> reduce(BinaryOperator<T> accumulator)
        T reduce(T identity, BinaryOperator<T> accumulator)
        <U> U reduce(U identity, BiFunction<U,? super T,U> accumulator, BinaryOperator<U> combiner)

        虽然函数定义越来越长，但语义不曾改变，多的参数只是为了指明初始值（参数identity），
        或者是指定并行执行时多个部分结果的合并方式（参数combiner）。reduce()最常用的场景就是从一堆值中生成一个值。
        用这么复杂的函数去求一个最大或最小值，你是不是觉得设计者有病。其实不然，因为“大”和“小”或者“求和"有时会有不同的语义。

        需求：从一组单词中找出最长的单词。这里“大”的含义就是“长”。
     */
    @Test
    public void reduceTest() {
        Optional<String> longest = stream.reduce((s1, s2) -> s1.length() >= s2.length() ? s1 : s2);
        //Optional<String> longest = stream.max((s1, s2) -> s1.length()-s2.length());
        System.out.println(longest.get());
    }

    @Test
    public void reduceTest2() {
        // 求单词长度之和
        Stream<String> stream = Stream.of("I", "love", "you", "too");
        Integer lengthSum = stream.reduce(0, //初始值      (1)
                (sum, str) -> sum+str.length(), // 累加器         (2)
                (a, b) -> a+b); // 部分和拼接器，并行执行时才会用到    (3)
        // int lengthSum = stream.mapToInt(str -> str.length()).sum();
        System.out.println(lengthSum);
    }

    /*
        收集器（Collector）是为Stream.collect()方法量身打造的工具接口（类）。考虑一下将一个Stream转换成一个容器（或者Map）需要做哪些工作？我们至少需要两样东西：

        目标容器是什么？是ArrayList还是HashSet，或者是个TreeMap。
        新元素如何添加到容器中？是List.add()还是Map.put()。
        如果并行的进行规约，还需要告诉collect() 3. 多个部分结果如何合并成一个。

        结合以上分析，collect()方法定义为
        <R> R collect(Supplier<R> supplier, BiConsumer<R,? super T> accumulator, BiConsumer<R,R> combiner)，
        三个参数依次对应上述三条分析。不过每次调用collect()都要传入这三个参数太麻烦，
        收集器Collector就是对这三个参数的简单封装,所以collect()的另一定义为
        <R,A> R collect(Collector<? super T,A,R> collector)。
        Collectors工具类可通过静态方法生成各种常用的Collector。
        举例来说，如果要将Stream规约成List可以通过如下两种方式实现：
        //　将Stream规约成List
        Stream<String> stream = Stream.of("I", "love", "you", "too");
        List<String> list = stream.collect(ArrayList::new, ArrayList::add, ArrayList::addAll);// 方式１
        //List<String> list = stream.collect(Collectors.toList());// 方式2
        System.out.println(list);

        通常情况下我们不需要手动指定collect()的三个参数，
        而是调用collect(Collector<? super T,A,R> collector)方法，
        并且参数中的Collector对象大都是直接通过Collectors工具类获得。
        实际上传入的收集器的行为决定了collect()的行为。
     */

    @Test
    public void collectTest1() {
        ArrayList<Student> students = getStudents();
        students.stream()
                .collect(toMap(s -> s.getId(), identity())) // 转换map 指定生成key的方式， 指定生成value的方式
                .entrySet().forEach(MAP_FORMAT_PRINT);
    }

    /*
        使用partitioningBy()生成的收集器，
        这种情况适用于将Stream中的元素依据某个二值逻辑（满足条件，或不满足）分成互补相交的两部分，比如男女性别、成绩及格与否等。
     */
    @Test
    public void collectTest2() {
        ArrayList<Student> students = getStudents();
        students.stream()                         // 分区 定义条件
                .collect(partitioningBy(s -> s.getAge() >= PASS_THRESHOLD))
                .entrySet().forEach(MAP_FORMAT_PRINT);
    }


    /*
        使用groupingBy()生成的收集器，这是比较灵活的一种情况。
        跟SQL中的group by语句类似，这里的groupingBy()也是按照某个属性对数据进行分组，
        属性相同的元素会被对应到Map的同一个key上。下列代码展示将员工按照部门进行分组
     */
    @Test
    public void collectTest3() {
        ArrayList<Student> students = getStudents();
        students.stream()                         // 分组条件 类似 group by
                .collect(groupingBy(Student::getAge))
                .entrySet().forEach(MAP_FORMAT_PRINT);
    }

    /*
        Test3只是分组的最基本用法，有些时候仅仅分组是不够的。
        在SQL中使用group by是为了协助其他查询，比如
        1. 先将员工按照部门分组，2. 然后统计每个部门员工的人数。
        Java类库设计者也考虑到了这种情况，增强版的groupingBy()能够满足这种需求。
        增强版的groupingBy()允许我们对元素分组之后再执行某种运算，
        比如求和、计数、平均值、类型转换等。这种先将元素分组的收集器叫做上游收集器，
        之后执行其他运算的收集器叫做下游收集器(downstream Collector)。
     */
    @Test
    public void collectTest4() {
        ArrayList<Student> students = getStudents();
        students.stream()                         // 分组条件 类似 group by
                .collect(groupingBy(Student::getAge, counting()))
                .entrySet().forEach(MAP_FORMAT_PRINT);
    }

    private ArrayList<Student> getStudents() {
        Student s1 = new Student(UUID.randomUUID().toString(),"张三", 16);
        Student s2 = new Student(UUID.randomUUID().toString(),"李四", 17);
        Student s3 = new Student(UUID.randomUUID().toString(),"王五", 18);
        Student s4 = new Student(UUID.randomUUID().toString(),"薛二麻子", 19);
        return CollUtil.newArrayList(s1, s2, s3, s4);
    }

}