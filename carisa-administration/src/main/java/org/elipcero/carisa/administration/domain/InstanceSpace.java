package org.elipcero.carisa.administration.domain;

import lombok.Builder;
import lombok.Getter;
import org.elipcero.carisa.core.data.Relation;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Table("carisa_instance_space")
@Builder
@Getter
public class InstanceSpace implements Relation {

    public static String INSTANCEID_COLUMN_NAME = "instanceId";
    public static String SPACEID_COLUMN_NAME = "spaceId";

    @Column("parentId")
    @PrimaryKeyColumn(ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private UUID instanceId;

    @PrimaryKeyColumn(ordinal = 2, type = PrimaryKeyType.CLUSTERED)
    private UUID spaceId;

    public UUID getParentId() {
        return this.instanceId;
    }

    public UUID getChildId() {
        return this.spaceId;
    }

    @Override
    public void setParentId(UUID value) {
        this.instanceId = value;
    }

    public static Map<String, Object> GetMapId(UUID instanceId, UUID spaceId) {
        return new HashMap<String, Object>() {{
            put(INSTANCEID_COLUMN_NAME, instanceId);
            put(SPACEID_COLUMN_NAME, spaceId);
        }};
    }
}
