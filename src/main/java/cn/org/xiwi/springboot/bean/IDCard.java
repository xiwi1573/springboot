package cn.org.xiwi.springboot.bean;

/**
 * 身份证信息
 * @author xiwi
 * */
public class IDCard {
	private String idCardNum;
	private String gender;
	private int genderCode;
	private String birthday;
	private int age;
	private String province;
	private String city;
	private String zone;
	
	public String getIdCardNum() {
		return idCardNum;
	}
	public void setIdCardNum(String idCardNum) {
		this.idCardNum = idCardNum;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public int getGenderCode() {
		return genderCode;
	}
	public void setGenderCode(int genderCode) {
		this.genderCode = genderCode;
	}
	public String getBirthday() {
		return birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getZone() {
		return zone;
	}
	public void setZone(String zone) {
		this.zone = zone;
	}
	@Override
	public String toString() {
		return "IDCard [idCardNum=" + idCardNum + ", gender=" + gender + ", genderCode=" + genderCode + ", birthday="
				+ birthday + ", age=" + age + ", province=" + province + ", city=" + city + ", zone=" + zone + "]";
	}
}
