package com.android.dream.app.bean;

import java.io.Serializable;

/**
 * @author zhaoj
 * 盆友圈赞档
 *
 */
public class AdFriendZanVo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8710117068206081604L;
	private String userno;
	private String username;
	
	public String getUserno() {
		return userno;
	}

	public void setUserno(String userno) {
		this.userno = userno;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
            return true;
		
		if (obj == null)
            return false;
		
		if (obj instanceof AdFriendZanVo)
		{
			final AdFriendZanVo other = (AdFriendZanVo) obj;
			
			return this.userno.equals(other.userno);
		}
       
		return false;
	}
}
