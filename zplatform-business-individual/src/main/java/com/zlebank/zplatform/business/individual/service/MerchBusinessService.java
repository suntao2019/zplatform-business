/* 
 * MerchService.java  
 * 
 * version TODO
 *
 * 2016年6月24日 
 * 
 * Copyright (c) 2016,zlebank.All rights reserved.
 * 
 */
package com.zlebank.zplatform.business.individual.service;

import com.zlebank.zplatform.business.individual.exception.CommonException;
import com.zlebank.zplatform.trade.model.PojoMerchWhiteList;

/**
 * 针对商户的服务service
 *
 * @author guojia
 * @version
 * @date 2016年6月24日 上午10:10:48
 * @since 
 */
public interface MerchBusinessService {

	
	/**
	 * 更新商户公钥
	 * @param memberId
	 * @param pub_key
	 * @return
	 */
	public boolean updateMerchPubKey(String memberId, String pub_key) throws CommonException;
	
	/**
	 * 保存白名单信息
	 * @param merchWhiteList
	 * @return
	 */
	public boolean saveWhiteList(PojoMerchWhiteList merchWhiteList)  throws CommonException;
	
	/**
	 * 更新白名单
	 * @param merchWhiteList
	 * @return
	 */
	public boolean updateWhiteList(PojoMerchWhiteList merchWhiteList)  throws CommonException;
	
	/**
	 * 删除白名单
	 * @param id
	 * @return
	 */
	public boolean deleteWhiteList(Long id)  throws CommonException;
	
	/**
	 * 重置商户支付密码
	 * @param memberId
	 * @param pwd
	 * @return
	 * @throws DataCheckFailedException
	 */
	public boolean resetPayPwd(String memberId, String pwd) throws CommonException;
}
