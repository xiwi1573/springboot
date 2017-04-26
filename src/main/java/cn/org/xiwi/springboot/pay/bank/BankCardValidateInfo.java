package cn.org.xiwi.springboot.pay.bank;

public class BankCardValidateInfo {
	private boolean validated;
	private String cardNum;
	private String cardType;
	private String cardTypeName;
	private String bank;
	public boolean isValidated() {
		return validated;
	}
	public void setValidated(boolean validated) {
		this.validated = validated;
	}
	public String getCardNum() {
		return cardNum;
	}
	public void setCardNum(String cardNum) {
		this.cardNum = cardNum;
	}
	public String getCardType() {
		return cardType;
	}
	public void setCardType(String cardType) {
		this.cardType = cardType;
	}
	public String getCardTypeName() {
		return cardTypeName;
	}
	public void setCardTypeName(String cardTypeName) {
		this.cardTypeName = cardTypeName;
	}
	public String getBank() {
		return bank;
	}
	public void setBank(String bank) {
		this.bank = bank;
	}
	@Override
	public String toString() {
		return "BankCardValidateInfo [validated=" + validated + ", cardNum=" + cardNum + ", cardType=" + cardType
				+ ", cardTypeName=" + cardTypeName + ", bank=" + bank + "]";
	}
	
}
