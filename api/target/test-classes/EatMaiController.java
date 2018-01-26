/**
 * 456
 * */
package com.qq.act2016.eatmai;

import com.qq.act.com.exception.EnumActExceptionCode;
import com.qq.act.com.module.ExchangeModule;
import com.qq.act.com.module.ExecuteConditionModule;
import com.qq.act.com.tools.ActGoodsTool;
import com.qq.act2015.common.pojo.result.ResultMapVO;
import com.qq.act2016.eatmai.pojo.AppleGiftVO;
import com.qq.act2016.eatmai.service.EatMaiConf;
import com.qq.act2016.eatmai.service.EatMaiHandle;
import com.qq.act2017.common.client.SimpleActController;
import com.qq.act2017.common.exc.ActException;
import com.qq.user.info.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * #API贪吃的小麦子
 *
 * @author j.chen@91kge.com
 * create on 2017/8/9
 */
@RestController
@RequestMapping("/act2017/08/eatmai")
public class EatMaiController extends SimpleActController<EatMaiHandle, EatMaiConf> {

    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private ExchangeModule exchangeModule;
    @Autowired
    private ExecuteConditionModule executeConditionModule;
    @Autowired
    private StringRedisTemplate template;


    /**
     * 主界面
     *
     * @author j.chen@91kge.com
     * @return ResultMap
     */
    @RequestMapping("/getDetail.get")
    public ResultMapVO getDetail(HttpServletRequest request) {
        return doController(request, user -> {
            ResultMapVO ret = getInitResultMap();
            ret.addTimer(conf.getEvents());
            //int(0-100) 拥有的苹果数量
            ret.addProp("num", exchangeModule.getUserGoodsNum(user.getUserId(), conf.getEvents().getAppleId()));
            //int(0-100) 拥有的kb数量
            ret.addProp("kcoins", userInfoService.getUserMoneyKcoin(user.getUserId()));

            //[{goodsId:int,numUnit:int,index:int,remain:int 剩余数量,condition:int 需要消费的金额,costs:{kcoins:int 消耗的kb,num:int 消耗的苹果}}]
            ret.addProp("list", ActGoodsTool.goodsWithIndexList2MapList(conf.getEvents().getList(), (m, t) -> {
                if (t.getLimit() > 0) {
                    m.put("remain", handle.getRemainNum(t));
                }
                if (t.getCondition() > 0) {
                    m.put("condition", t.getCondition());
                }
                Map<String, Object> costs = new HashMap<>();
                t.getCosts().forEach(s -> {
                    if (s.getCostId() == -1) {
                        costs.put("kcoins", s.getNum());
                    } else if (s.getCostId() == conf.getEvents().getAppleId()) {
                        costs.put("num", s.getNum());
                    }
                });
                m.put("costs", costs);
            }));
            return ret;
        });
    }

    /**
     * 兑换
     *
     * @param index 兑换的序号
     * @code  9010032 消费不足
     * @code 9010039 余量不足
     * @code 9010071 材料不足
     * @author j.chen@91kge.com
     */
    @RequestMapping("/exchange.do")
    public ResultMapVO exchange(@RequestParam("index") int index, HttpServletRequest request) {
        return doController(request, user -> {
            ResultMapVO ret = getActOnResultMap();
            AppleGiftVO giftVO = conf.getAppleGiftByIndex(index);
            if (giftVO.getCondition() > 0) {
                //需要消费
                Double value = template.boundZSetOps(conf.keyUserConsumeNum()).score("" + user.getUserId());
                if (value == null || value < giftVO.getCondition()) {
                    throw new ActException(EnumActExceptionCode.LACK_RIGHT2, "消费不足");
                }

            }


            if (giftVO.getLimit() > 0) {
                //有限制
                executeConditionModule.executeWithLock("sAct:eat:mai:exchange:" + index, () -> handle.getRemainNum(giftVO) > 0, () -> {
                    exchangeModule.exchangeByMultiGoods(user, giftVO, conf.getEventsConfCodeDetail(), "exchange");
                    template.boundZSetOps(conf.keyGiftDrawNum()).incrementScore("" + giftVO.getIndex(), 1);
                    template.boundZSetOps(conf.keyGiftDrawNum()).expireAt(conf.getExpireAt());
                });
            } else {
                exchangeModule.exchangeByMultiGoods(user, giftVO, conf.getEventsConfCodeDetail(), "exchange");
            }


            return ret;
        });
    }
}
