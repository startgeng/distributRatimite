package com.kevin.ratemlimiter.annotion;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * 切面
 * @author kevin
 * @version 1.0
 * @date 2021-07-03 0:40
 */
@Aspect
@Slf4j
@Component
public class AccessLimiterAspect {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisScript<Boolean> rateLimitLua;

    @Pointcut("@annotation(com.kevin.ratemlimiter.annotion.AccesstionLimit)")
    public void cut(){
        log.info("cut");
    }

    @Before("cut()")
    public void before(JoinPoint joinPoint){
        //获得方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        AccesstionLimit annotation = method.getAnnotation(AccesstionLimit.class);
        if (annotation == null){
            return;
        }
        String key = annotation.methodKey();
        Integer limit = annotation.limit();
        //如果没谁知Key 从调用方法生成key
        if (StringUtils.isEmpty(key)){
            Class<?>[] type = method.getParameterTypes();
            key = method.getName();
            if (type != null){
                String paramType = Arrays.stream(type)
                        .map(Class::getName)
                        .collect(Collectors.joining(","));
                log.info("param type");
                key += "#" + paramType;
            }
        }
        // 2. 调用Redis
        boolean acquired = stringRedisTemplate.execute(
                rateLimitLua, // Lua script的真身
                Lists.newArrayList(key), // Lua脚本中的Key列表
                limit.toString() // Lua脚本Value列表
        );

        if (!acquired) {
            log.error("your access is blocked, key={}", key);
            throw new RuntimeException("Your access is blocked");
        }
    }
}
