package com.xtj.hikvisionsdkintegration.dto;



import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.logging.Logger;


@Component
@ConfigurationProperties(prefix = "mon-data")
public class AssetPool {

    Logger logger = Logger.getLogger("com.xtj.hikvisionsdkintegration.dto.AssetPool");




    public static class Zone {
        private String buildingName;


        private List<BuildingMultistory> buildingMultistory;

        public String getBuildingName() {
            return buildingName;
        }

        public void setBuildingName(String buildingName) {
            this.buildingName = buildingName;
        }

        public List<BuildingMultistory> getBuildingMultistory() {
            return buildingMultistory;
        }

        public void setBuildingMultistory(List<BuildingMultistory> buildingMultistory) {
            this.buildingMultistory = buildingMultistory;
        }

        public static class BuildingMultistory {
            private String floorName;


            private List<IpInfo> ipInfo;

            public String getFloorName() {
                return floorName;
            }

            public void setFloorName(String floorName) {
                this.floorName = floorName;
            }

            public List<IpInfo> getIpInfo() {
                return ipInfo;
            }

            public void setIpInfo(List<IpInfo> ipInfo) {
                this.ipInfo = ipInfo;
            }

            public static class IpInfo {
                private String ipDesc;
                private String ip;

                public String getIpDesc() {
                    return ipDesc;
                }

                public void setIpDesc(String ipDesc) {
                    this.ipDesc = ipDesc;
                }

                public String getIp() {
                    return ip;
                }

                public void setIp(String ip) {
                    this.ip = ip;
                }
            }


        }
    }


}
