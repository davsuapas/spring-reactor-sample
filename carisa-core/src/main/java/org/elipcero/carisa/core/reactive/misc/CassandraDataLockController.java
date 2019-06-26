/*
 * Copyright 2019-2022 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.elipcero.carisa.core.reactive.misc;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.cassandra.core.ReactiveCassandraOperations;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

/**
 * Controller to critical section lock between process using cassandra database
 *
 * @author David Suárez
 */
@RequiredArgsConstructor
public class CassandraDataLockController {

    @NonNull
    private final ReactiveCassandraOperations cqlTemplate;

    /**
     * Lock a resource
     * @param id the resource id
     * @param secondsExpired when expire the resource in seconds
     * @return if it can be locked return true otherwise false
     */
    public Mono<Boolean> lock(final UUID id, final int secondsExpired) {

        return this.cqlTemplate.getReactiveCqlOperations()
                .execute("INSERT INTO data_lock (Id, DateExpired) VALUES (?, ?) IF NOT EXISTS",
                    id, Date.from(Instant.now().plusSeconds(secondsExpired)))
            .flatMap(applied -> {
                if (applied) {
                    return Mono.just(true);
                }
                else { // Update if expired
                    Instant now = Instant.now();
                    return this.cqlTemplate.getReactiveCqlOperations()
                            .execute("UPDATE data_lock SET DateExpired = ? WHERE Id = ? IF DateExpired < ?",
                                    Date.from(now.plusSeconds(secondsExpired)), id, Date.from(now));
                }
            });
    }

    /**
     * Unlock a resource
     * @param id the resource id
     * @return if it can be unlocked return true otherwise false
     */
    public Mono<Boolean> unLock(UUID id) {
        return this.cqlTemplate.getReactiveCqlOperations().execute("DELETE FROM data_lock WHERE Id = ?", id);
    }
}
