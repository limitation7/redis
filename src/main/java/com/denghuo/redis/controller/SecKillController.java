package com.denghuo.redis.controller;

import com.denghuo.redis.service.SecKill_redisByScript;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.util.Map;
import java.util.function.BiConsumer;

@Controller
public class SecKillController {
    @RequestMapping(value = "/index",method = RequestMethod.GET)
    public String secKill(){
        return "index";
    }
    @Autowired
    SecKill_redisByScript secKill_redisByScript;

    @RequestMapping(value = "/kill",method = RequestMethod.GET)
    public String doKillController(@RequestParam Map<String,String> map) throws IOException {
        String pid = map.get("pid");
        secKill_redisByScript.doSecKill("123",pid);
        return "redirect:index";
    }
    @Autowired
    RedisTemplate redisTemplate;

    @RequestMapping(value = "/putPackage",method = RequestMethod.POST)
    public String doAdd(@RequestParam Map<String,Object> map) {
        String pid = (String) map.get("pid");
        String count = (String) map.get("count");
        redisTemplate.opsForValue().set("sk:"+pid+":qt",count);
        return "redirect:index";
    }
}
