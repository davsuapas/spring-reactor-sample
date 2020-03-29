package org.elipcero.carisa.administration.domain;

import lombok.Builder;
import org.elipcero.carisa.administration.domain.support.ManyRelation;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.UUID;

/**
 * @author David Suárez
 */

@Table("carisa_instance_space")
public class InstanceSpace extends ManyRelation {

    @Builder
    public InstanceSpace(UUID parentId, UUID childId) {
        super(parentId, childId);
    }
}
