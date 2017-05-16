package de.blinkt.openvpn.model;

/**
 * Created by Administrator on 2017/4/12 0012.
 */

public class UsageRemainEntity {
    private Used Used;
    private Unactivated Unactivated;

    public UsageRemainEntity.Used getUsed() {
        return Used;
    }

    public void setUsed(UsageRemainEntity.Used used) {
        Used = used;
    }

    public UsageRemainEntity.Unactivated getUnactivated() {
        return Unactivated;
    }

    public void setUnactivated(UsageRemainEntity.Unactivated unactivated) {
        Unactivated = unactivated;
    }

   public class Used{
        private String ServiceName;
        private String TotalNum;
        private String TotalNumFlow;
        private String TotalRemainingCallMinutes;

	   public String getServiceName() {
		   return ServiceName;
	   }

	   public void setServiceName(String serviceName) {
		   ServiceName = serviceName;
	   }

	   public String getTotalNum() {
            return TotalNum;
        }

        public void setTotalNum(String totalNum) {
            TotalNum = totalNum;
        }

        public String getTotalNumFlow() {
            return TotalNumFlow;
        }

        public void setTotalNumFlow(String totalNumFlow) {
            TotalNumFlow = totalNumFlow;
        }

        public String getTotalRemainingCallMinutes() {
            return TotalRemainingCallMinutes;
        }

        public void setTotalRemainingCallMinutes(String totalRemainingCallMinutes) {
            TotalRemainingCallMinutes = totalRemainingCallMinutes;
        }
    }
    public class Unactivated{
        private String TotalNumFlow;

        public String getTotalNumFlow() {
            return TotalNumFlow;
        }

        public void setTotalNumFlow(String totalNumFlow) {
            TotalNumFlow = totalNumFlow;
        }
    }

}
