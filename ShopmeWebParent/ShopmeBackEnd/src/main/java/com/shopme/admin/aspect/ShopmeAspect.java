package com.shopme.admin.aspect;

import com.shopme.admin.utils.Log;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ShopmeAspect {

    //@Pointcut("execution(* com.shopme.admin.controller.MainController.*(..))")
    @Pointcut("execution(* com.shopme.admin.controller.*.*(..))")
    private void controllerPointcut() {}

    @Before("controllerPointcut()")
    public void beforeAdvice() {
        Log.info("== visiting controller ==");
    }
}
