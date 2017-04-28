package cn.org.xiwi.springboot.mangodb.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import cn.org.xiwi.springboot.mangodb.bean.DemoInfo;

/**
 * 继承自MongoRepository接口，MongoRepository接口包含了常用的CRUD操作，
 * 例如：save、insert、findall等等。我们可以定义自己的查找接口，
 * 例如根据demoInfo的name搜索，具体的DemoInfoRepository接口代码如下：
 * @author Angel --守护天使
 * @version v.0.1
 * @date 2016年8月18日下午3:15:49
 */
public interface  DemoInfoRepository  extends MongoRepository<DemoInfo, String> {
    DemoInfo findByName(String name);
}
