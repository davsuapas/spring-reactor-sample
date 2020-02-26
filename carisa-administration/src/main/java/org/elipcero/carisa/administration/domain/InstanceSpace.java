package org.elipcero.carisa.administration.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import org.elipcero.carisa.core.data.Relation;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Table("carisa_instance_space")
@Builder
@Getter
public class InstanceSpace implements Relation {

    public static String INSTANCEID_COLUMN_NAME = "parentId";
    public static String SPACEID_COLUMN_NAME = "spaceId";

    @PrimaryKeyColumn(ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private UUID parentId; // instance identifier

    @PrimaryKeyColumn(ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    private UUID spaceId;

    @Override
    public UUID getParentId() {
        return this.parentId;
    }

    @Override
    @JsonIgnore
    public UUID getChildId() {
        return this.spaceId;
    }

    public static Map<String, Object> GetMapId(UUID instanceId, UUID spaceId) {
        return new HashMap<String, Object>() {{
            put(INSTANCEID_COLUMN_NAME, instanceId);
            put(SPACEID_COLUMN_NAME, spaceId);
        }};
    }
}
