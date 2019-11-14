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

@Controller //�ѵ�ǰcontroller����spring������
@RequestMapping("/seckill")//url:ģ��/��Դ/{id}/...
public class SeckillController {
  //��־����
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  @Autowired
  private SeckillService seckillService;

  //��ȡ�б�ҳ
  @RequestMapping(value = "/list", method = RequestMethod.GET)
  public String list(Model model) {
      List<Seckill> list = seckillService.getSeckillList();
      model.addAttribute("list", list);
      System.out.println(seckillService.getSeckillList());
      return "list";
  }
  /*
   * [Seckill [seckillId=1010, name=40Ԫ��ɱƻ��3, number=19, startTime=Tue Oct 08 21:14:00 CST 2019, endTime=Fri Oct 11 08:00:00 CST 2019, createTime=Wed Oct 09 05:12:25 CST 2019], 
   * Seckill [seckillId=1009, name=40Ԫ��ɱƻ��2, number=19, startTime=Tue Oct 08 21:20:00 CST 2019, endTime=Fri Oct 11 08:00:00 CST 2019, createTime=Wed Oct 09 05:10:02 CST 2019], 
   * Seckill [seckillId=1008, name=40Ԫ��ɱƻ��1, number=20, startTime=Tue Oct 08 22:30:00 CST 2019, endTime=Fri Oct 11 08:00:00 CST 2019, createTime=Wed Oct 09 05:09:31 CST 2019], 
   * Seckill [seckillId=1001, name=500Ԫ��ɱ���˻�, number=10, startTime=Wed Oct 09 09:00:00 CST 2019, endTime=Fri Oct 11 08:00:00 CST 2019, createTime=Wed Oct 09 04:06:31 CST 2019]]
   */

  //��ȡ����ҳ
  @RequestMapping(value = "/{seckillId}/detail", method = RequestMethod.GET)
  //@PathVariableע������������·���е�seckillId��ֵ���β�
  public String detail(@PathVariable("seckillId") Long seckillId, Model model) {
      if (seckillId == null) {
          return "redirect:/seckill/list";//�ض��򣺿ͻ��˷����ɷ��������ص�url����˵�ַ����仯
      }
      Seckill seckill = seckillService.getById(seckillId);
      if (seckill == null) {
          return "forward:/seckill/list";//ת�������������з���url����ַ�����仯�����ݲ��ᶪʧ
      }
      model.addAttribute("seckill", seckill);
      return "detail";
  }

  //��¶��ַ  ajax,json
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

  //ִ����ɱ
  @RequestMapping(value = "/{seckillId}/{md5}/execution", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
  @ResponseBody
  public SeckillResult<SeckillExecution> execute(@PathVariable("seckillId") Long seckillId,@PathVariable("md5") String md5,@CookieValue(value = "killPhone", required = false) Long phone) {

      if (phone == null) {
          return new SeckillResult<SeckillExecution>(false, "δע��");
      }
      SeckillResult<SeckillExecution> result;
      try {
    	  //�洢���̵���
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
