package org.apache.ranger.services.gaian;

import java.sql.Date;
import java.sql.Types;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.types.TypeId;

/**
 * Created by shiwang on 2/28/18.
 */
public class ApplyMasking {

    public static void redact(DataValueDescriptor dataValueDescriptor) {
        try {

            if (dataValueDescriptor == null) return;
            int jdbcType = TypeId.getBuiltInTypeId(dataValueDescriptor.getTypeName()).getJDBCTypeId();
            switch (jdbcType) {
                case Types.CHAR:
                case Types.VARCHAR:
                case Types.LONGVARCHAR:
                case Types.CLOB:
                    dataValueDescriptor.setValue("####");
                    break;
                case Types.DATE:
                case Types.TIME:
                case Types.TIMESTAMP:
                    dataValueDescriptor.setValue(new Date(0));
                    break;
                case Types.INTEGER:
                case Types.DOUBLE:
                case Types.DECIMAL:
                case Types.FLOAT:
                    dataValueDescriptor.setValue(1111);
                    break;
                default:
                    dataValueDescriptor.setValue("Masked");
            }
        } catch (StandardException e) {
            e.printStackTrace();
        }
    }
}
