package cn.org.xiwi.springboot.mangodb.control;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.org.xiwi.springboot.mangodb.bean.DemoInfo;
import cn.org.xiwi.springboot.mangodb.repository.DemoInfoRepository;
 
/**
 *
 * @author Angel --守护天使
 * @version v.0.1
 * @date 2016年8月18日下午8:49:35
 */
@RestController
public class DemoController {
   
    @Autowired
    private DemoInfoRepository demoInfoRepository;
   
    @RequestMapping("save")
    public String save(){
       DemoInfo demoInfo = new DemoInfo();
       demoInfo.setName("张三");
       demoInfo.setAge(20);
       demoInfoRepository.save(demoInfo);
      
       demoInfo = new DemoInfo();
       demoInfo.setName("李四");
       demoInfo.setAge(30);
       demoInfoRepository.save(demoInfo);
      
       return "ok";
    }
   
    @RequestMapping("find")
    public List<DemoInfo> find(){
       return demoInfoRepository.findAll();
    }
   
    @RequestMapping("findByName")
    public DemoInfo findByName(){
       return demoInfoRepository.findByName("张三");
    }
   
}
