package org.seckill.web;

import java.util.Date;
import java.util.List;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.dto.SeckillResult;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.nums.SeckillStatEnum;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller //把当前controller放入spring容器中
@RequestMapping("/seckill")//url:模块/资源/{id}/...
public class SeckillController {
  //日志对象
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  @Autowired
  private SeckillService seckillService;

  //获取列表页
  @RequestMapping(value = "/list", method = RequestMethod.GET)
  public String list(Model model) {
      List<Seckill> list = seckillService.getSeckillList();
      model.addAttribute("list", list);
      System.out.println(seckillService.getSeckillList());
      return "list";
  }
  /*
   * [Seckill [seckillId=1010, name=40元秒杀苹果3, number=19, startTime=Tue Oct 08 21:14:00 CST 2019, endTime=Fri Oct 11 08:00:00 CST 2019, createTime=Wed Oct 09 05:12:25 CST 2019], 
   * Seckill [seckillId=1009, name=40元秒杀苹果2, number=19, startTime=Tue Oct 08 21:20:00 CST 2019, endTime=Fri Oct 11 08:00:00 CST 2019, createTime=Wed Oct 09 05:10:02 CST 2019], 
   * Seckill [seckillId=1008, name=40元秒杀苹果1, number=20, startTime=Tue Oct 08 22:30:00 CST 2019, endTime=Fri Oct 11 08:00:00 CST 2019, createTime=Wed Oct 09 05:09:31 CST 2019], 
   * Seckill [seckillId=1001, name=500元秒杀无人机, number=10, startTime=Wed Oct 09 09:00:00 CST 2019, endTime=Fri Oct 11 08:00:00 CST 2019, createTime=Wed Oct 09 04:06:31 CST 2019]]
   */

  //获取详情页
  @RequestMapping(value = "/{seckillId}/detail", method = RequestMethod.GET)
  //@PathVariable注解用来把请求路径中的seckillId赋值给形参
  public String detail(@PathVariable("seckillId") Long seckillId, Model model) {
      if (seckillId == null) {
          return "redirect:/seckill/list";//重定向：客户端访问由服务器返回的url，因此地址栏会变化
      }
      Seckill seckill = seckillService.getById(seckillId);
      if (seckill == null) {
          return "forward:/seckill/list";//转发：服务器自行访问url，地址栏不变化，数据不会丢失
      }
      model.addAttribute("seckill", seckill);
      return "detail";
  }

  //暴露地址  ajax,json
  @RequestMapping(value = "/{seckillId}/exposer", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
  @ResponseBody
  public SeckillResult<Exposer> exposer(@PathVariable("seckillId") Long seckillId) {

      SeckillResult<Exposer> result;
      try {
          Exposer exposer = seckillService.exportSeckillUrl(seckillId);
          result = new SeckillResult<Exposer>(true, exposer);
      } catch (Exception e) {
          logger.error(e.getMessage(), e);
          result = new SeckillResult<Exposer>(false, e.getMessage());
      }
      return result;
  }

  //执行秒杀
  @RequestMapping(value = "/{seckillId}/{md5}/execution", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
  @ResponseBody
  public SeckillResult<SeckillExecution> execute(@PathVariable("seckillId") Long seckillId,@PathVariable("md5") String md5,@CookieValue(value = "killPhone", required = false) Long phone) {

      if (phone == null) {
          return new SeckillResult<SeckillExecution>(false, "未注册");
      }
      SeckillResult<SeckillExecution> result;
      try {
    	  //存储过程调用
          SeckillExecution execution = seckillService.executeSeckillProcedure(seckillId, phone, md5);
          return new SeckillResult<SeckillExecution>(true, execution);
      } catch (RepeatKillException e) {
          SeckillExecution execution = new SeckillExecution(seckillId, SeckillStatEnum.REPEAT_KILL);
          return new SeckillResult<SeckillExecution>(true, execution);
      } catch (SeckillCloseException e) {
          SeckillExecution execution = new SeckillExecution(seckillId, SeckillStatEnum.END);
          return new SeckillResult<SeckillExecution>(true, execution);
      } catch (Exception e) {
          logger.error(e.getMessage(), e);
          SeckillExecution execution = new SeckillExecution(seckillId, SeckillStatEnum.INNER_ERROR);
          return new SeckillResult<SeckillExecution>(true, execution);
      }
  }

  @RequestMapping(value = "/time/now", method = RequestMethod.GET)
  @ResponseBody
  public SeckillResult<Long> time() {
      Date now = new Date();
      return new SeckillResult<Long>(true, now.getTime());
  }
}
