package kz.halyqsoft.univercity.entity.beans.univercity.view;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

public class VDetailEmployeeLate extends AbstractEntity {

        @FieldInfo(type=EFieldType.TEXT, order = 1)
        private String FIO;

        @FieldInfo(type = EFieldType.TEXT, order = 2)
        private String postName;

        @FieldInfo(type = EFieldType.DOUBLE, order = 3)
        private Double absentSum;

        @FieldInfo(type = EFieldType.INTEGER, order = 5, inGrid = false, inView = false, inEdit = false )
        private String time;

        public String getFIO() {
            return FIO;
        }

        public void setFIO(String FIO) {
            this.FIO = FIO;
        }

        public String getPostName() {
            return postName;
        }

        public void setPostName(String postName) {
            this.postName = postName;
        }

        public Double getAbsentSum() {
            return absentSum;
        }

        public void setAbsentSum(Double absentSum) {
            this.absentSum = absentSum;
        }

        public String getTime() {
                return time;
            }

        public void setTime(String time) {
            this.time = time;
        }

}
