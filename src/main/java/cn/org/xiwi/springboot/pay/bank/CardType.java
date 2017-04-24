package cn.org.xiwi.springboot.pay.bank;

public enum CardType {
    SAVINGS("DC","储蓄卡"),
    VISA("CC","信用卡");
    private String cardType;
    private String cardTypeName;
    CardType(String cardType,String cardTypeName){
        this.cardType = cardType;
        this.cardTypeName = cardTypeName;
    }

    @Override
    public String toString() {
        return "CardType{" +
                "cardType='" + cardType + '\'' +
                ", cardTypeName='" + cardTypeName + '\'' +
                '}';
    }
}
