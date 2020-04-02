package org.elipcero.carisa.administration.domain;

import org.elipcero.carisa.core.data.ManyRelation;
import org.springframework.data.cassandra.core.mapping.Table;

/**
 * @author David Suárez
 */

@Table("carisa_instance_space")
public class InstanceSpace extends ManyRelation {
}
