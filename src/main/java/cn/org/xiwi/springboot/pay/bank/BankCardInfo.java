package cn.org.xiwi.springboot.pay.bank;

public class BankCardInfo {
	private String bank;// 银行标识
	private String bankName;// 银行名称
	private String bankImg;// 银行LOGO
	private String cardType;// 卡类型
	private String cardTypeName;// 卡类型名称

	public String getBank() {
		return bank;
	}

	public void setBank(String bank) {
		this.bank = bank;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getBankImg() {
		return bankImg;
	}

	public void setBankImg(String bankImg) {
		this.bankImg = bankImg;
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

	@Override
	public String toString() {
		return "BankCardInfo [bank=" + bank + ", bankName=" + bankName + ", bankImg=" + bankImg + ", cardType="
				+ cardType + ", cardTypeName=" + cardTypeName + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((bank == null) ? 0 : bank.hashCode());
		result = prime * result + ((bankImg == null) ? 0 : bankImg.hashCode());
		result = prime * result + ((bankName == null) ? 0 : bankName.hashCode());
		result = prime * result + ((cardType == null) ? 0 : cardType.hashCode());
		result = prime * result + ((cardTypeName == null) ? 0 : cardTypeName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BankCardInfo other = (BankCardInfo) obj;
		if (bank == null) {
			if (other.bank != null)
				return false;
		} else if (!bank.equals(other.bank))
			return false;
		if (bankImg == null) {
			if (other.bankImg != null)
				return false;
		} else if (!bankImg.equals(other.bankImg))
			return false;
		if (bankName == null) {
			if (other.bankName != null)
				return false;
		} else if (!bankName.equals(other.bankName))
			return false;
		if (cardType == null) {
			if (other.cardType != null)
				return false;
		} else if (!cardType.equals(other.cardType))
			return false;
		if (cardTypeName == null) {
			if (other.cardTypeName != null)
				return false;
		} else if (!cardTypeName.equals(other.cardTypeName))
			return false;
		return true;
	}

}
