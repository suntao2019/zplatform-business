/* 
 * SMSSendService.java  
 * 
 * version TODO
 *
 * 2016年1月21日 
 * 
 * Copyright (c) 2016,zlebank.All rights reserved.
 * 
 */
package com.zlebank.zplatform.business.individual.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zlebank.zplatform.business.individual.bean.enums.RealNameTypeEnum;
import com.zlebank.zplatform.business.individual.exception.InvalidBindIdException;
import com.zlebank.zplatform.business.individual.service.SmsService;
import com.zlebank.zplatform.commons.bean.CardBin;
import com.zlebank.zplatform.commons.dao.CardBinDao;
import com.zlebank.zplatform.member.bean.QuickpayCustBean;
import com.zlebank.zplatform.member.pojo.PojoCoopInsti;
import com.zlebank.zplatform.member.service.MemberBankCardService;
import com.zlebank.zplatform.sms.pojo.enums.ModuleTypeEnum;
import com.zlebank.zplatform.sms.service.ISMSService;
import com.zlebank.zplatform.trade.bean.ResultBean;
import com.zlebank.zplatform.trade.bean.wap.WapCardBean;
import com.zlebank.zplatform.trade.exception.TradeException;
import com.zlebank.zplatform.trade.model.QuickpayCustModel;
import com.zlebank.zplatform.trade.service.IGateWayService;
import com.zlebank.zplatform.trade.service.IQuickpayCustService;

/**
 * Class Description
 *
 * @author guojia
 * @version
 * @date 2016年1月21日 上午10:57:00
 * @since 
 */
@Service("smsSendService")
public class SMSSendService implements SmsService{
	private static final Log log = LogFactory.getLog(SMSSendService.class);
	@Autowired
	private ISMSService smsSendService;
	@Autowired 
	private IGateWayService gateWayService;
	@Autowired
    private IQuickpayCustService quickpayCustService;
	@Autowired
	private CardBinDao cardBinDao;
	@Autowired
	private MemberBankCardService memberBankCardService;
	/**
	 *
	 * @param memberId
	 * @param phone
	 * @param moduleType
	 * @return
	 */
	@Override
	public boolean sendSmsCode(String memberId, String phone,
			ModuleTypeEnum moduleType) {
		int retcode = smsSendService.sendSMS(moduleType, phone, "", "");
		if(retcode==100||retcode==105){
			return true;
		}
		return false;
	}
	/**
	 *
	 * @param json
	 * @param moduleType
	 * @return
	 */
	@Override
	public boolean sendSmsCode(String json, ModuleTypeEnum moduleType) {
		JSONObject jsonObject =  JSON.parseObject(json);
		int retcode = 999;
		String phoneNo = null;
		switch (moduleType) {
			case BINDCARD:
				phoneNo = jsonObject.get("phoneNo").toString();
				retcode = smsSendService.sendSMS(moduleType, phoneNo, "", "");
				break;
			case CHANGELOGINPWD:
				phoneNo = jsonObject.get("phoneNo").toString();
				retcode = smsSendService.sendSMS(moduleType, phoneNo, "", "");
				break;
			case CHANGEPAYPWD:
				phoneNo = jsonObject.get("phoneNo").toString();
				retcode = smsSendService.sendSMS(moduleType, phoneNo, "", "");
				break;
			case PAY:
				//需要bindId tn
				try {
					gateWayService.sendSMSMessage(json);
				} catch (TradeException e) {
					e.printStackTrace();
					return false;
				}
				return true;
			case REGISTER:
				phoneNo = jsonObject.get("phoneNo").toString();
				retcode = smsSendService.sendSMS(moduleType, phoneNo, "", "");
				break;
			case ACCOUNTPAY:
				phoneNo = jsonObject.get("phoneNo").toString();
				String tn = jsonObject.get("tn").toString();
				retcode = smsSendService.sendSMS(moduleType, phoneNo, tn, "");
				break;
			case ANONYMOUSPAY:
				 String tn_=jsonObject.get("tn").toString();
				 String cardNo=jsonObject.get("cardNo").toString();
				 String cardType=jsonObject.get("cardType").toString();
				 String customerNm=jsonObject.get("customerNm").toString();
				 String certifTp=jsonObject.get("certifTp").toString();
				 String certifId=jsonObject.get("certifId").toString();
				 phoneNo=jsonObject.get("phoneNo").toString();
				 String cvn2=jsonObject.get("cvn2")+"";
				 String expired=jsonObject.get("expired")+"";
				 String bindFlag = jsonObject.get("bindFlag")+"";
				 String instiCode = jsonObject.get("instiCode")+"";
				 String devId = jsonObject.get("devId")+"";
				 List<QuickpayCustModel> cardList = this.quickpayCustService.getCardList(cardNo, customerNm, phoneNo, certifId, "999999999999999");
				//(List<QuickpayCustModel>) quickpayCustService.queryByHQL("from QuickpayCustModel where cardno=? and accname = ? and phone = ? and idnum = ? and relatememberno = ? and status = ?", new Object[]{cardNo,customerNm,phoneNo,certifId,"999999999999999","00"});
	        	if(cardList.size()>0){//已绑卡
	        		Map<String, Object> resultMap = new HashMap<String, Object>();
	        		resultMap.put("tn", tn_);
	        		resultMap.put("bindId", cardList.get(0).getId());
	        		try {
						gateWayService.sendSMSMessage(JSON.toJSONString(resultMap));
						return true;
					} catch (TradeException e) {
						e.printStackTrace();
						log.error("发送短信失败"+e.getMessage());
						return false;
					}
	        	}else{
	        		if("1".equals(bindFlag)){//需要进行绑卡签约
	        	        WapCardBean cardBean = new WapCardBean(cardNo,cardType , customerNm,certifTp, certifId, phoneNo, cvn2, expired);
	        	        ResultBean resultBean = gateWayService.bindingBankCard(instiCode, "999999999999999", cardBean);
	        	        if(resultBean.isResultBool()){
	        	        	//保存绑卡信息
	        	            QuickpayCustBean quickpayCustBean = new QuickpayCustBean();
	        	            quickpayCustBean.setCustomerno(instiCode);
	        	            quickpayCustBean.setCardno(cardNo);
	        	            quickpayCustBean.setCardtype(cardType);
	        	            quickpayCustBean.setAccname(customerNm);
	        	            quickpayCustBean.setPhone(phoneNo);
	        	            quickpayCustBean.setIdtype(certifTp);
	        	            quickpayCustBean.setIdnum(certifId);
	        	            quickpayCustBean.setCvv2(cvn2);
	        	            quickpayCustBean.setValidtime(expired);
	        	            quickpayCustBean.setRelatememberno("999999999999999");
	        	            //新增设备ID支持匿名支付
	        	            quickpayCustBean.setDevId(devId);
	        	            CardBin cardBin = cardBinDao.getCard(cardNo);
	        	            quickpayCustBean.setBankcode(cardBin.getBankCode());
	        	            quickpayCustBean.setBankname(cardBin.getBankName());
	        	            long bindId = memberBankCardService.saveQuickPayCust(quickpayCustBean);
	        	            
	        	            Map<String, Object> resultMap = new HashMap<String, Object>();
	    	        		resultMap.put("tn", tn_);
	    	        		resultMap.put("bindId", bindId+"");
	    	        		try {
	    						gateWayService.sendSMSMessage(JSON.toJSONString(resultMap));
	    						return true;
	    					} catch (TradeException e) {
	    						e.printStackTrace();
	    						log.error("发送短信失败"+e.getMessage());
	    						return false;
	    					}
	        	            
	        	        }
	        		}else{
	        			log.error("bindFlag is null");
	        			return false;
	        		}
	        		
	        	}
			default:
				
				break;
		}
		if(retcode==100||retcode==105){
			return true;
		}
		log.error("moduleType is error");
		return false;
	}

	public static void main(String[] args) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("tn", "123");
		resultMap.put("bindId", "123456");
		
		System.out.println(JSON.toJSONString(resultMap));
	}
}
