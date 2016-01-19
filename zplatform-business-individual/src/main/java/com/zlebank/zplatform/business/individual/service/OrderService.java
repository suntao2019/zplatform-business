package com.zlebank.zplatform.business.individual.service;

import java.util.Date;

import com.zlebank.zplatform.business.individual.bean.Order;

public interface OrderService {
	/**
	 * 订单查询列表
	 * @param memberId 会员号
	 * @param startDate 开始日期
	 * @param endDate 结束日期
	 * @param page 页数
	 * @param pageSize 当前页行数
	 * @return
	 */
	public Object queryOrderList(String memberId,Date startDate,Date endDate,int page,int pageSize);
	/**
	 * 订单明细信息
	 * @param memberId
	 * @param orderNo
	 * @return
	 */
	public Order queryOrder(String memberId,String orderNo);
	/**
	 * 生成消费订单
	 * @param order
	 * @return tn
	 */
	public String createOrder(Order order);
	
	/**
	 * 订单支付
	 * @param order 订单信息
	 * @param smsCode 短信验证码
	 * @param payPwd 支付密码
	 * @return
	 */
	public String pay(Order order,String smsCode,String payPwd);
}
