package com.chapter3;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.oracle.jrockit.jfr.Producer;
import org.junit.Assert;
import static java.util.stream.Collectors.toList;
import java.util.function.Function;

public class StreamDemo {
    public void main1() {
        //collect
        List<String> collected = Stream.of("a","b","c")
                .collect(Collectors.toList());
        Assert.assertEquals(Arrays.asList("a","b","c"), collected);
        //map 1.7
        List<String> collected1 = new ArrayList<>();
        for(String s : Arrays.asList("a","b","hello")){
            String uppercaseString = s.toUpperCase();
            collected1.add(uppercaseString);
        }
        Assert.assertEquals(Arrays.asList("A","B","HELLO"), collected1);

        //map 1.8
        List<String> collected11 = Stream.of("a","b","hello")
                .map(s -> s.toUpperCase()).collect(Collectors.toList());
        Assert.assertEquals(Arrays.asList("A","B","HELLO"), collected11);

        //filter 1.7
        List<String> beginningWithNumbers = new ArrayList<>();
        for (String value : Arrays.asList("a","1abc", "abc1")) {
            if(Character.isDigit(value.charAt(0))){//isDigit() 方法用于判断指定字符是否为数字。
                beginningWithNumbers.add(value);
            }
        }
        Assert.assertEquals(Arrays.asList("1abc"), beginningWithNumbers);

        //filter 1.8
        List<String> beginningWithNumbers1 = Stream.of("a","1abc", "abc1")
                .filter(t -> Character.isDigit(t.charAt(0)))
                .collect(Collectors.toList());
        Assert.assertEquals(Arrays.asList("1abc"), beginningWithNumbers1);

        //flatMap
        List<Integer> together = Stream.of(Arrays.asList(1,2), Arrays.asList(3,4))
                .flatMap(t -> t.stream())
                .collect(Collectors.toList());
        Assert.assertEquals(Arrays.asList(1,2,3,4), together);


        //max    min    1.8
        List<Track> tracks = Arrays.asList(
                new Track("Bakai",524),
                new Track("Violets for Your Furs",378),
                new Track("Time Was",451));
        Track theTrack = tracks.stream()
                .min(Comparator.comparing(t -> t.getTrackLength()))
                .get();
        Assert.assertEquals(tracks.get(1), theTrack);

        //max   min     1.7
        List<Track> tracks1 = Arrays.asList(
                new Track("Bakai",524),
                new Track("Violets for Your Furs",378),
                new Track("Time Was",451));
        Track shortTrack = tracks.get(0);
        for(Track t : tracks) {
            if(t.getTrackLength() < shortTrack.getTrackLength()) {
                shortTrack = t;
            }
        }
        Assert.assertEquals(tracks.get(1), shortTrack);


        //reduce
        int count = Stream.of(1,2,3)
                .reduce(0, (accumulator, element) -> accumulator + element);
        Assert.assertEquals(6, count);

        //展开reduce
        BinaryOperator<Integer> accumulator = (acc, element) -> acc + element;
        int count1  = accumulator.apply(
                accumulator.apply(
                        accumulator.apply(0,1),2
                ),3
        );

        //找出某张专辑中所有乐队的国籍
        Album album = new Album();
        Set<String> origins = album.getMusicians()    //获取专辑中所有的表演者
                .filter(artist -> artist.getName().startsWith("The"))   //对表演者过滤，只剩下乐队  一般认为乐队以The开头
                .map(artist -> artist.getOrigin())  //将表演者映射为表演者的国籍
                .collect(Collectors.toSet());   //将国籍放入集合

    }
    //遗留代码：找出长度大于1分钟的曲目
    public Set<String> findLongTracks(List<Album> albums) {
        Set<String> trackNames = new HashSet<>();
        for(Album album : albums) {
            for(Track track : album.getTracks().collect(Collectors.toList())){
                if(track.getTrackLength() > 60) {
                    String name = track.getName();
                    trackNames.add(name);
                }
            }
        }
        return trackNames;
    }

    //重构    注意：流重构代码方式很多，这里只是一种，
    //第一步重构：Stream的forEach方法替换掉for循环，但还保留原来循环体中的代码（很重要的技巧）

    public Set<String> findLongTracks1(List<Album> albums) {
        Set<String> trackNames = new HashSet<>();

        albums.stream().forEach(album -> {
            album.getTracks().forEach(track -> {
                if(track.getTrackLength() > 60) {
                    String name = track.getName();
                    trackNames.add(name);
                }
            });
        });
        return trackNames;
    }

    //第二步重构：将内部forEach拆分
    public Set<String> findLongTracks2(List<Album> albums) {
        Set<String> trackNames = new HashSet<>();

        albums.stream().forEach(album -> {
            album.getTracks()
                    .filter(t -> t.getTrackLength()>60)
                    .map(t -> t.getName())
                    .forEach(t -> trackNames.add(t));
        });
        return trackNames;
    }
    //第三步重构：将专辑转化为曲目Stream，利用比map更复杂的flatMap，把多个流合并成一个并返回
    public Set<String> findLongTracks3(List<Album> albums) {
        Set<String> trackNames = new HashSet<>();

        albums.stream()
                .flatMap(album -> album.getTracks())
                .filter(t -> t.getTrackLength()>60)
                .map(t -> t.getName())
                .forEach(t -> trackNames.add(t));
        return trackNames;
    }

    //第四步重构：我们不希望单独创建Set集合，而是希望在流中解决，将forEach方法替换为collect方法，并且删掉变量trackNames。
    public Set<String> findLongTracks4(List<Album> albums) {
        return albums.stream()
                .flatMap(album -> album.getTracks())
                .filter(t -> t.getTrackLength()>60)
                .map(t -> t.getName())
                .collect(Collectors.toSet());
    }

    //练习
    //编写一个求和函数
    public int addUp(Stream<Integer> numbers){
        return numbers.reduce(0, (a,b) -> a+b);
    }
    //艺术家列表作为参数，艺术家名称和国籍为返回的字符串列表
    public List<String> testb(List<Artist> artist) {
        return artist.stream().flatMap(t -> Stream.of(t.getName(),t.getOrigin())).collect(toList());
    }

    //接收专辑列表作为参数，返回一个由最多包含3首歌曲的专辑组成的列表
    public List<Album> testc(List<Album> album) {
        //return album.stream().filter(t -> t.getTracks().count() <= 3).collect(toList());
        return album.stream().filter(t -> t.getTracks().collect(toList()).size() <= 3).collect(toList());
    }
    //迭代   修改下面代码  将外部迭代转化为内部迭代
    public void testD(List<Artist> artists) {
        //外部迭代
        int totalMembers = 0;
        for(Artist artist : artists) {
            Stream<Artist> members = artist.getMembersStream();
            totalMembers += members.count();
        }

        //内部迭代
       // artists.stream().flatMap(t -> t.getMembersStream()).count();
        int result = artists.stream().map(t -> t.getMembersStream().count()).reduce(0L, Long::sum).intValue();
    }

    //计算一个字符串中小写字母的个数
    public static int lowCount(String string){
        return (int)string.chars().filter(Character::isLowerCase).count();
    }


    //在一个字符串列表中，找出包含最多小写字母的字符串 ,对于空列表返回Optional<String>对象
    public String lotLowString(List<String> strings){
        String ss = strings.get(0);
        for(String s : strings) {
            int size = (int)s.chars().filter(Character::isLowerCase).count();
            if(size > ss.length()){
                ss = s;
            }
        }
        return ss;
    }
    public Optional<String> lotLowStringStream1(List<String> strings){
        return strings.stream().max(Comparator.comparing(t -> t.chars().filter(Character::isLowerCase).count()));
    }
    public Optional<String> lotLowStringStream2(List<String> strings){
        return strings.stream().max(Comparator.comparing(StreamDemo::lowCount));//类：：方法
    }
//    PECS原则
//    最后看一下什么是PECS（Producer Extends Consumer Super）原则，已经很好理解了：
//    频繁往外读取内容的，适合用上界Extends。
//    经常往里插入的，适合用下界Super。
//    extends 可用于返回类型限定，不能用于参数类型限定（换句话说：? extends xxx 只能用于方法返回类型限定，jdk能够确定此类的最小继承边界为xxx，只要是这个类的父类都能接收，但是传入参数无法确定具体类型，只能接受null的传入）。
//    super 可用于参数类型限定，不能用于返回类型限定（换句话说：? supper xxx 只能用于方法传参，因为jdk能够确定传入为xxx的子类，返回只能用Object类接收）。
//    ? 既不能用于方法参数传入，也不能用于方法返回。

    //进阶练习
    //只用reduce和Lambda表达式写出实现Stream上的map操作的代码，如果不想返回Stream，可以返回List
    public static <T, R> List<R> reduceToMap(Stream<T> stream, Function<? super T, ? extends R> mapper){
//          <R> Stream<R> map(Function<? super T, ? extends R> mapper);

//        给定一个初始值identity，通过累加器accumulator迭代计算，得到一个identity类型的结果，第三个参数用于使用并行流时合并结果
//          <U> U reduce(U identity, BiFunction<U, ? super T, U> accumulator, BinaryOperator<U> combiner);

        return stream.reduce(new ArrayList<>(), (acc, x) -> {
                    List<R> newAcc = new ArrayList<>(acc);
                    newAcc.add(mapper.apply(x));
                    return newAcc;
//                      acc.add(mapper.apply(x));
//                      return acc;
                },(List<R> left, List<R> right) -> {
                    List<R> newLeft = new ArrayList<>(left);
                    newLeft.addAll(right);
                    return newLeft;
//                      left.addAll(right);
//                      return left;
                });
    }


    //只用reduce和Lambda表达式写出实现Stream上的filter操作的代码，如果不想返回Stream，可以返回List
    public static <T> List<T> reduceToFilter(Stream<T> stream, Predicate<? super T> predicate){
        return stream.reduce(new ArrayList<>(), (List<T> acc, T ele) -> {
            if(predicate.test(ele)){
                List<T> accNew = new ArrayList<>(acc);
                accNew.add(ele);
                return accNew;
            }else{
                return acc;
            }
        }, (List<T> left, List<T> right) -> {
            List<T> newLeft = new ArrayList<>(left);
            newLeft.addAll(right);
            return newLeft;
        });
    }
}
