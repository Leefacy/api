/**
 * 123
 * */
package com.teddy.api.clz;

import com.kge.commons.DateUtil;
import com.qq.act.com.exception.EnumActExceptionCode;
import com.qq.act.com.lock.KgeActExecuteRedisLock;
import com.qq.act.com.module.ExchangeModule;
import com.qq.act.com.module.ExecuteConditionModule;
import com.qq.act.com.service.AwardsService;
import com.qq.act.com.tools.ActGoodsTool;
import com.qq.act2015.common.pojo.result.ResultMapVO;
import com.qq.act2016.schoolopen.pojo.EnumScoreType;
import com.qq.act2016.schoolopen.pojo.SchoolOpenGiftVO;
import com.qq.act2016.schoolopen.service.SchoolOpenConf;
import com.qq.act2016.schoolopen.service.SchoolOpenHandle;
import com.qq.act2017.common.client.SimpleActController;
import com.qq.act2017.common.exc.ActException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.*;

/**
 * #API开学积分测试文件
 *
 * @author j.chen@91kge.com create on 2017/8/9
 */
@RestController
@RequestMapping("/act2017/08/schoolopen")
public class SchoolOpenController extends SimpleActController<SchoolOpenHandle, SchoolOpenConf> {

	@Autowired
	private StringRedisTemplate template;
	@Autowired
	private ExchangeModule exchangeModule;
	@Autowired
	private ExecuteConditionModule executeConditionModule;

	public int x = 1;

	/**
	 * 获取主界面
	 *
	 * @return ResultMap
	 * @author j.chen@91kge.com
	 */
	@RequestMapping(value = "/getDetail.get", path = "xyz")
	public ResultMapVO getDetail(HttpServletRequest request) {
		return doController(request, user -> {
			ResultMapVO ret = getInitResultMap();
			ret.addTimer(conf.getEvents());
			// int 每日积分
			ret.addProp("dScore", conf.getEvents().getDailyScore());
			// int(0,1,2) 每日积分领取状态
			ret.addProp("dStatus", handle.getDailyScoreStatus(user.getUserId()));
			/* string(10-12) 每周积分 */
			ret.addProp("wScore", conf.getEvents().getWeekScore());
			// double(10-22) 每周积分领取状态
			ret.addProp("wStatus", handle.getWeekScoreStatus(user.getUserId()));
			ret.addProp("rScore", conf.getEvents().getRechargeScore()); // int
																		// 充值积分
			/* int(0-2) 充值积分领取状态 */
			ret.addProp("rStatus", handle.getRechargeScoreStatus(user.getUserId()));
			// int 我的总积分
			ret.addProp("myScore",
					Optional.ofNullable(
							template.boundZSetOps(conf.keyUserScore()).score("" + user.getUserId()))
							.map(Double::intValue).orElse(0));
			// [{goodsId:int 物品id,numUnit:int 数量,index:int 序号,cost:int
			// 花费,max:int 最大值,remain:int 剩余值}] 可兑换的奖励
			ret.addProp("list", ActGoodsTool
					.goodsWithIndexAndCostList2MapList(conf.getEvents().getGifts(), (m, t) -> {
						if (t.getLimit() > 0) {
							m.put("max", t.getLimit());
							m.put("remain", handle.getRemainNum(t));
						}
					}));
			return ret;
		});
	}

	/**
	 * 领取学分
	 *
	 * @param scoreType
	 *            学分类型 DAILY 每日学分 WEEK 周学分 RECHARGE 充值学分
	 * @author j.chen@91kge.com
	 * @code 9010039 兑换完了
	 * @code 9010071 学分不够
	 */
	@RequestMapping("/drawScore.do")
	public ResultMapVO drawScore(@RequestParam("scoreType") EnumScoreType scoreType,
			HttpServletRequest request) {
		return doController(request, user -> {
			ResultMapVO ret = getActOnResultMap();
			long userId = user.getUserId();
			int status = 0;
			int i = -1;
			if (scoreType == EnumScoreType.DAILY) {
				status = handle.getDailyScoreStatus(userId);
				i = LocalDate.now().getDayOfWeek().getValue() - 1;
			} else if (scoreType == EnumScoreType.WEEK) {
				status = handle.getWeekScoreStatus(userId);
				i = 7;
			} else if (scoreType == EnumScoreType.RECHARGE) {
				status = handle.getRechargeScoreStatus(userId);
			} else {
				throw new ActException(EnumActExceptionCode.ILL_INPUT, "");
			}
			if (status != 1) {
				if (status == 0) {
					throw new ActException(EnumActExceptionCode.LACK_RIGHT1, "不能领取");
				} else {
					throw new ActException(EnumActExceptionCode.REPEAT_OP, "已经领取");
				}
			} else {
				// 增加积分
				template.boundZSetOps(conf.keyUserScore()).incrementScore("" + userId,
						conf.getScore(scoreType));
				template.boundZSetOps(conf.keyUserScore()).expireAt(conf.getExpireAt());
				if (i == -1) {
					template.boundHashOps(conf.keyUserDrawRechargeScoreStatus())
							.put(userId + ":" + DateUtil.getDaysOfToday(), "1");
					template.boundHashOps(conf.keyUserDrawRechargeScoreStatus())
							.expireAt(conf.getExpireAt());
				} else {
					template.boundHashOps(conf.keyUserWeekDrawDaily()).increment("" + userId,
							1 << i);
					template.boundHashOps(conf.keyUserWeekDrawDaily()).expireAt(conf.getExpireAt());
				}
			}
			return ret;
		});
	}

	/**
	 * 兑换商品
	 *
	 * @param index
	 *            兑换的物品序号
	 * @param num
	 *            兑换的数量
	 * @author j.chen@91kge.com
	 * @code 9010031 条件不满足 不能领取
	 * @code 9010041 已经领取
	 */
	@RequestMapping("/exchange.do")
	public ResultMapVO exchange(@RequestParam("index") int index, @RequestParam("n") int num,
			HttpServletRequest request) {
		return doController(request, user -> {
			ResultMapVO ret = getActOnResultMap();
			SchoolOpenGiftVO giftVO = conf.getGiftByIndex(index);
			if (giftVO.getLimit() > 0) {
				executeConditionModule.executeWithLock("sAct:school:open:exchang:" + index,
						() -> handle.getRemainNum(giftVO) > 0, () -> {
							exchangeModule.exchangeBySingleScore(user.getUserId(), giftVO,
									conf.keyUserScore(), conf.getEventsConfCodeDetail(),
									"exchange");
							template.boundZSetOps(conf.keyGiftDrawNum())
									.incrementScore("" + giftVO.getIndex(), 1);
							template.boundZSetOps(conf.keyGiftDrawNum())
									.expireAt(conf.getExpireAt());
						});

			} else {
				// 直接兑换
				exchangeModule.exchangeBySingleScore(user.getUserId(), giftVO, conf.keyUserScore(),
						conf.getEventsConfCodeDetail(), "exchange");
			}
			return ret;
		});
	}

}
