package com.god.dragon.utils.convert;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wuyuxiang
 * @version 1.0.0
 * @package com.god.dragon.utils.convert
 * @date 2023/2/8 16:14
 */
public class TestBean {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static void main(String[] args) {
        Map a = new HashMap();
        a.put("name1", "123456");
        TestBean testBean = BeanUtils2.map2Bean(a, TestBean.class,(src, target) -> {((TestBean)target).setName("123456");});
        System.out.println(testBean.getName());
    }
}
