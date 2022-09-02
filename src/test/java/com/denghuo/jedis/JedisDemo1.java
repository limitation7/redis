package com.denghuo.jedis;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;

import java.util.*;
import java.util.function.BiConsumer;

public class JedisDemo1 {
    Jedis jedis=new Jedis("43.143.49.15",6379);
    //操作key String
    @Test
    public void demo1(){
        jedis.set("name","lucy");
        jedis.mset("k1","v1","k2","v2");
        System.out.println("jedis.mget(\"k1\",\"k2\") = " + jedis.mget("k1", "k2"));
        //获取
        System.out.println("jedis.get(\"name\") = " + jedis.get("name"));

        Set<String> keys = jedis.keys("*");
        keys.forEach(System.out::println);
    }
    //操作list
    @Test
    public void demo2(){
        jedis.lpush("key1","lucy","merry","jack");
        List<String> key1 = jedis.lrange("key1", 0, -1);
        System.out.println(key1);
    }

    //操作set
    @Test
    public void demo3(){
        jedis.sadd("name","lucy","jack");
        Set<String> name = jedis.smembers("name");
        name.forEach(System.out::println);
    }

    //操作hash
    @Test
    public void demo4(){
        Map<String, String> hm = new HashMap<>();
        hm.put("sttt","aa");
        jedis.hset("stu",hm);
        jedis.hset("stu","age","11");

        Map<String, String> stu = jedis.hgetAll("stu");
        stu.forEach(new BiConsumer<String, String>() {
            @Override
            public void accept(String s, String s2) {
                System.out.println("field:"+s+"  val:"+s2);
            }
        });

    }
    //操作zset
    @Test
    public void demo5(){
        jedis.zadd("china",100,"shanghai");
        jedis.zadd("china",200,"beijing");
        jedis.zadd("china",300,"jiangxi");
        Set<String> china = jedis.zrange("china", 0, -1);
        for (String s : china) {
            System.out.println(s);
        }
    }


}
