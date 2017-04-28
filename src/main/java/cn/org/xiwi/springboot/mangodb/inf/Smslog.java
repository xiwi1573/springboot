package cn.org.xiwi.springboot.mangodb.inf;

import java.util.Date;

public class Smslog {

	private String businessNo;
	private String mobileNo;
	private Date createDate;
	
	public void setBusinessNo(String businessNo) {
		this.businessNo = businessNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getBusinessNo() {
		return businessNo;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public Date getCreateDate() {
		return createDate;
	}

}
