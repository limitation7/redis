package com.denghuo.redis.service;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import redis.clients.jedis.*;

import java.io.IOException;
import java.util.*;

@Component
public class SecKill_redisByScript {
	@Autowired
	RedisTemplate redisTemplate;
	public boolean doSecKill(String uid,String prodid) throws IOException {
		if(uid==null||prodid==null){
			return false;
		}
		//库存key
		String kcKey="sk:"+prodid+":qt";
		//秒杀成功用户key
		String userKey="sk:"+prodid+":user";
		//获取库存，如果库存null，秒杀还没开始
		String kc=(String) redisTemplate.opsForValue().get(kcKey);
		if(kc==null){
			System.out.println("秒杀还没开始");
//			jedis.close();
			return false;
		}
//		//判断用户是否重复秒杀
//		if(redisTemplate.opsForSet().isMember(userKey, uid)){
//			System.out.println("已经秒杀成功，不能重复秒杀");
////			jedis.close();
//			return false;
//		}
		//判断如果商品数量，库存数量小于1，秒杀结束
		if(Integer.parseInt(redisTemplate.opsForValue().get(kcKey).toString())<=0){
			System.out.println("秒杀已经结束没有商品");
//			jedis.close();
			return false;
		}
		//秒杀过程
		ArrayList<String> arrayList=new ArrayList<>();
		arrayList.add(kcKey);
		arrayList.add(userKey);
		//开启事务操作
		redisTemplate.setEnableTransactionSupport(true);
		redisTemplate.watch(kcKey);
		redisTemplate.multi();
		//库存-1
		redisTemplate.opsForValue().decrement(kcKey);
		//把秒杀成功用户添加到清单里
//		redisTemplate.opsForSet().add(userKey,uid);

		List<Object> exec = redisTemplate.exec();
		if(exec==null||exec.size()==0){
			System.out.println(exec);
//			System.out.println("秒杀失败");
			return false;
		}
		System.out.println(exec);
//		System.out.println("秒杀成功");
//		jedis.close();
		return true;
	}
	private static final  org.slf4j.Logger logger =LoggerFactory.getLogger(SecKill_redisByScript.class) ;

	public static void main(String[] args) {
		JedisPool jedispool =  JedisPoolUtil.getJedisPoolInstance();
 
		Jedis jedis=jedispool.getResource();
		System.out.println(jedis.ping());
		
		Set<HostAndPort> set=new HashSet<HostAndPort>();

	//	doSecKill("201","sk:0101");
	}
	
	static String secKillScript ="local userid=KEYS[1];\r\n" + 
			"local prodid=KEYS[2];\r\n" + 
			"local qtkey='sk:'..prodid..\":qt\";\r\n" + 
			"local usersKey='sk:'..prodid..\":usr\";\r\n" + 
			"local userExists=redis.call(\"sismember\",usersKey,userid);\r\n" + 
			"if tonumber(userExists)==1 then \r\n" + 
			"   return 2;\r\n" + 
			"end\r\n" + 
			"local num= redis.call(\"get\" ,qtkey);\r\n" + 
			"if tonumber(num)<=0 then \r\n" + 
			"   return 0;\r\n" + 
			"else \r\n" + 
			"   redis.call(\"decr\",qtkey);\r\n" + 
			"   redis.call(\"sadd\",usersKey,userid);\r\n" + 
			"end\r\n" + 
			"return 1" ;
			 
	static String secKillScript2 = 
			"local userExists=redis.call(\"sismember\",\"{sk}:0101:usr\",userid);\r\n" +
			" return 1";


}
