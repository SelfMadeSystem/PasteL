package com.ihl.client.event;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EventHandler {

    Class<? extends Event>[] events() default {};

}
